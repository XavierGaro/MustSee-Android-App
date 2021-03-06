package ioc.mustsee.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import ioc.mustsee.R;
import ioc.mustsee.ui.GridViewGalleryAdapter;

/**
 * Fragment que mostra en una graella totes les imatges pertanyents al lloc seleccionat actualment.
 *
 * @author Xavier García
 */
public class GalleryFragment extends MustSeeFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "GalleryFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Si la vista no existeix la inflem i inicialitzem els widgets
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_gallery, null);
            initWidgets();
        }
        return mView;
    }

    /**
     * Inicialitzem la graella, li assignem l'adaptador i l'associem amb el listener.
     */
    @Override
    void initWidgets() {
        GridViewGalleryAdapter customGridAdapter = new GridViewGalleryAdapter(getActivity(),
                R.layout.grid_row_gallery, mCallback.getCurrentLloc().getImages());
        GridView gridView = (GridView) mView.findViewById(R.id.gridView);
        gridView.setAdapter(customGridAdapter);
        gridView.setOnItemClickListener(this);
    }

    /**
     * Listener pels clicks a la graella. Quan rep un click ho comunica a la acció principal enviant
     * en un bundle la informació referent a la posició de la vista clicada.
     *
     * @param parent   vista del adaptador.
     * @param view     vista clicada.
     * @param position posició de la vista a la graella.
     * @param id       index de la vista a la graella.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putInt("PICTURE", position);
        mCallback.OnActionDetected(OnFragmentActionListener.ACTION_PICTURE, bundle);
    }
}
