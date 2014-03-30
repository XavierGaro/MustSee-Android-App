package ioc.mustsee.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

public class MustSeeFragment extends Fragment {
    private static final String TAG = "MustSeeFragment";
    OnFragmentActionListener mCallback;

    SharedPreferences prefs;

    View mView;

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

    @Override
    public void onDestroyView() {
        Log.d(TAG, "Entrando en onDestroyView: " + fragmentId);

        // Esto es llamado cuando se hace BackStack, eliminamos el fragmento

        super.onDestroyView();


        Fragment fragment = (getFragmentManager().findFragmentById(fragmentId));

        if (fragment != null) {
            Log.d(TAG, "El fragmento NO es null");
            try {
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


        // Si no eliminamos la vista, al hacer atras de detalle a mapa falla.

        /*
        if (mView != null) {
            ViewGroup parentViewGroup = (ViewGroup) mView.getParent();
            Log.d(TAG, "parentViewGroup es null?: " + parentViewGroup);
            if (parentViewGroup != null) {
                Log.d(TAG, "Numero de childs antes de eliminar: " + parentViewGroup.getChildCount());
                parentViewGroup.removeAllViews();
                Log.d(TAG, "Numero de childs despues de eliminar: " + parentViewGroup.getChildCount());
            }
        }
*/

        Log.d(TAG, "Saliendo de onDestroyView: " + fragmentId);
        // Update en activity main la visibilidad de los fragmentos

        return;
    }
}
