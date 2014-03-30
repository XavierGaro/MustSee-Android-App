package ioc.mustsee.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

public class MustSeeFragment extends Fragment {
    private static final String TAG = "MustSeeFragment";
    OnFragmentActionListener mCallback;

    SharedPreferences prefs;

    int fragmentId;

    public MustSeeFragment(int fragmentId) {
        this.fragmentId = fragmentId;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Establecemos el archivo de preferencias
        prefs = this.getActivity().getSharedPreferences("mustsee", Context.MODE_PRIVATE);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFragmentActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " debe implementar OnFragmentActionListener");
        }
    }

    /*
    @Override
    public void onDestroyView() {
        Log.d(TAG, "Entrando en onDestroyView: "+fragmentId);
        super.onDestroyView();
        Fragment fragment = (getFragmentManager().findFragmentById(fragmentId));

        // Es el fragmento de mapa?
        if (fragment== ((MainActivity)mCallback).fragmentAmbMapa) {
            Log.d(TAG, "ES EL FRAGMENT AMB MAPA");
        }

        if (fragment != null) {
            Log.d(TAG, "El fragmento NO es null");
            try {
                //Fragment fragment = (getFragmentManager().findFragmentById(R.id.mapFragment));
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                ft.commit();
                Log.d(TAG, "Fragmento destruido");
            } catch (Exception e) {
                // TODO esto no se puede dejar así
                Log.e(TAG, "Error al destruir el fragmento");
            }
        } else {
            Log.d(TAG, "El fragmento es null");
        }

        Log.d(TAG, "Saliendo de onDestroyView");
    }
*/

}
