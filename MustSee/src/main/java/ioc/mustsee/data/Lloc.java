package ioc.mustsee.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Lloc {
    public static final int DEFAULT_ICON = 0; // TODO: establecer el icono por defecto
    // TODO fer servir un builder
    public static int idCounter = 10000; // TODO: contador por defecto
    public final int id;
    public final LatLng posicio;
    public final String nom;
    public final String descripcio;
    public final int iconResource;
    public final int categoria;
    private List<Imatge> galeria;

    public Lloc(int id, String nom, int categoria, float latitud, float longitud, String descripcio) {
        this(id, nom, categoria, latitud, longitud, descripcio, DEFAULT_ICON);
    }

    public Lloc(String nom, int categoria, float latitud, float longitud, String descripcio) {
        this(nom, categoria, latitud, longitud, descripcio, DEFAULT_ICON);
    }

    public Lloc(String nom, int categoria, float latitud, float longitud, String descripcio, int iconResource) {
        this(idCounter++, nom, categoria, latitud, longitud, descripcio, iconResource);
    }

    public Lloc(int id, String nom, int categoria, float latitud, float longitud, String descripcio, int iconResource) {
        this.id = id;
        this.posicio = new LatLng(latitud, longitud);
        this.nom = nom;
        this.categoria = categoria;
        this.descripcio = descripcio;
        this.iconResource = iconResource;
    }


    public void addImatge(Imatge imatge) {
        if (galeria == null) {
            galeria = new ArrayList<Imatge>();
        }
        galeria.add(imatge);
    }

    public List<Imatge> getImages() {
        return galeria;
    }

    // Devuelve la primera imagen
    public Imatge getImatgePrincipal() {
        if (galeria == null) {
            return null;
        } else {
            return galeria.get(0);
        }
    }

}
