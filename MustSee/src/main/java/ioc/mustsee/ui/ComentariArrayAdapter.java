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
import ioc.mustsee.data.Comentari;

public class ComentariArrayAdapter extends ArrayAdapter<Comentari> {
    private static final String TAG = "ComentariArrayAdapter ";

    private static final int MAX_SIZE = 60; // Grandària del avatar

    private Context mContext;
    private int mLayoutResourceId;
    private List<Comentari> mComentaris = new ArrayList<Comentari>();


    public ComentariArrayAdapter(Context context, int layoutResourceId,
                                 List<Comentari> comentaris) {
        super(context, layoutResourceId, comentaris);
        this.mLayoutResourceId = layoutResourceId;
        this.mContext = context;
        this.mComentaris = comentaris;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            // Si la casella no existeix la inflem
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
            // Emmagetzem la informació en el holder
            holder = new ViewHolder();
            holder.text = (TextView) row.findViewById(R.id.textViewText);
            holder.nomUsuari = (TextView) row.findViewById(R.id.textViewUsuari);
            holder.avatar = (ImageView) row.findViewById(R.id.imageViewAvatar);
            row.setTag(holder);
        } else {
            // Si existeix recuperem les dades del holder
            holder = (ViewHolder) row.getTag();
        }

        // Carreguem les dades de la imatge en la casella.
        Comentari item = mComentaris.get(position);
        holder.text.setText(item.text);
        holder.nomUsuari.setText(item.nomUsuari);
        holder.avatar.setImageResource(R.drawable.ic_action_user);
        return row;
    }

    static class ViewHolder {
        TextView text;
        TextView nomUsuari;
        ImageView avatar;

    }
}