package ioc.mustsee.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import ioc.mustsee.R;
import ioc.mustsee.data.Usuari;

import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_MAIN;
import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_REGISTER;

/**
 * Fragment per realitzar la autenticació del usuari i que dona pass al fragment per enregistrar-se
 * en cas de no tenir compte.
 * TODO: NO FA RES. Actualment aquest fragment només està incluit per demostrar la navegació.
 *
 * @author Javier García
 */
public class LoginFragment extends MustSeeFragment implements View.OnClickListener {
    private static final String TAG = "LoginFragment";

    // UI
    ImageButton mImageButtonLogin;
    ImageButton mImageButtonCancel;
    TextView mTextViewRegister;

    // Dades
    Usuari mUsusari = null;

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
     * Inicialitzem els butons, els quadres de text, i els seus respectius listeners.
     */
    void initWidgets() {
        mImageButtonLogin = (ImageButton) mView.findViewById(R.id.imageButtonLogin);
        mImageButtonLogin.setOnClickListener(this);

        mImageButtonCancel = (ImageButton) mView.findViewById(R.id.imageButtonCancel);
        mImageButtonCancel.setOnClickListener(this);

        mTextViewRegister = (TextView) mView.findViewById(R.id.textViewRegister);
        mTextViewRegister.setOnClickListener(this);
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
            if (autenticar()) {
                // Si s'ha autenticat correctament tornem al fragment principal.
                mCallback.OnActionDetected(ACTION_MAIN);
            }
        } else if (v == mImageButtonCancel) {
            // Tornem al fragment principal
            mCallback.OnActionDetected(ACTION_MAIN);

        } else if (v == mTextViewRegister) {
            mCallback.OnActionDetected(ACTION_REGISTER);
        }
    }

    /**
     * Comprovar si les dades introduides son correctes, i si ho son guarda l'usuari i retorna cert,
     * en cas contrari retorna false i mostra un avis.
     *
     * @return true si s'autentica amb èxit o false en cas contrari.
     */
    private boolean autenticar() {
        if (comprovarUsuari()) {
            guardarUsuari();
            return true;
        } else {
            // TODO: Implementar el missatge d'error
            return false;
        }
    }

    /**
     * Aquest métode comprova si les dades introduidas coincideixen amb les de la base de dades i
     * retorna cert si ho son o false en cas contrari. En cas de existir emmagatzemarà les dades del
     * usuari com atribut de la classe.
     * TODO: Sense implementar, sempre retorna true per poder testejar.
     *
     * @return true si les dades introduides als cuadres de text son correctes o false en cas
     * contrari.
     */
    private boolean comprovarUsuari() {
        return true;
    }

    /**
     * Guarda les dades del usuari en l'arxiu de preferencies de l'activitat.
     * TODO: Sense implementar, guardem un nom de prova.
     */
    private void guardarUsuari() {
        // TODO: Implementar los datos del mUsusari correctamente
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("NOM_USUARI", "Xavier");
        editor.commit();
    }
}
