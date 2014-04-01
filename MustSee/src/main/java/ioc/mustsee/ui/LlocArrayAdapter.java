package ioc.mustsee.ui;

import android.content.Context;
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

/**
 * Adaptador per el ListView que mostra una llista de llocs. Fem servir un objecte per sincronitzar
 * les operacions d'escriptura al filtre.
 *
 * @author Javier García
 * @see ioc.mustsee.data.Lloc
 */
public class LlocArrayAdapter extends ArrayAdapter<Lloc> {
    private static final String TAG = "MobileArrayAdapter";

    private final Object mLock = new Object();
    private final Context mContext;
    private ItemsFilter mFilter;
    private List<Lloc> mFilteredLlocs;
    private List<Lloc> mOriginalLlocs;

    /**
     * El constructor requereix el context de la activitat i la llista de llocs completa.
     *
     * @param context       context de la activitat.
     * @param originalLlocs llista de llocs original.
     */
    public LlocArrayAdapter(Context context, List<Lloc> originalLlocs) {
        super(context, R.layout.list_item_lloc, originalLlocs);
        this.mContext = context;
        this.mOriginalLlocs = originalLlocs;
    }

    /**
     * Retornem la vista modificada amb les dades del lloc.
     *
     * @param position    posició al adaptador
     * @param convertView vista original
     * @param parent      grup al que pertany la vista
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtenim el layout de la fila i l'inflem
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewRow = inflater.inflate(R.layout.list_item_lloc, parent, false);

        // Obtenim la referencia als widgets
        TextView textViewName = (TextView) viewRow.findViewById(R.id.textViewName);
        TextView textViewDistance = (TextView) viewRow.findViewById(R.id.textViewDistance);
        TextView textViewDescription = (TextView) viewRow.findViewById(R.id.textViewDescription);
        ImageView imageView = (ImageView) viewRow.findViewById(R.id.imageViewThumbnail);

        // Apliquem els valors del Lloc als widgets
        Lloc lloc = mFilteredLlocs.get(position);
        textViewName.setText(lloc.nom);
        textViewDistance.setText(lloc.getDistance() + " km."); // TODO: aquest valor serà calculat
        textViewDescription.setText(lloc.descripcio);

        if (lloc.getImatgePrincipal() != null) {
            imageView.setImageBitmap(lloc.getImatgePrincipal().carregarImatge(mContext));
        }


        return viewRow;
    }

    /**
     * Neteja la llista i notifica que les dades han canviat.
     */
    @Override
    public void clear() {
        mFilteredLlocs.clear();
        notifyDataSetChanged();
    }

    /**
     * Retorna la llista de llocs filtrats.
     *
     * @return la llista de llocs filtrats si existeix, la llista de llocs originals o una llista
     * buida si no existeix cap de les dues anteriors.
     */
    private List<Lloc> getFilteredLlocs() {
        if (mFilteredLlocs != null) {
            return mFilteredLlocs;
        } else if (mOriginalLlocs != null) {
            return mOriginalLlocs;
        } else {
            return new ArrayList<Lloc>();
        }
    }

    /**
     * Si no hi ha filtre crea un de nou, si existeix retorna l'anterior.
     *
     * @return el filtre anterior o un nou si no existeix.
     */
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ItemsFilter();
        }
        return mFilter;
    }

    /**
     * Retorna la quantitat d'objectes filtrats.
     *
     * @return quantitat d'objectes filtrats o 0 si no hi ha llocs filtrats.
     */
    @Override
    public int getCount() {
        if (mFilteredLlocs == null) return 0;
        return mFilteredLlocs.size();
    }

    @Override
    public Lloc getItem(int position) {
        return mFilteredLlocs.get(position);
    }

    @Override
    public int getPosition(Lloc item) {
        return mFilteredLlocs.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Aquesta classe implementa un filtre que retorna els llocs que coincideixin amb la categoría
     * passada com a prefix al mètode performFiltering().
     * TODO: Afegir un parser de manera que el prefix accepti una cadena preparada per filtrar per
     * categoria i per paraules.
     */
    private class ItemsFilter extends Filter {

        /**
         * Retorna un objecte amb els resultats filtrats segons la categoria pasada per argument. La
         * categoria es converteix a enter i es compara amb el id de la categoria dels llocs,
         * deixant només els que coincideixin.
         *
         * @param prefix condició que han de acomplir.
         * @return resultats filtrats.
         */
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            // Si no hi ha resultats filtrats, creem un nou array
            if (mFilteredLlocs == null) {
                synchronized (mLock) {
                    mFilteredLlocs = new ArrayList<Lloc>(mOriginalLlocs);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                // Si no s'ha passat cap prefix, retornem el filtre amb la llista original.
                synchronized (mLock) {
                    results.values = mOriginalLlocs;
                    results.count = mOriginalLlocs.size();
                }
            } else {
                // Creem un nou array on emmagatzemar els resultats que anem filtrant.
                List<Lloc> newLlocs = new ArrayList<Lloc>(mOriginalLlocs.size());
                int prefixCategoriaId = Integer.parseInt(prefix.toString());

                // Recorrem els llocs originals i comprovem si pertanyen a la categoria del prefix
                for (Lloc lloc : mOriginalLlocs) {
                    int categoriaId = lloc.categoriaId;
                    if (categoriaId == prefixCategoriaId) newLlocs.add(lloc);
                }

                // Assignem els llocs filtrats al resultat
                results.values = newLlocs;
                results.count = newLlocs.size();
            }
            return results;
        }

        /**
         * Es cridat desde el fil de la UI per filtrar els resultats del adaptador segons el prefix
         * enviat. No es crida manualment.
         *
         * @param prefix prefix per filtrar
         * @param results resultat del filtratge
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence prefix, FilterResults results) {
            // Establim la llista de llocs filtrats als llocs retornats pel filtre.
            mFilteredLlocs = (ArrayList<Lloc>) results.values;

            // Notifiquem a l'adaptador dels canvis
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

            // Passem aquesta llista a la activitat principal.
            // TODO: Això s'ha de controlar desde la base de dades
            ((OnFragmentActionListener) mContext).setFilteredLlocs(getFilteredLlocs());
        }
    }
}
