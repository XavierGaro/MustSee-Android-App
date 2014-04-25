package ioc.mustsee.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ioc.mustsee.R;
import ioc.mustsee.data.Usuari;
import ioc.mustsee.parser.DownloadManager;
import ioc.mustsee.parser.OnTaskCompleted;
import ioc.mustsee.parser.ParserMustSee;

import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_MAIN;
import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_REGISTER;

/**
 * Fragment per realitzar la autenticació del usuari i que dona pass al fragment per enregistrar-se
 * en cas de no tenir compte.
 * TODO: NO FA RES. Actualment aquest fragment només està incluit per demostrar la navegació.
 *
 * @author Javier García
 */
public class LoginFragment extends MustSeeFragment implements View.OnClickListener, OnTaskCompleted {
    private static final String TAG = "LoginFragment";

    // UI
    ImageButton mImageButtonLogin;
    ImageButton mImageButtonCancel;
    TextView mTextViewRegister;
    EditText mEditTextCorreu;
    EditText mEditTextPassword;


    // Dades
    Usuari mUsusari;
    String mCorreu;
    String mPassword;

    // Descarrega
    DownloadManager mGestor;

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

        mTextViewRegister = (TextView) mView.findViewById(R.id.textViewRegister);
        mTextViewRegister.setOnClickListener(this);

        mEditTextCorreu = (EditText) mView.findViewById(R.id.editTextUserName);
        mEditTextPassword = (EditText) mView.findViewById(R.id.editTextPassword);
    }

    /**
     * Listeners per respondre als events de click sobre els botons i el text per enregistrar-se.
     * TODO: Aquestes accions no han de afegir-se al BackStack
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

        } else if (v == mTextViewRegister) {
            mCallback.OnActionDetected(ACTION_REGISTER);
        }
    }

    /**
     * Comprovar si les dades introduïdes son correctes, i si ho son guarda l'usuari i retorna cert,
     * en cas contrari retorna false i mostra un avis.
     *
     * @return true si s'autentica amb èxit o false en cas contrari.
     */
    private void autenticar() {
        // Aquest mètode no retorna cap valor, el resultat s'obté al completar-se la tasca
        // asincronament
        if (mGestor == null) {
            mGestor = ((DownloadManager) getActivity());
        }

        mGestor.descarregaEnCurs(true);

        mCorreu = mEditTextCorreu.getText().toString();
        mPassword = mEditTextPassword.getText().toString();

        new ParserMustSee().getAuth(this, mCorreu, mPassword);

    }


    /**
     * Guarda les dades del usuari en l'arxiu de preferencies de l'activitat.
     * TODO: Sense implementar, guardem un nom de prova.
     */
    private void guardarUsuari() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("correu", mCorreu);
        editor.putString("password", mPassword);
        editor.commit();
    }

    @Override
    public void onTaskCompleted(List result) {
        // Aqui es comprova el resultat, si es correcte es passa a autenticat
        List<Boolean> resultats = result;
        Log.d(TAG, "Resultat de autenticar obtingut: " + result.toString());
        mGestor.descarregaEnCurs(false);

        // El resultat ha de ser una llista d'un únic element amb cert si la connexió ha estat correcte o false en cas contrari
        boolean auth = (Boolean) result.get(0);

        if (auth) {
            // Si ho es cridem a guardarUsuari
            guardarUsuari();
            mCallback.OnActionDetected(ACTION_MAIN);
            Toast.makeText(getActivity(), "Autenticat correctament", Toast.LENGTH_SHORT).show();
        } else {
            // Si no ho es mostrem missatge d'error
            Toast.makeText(getActivity(), "Error d'autenticació", Toast.LENGTH_SHORT).show();
        }


        //Toast.makeText(getActivity(), result.toString(), Toast.LENGTH_SHORT).show();
    }
}
