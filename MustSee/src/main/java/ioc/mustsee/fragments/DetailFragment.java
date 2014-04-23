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
import ioc.mustsee.data.Imatge;
import ioc.mustsee.data.Lloc;

/**
 * Aquest fragment mostra la informació del lloc i el nom de la categoria.
 *
 * @author Javier García
 */
public class DetailFragment extends MustSeeFragment implements View.OnClickListener {
    private static final String TAG = "DetailFragment";

    private static final int MAX_SIZE = 700; // Grandària de la imatge

    // UI
    TextView mTextViewName;
    TextView mTextViewCategory;
    ImageView mImageViewPicture;
    TextView mTextViewDescription;
    ListView mListViewComments;

    private Categoria mCategoria;

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
    private void loadDetail() {
        // Obtenim el mLloc actual
        Lloc lloc = mCallback.getCurrentLloc();

        // Obtenim el nom de la categoria corresponent. TODO això s'extraurà de la base de dades
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
}
