package ioc.mustsee.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import ioc.mustsee.R;

import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_EXPLORE;
import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_LOG;
import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_SEARCH;

public class MainFragment extends MustSeeFragment implements View.OnClickListener {
    private static final String TAG = "MainFragment";
    // UI
    ImageButton imageButtonLog;
    ImageButton imageButtonSearch;
    ImageButton imageButtonExplore;
    TextView textViewLog;
    TextView textViewSearch;
    TextView textViewExplore;

    private boolean autenticat = false;

    public MainFragment(int fragmentId) {
        super(fragmentId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreate de " + TAG);

        mView = inflater.inflate(R.layout.fragment_main, null);
        // Inflate the layout for this fragment

        /*
        if (mView == null) {
            Log.d(TAG, "La mView es null " + mView);
            mView = inflater.inflate(R.layout.fragment_main, container, false);
        } else {
            Log.d(TAG, "La mView es NO es null " + mView);
        }
*/
        initWidgets(mView);

        Log.d(TAG, "devolviendo mView de " + TAG + " : " + mView);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizarEstado();
    }


    private void initWidgets(View v) {
        Log.d(TAG, "Inicialitzat widtegs de " + TAG);
        imageButtonLog = (ImageButton) v.findViewById(R.id.imageButtonLog);
        imageButtonLog.setOnClickListener(this);
        textViewLog = (TextView) v.findViewById(R.id.textViewLog);


        imageButtonSearch = (ImageButton) v.findViewById(R.id.imageButtonSearch);
        imageButtonSearch.setOnClickListener(this);
        textViewSearch = (TextView) v.findViewById(R.id.textViewSearch);

        imageButtonExplore = (ImageButton) v.findViewById(R.id.imageButtonExplore);
        imageButtonExplore.setOnClickListener(this);
        textViewExplore = (TextView) v.findViewById(R.id.textViewExplore);
        Log.d(TAG, "Widgedts inicializados de " + TAG);
    }

    @Override
    public void onClick(View v) {
        if (v == imageButtonLog) {
            // Si estamos autenticados desautenticamos
            if (autenticat) {
                borrarPreferencias();
            } else {
                mCallback.OnActionDetected(ACTION_LOG);
            }
            actualizarEstado();

        } else if (v == imageButtonSearch) {
            mCallback.OnActionDetected(ACTION_SEARCH);

        } else if (v == imageButtonExplore) {
            mCallback.OnActionDetected(ACTION_EXPLORE);
        }
    }

    private void borrarPreferencias() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("NOM_USUARI");
        editor.commit();
    }

    private void actualizarEstado() {
        // Comprovem si hi han preferencies
        if (prefs.contains("NOM_USUARI")) {
            Log.d(TAG, "Encontradas preferencias");
            // El botó es logout i no login
            imageButtonLog.setBackgroundResource(R.drawable.ic_action_remove);
            textViewLog.setText(R.string.logout);
            autenticat = true;
        } else {
            imageButtonLog.setBackgroundResource(R.drawable.ic_action_person);
            textViewLog.setText(R.string.login);
            autenticat = false;
        }
    }
}
