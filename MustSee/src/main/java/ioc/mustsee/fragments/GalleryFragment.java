package ioc.mustsee.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import ioc.mustsee.R;
import ioc.mustsee.data.Imatge;
import ioc.mustsee.ui.GridViewAdapter;
import ioc.mustsee.ui.ImageItem;

/**
 * Fragment que mostra en una graella totes les imatges pertanyents al lloc seleccionat actualment.
 *
 * @author Javier García
 */
public class GalleryFragment extends MustSeeFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "GalleryFragment";

    private GridView mGridView;
    private GridViewAdapter mCustomGridAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Si la vista no existeix la inflem i inicalitzem els widgets
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_gallery, null);
            initWidgets();
        }
        return mView;
    }

    /**
     * Inicialitzem la graella, li asignem l'adaptador i l'associem amb el listener.
     */
    @Override
    void initWidgets() {
        mGridView = (GridView) mView.findViewById(R.id.gridView);
        mCustomGridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_row_gallery, getData());
        mGridView.setAdapter(mCustomGridAdapter);
        mGridView.setOnItemClickListener(this);
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
        mCallback.OnActionDetected(OnFragmentActionListener.ACTION_PHOTO, bundle);
    }

    /**
     * Retorna un ArrayList amb les imatges que formaran la graella.
     *
     * @return ArrayList amb les imatges que forman la graella.
     * TODO: Substituir ImateItem per Imatge aqui i al adaptador.
     */
    private ArrayList getData() {
        List<Imatge> imatges = mCallback.getCurrentLloc().getImages();
        final ArrayList imageItems = new ArrayList();

        for (Imatge imatge : imatges) {
            Bitmap bitmap = imatge.carregarImatge(getActivity());
            imageItems.add(new ImageItem(bitmap, imatge.tittle));
        }
        return imageItems;
    }
}
