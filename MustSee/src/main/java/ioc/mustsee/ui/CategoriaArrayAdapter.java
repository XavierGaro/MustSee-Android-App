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

public class CategoriaArrayAdapter extends ArrayAdapter<Categoria> {

    private static final String TAG = "MobileArrayAdapter";
    private final Context context;
    private List<Categoria> categorias;

    public CategoriaArrayAdapter(Context context, List<Categoria> categorias) {
        super(context, R.layout.spinner_category, categorias);
        this.context = context;
        this.categorias = categorias;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View mySpinner = inflater.inflate(R.layout.spinner_category, parent,
                false);
        TextView main_text = (TextView) mySpinner
                .findViewById(R.id.text_main_seen);
        main_text.setText(categorias.get(position).nom);

        TextView subSpinner = (TextView) mySpinner
                .findViewById(R.id.sub_text_seen);
        subSpinner.setText(categorias.get(position).descripcio);

        /* Sin implementar
        ImageView left_icon = (ImageView) mySpinner
                .findViewById(R.id.left_pic);
        left_icon.setImageResource(categorias.get(position).icon);
        */

        return mySpinner;
    }
}

