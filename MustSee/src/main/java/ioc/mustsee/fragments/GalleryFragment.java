package ioc.mustsee.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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

public class GalleryFragment extends MustSeeFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "GalleryFragment";

    private GridView gridView;
    private GridViewAdapter customGridAdapter;

    public GalleryFragment(int fragmentId) {
        super(fragmentId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "inflando layout galeria");


        mView = inflater.inflate(R.layout.fragment_gallery, null);
/*
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_gallery, container, false);
        }
*/

        initWidgets(mView);
        return mView;
    }

    private void initWidgets(View v) {
        Log.d(TAG, "Inicialitzat widtegs de " + TAG);

        gridView = (GridView) v.findViewById(R.id.gridView);
        customGridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_row_gallery, getData());
        gridView.setAdapter(customGridAdapter);

        gridView.setOnItemClickListener(this);

        /*
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });
*/
        Log.d(TAG, "Hay listener asociado a la galeria?" + gridView.getOnItemClickListener());
    }

    // TODO no deben usarse imageItem, solo Imatges, modificar esto y el adaptador
    private ArrayList getData() {
        // Obtiene la galeria de imagenes del curren lloc
        Log.d(TAG, "Gallery: Intentando recuperar lloc actual:" + mCallback.getLlocActual());
        Log.d(TAG, "Gallery: Imatges." + mCallback.getLlocActual().getImages());
        List<Imatge> imatges = mCallback.getLlocActual().getImages();

        final ArrayList imageItems = new ArrayList();

        int i = 0;
        for (Imatge imatge : imatges) {
            Bitmap bitmap = imatge.carregarImatge(getActivity());
            imageItems.add(new ImageItem(bitmap, imatge.tittle));
        }


        return imageItems;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "Se ha hecho click en el item position:" + position);
        // Mostrar imagen
        Bundle bundle = new Bundle();
        // TODO: Cambiar la manera en la que se manejan las ids, no hay garantia de que el indice corresponda correctamente con la photo
        bundle.putInt("PHOTO", position);
        mCallback.OnActionDetected(OnFragmentActionListener.ACTION_PHOTO, bundle);

    }
}
