package ioc.mustsee.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ioc.mustsee.R;
import ioc.mustsee.data.Imatge;

/**
 * Adaptador per personalitzar un GridView com una galeria de imatges que mostrarà la imatge i el
 * títol de la imatge.
 *
 * @author Javier García
 * @see ioc.mustsee.data.Imatge
 */
public class GridViewGalleryAdapter extends ArrayAdapter {
    private static final String TAG = "GridViewGalleryAdapter";

    private Context mContext;
    private int mLayoutResourceId;
    private List<Imatge> mImatges = new ArrayList<Imatge>();

    /**
     * Hem de passar al constructor el context, el id del layout que farà servir l'adaptador i la
     * llista de imatges amb el que l'omplirem. El layout pasat com a paràmetre ha de tenir un
     * TextView amb el id textViewTitle, i un ImageView amb el id imageViewPicture.
     *
     * @param context          context de l'activitat principal
     * @param layoutResourceId id del layout que es farà servir per cada fila.
     * @param imatges          llista de objectes Imatge
     */
    public GridViewGalleryAdapter(Context context, int layoutResourceId,
                                  List<Imatge> imatges) {
        super(context, layoutResourceId, imatges);
        this.mLayoutResourceId = layoutResourceId;
        this.mContext = context;
        this.mImatges = imatges;
    }

    /**
     * Retorna la vista corresponent a una casella de la graella.
     *
     * @param position    posició de la casella a la graella.
     * @param convertView vista de la casella.
     * @param parent      vista contenidora de la graella.
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View square = convertView;
        ViewHolder holder = null;

        if (square == null) {
            // Si la casella no existeix la inflem
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            square = inflater.inflate(mLayoutResourceId, parent, false);
            // Emmagetzem la informació en el holder
            holder = new ViewHolder();
            holder.title = (TextView) square.findViewById(R.id.textViewTitle);
            holder.picture = (ImageView) square.findViewById(R.id.imageViewPicture);
            square.setTag(holder);
        } else {
            // Si existeix recuperem les dades del holder
            holder = (ViewHolder) square.getTag();
        }

        // Carreguem les dades de la imatge en la casella.
        Imatge item = mImatges.get(position);
        holder.title.setText(item.titol);
        holder.picture.setImageBitmap(item.carregarImatge(mContext));
        return square;
    }

    /**
     * Implementació del ViewHolder pattern,es una optimització per evitar crides a findViewById()
     * el que millora el rendiment del adaptador.
     */
    static class ViewHolder {
        TextView title;
        ImageView picture;
    }
}