package ioc.mustsee.downloaders;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class DownloadXMLAsyncTaskGET<T> extends DownloadXMLAsyncTask<T> {
    private final static String TAG = "DownloadXMLAsyncTaskGET";

    public DownloadXMLAsyncTaskGET(OnTaskCompleted callback, String root, Map<String, String> params) {
        super(callback, root, params);
    }

    public DownloadXMLAsyncTaskGET(OnTaskCompleted callback, String root) {
        this(callback, root, new HashMap<String, String>());
    }


    InputStream send(String urlString) throws IOException, SocketTimeoutException{
        // Si hi han paràmetres ens preparem per enviar-los
        if (!mParams.isEmpty()) {
            urlString = urlString + getQuery();
        }

        java.net.URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        // Si la resposta no es correcte, o hi ha algun error retornem null
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        } else {
            Log.d(TAG, "Resposta incorrecta? " + conn.getResponseCode());
            return null;
        }
    }

    // Construïm la seqüència de paràmetres
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
