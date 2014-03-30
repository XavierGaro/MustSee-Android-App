package ioc.mustsee.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import ioc.mustsee.R;
import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Lloc;
import ioc.mustsee.ui.CategoriaArrayAdapter;
import ioc.mustsee.ui.LlocArrayAdapter;
import ioc.mustsee.ui.MySpinner;

import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_DETAIL;

public class MyListFragment extends MustSeeFragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private static final String TAG = "MyListFragment";
    private ListView listViewLlocs;
    private MySpinner spinnerCategories;
    private List<Lloc> llocs;
    private List<Categoria> categories;
    private ArrayAdapter<Lloc> adapterLlocs;
    private ArrayAdapter<Categoria> adapterCategories;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_list, null);
            initWidgets();
        }

        return mView;
    }

    private void initWidgets() {
        Log.d(TAG, "Iniciando Widgets");
        // Afegim les categories al spinner
        categories = mCallback.getCategories();
        spinnerCategories = (MySpinner) mView.findViewById(R.id.spinnerCategoria);
        spinnerCategories.setOnItemSelectedListener(this);
        spinnerCategories.setOnItemSelectedEvenIfUnchangedListener(this);
        adapterCategories = new CategoriaArrayAdapter(getActivity(), categories);
        spinnerCategories.setAdapter(adapterCategories);

        // Afegim els llocs a la list mView
        llocs = mCallback.getLlocs();
        listViewLlocs = (ListView) mView.findViewById(R.id.listViewLlocs);
        listViewLlocs.setOnItemClickListener(this);
        adapterLlocs = new LlocArrayAdapter(getActivity(), llocs);
        listViewLlocs.setAdapter(adapterLlocs);
        Log.d(TAG, "Fin de Iniciando Widgets");

    }


    @Override
    public void onResume() {
        Log.d(TAG, "Entrando en onResume");
        super.onResume();
        Log.d(TAG, "Saliendo de onResume");

    }

    // Spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int catId = categories.get(position).id;
        Log.d(TAG, "onItemSelected: " + categories.get(position).nom);

        if (catId == 0) {
            // Selecciona tot
            adapterLlocs.getFilter().filter(null);
        } else {
            adapterLlocs.getFilter().filter(String.valueOf(catId));
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Esto ocurre cuando el elemento seleccionado desaparece del spinner (por ser filtrado por ejemplo)
        Log.d(TAG, "No se ha seleccionado nada nuevo. Ha ocurrido?");

    }

    // List View
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Establecemos el item como seleccionado
        view.setSelected(true);
        Log.d(TAG, "Está seleccionado? " + view.isSelected());

        Lloc selected = (Lloc) parent.getItemAtPosition(position);
        Lloc current = mCallback.getLlocActual();
        Log.d(TAG, "Se ha cliclado el sitio: " + selected.nom);
        if (current != null) {
            Log.d(TAG, "El sitio actual es : " + current.nom);

        } else {
            Log.d(TAG, "El sitio actual es : null");
        }

        // Si el sitio ya está en selected muestra el detalle
        if (current == null || selected.id != current.id) {
            Log.d(TAG, "Estableciendo como actual: " + selected.nom);
            // Centra el sitio en la pantalla y muestra su info (en el principal,aquí solo se setea)
            mCallback.setLlocActual(selected);
        } else {
            Log.d(TAG, "Se debería mostrar detalle de: " + selected.nom);
            // TODO: cambiar la vista de mapa a detalle
            mCallback.OnActionDetected(ACTION_DETAIL);
        }


    }

    public void setSelected(Lloc seleccionarLloc) {
        Log.d(TAG, "Set selected: " + seleccionarLloc.nom);


        // Recorremos la lista de sitios
        int len = listViewLlocs.getCount();
        for (int i = 0; i < len; i++) {
            if (listViewLlocs.getItemAtPosition(i) == seleccionarLloc) {

                // OJO! Si no se hace el request Focus from touch no se establece la selección
                listViewLlocs.requestFocusFromTouch();
                listViewLlocs.setSelection(i);
                //listViewLlocs.setItemChecked(i, true);

                // Es mou la barra fins aquesta posició NO HACE FALTA, no aprecio diferencia entre el comportamiento normal y este. TODO: Comprovar con más elementos en la lista
                listViewLlocs.smoothScrollToPosition(i);

                listViewLlocs.setSelected(true); //TODO: Comprobar en dispositivo si funciona o no

                Log.d(TAG, "Encontrado: " + ((Lloc) listViewLlocs.getItemAtPosition(i)).nom);
                Log.d(TAG, "position: " + i);
                Log.d(TAG, "Posición seleccionada: " + listViewLlocs.getSelectedItemPosition());
                Log.d(TAG, "Item selecionado: " + ((Lloc) listViewLlocs.getSelectedItem()).nom);
                return;
            }
        }
        /*



        for (int i=0,len=llocs.size(); i<len; i++) {
            if (llocs.get(i)==seleccionarLloc) {
                listViewLlocs.setSelection(i);
                //listViewLlocs.setItemChecked(i, true);


                Log.d(TAG, "Lugar en la posición "+i+"?: "+((Lloc) listViewLlocs.getItemAtPosition(i)).nom);
                Log.d(TAG, "Lugar en la posición "+i+"?: "+((Lloc) listViewLlocs.getItemAtPosition(i)).nom);

                return;
            }
        }
        */
    }
}
