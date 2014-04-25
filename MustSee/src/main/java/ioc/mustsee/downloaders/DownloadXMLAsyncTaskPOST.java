package ioc.mustsee.downloaders;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class DownloadXMLAsyncTaskPOST<T> extends DownloadXMLAsyncTask<T> {
    private final static String TAG = "DownloadXMLAsyncTaskPOST";

    public DownloadXMLAsyncTaskPOST(OnTaskCompleted callback, String root, Map<String, String> params) {
        super(callback, root, params);
    }

    public DownloadXMLAsyncTaskPOST(OnTaskCompleted callback, String root) {
        this(callback, root, new HashMap<String, String>());
    }

    @Override
    InputStream send(String urlString) throws IOException, SocketTimeoutException {
        java.net.URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);

        // Si hi han paràmetres ens preparem per enviar-los
        if (!mParams.isEmpty()) {
            conn.setDoOutput(true);
            // Obrim el stream de dades per enviar els paràmetres al servidor
            OutputStream out = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(getQuery());
            writer.flush();
            writer.close();
            out.close();
        }


        // Afegim els paràmetres per la petició
        conn.connect();

        // Si la resposta no es correcte, o hi ha algun error retornem null
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        } else {
            Log.d(TAG, "Resposta incorrecta? " + conn.getResponseCode());
            return null;
        }
    }


    @Override
    String getQuery() throws UnsupportedEncodingException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        Map<String, String> p = mParams;
        for (Map.Entry<String, String> entry : p.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
