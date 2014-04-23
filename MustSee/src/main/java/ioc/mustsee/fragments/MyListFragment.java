package ioc.mustsee.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Comparator;
import java.util.List;

import ioc.mustsee.R;
import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Lloc;
import ioc.mustsee.ui.CategoriaArrayAdapter;
import ioc.mustsee.ui.LlocArrayAdapter;
import ioc.mustsee.ui.MySpinner;

import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_DETAIL;

/**
 * Aquest fragment mostra un Spinner personalitzat amb les categories i un ListView amb la
 * informació dels llocs que coincideixin amb la categoria seleccionada.
 *
 * @author Javier García
 */
public class MyListFragment extends MustSeeFragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private static final String TAG = "MyListFragment";

    private ListView mListViewLlocs;
    private List<Categoria> mCategories;
    private ArrayAdapter<Lloc> mAdapterLlocs;

    /**
     * Aquest comparador ordena els llocs per distancia del més proper al més llunyà.
     */
    private static Comparator<Lloc> sortLlocs = new Comparator<Lloc>() {
        public int compare(Lloc llocA, Lloc llocB) {
            if (llocA == llocB) {
                return 0;
            } else if (llocA.getDistance() > llocB.getDistance()) {
                return 11;
            } else {
                return -1;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Si la vista no existeix la inflem i inicialitzem els widgets
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_list, null);
            initWidgets();
        }
        return mView;
    }

    /**
     * Inicialitzem el Spinner i el ListView amb la informació de la activitat principal, i els
     * afegim els listener corresponents.
     */
    void initWidgets() {
        // Afegim les categories al Spinner
        mCategories = mCallback.getCategories();
        ArrayAdapter<Categoria> adapterCategories = new CategoriaArrayAdapter(getActivity(), mCategories);
        MySpinner spinnerCategories = (MySpinner) mView.findViewById(R.id.spinnerCategoria);
        spinnerCategories.setOnItemSelectedListener(this);
        spinnerCategories.setOnItemSelectedEvenIfUnchangedListener(this);
        spinnerCategories.setAdapter(adapterCategories);

        // Afegim els llocs a la ListView
        List<Lloc> llocs = mCallback.getLlocs();
        mAdapterLlocs = new LlocArrayAdapter(getActivity(), llocs);
        mListViewLlocs = (ListView) mView.findViewById(R.id.listViewLlocs);
        mListViewLlocs.setOnItemClickListener(this);
        mListViewLlocs.setAdapter(mAdapterLlocs);

        // Actualitzem la llista
        updateListView();

    }

    /**
     * Listener cridat pel Spinner que activa el filtre del ListView. Si la categoriaID es 0 es
     * selecciona tot.
     *
     * @param parent   adaptador.
     * @param view     vista clicada.
     * @param position posició de la vista clicada a la llista.
     * @param id       ide de la vista clicada a la llista.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int categoriaId = mCategories.get(position).id;
        if (mAdapterLlocs == null) {
            return;

        } else if (categoriaId == 0) {
            // Seleccionem tots els llocs
            mAdapterLlocs.getFilter().filter(null);

        } else {
            // Filtrem els llocs que pertanyen a la categoria.
            mAdapterLlocs.getFilter().filter(String.valueOf(categoriaId));
        }
    }

    /**
     * Aquest mètode es obligatori implementar-lo encara que no fem res amb ell.
     *
     * @param parent adaptador
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Es cridat quan l'element seleccionat al Spinner desapareix.
    }

    /**
     * Listener cridat pel ListView al clicar sobre un element de la llista, segons si ja teniem el
     * mateix lloc seleccionat, si està disponible la vista de detall o de mapa realitzarà diferents
     * accions sobre l'activitat principal.
     *
     * @param parent   adaptador.
     * @param view     vista clicada.
     * @param position posició de la vista clicada a la llista.
     * @param id       ide de la vista clicada a la llista.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Marquem el lloc com a seleccionat a la vista.
        view.setSelected(true);

        // Recuperem els llocs actual i seleccionat per comparar-los
        Lloc selectedLloc = (Lloc) parent.getItemAtPosition(position);
        Lloc currentLloc = mCallback.getCurrentLloc();

        if (currentLloc == null || (selectedLloc != null && selectedLloc.id != currentLloc.id)) {
            // Si no hi ha cap lloc actual, o el lloc actual i el seleccionat son diferents establim
            // el lloc com a actual
            mCallback.setCurrentLloc(selectedLloc);
        } else {
            // Si es el mateix mostrem la vista de detall
            mCallback.OnActionDetected(ACTION_DETAIL);
        }
    }

    /**
     * Estableix el lloc passat com argument com a lloc seleccionat, intenta marcarlo com seleccionat
     * i mou la barra de scroll suaument fins a la seva posició. Aquest mètode es cridat des de la
     * activitat principal per actualitzar la posició de la llista. Degut al funcionament del focus
     * i el mode Touch es es normal que no es mostri la selecció quan toquem altres parts de la
     * pantalla.
     *
     * @param selectedLloc lloc per establir com seleccionat.
     */
    public void setSelected(Lloc selectedLloc) {
        // Recorrem la llista de llocs fins trobar el seleccionat
        for (int i = 0, len = mListViewLlocs.getCount(); i < len; i++) {
            if (mListViewLlocs.getItemAtPosition(i) == selectedLloc) {
                // Es requereix cridar a requestFocusFromTouch() per poder establir la selecció
                mListViewLlocs.requestFocusFromTouch();
                mListViewLlocs.setSelection(i);
                mListViewLlocs.smoothScrollToPosition(i);
                mListViewLlocs.setSelected(true);
                return;
            }
        }
    }

    /**
     * Reordena la llista i actualitza les dades, això permet tenir les distancies actualitzades i
     * ordenades.
     */
    public void updateListView() {
        mAdapterLlocs.sort(sortLlocs);
        mAdapterLlocs.notifyDataSetChanged();
    }
}
