package ioc.mustsee.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ioc.mustsee.R;
import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Comentari;
import ioc.mustsee.data.Imatge;
import ioc.mustsee.data.Lloc;
import ioc.mustsee.parser.DownloadManager;
import ioc.mustsee.parser.OnTaskCompleted;
import ioc.mustsee.parser.ParserMustSee;
import ioc.mustsee.ui.ComentariArrayAdapter;

/**
 * Aquest fragment mostra la informació del lloc i el nom de la categoria.
 *
 * @author Javier García
 */
public class DetailFragment extends MustSeeFragment implements View.OnClickListener, OnTaskCompleted {
    private static final String TAG = "DetailFragment";

    private static final int MAX_SIZE = 700; // Grandària de la imatge

    // UI
    private TextView mTextViewName;
    private TextView mTextViewCategory;
    private ImageView mImageViewPicture;
    private TextView mTextViewDescription;
    private ListView mListViewComments;
    ComentariArrayAdapter mCustomAdapter;
    private EditText mEditTextComentari;
    private Button mButtonSend;
    private RelativeLayout mRelativeLayoutComentari;


    // Dades
    private Categoria mCategoria;
    private DownloadManager mGestor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Si la vista no existeix la inflem i inicialitzem els widgets
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_detail, null);
            initWidgets();
            loadDetail();
        }
        return mView;
    }

    /**
     * Inicialitza els widgets del fragment i els asigna els listener corresponents.
     */
    @Override
    void initWidgets() {
        mTextViewName = (TextView) mView.findViewById(R.id.textViewName);
        mTextViewCategory = (TextView) mView.findViewById(R.id.textViewCategory);
        mTextViewCategory.setOnClickListener(this);
        mTextViewDescription = (TextView) mView.findViewById(R.id.textViewDescription);
        mImageViewPicture = (ImageView) mView.findViewById(R.id.imageViewPicture);
        mImageViewPicture.setOnClickListener(this);

        mCustomAdapter = new ComentariArrayAdapter(getActivity(),
                R.layout.list_item_comentari, mCallback.getCurrentLloc().getComentaris());
        mListViewComments = (ListView) mView.findViewById(R.id.listViewComentaris);
        mListViewComments.setAdapter(mCustomAdapter);

        mEditTextComentari = (EditText) mView.findViewById(R.id.editTextComentari);
        mButtonSend = (Button) mView.findViewById(R.id.buttonSend);
        mButtonSend.setOnClickListener(this);

        mRelativeLayoutComentari = (RelativeLayout) mView.findViewById(R.id.relativeLayoutComentari);

        if (isUserAuthenticated()) {
            mRelativeLayoutComentari.setVisibility(View.VISIBLE);
        } else {
            mRelativeLayoutComentari.setVisibility(View.GONE);
        }
    }

    /**
     * Listener pels events click.
     *
     * @param v vista en la que s'ha realitzat el click.
     */
    @Override
    public void onClick(View v) {
        if (v == mImageViewPicture) {
            mCallback.OnActionDetected(OnFragmentActionListener.ACTION_GALLERY);
        } else if (v == mTextViewCategory) {
            // TODO: Si es fa click a aquest widget s'ha de seleccionar la categoriaId al Spinner.
        } else if (v == mButtonSend) {
            sendComentari();
        }
    }

    /**
     * Obté les dades del lloc a mostrar i actualitza els widgets per mostrar-les.
     */
    private void loadDetail() {
        // Obtenim el mLloc actual
        Lloc lloc = mCallback.getCurrentLloc();

        List<Categoria> categories = mCallback.getCategories();
        for (Categoria categoria : categories) {
            if (categoria.id == lloc.categoriaId) {
                mCategoria = categoria;
                break;
            }
        }

        // Omplim els widgets
        if (lloc == null) throw new NullPointerException("Error, el mLloc es null");
        if (mCategoria == null) throw new NullPointerException("Error, el mCategory es null");
        mTextViewName.setText(lloc.nom);
        mTextViewDescription.setText(lloc.descripcio);
        mTextViewCategory.setText(mCategoria.nom);
        if (lloc.getImatgePrincipal() != null) {
            // Si hi ha imatge la carreguem
            //mImageViewPicture.setImageBitmap(lloc.getImatgePrincipal().loadImatge(getActivity()));
            mImageViewPicture.setImageBitmap(
                    Imatge.ShrinkBitmap(lloc.getImatgePrincipal().nomFitxer, MAX_SIZE, MAX_SIZE)
            );
        } else {
            // Si no hi ha imatge eliminem el listener
            mImageViewPicture.setOnClickListener(null);
        }

    }

    private void sendComentari() {
        String text = mEditTextComentari.getText().toString();
        // Comprovem si hi ha cap text
        if (text.length() == 0) {
            Toast.makeText(getActivity(), R.string.error_no_text, Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO, això es de prova
        Toast.makeText(getActivity(), "Enviant comentari: " + text, Toast.LENGTH_SHORT).show();

        if (mGestor == null) {
            mGestor = ((DownloadManager) getActivity());
        }

        mGestor.descarregaEnCurs(true);

        String correu = mPreferences.getString("correu", "");
        String password = mPreferences.getString("password", "");
        int llocId = mCallback.getCurrentLloc().id;

        new ParserMustSee().postComment(this, correu, password, text, llocId);


    }

    @Override
    public void onTaskCompleted(List result) {
        // Aqui es comprova el resultat, si es correcte es passa a autenticat

        Log.d(TAG, "Resultat de autenticar obtingut: " + result.toString());
        mGestor.descarregaEnCurs(false);

        // El resultat ha de ser una llista d'un únic element amb cert si la connexió ha estat correcte o false en cas contrari
        boolean success = (Boolean) result.get(0);

        if (success) {
            // TODO: Si ho refresquem la llista de comentaris
            refreshComments();
            Toast.makeText(getActivity(), R.string.comentari_send, Toast.LENGTH_SHORT).show();
            // Esborrem el text del EditText
            mEditTextComentari.setText("");

        } else {
            // Si no ho es mostrem missatge d'error
            Toast.makeText(getActivity(), R.string.post_error, Toast.LENGTH_SHORT).show();
        }

    }

    private void refreshComments() {
        mGestor.descarregaEnCurs(true);

        Log.d(TAG, "Refresquem");

        new ParserMustSee().getComentarisFromLloc(new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(List result) {
                Lloc lloc = mCallback.getCurrentLloc();
                lloc.setComentaris(result);
                Log.d(TAG, "Tasca completa: " +result.toString());

                // Actualitzar la llista
                mCustomAdapter.clear();
                for (Comentari comentari : (List<Comentari>) result) {
                    mCustomAdapter.add(comentari);
                    Log.d(TAG, "Afegit un comentari al adapter");
                }
                mCustomAdapter.notifyDataSetChanged();


                mGestor.descarregaEnCurs(false);
            }
        }, mCallback.getCurrentLloc().id);
    }
}