package ioc.mustsee.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ioc.mustsee.R;

public class PictureFragment extends MustSeeFragment {
    private static final String TAG = "PictureFragment";
    private ImageView picture;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_photo, null);

        Log.d(TAG, "inflando layout foto");

        /*
        if (mView == null) {

            mView = inflater.inflate(R.layout.fragment_photo, container, false);
        }
        */

        initWidgets();

        return mView;
    }

    void initWidgets() {
        Log.d(TAG, "Inicialitzat widtegs de " + TAG);

        Bundle bundle = getArguments();
        Log.d(TAG, "Se han pasado los argumentos: " + bundle.getInt("PICTURE"));

        picture = (ImageView) mView.findViewById(R.id.imageViewPicture);
        picture.setImageBitmap(mCallback.getCurrentLloc().getImages().get(bundle.getInt("PICTURE")).carregarImatge(getActivity()));

    }

}
