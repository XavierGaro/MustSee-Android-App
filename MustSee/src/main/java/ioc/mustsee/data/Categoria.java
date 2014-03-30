package ioc.mustsee.data;

public class Categoria {
    // TODO fer servir un builder
    public static int idCounter = 10000; // TODO: contador por defecto

    public final int id;
    public final String nom;
    public final String descripcio;


    public Categoria(String nom, String descripcio) {
        this(idCounter, nom, descripcio);
    }

    public Categoria(int id, String nom, String descripcio) {
        this.id = id;
        this.nom = nom;
        this.descripcio = descripcio;
    }

}
