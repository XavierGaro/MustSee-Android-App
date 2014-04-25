package ioc.mustsee.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

/**
 * Aquesta es la classe de la que hereten tots els fragments de la aplicació. La activitat que els
 * adjunti ha de implementar la interfície OnFragmentActionListener o llençarà una excepció.
 *
 * @author Xavier García
 * @see ioc.mustsee.fragments.OnFragmentActionListener
 */
public abstract class MustSeeFragment extends Fragment {
    private static final String TAG = "MustSeeFragment";

    private static final String PREFERENCES_FILENAME = "mustsee";


    // Activitat a la que s'adjunta el fragment per poder fer les crides.
    OnFragmentActionListener mCallback;

    // Arxiu de preferències
    SharedPreferences mPreferences;

    // Vista a la que es mostra el fragment
    View mView;

    /**
     * Quan s'adjunta el fragment a una activitat establim l'arxiu de preferències i comprovem que
     * la activitat implementi la interfície de callback.
     *
     * @param activity a la que s'adjunta el fragment.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Establim l'arxiu de preferències
        mPreferences = this.getActivity().getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE);

        // Si l'activitat no implementa la interfície de callback llencem una excepció informant
        try {
            mCallback = (OnFragmentActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " ha d'implementar OnFragmentActionListener");
        }
    }

    /**
     * Quan es destrueix la vista ens assegurem que totes les vistes associades son eliminades també.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mView != null) {
            ViewGroup parentViewGroup = (ViewGroup) mView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }

    }

    /**
     * Obliguem a totes els fragments a implementar aquest mètode, on s'inicialitzaran els widgets.
     */
    abstract void initWidgets();

    public boolean isUserAuthenticated() {
        if (mPreferences.contains("correu") && mPreferences.contains("password")) {
            return true;
        } else {
            return false;
        }
    }
}
