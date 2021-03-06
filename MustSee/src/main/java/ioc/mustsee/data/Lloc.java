package ioc.mustsee.data;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Aquesta classe emmagatzema la informació d'un lloc, incloent la llista de imatges enllaçades. La
 * majoria dels atributs son immutables i poden ser llegits directament. Pels que no ho son es
 * faciliten getters i setters segons sigui apropiat. Es fa servir el patró Builder per la
 * construcció.
 *
 * @author Xavier García
 */
public class Lloc {
    public static final int NO_ICON = -1;
    public static final LatLng NO_LOCATION = new LatLng(0, 0);

    // Posició del usuari
    public static LatLng sPosition;

    // Atributs immutables de la classe
    public final int id;
    public final LatLng posicio;
    public final String nom;
    public final String descripcio;
    public final int iconResource;
    public final int categoriaId;

    // Atributs mutables
    private List<Imatge> imatges;
    private List<Comentari> comentaris;

    /**
     * El constructor es privat, només es poden instanciar objectes de aquesta classe fent servir el
     * builder incorporat per configurar-lo.
     *
     * @param builder objecte amb la configuració per crear el Lloc.
     */
    private Lloc(LlocBuilder builder) {
        this.id = (builder.id == -1 ? -1 : builder.id);
        this.nom = builder.nom;
        this.descripcio = builder.descripcio;
        this.posicio = builder.posicio;
        this.categoriaId = builder.categoriaId;
        this.iconResource = builder.iconResource;
    }

    /**
     * Afegeix el comentari.
     *
     * @param comentari comentari per afegir
     */
    public void addComentari(Comentari comentari) {
        if (comentaris == null) {
            comentaris = new ArrayList<Comentari>();
        }
        comentaris.add(comentari);
    }

    /**
     * Afegeix la llista de comentaris als actuals.
     *
     * @param comentaris llista de comentaris per afegir
     */
    public void addComentaris(List<Comentari> comentaris) {
        if (this.comentaris == null) {
            this.comentaris = new ArrayList<Comentari>();
        }
        this.comentaris.addAll(comentaris);
    }

    /**
     * Reemplaça la llista actual de comentaris amb la passada com argument
     *
     * @param comentaris llista de comentaris que sustituiran als actuals
     */
    public void setComentaris(List<Comentari> comentaris) {
        this.comentaris = comentaris;
    }

    public List<Comentari> getComentaris() {
        if (comentaris == null) {
            comentaris = new ArrayList<Comentari>();
        }
        return comentaris;
    }

    /**
     * Afegeix una imatge al lloc.
     *
     * @param imatge imatge per afegir.
     */
    public void addImatge(Imatge imatge) {
        if (imatges == null) {
            imatges = new ArrayList<Imatge>();
        }
        imatges.add(imatge);
    }

    /**
     * Afegeix una llista d'imatges al lloc.
     *
     * @param imatges llista d'imatges per afegir.
     */
    public void addImatges(List<Imatge> imatges) {
        if (this.imatges == null) {
            this.imatges = new ArrayList<Imatge>();
        }
        this.imatges.addAll(imatges);
    }

    /**
     * Retorna la llista completa de imatges associada amb el lloc.
     *
     * @return llista d'imatges o buida si no hi ha cap.
     */
    public List<Imatge> getImages() {
        if (imatges == null) {
            imatges = new ArrayList<Imatge>();
        }
        return imatges;
    }

    /**
     * Si hi ha cap imatge associada a aquest lloc torna la primera de la llista.
     *
     * @return primera imatge de la llista o null si no hi ha cap.
     */
    public Imatge getImatgePrincipal() {
        if (imatges == null || imatges.isEmpty()) {
            return null;
        } else {
            return imatges.get(0);
        }
    }

    /**
     * Retorna una versió ajustada de la descripció a un màxim de 30 caràcters.
     *
     * @return descripció complete si es menor de 30 caràcters o versió acurtada a 30 caràcters.
     */
    public String getShortDescripcio() {
        if (descripcio.length() > 30) {
            return descripcio.substring(0, 27) + "...";
        } else {
            return descripcio;
        }
    }

    /**
     * Retorna la distancia entre aquest lloc i el lloc passat com argument. Si no s'ha inicialitzat
     * el lloc retorna -1.
     *
     * @return distancia en km entre els dos llocs o -1 si no s'ha inicialitzat.
     */
    public float getDistance() {
        float[] results = {0f};
        if (sPosition == NO_LOCATION) {
            return -1f;
        }
        Location.distanceBetween(sPosition.latitude, sPosition.longitude, this.posicio.latitude,
                this.posicio.longitude, results);
        return results[0] / 1000; // Pasem el resultat a km
    }

    /**
     * Builder Pattern. El nom i la posició del lloc son obligatoris, la resta son opcionals. Si no
     * s'especifica cap icon es farà servir el icon per defecte, i si no s'especifica la categoria
     * es farà servir la genèrica.
     */
    public static class LlocBuilder {
        private int id = -1; // Si no s'estableix una id es passarà aquest valor.
        private int iconResource = NO_ICON;
        private int categoriaId = 0;
        private String nom;
        private String descripcio = ""; // Evitem passar valors nulls
        private LatLng posicio;

        public LlocBuilder(String nom, LatLng posicio) {
            this.nom = nom;
            this.posicio = posicio;
        }

        public LlocBuilder(String nom, float latitud, float longitud) {
            this.nom = nom;
            this.posicio = new LatLng(latitud, longitud);
        }

        public LlocBuilder descripcio(String descripcio) {
            this.descripcio = descripcio;
            return this;
        }

        public LlocBuilder id(int id) {
            this.id = id;
            return this;
        }

        public LlocBuilder icon(int iconResource) {
            this.iconResource = iconResource;
            return this;
        }

        public LlocBuilder categoria(int categoriaId) {
            this.categoriaId = categoriaId;
            return this;
        }

        public LlocBuilder categoria(Categoria categoria) {
            this.categoriaId = categoria.id;
            return this;
        }

        public Lloc build() {
            return new Lloc(this);
        }
    }
}
