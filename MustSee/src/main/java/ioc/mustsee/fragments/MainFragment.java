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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Si la vista no existeix la inflem i inicalitzem els widgets
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_main, null);
            initWidgets();
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizarEstado();
    }


    void initWidgets() {
        Log.d(TAG, "Inicialitzat widtegs de " + TAG);
        imageButtonLog = (ImageButton) mView.findViewById(R.id.imageButtonLog);
        imageButtonLog.setOnClickListener(this);
        textViewLog = (TextView) mView.findViewById(R.id.textViewLog);

        imageButtonSearch = (ImageButton) mView.findViewById(R.id.imageButtonSearch);
        imageButtonSearch.setOnClickListener(this);
        textViewSearch = (TextView) mView.findViewById(R.id.textViewSearch);

        imageButtonExplore = (ImageButton) mView.findViewById(R.id.imageButtonExplore);
        imageButtonExplore.setOnClickListener(this);
        textViewExplore = (TextView) mView.findViewById(R.id.textViewExplore);
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
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove("NOM_USUARI");
        editor.commit();
    }

    private void actualizarEstado() {
        // Comprovem si hi han preferencies
        if (mPreferences.contains("NOM_USUARI")) {
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
