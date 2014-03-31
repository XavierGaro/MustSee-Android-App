package ioc.mustsee.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ioc.mustsee.R;
import ioc.mustsee.data.Categoria;

/**
 * Adaptador per el Spinner que mostra el nom i la descripció de cada categoría.
 *
 * @author Javier García
 * @see ioc.mustsee.data.Categoria
 */
public class CategoriaArrayAdapter extends ArrayAdapter<Categoria> {
    private static final String TAG = "CategoriaArrayAdapter";

    private final Context mContext;
    private List<Categoria> mCategories;

    /**
     * Constructor de la classe al que se li passa el context de la aplicació i la llista de
     * categories.
     * TODO: Fer més genéric incluint la id del layout del Spinner a inflar en el constructor.
     *
     * @param context    context de la aplicació original
     * @param categories llista de categories
     */
    public CategoriaArrayAdapter(Context context, List<Categoria> categories) {
        super(context, R.layout.spinner_category, categories);
        this.mContext = context;
        this.mCategories = categories;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    /**
     * Aquest métode retorna la vista personalitzada. Aquí es on s'afegeix el text a mostrar a la
     * vista.
     *
     * @param position posicio de la vista al adaptador
     * @param parent   grup al que pertany la vista
     * @return vista modificada
     */
    public View getCustomView(int position, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewSpinner = inflater.inflate(R.layout.spinner_category, parent, false);
        TextView textViewMain = (TextView) viewSpinner.findViewById(R.id.textViewTitle);
        TextView textViewSub = (TextView) viewSpinner.findViewById(R.id.textViewDescription);
        textViewMain.setText(mCategories.get(position).nom);
        textViewSub.setText(mCategories.get(position).descripcio);

        return viewSpinner;
    }
}

