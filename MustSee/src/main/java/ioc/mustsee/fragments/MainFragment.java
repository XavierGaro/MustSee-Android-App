package ioc.mustsee.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import ioc.mustsee.R;

import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_EXPLORE;
import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_LOG;
import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_SEARCH;

/**
 * Aquest es el fragment principal que es carrega al iniciar la aplicació. Al reprendre's comprova
 * les dades emmagatzemades a preferències i actualitza la vista com correspongui. Aquest fragment
 * permet accedir als fragments d'actualitzar, i les vistes explorar i cerca.
 *
 * @author Xavier García
 */
public class MainFragment extends MustSeeFragment implements View.OnClickListener {
    private static final String TAG = "MainFragment";

    // UI
    ImageButton mImageButtonLog;
    ImageButton mImageButtonSearch;
    ImageButton mImageButtonExplore;
    TextView mTextViewLog;

    // Usuari
    private boolean mAutenticat = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Si la vista no existeix la inflem i inicialitzem els widgets
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_main, null);
            initWidgets();
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        actualitzarEstat();
    }

    /**
     * Inicialitza els botons, els quadres de text i els afegeix el listener.
     */
    void initWidgets() {
        mImageButtonLog = (ImageButton) mView.findViewById(R.id.imageButtonLog);
        mImageButtonLog.setOnClickListener(this);
        mTextViewLog = (TextView) mView.findViewById(R.id.textViewLog);

        mImageButtonSearch = (ImageButton) mView.findViewById(R.id.imageButtonSearch);
        mImageButtonSearch.setOnClickListener(this);

        mImageButtonExplore = (ImageButton) mView.findViewById(R.id.imageButtonExplore);
        mImageButtonExplore.setOnClickListener(this);
    }

    /**
     * Listener pels botons que ens permet navegar a altres fragments de la aplicació o desconnectar
     * si estem connectats.
     *
     * @param v vista que ha rebut el click.
     */
    @Override
    public void onClick(View v) {
        if (v == mImageButtonLog) {
            if (mAutenticat) {
                esborrarPreferencies();
            } else {
                mCallback.OnActionDetected(ACTION_LOG);
            }
            actualitzarEstat();

        } else if (v == mImageButtonSearch) {
            mCallback.OnActionDetected(ACTION_SEARCH);

        } else if (v == mImageButtonExplore) {
            mCallback.OnActionDetected(ACTION_EXPLORE);
        }
    }

    /**
     * Esborra les preferències del fitxer.
     */
    private void esborrarPreferencies() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove("correu");
        editor.remove("password");
        editor.commit();
    }

    /**
     * Comprova les dades del fitxer de preferència i actualitza la interfície en conseqüència.
     */
    private void actualitzarEstat() {
        if (isUserAuthenticated()) {
            // Si existeix un nom d'usuari mostrem el botó de desconnexió
            mImageButtonLog.setBackgroundResource(R.drawable.ic_action_remove);
            mTextViewLog.setText(R.string.logout);
            mAutenticat = true;

        } else {
            // Si no existeix mostrem el botó de connectar
            mImageButtonLog.setBackgroundResource(R.drawable.ic_action_person);
            mTextViewLog.setText(R.string.login);
            mAutenticat = false;
        }
    }
}
