package ioc.mustsee.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ioc.mustsee.R;
import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Lloc;

/**
 * Aquest fragment mostra la informació del lloc i el nom de la categoria.
 *
 * @author Javier García
 */
public class DetailFragment extends MustSeeFragment implements View.OnClickListener {
    private static final String TAG = "DetailFragment";

    // UI
    TextView mTextViewName;
    TextView mTextViewCategory;
    ImageView mImageViewPicture;
    TextView mTextViewDescription;
    ListView mListViewComments;

    // Dades del lloc del que mostrem el detall
    private Lloc mLloc;
    private Categoria mCategoria;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Si no existeix la vista la inflem
        if (mView == null) mView = inflater.inflate(R.layout.fragment_detail, null);

        initWidgets();
        carregarDetall();
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
        mListViewComments = (ListView) mView.findViewById(R.id.listViewComments);
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
        }
    }

    /**
     * Obté les dades del lloc a mostrar i actualitza els widgets per mostrar-les.
     */
    private void carregarDetall() {
        // Obtenim el mLloc actual
        mLloc = mCallback.getCurrentLloc();

        // Obtenim el nom de la categoria corresponent. TODO això s'extraurà de la base de dades
        List<Categoria> categories = mCallback.getCategories();
        for (Categoria categoria : categories) {
            if (categoria.id == mLloc.categoriaId) {
                mCategoria = categoria;
                break;
            }
        }

        // Omplim els widgets
        if (mLloc == null) throw new NullPointerException("Error, el mLloc es null");
        if (mCategoria == null) throw new NullPointerException("Error, el mCategory es null");
        mTextViewName.setText(mLloc.nom);
        mTextViewDescription.setText(mLloc.descripcio);
        mTextViewCategory.setText(mCategoria.nom);
        mImageViewPicture.setImageBitmap(mLloc.getImatgePrincipal().carregarImatge(getActivity()));
    }
}
