package ioc.mustsee.data;

/**
 * Aquesta classe emmagatzema la informació d'una categoria. Tots els seus atributs son immutables i
 * poden ser llegits directament.
 *
 * @author Xavier García
 */
public class Categoria {

    public final int id;
    public final String nom;
    public final String descripcio;


    /**
     * Constructor sense id, es fa servir una id per defecte que sempre es trobarà per sobre de 10000.
     *
     * @param nom        nom de la categoria
     * @param descripcio descripció de la categoria
     */
    public Categoria(String nom, String descripcio) {
        this(-1, nom, descripcio);
    }

    /**
     * Constructor complet de la categoria.
     *
     * @param id         identificador
     * @param nom        nom de la categoria
     * @param descripcio descripció de la categoria
     */
    public Categoria(int id, String nom, String descripcio) {
        this.id = id;
        this.nom = nom;
        this.descripcio = descripcio;
    }
}
