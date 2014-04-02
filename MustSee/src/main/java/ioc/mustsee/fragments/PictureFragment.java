package ioc.mustsee.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ioc.mustsee.R;

/**
 * Aquest fragment mostra una imatge que s'obté del lloc actual de la activitat principal.
 */
public class PictureFragment extends MustSeeFragment {
    private static final String TAG = "PictureFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Si la vista no existeix la inflem i inicialitzem els widgets
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_picture, null);
            initWidgets();
        }
        return mView;
    }

    /**
     * Inicialitzem la imatge amb la id que s'ha passat com amb el bundle al crear el fragment.
     */
    void initWidgets() {
        Bundle bundle = getArguments();
        int imatgeId = bundle.getInt("PICTURE");

        ImageView imageViewPicture;
        imageViewPicture = (ImageView) mView.findViewById(R.id.imageViewPicture);
        imageViewPicture.setImageBitmap(mCallback
                        .getCurrentLloc()
                        .getImages()
                        .get(imatgeId)
                        .loadImatge(getActivity())
        );
    }
}