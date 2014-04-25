package ioc.mustsee.parser;

import java.util.HashMap;
import java.util.Map;

import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Comentari;
import ioc.mustsee.data.Lloc;
import ioc.mustsee.downloaders.DownloadXmlAsyncTaskGET;
import ioc.mustsee.downloaders.DownloadXmlAsyncTaskPOST;
import ioc.mustsee.downloaders.OnTaskCompleted;

/**
 * Aquesta classe s'encarrega de realitzar les operacions necessaries per carregar les dades i
 * retornar-les a la classe que les demana quan acaba la descarrega.
 *
 * @author Xavier García
 */
public class RetrieveData {
    private static final String TAG = "RetrieveData";

    private static final String URL_LLOCS = "http://mustseers.hol.es/api/v1/llocs.xml";
    private static final String URL_CATEGORIES = "http://mustseers.hol.es/api/v1/categories.xml";
    private static final String URL_AUTH = "http://mustseers.hol.es/api/v1/auth.xml";
    private static final String URL_COMMENT_FROM_LLOC = "http://mustseers.hol.es/api/v1/comentaris/llocs/";

    /**
     * Retorna la llista completa de llocs.
     *
     * @param callback objecte que rebrà la resposta
     */
    public static void getLlocs(OnTaskCompleted callback) {
        new DownloadXmlAsyncTaskGET<Lloc>(callback, "llocs").execute(URL_LLOCS);
    }

    /**
     * Retorna la llista completa de categories.
     *
     * @param callback objecte que rebrà la resposta
     */
    public static void getCategories(OnTaskCompleted callback) {
        new DownloadXmlAsyncTaskGET<Categoria>(callback, "categories").execute(URL_CATEGORIES);
    }

    /**
     * Retorna una llista amb amb el resultat de la autenticació, per ser correcte només ha de
     * retornar un booleà que serà true.
     *
     * @param callback objecte que rebrà la resposta
     */
    public static void getAuth(OnTaskCompleted callback, String correu, String password) throws RuntimeException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("correu", correu);
        params.put("password", password);

        new DownloadXmlAsyncTaskGET<Boolean>(callback, "auth", params).execute(URL_AUTH);
    }

    /**
     * Envia un comentari sobre un lloc que serà afegit a la base de dades a través del web service.
     *
     * @param callback objecte que rebrà la resposta, per ser correcte només ha de
     *                 retornar un booleà que serà true.
     * @param correu   correu del usuari
     * @param password password del usuari
     * @param text     text del comentari
     * @param llocId   id del lloc
     */
    public static void postComment(OnTaskCompleted callback, String correu, String password, String text, int llocId) {
        String url = URL_COMMENT_FROM_LLOC + llocId + ".xml";

        Map<String, String> params = new HashMap<String, String>();
        params.put("correu", correu);
        params.put("password", password);
        params.put("comentari", text);

        new DownloadXmlAsyncTaskPOST<Boolean>(callback, "comentari", params).execute(url);
    }

    /**
     * Obté tots els comentaris d'un lloc
     *
     * @param callback objecte que rebrà la resposta
     * @param llocId   id del lloc del que volem obtenir els comentaris
     */
    public static void getComentarisFromLloc(OnTaskCompleted callback, int llocId) {
        String url = URL_COMMENT_FROM_LLOC + llocId + ".xml";
        new DownloadXmlAsyncTaskGET<Comentari>(callback, "comentaris").execute(url);
    }
}
