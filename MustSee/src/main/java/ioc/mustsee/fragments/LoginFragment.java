package ioc.mustsee.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import ioc.mustsee.R;
import ioc.mustsee.downloaders.DownloadManager;
import ioc.mustsee.downloaders.OnTaskCompleted;
import ioc.mustsee.parser.RetrieveData;

import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_MAIN;

/**
 * Fragment per realitzar la autenticació del usuari i que dona pass al fragment per enregistrar-se
 * en cas de no tenir compte.
 *
 * @author Xavier García
 */
public class LoginFragment extends MustSeeFragment implements View.OnClickListener, OnTaskCompleted {
    private static final String TAG = "LoginFragment";

    // UI
    ImageButton mImageButtonLogin;
    ImageButton mImageButtonCancel;
    EditText mEditTextCorreu;
    EditText mEditTextPassword;


    // Dades
    String mCorreu;
    String mPassword;

    // Descarrega
    DownloadManager mDownloadManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Si la vista no existeix la inflem i inicialitzem els widgets
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_login, null);
            initWidgets();
        }
        return mView;
    }

    /**
     * Inicialitzem els botons, els quadres de text, i els seus respectius listeners.
     */
    void initWidgets() {
        mImageButtonLogin = (ImageButton) mView.findViewById(R.id.imageButtonLogin);
        mImageButtonLogin.setOnClickListener(this);

        mImageButtonCancel = (ImageButton) mView.findViewById(R.id.imageButtonCancel);
        mImageButtonCancel.setOnClickListener(this);

        mEditTextCorreu = (EditText) mView.findViewById(R.id.editTextUserName);
        mEditTextPassword = (EditText) mView.findViewById(R.id.editTextPassword);
    }

    /**
     * Listeners per respondre als events de click sobre els botons i el text per enregistrar-se.
     *
     * @param v vista on s'ha fer el click.
     */
    @Override
    public void onClick(View v) {
        if (v == mImageButtonLogin) {
            autenticar();

        } else if (v == mImageButtonCancel) {
            // Tornem al fragment principal
            mCallback.OnActionDetected(ACTION_MAIN);
        }
    }

    /**
     * Comprovar si les dades introduïdes son correctes, i si ho son guarda l'usuari i retorna cert,
     * en cas contrari retorna false i mostra un avis.
     */
    private void autenticar() {
        // Aquest mètode no retorna cap valor, el resultat s'obté al completar-se la tasca
        // asíncronament.
        if (mDownloadManager == null) {
            mDownloadManager = ((DownloadManager) getActivity());
        }

        mDownloadManager.donwloadInProgress(true);

        mCorreu = mEditTextCorreu.getText().toString();
        mPassword = mEditTextPassword.getText().toString();

        RetrieveData.getAuth(this, mCorreu, mPassword);
    }

    /**
     * Guarda les dades del usuari en l'arxiu de preferències de l'activitat.
     */
    private void guardarUsuari() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("correu", mCorreu);
        editor.putString("password", mPassword);
        editor.commit();
    }

    @Override
    public void onTaskCompleted(List result) {
        // Aquí es comprova el resultat, si es correcte es passa a autenticat
        mDownloadManager.donwloadInProgress(false);

        // El resultat ha de ser una llista d'un únic element amb cert si la connexió ha estat correcte o false en cas contrari
        boolean auth;
        if (result.size() == 1) {
            auth = (Boolean) result.get(0);
        } else {
            auth = false;
        }

        if (auth) {
            // Si ho es cridem a guardarUsuari
            guardarUsuari();
            mCallback.OnActionDetected(ACTION_MAIN);
            Toast.makeText(getActivity(), R.string.auth_success, Toast.LENGTH_SHORT).show();
        } else {
            // Si no ho es mostrem missatge d'error
            Toast.makeText(getActivity(), R.string.auth_error, Toast.LENGTH_SHORT).show();
        }
    }
}
