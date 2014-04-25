package ioc.mustsee.data;

/**
 * Classe de dades immutable que emmagatzema la informació d'un comentari.
 *
 * @author Xavier García
 */
public class Comentari {

    public final int id;
    public final String text;
    public final int llocId;
    public final int usuariId;
    public final String nomUsuari;
    public final String data;

    /**
     * Aquest constructor del comentari requereix especificar totes les dades.
     *
     * @param id        id del comentari
     * @param text      text del comentari
     * @param usuariId  id del usuari que ha fet el comentari
     * @param nomUsuari nom del usuari que ha fet el comentari
     * @param data      data en la que s'ha fet el comentari
     * @param llocId    id del lloc sobre el que s'ha fet el comentari
     */
    public Comentari(int id, String text, int usuariId, String nomUsuari, String data, int llocId) {
        this.id = id;
        this.text = text;
        this.usuariId = usuariId;
        this.llocId = llocId;
        this.nomUsuari = nomUsuari;
        this.data = data;
    }

    /**
     * Aquest constructor requereix totes les dades excepte la id del comentari que serà -1.
     *
     * @param text      text del comentari
     * @param usuariId  id del usuari que ha fet el comentari
     * @param nomUsuari nom del usuari que ha fet el comentari
     * @param data      data en la que s'ha fet el comentari
     * @param llocId    id del lloc sobre el que s'ha fet el comentari
     */
    public Comentari(String text, int usuariId, String nomUsuari, String data, int llocId) {
        this(-1, text, usuariId, nomUsuari, data, llocId);
    }
}
