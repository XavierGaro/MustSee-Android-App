package ioc.mustsee.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

public abstract class MustSeeFragment extends Fragment {
    private static final String TAG = "MustSeeFragment";
    OnFragmentActionListener mCallback;

    SharedPreferences prefs;

    View mView;

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
        super.onDestroyView();
        if (mView != null) {
            ViewGroup parentViewGroup = (ViewGroup) mView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
    }

    abstract void initWidgets();
}
