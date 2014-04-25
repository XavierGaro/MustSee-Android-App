package ioc.mustsee.downloaders;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe concreta de DownloadXmlAsyncTask per obtenir objectes analitzant un document XML fent
 * servir el mètode GET.
 *
 * @param <T> tipus de objectes a retornar en la llista
 * @author Xavier García
 */
public class DownloadXmlAsyncTaskGET<T> extends DownloadXmlAsyncTask<T> {
    private final static String TAG = "DownloadXmlAsyncTaskGET";

    public DownloadXmlAsyncTaskGET(OnTaskCompleted callback, String root, Map<String, String> params) {
        super(callback, root, params);
    }

    public DownloadXmlAsyncTaskGET(OnTaskCompleted callback, String root) {
        this(callback, root, new HashMap<String, String>());
    }

    @Override
    InputStream send(String urlString) throws IOException {
        // Si hi han paràmetres ens preparem per enviar-los
        if (!mParams.isEmpty()) {
            urlString = urlString + getQuery();
        }

        java.net.URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(DownloadXmlAsyncTask.READ_TIMEOUT);
        conn.setConnectTimeout(DownloadXmlAsyncTask.CONNECT_TIMEOUT);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        // Si la resposta no es correcte, o hi ha algun error retornem null
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        } else {
            return null;
        }
    }

    @Override
    String getQuery() {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        result.append("?");

        Map<String, String> p = mParams;
        for (Map.Entry<String, String> entry : p.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        return result.toString();
    }
}
