package ioc.mustsee.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ioc.mustsee.R;
import ioc.mustsee.data.Lloc;
import ioc.mustsee.fragments.OnFragmentActionListener;


public class LlocArrayAdapter extends ArrayAdapter<Lloc> {
    private static final String TAG = "MobileArrayAdapter";
    private final Context context;
    private final Object mLock = new Object();
    private ItemsFilter mFilter;
    private List<Lloc> llocsFiltrats;
    private List<Lloc> llocsOriginal;

    public LlocArrayAdapter(Context context, List<Lloc> llocsOriginal) {
        super(context, R.layout.list_item_lloc, llocsOriginal);
        this.context = context;
        this.llocsOriginal = llocsOriginal;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtenim el layout
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_lloc, parent, false);

        // Obtenim la referencia als widgets
        TextView textViewName = (TextView) rowView.findViewById(R.id.textViewName);
        TextView textViewDistance = (TextView) rowView.findViewById(R.id.textViewDistance);
        TextView textViewDescription = (TextView) rowView.findViewById(R.id.textViewDescription);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewThumbnail);

        // Apliquem els valors del Lloc als widgets
        Lloc lloc = llocsFiltrats.get(position);
        textViewName.setText(lloc.nom);
        textViewDistance.setText("100" + " km."); // TODO Esto se calcula respecto a la posicion actual y la posición del lloc
        textViewDescription.setText(lloc.descripcio);
        imageView.setImageBitmap(carregarThumbnailLloc(lloc));

        // Retornem la vista d'aquesta fila
        return rowView;
    }

    @Override
    public void clear() {
        // Netegem la llista
        llocsFiltrats.clear();
        notifyDataSetChanged();
    }


    private Bitmap carregarThumbnailLloc(Lloc lloc) {
        Bitmap bitmap = lloc.getImatgePrincipal().carregarImatge(context);
        // TODO Ajustar tamaño? Si no hace falta ajustar nada se puede eliminar el método

        return bitmap;
    }


    public List<Lloc> getFiltered() {
        if (llocsFiltrats != null) {
            return llocsFiltrats;
        } else if (llocsOriginal != null) {
            return llocsOriginal;
        } else {
            return new ArrayList<Lloc>();
        }

    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ItemsFilter();
        }
        return mFilter;
    }

    @Override
    public int getCount() {
        if (llocsFiltrats == null) return 0;
        return llocsFiltrats.size();

    }

    @Override
    public Lloc getItem(int position) {
        return llocsFiltrats.get(position);
    }

    @Override
    public int getPosition(Lloc item) {
        return llocsFiltrats.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Este filtro se aplica a los items que empiecen por los caracteres introducidos
    private class ItemsFilter extends Filter {
        protected FilterResults performFiltering(CharSequence prefix) {
            // Initiate our results object
            FilterResults results = new FilterResults();
            // If the adapter array is empty, check the actual items array and use it
            if (llocsFiltrats == null) {
                synchronized (mLock) { // Notice the declaration above
                    Log.d(TAG, "No existeix la llista de llocsFiltrats. Es crea un arraylist buit");
                    llocsFiltrats = new ArrayList<Lloc>(llocsOriginal);

                }
            }
            // No prefix is sent to filter by so we're going to send back the original array
            Log.d(TAG, "Prefix: " + prefix);

            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    results.values = llocsOriginal;
                    results.count = llocsOriginal.size();
                }
            } else {
                // Compare lower case strings
                // String prefixString = prefix.toString().toLowerCase();
                // Local to here so we're not changing actual array
                final List<Lloc> items = llocsFiltrats;

                final int count = llocsOriginal.size();
                Log.d(TAG, "Items al adaptador original: " + count);
                final ArrayList<Lloc> newItems = new ArrayList<Lloc>(count);

                for (int i = 0; i < count; i++) {
                    final Lloc item = llocsOriginal.get(i);
                    // final String itemName = item.nom.toString().toLowerCase();
                    final int itemId = item.categoria;
                    Log.d(TAG, "id del item: " + item.categoria);
                    if (itemId == Integer.parseInt(prefix.toString())) {
                        newItems.add(item);
                    }

                    /* TODO: Este código filtra los lugares que empiezan por la palabra
                    // First match against the whole, non-splitted value
                    if (itemName.startsWith(prefixString)) {
                        newItems.add(item);
                    } else {
                    } /* This is option and taken from the source of ArrayAdapter
                            final String[] words = itemName.split(" ");
                            final int wordCount = words.length;
                            for (int k = 0; k < wordCount; k++) {
                                if (words[k].startsWith(prefixString)) {
                                    newItems.add(item);
                                    break;
                                }
                            }
                        } */
                }
                // Set and return
                results.values = newItems;
                results.count = newItems.size();
            }
            return results;
        }

        //@SuppressWarnings("unchecked")
        protected void publishResults(CharSequence prefix, FilterResults results) {
            //noinspection unchecked
            llocsFiltrats = (ArrayList<Lloc>) results.values;
            // Let the adapter know about the updated list
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

            List<Lloc> llocsFiltrats = getFiltered();
            Log.d(TAG, "Llista de llocs filtrats: " + llocsFiltrats.size());
            // Pasamos la lista filtrada al principal

            ((OnFragmentActionListener) context).setFilteredLlocs(llocsFiltrats);
            Log.d(TAG, "Pasando la lista de llosFiltrats: " + llocsFiltrats.size());
            //throw new UnsupportedOperationException();
        }
    }
}
