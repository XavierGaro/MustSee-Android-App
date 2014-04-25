package ioc.mustsee.downloaders;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ioc.mustsee.parser.XmlParserMustSee;

/**
 * Aquesta classe proporciona els mètodes comuns per descarregar fluxos de dades XML de
 * asíncronament, analitzar-los i retornar els resultats al acabar.
 * <p/>
 * La URL de la que s'obtenen les dades es passa a través del mètode AsyncTask#execute().
 *
 * @param <T> tipus de la llista que retornarà amb els objectes obtinguts de analitzar les dades.
 * @author Xavier García
 */
public abstract class DownloadXmlAsyncTask<T> extends AsyncTask<String, Void, List<T>> {
    private final static String TAG = "DownloadXmlAsyncTask";

    public static final int READ_TIMEOUT = 10000; // 10000
    public static final int CONNECT_TIMEOUT = 15000; // 15000

    final static int URL = 0;

    final OnTaskCompleted mCallback;
    final String mRoot;
    final Map<String, String> mParams;

    /**
     * Al constructor s'ha de passar el objecte que rebrà el resultat, el nom del node arrel del
     * document XML, i un mapa amb els paràmetres per enviar.
     *
     * @param callback objecte que rebrà les dades
     * @param root     nom del node arrel
     * @param params   paràmetres per fer la consulta
     */
    public DownloadXmlAsyncTask(OnTaskCompleted callback, String root, Map<String, String> params) {
        this.mCallback = callback;
        this.mRoot = root;
        this.mParams = params;
    }

    /**
     * Amb aquest constructor els paràmetres son opcionals.
     *
     * @param callback objecte que rebrà les dades
     * @param root     nom del node arrel
     */
    public DownloadXmlAsyncTask(OnTaskCompleted callback, String root) {
        this(callback, root, new HashMap<String, String>());
    }

    @Override
    protected List<T> doInBackground(String... urls) {
        XmlParserMustSee<T> parser = new XmlParserMustSee<T>(mRoot);
        try {
            // Obrim el flux de dades i el retornem analitzat
            InputStream in;
            in = send(urls[URL]);
            return parser.parse(in);

        } catch (Exception e) {
            // Si hi ha cap error retornem una llista buida i mostrem l'error pel Log.
            Log.e(TAG, "No s'ha pogut generar el XML: " + e);
            return new ArrayList<T>();
        }
    }

    @Override
    protected void onPostExecute(List<T> result) {
        // Retornem el resultat
        mCallback.onTaskCompleted(result);
    }

    /**
     * Aquest mètode ha de ser implementat per les classes concretes. Es l'encarregat d'enviar la
     * petició al web service i realitzar la connexió per poder returna el flux de dades.
     *
     * @param urlString url del servidor
     * @return flux de dades obtingut de la connexió
     * @throws IOException si hi ha cap problema
     */
    abstract InputStream send(String urlString) throws IOException;

    /**
     * Aquest mètode l'han de implementar les classes concretes per indicar com s'ha d'afegir els
     * paràmetres per realitzar la connexió.
     *
     * @return string amb els paràmetres enllaçats de forma adequada pel tipus de petició
     * @throws UnsupportedEncodingException si hi ha cap problema al codificar els paràmetres
     */
    abstract String getQuery() throws UnsupportedEncodingException;
}
