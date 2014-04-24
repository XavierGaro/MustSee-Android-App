package ioc.mustsee.data;

public class Usuari {

    public static int sIdCounter = 10000; // TODO: contador por defecto

    public final int id;
    public final String nom;
    public final String correu;
    public final String password;

    public Usuari(int id, String nom, String correu, String password) {
        this.id = id;
        this.nom = nom;
        this.correu = correu;
        this.password = password;
    }

    public Usuari(String nom, String correu, String password) {
        this(sIdCounter++, nom, correu, password);
    }
}
