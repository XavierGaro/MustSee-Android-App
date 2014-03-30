package ioc.mustsee.fragments;


import android.os.Bundle;
import android.util.Log;
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

public class DetailFragment extends MustSeeFragment implements View.OnClickListener {
    private static final String TAG = "DetailFragment";
    // UI
    TextView textViewName;
    TextView textViewCategory;
    ImageView imageViewPicture;
    TextView textViewDescription;
    ListView listViewComments;
    View view;
    private Lloc mLloc;
    private Categoria mCategoria;

    public DetailFragment(int fragmentId) {
        super(fragmentId);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "Entrando en onCreateView");
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_detail, null);
        }
        /*
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_detail, container, false);
        }*/

        initWidgets(view);
        carregarDetall();

        return view;
    }

    private void initWidgets(View v) {
        Log.d(TAG, "Inicialitzat widtegs de " + TAG);

        textViewName = (TextView) v.findViewById(R.id.textViewName);
        textViewCategory = (TextView) v.findViewById(R.id.textViewCategory);
        textViewCategory.setOnClickListener(this);

        textViewDescription = (TextView) v.findViewById(R.id.textViewDescription);
        imageViewPicture = (ImageView) v.findViewById(R.id.imageViewPicture);

        imageViewPicture.setOnClickListener(this);

        listViewComments = (ListView) v.findViewById(R.id.listViewComments);
    }

    @Override
    public void onClick(View v) {
        if (v == imageViewPicture) {
            Log.d(TAG, "Llamando a action Gallery");
            mCallback.OnActionDetected(OnFragmentActionListener.ACTION_GALLERY);

        } else if (v == textViewCategory) {
            throw new UnsupportedOperationException("Sin implementar seleccioando en spinner");
        }
    }

    private void carregarDetall() {
        // Obtenim el mLloc actual
        mLloc = ((OnFragmentActionListener) getActivity()).getLlocActual();

        // Obtenim el nom de la categoria corresponent: TODO esto se debe extraer de la BD
        List<Categoria> categories = ((OnFragmentActionListener) getActivity()).getCategories();
        for (Categoria categoria : categories) {
            if (categoria.id == mLloc.categoria) {
                mCategoria = categoria;
                break;
            }
        }

        // Omplim els widgets
        if (mLloc == null) throw new IndexOutOfBoundsException("Error, el mLloc es nulo");
        if (mCategoria == null) throw new IndexOutOfBoundsException("Error, el mCategory es nulo");

        textViewName.setText(mLloc.nom);
        textViewDescription.setText(mLloc.descripcio);
        textViewCategory.setText(mCategoria.nom);

        Log.d(TAG, "Existe la view? " + imageViewPicture);
        Log.d(TAG, "Existe al menos una imagen? " + mLloc.getImatgePrincipal());
        imageViewPicture.setImageBitmap(mLloc.getImatgePrincipal().carregarImatge(getActivity()));

        Log.d(TAG, "Se ha cargado la imagen, saliendo de carregarDetall");

    }


}
