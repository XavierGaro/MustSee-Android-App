package ioc.mustsee.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ioc.mustsee.R;

public class PhotoFragment extends MustSeeFragment {
    private static final String TAG = "GalleryFragment";
    View view;
    private ImageView picture;

    public PhotoFragment(int fragmentId) {
        super(fragmentId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_photo, null);

        Log.d(TAG, "inflando layout foto");

        /*
        if (view == null) {

            view = inflater.inflate(R.layout.fragment_photo, container, false);
        }
        */

        initWidgets(view);

        return view;
    }

    private void initWidgets(View v) {
        Log.d(TAG, "Inicialitzat widtegs de " + TAG);

        Bundle bundle = getArguments();
        Log.d(TAG, "Se han pasado los argumentos: " + bundle.getInt("PHOTO"));

        picture = (ImageView) v.findViewById(R.id.imageViewPicture);
        picture.setImageBitmap(mCallback.getLlocActual().getImages().get(bundle.getInt("PHOTO")).carregarImatge(getActivity()));

    }

}
