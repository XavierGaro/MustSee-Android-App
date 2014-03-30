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
import ioc.mustsee.data.Usuario;

import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_MAIN;
import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_REGISTER;

public class LoginFragment extends MustSeeFragment implements View.OnClickListener {
    private static final String TAG = "MainFragment";

    // UI
    ImageButton imageButtonLogin;
    ImageButton imageButtonCancel;
    TextView textViewRegister;

    // Datos
    Usuario usuario = null;

    public LoginFragment(int fragmentId) {
        super(fragmentId);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mView = inflater.inflate(R.layout.fragment_login, null);
        // Inflate the layout for this fragment

        /*
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_login, container, false);

        }
        */

        initWidgets(mView);

        return mView;
    }

    private void initWidgets(View v) {
        Log.d(TAG, "View: " + v);
        imageButtonLogin = (ImageButton) v.findViewById(R.id.imageButtonLogin);
        imageButtonLogin.setOnClickListener(this);
        imageButtonCancel = (ImageButton) v.findViewById(R.id.imageButtonCancel);
        imageButtonCancel.setOnClickListener(this);
        textViewRegister = (TextView) v.findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == imageButtonLogin) {
            Log.d(TAG, "Iniciando proceso de autenticación");
            if (autenticar()) {
                // Si s'ha autenticat correctament tornem enrere.
                mCallback.OnActionDetected(ACTION_MAIN);
            }

        } else if (v == imageButtonCancel) {
            mCallback.OnActionDetected(ACTION_MAIN);
            Log.d(TAG, "Cancelando");

        } else if (v == textViewRegister) {
            mCallback.OnActionDetected(ACTION_REGISTER);
            Log.d(TAG, "Pasando a registro");
        }
    }

    private boolean autenticar() {
        // Comprobamos si el usuario y la contraseña son correctos
        if (comprobarUsuario()) {
            // Guardamos los datos del usuario en las preferencias
            guardarUsuario();
            return true;

        } else {
            // Mostramos un toast avisando que los datos no son correctos
            // TODO: Implementar toast
            return false;
        }
    }

    // TODO: Implementar la comprobación. Si es correcto se creará también un objeto usuario con sus datos.
    private boolean comprobarUsuario() {
        return true;
    }

    private void guardarUsuario() {
        // TODO: Implementar los datos del usuario correctamente
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("NOM_USUARI", "Xavier");
        editor.commit();
        Log.d(TAG, "Preferencias guardadas");

    }
}
