package ioc.mustsee.parser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import ioc.mustsee.data.Lloc;

public class ParserMustSee {
    private static final String TAG = "ParserMustSee";
    private static final String URL_GET_LLOCS = "http://mustseers.hol.es/api/v1/llocs.xml";
    private static final String URL_GET_CATEGORIES = "http://mustseers.hol.es/api/v1/categories.xml";

    public void getLlocs(OnTaskCompleted callback) {
        new DownloadXmlTask<Lloc>(callback, "llocs").execute(URL_GET_LLOCS);
    }

    public void getCategories(OnTaskCompleted callback) {
        new DownloadXmlTask<Lloc>(callback, "categories").execute(URL_GET_CATEGORIES);
    }


    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask<T> extends AsyncTask<String, Void, List<T>> {
        private final static String TAG = "DownloadXmlTask";

        private OnTaskCompleted mCallback;
        private String mRoot;

        public DownloadXmlTask(OnTaskCompleted callback, String root) {
            this.mCallback = callback;
            this.mRoot = root;
        }

        @Override
        protected List<T> doInBackground(String... urls) {
            try {
                MustSeeXMLParser parser = new MustSeeXMLParser(mRoot);
                InputStream in = downloadUrl(urls[0]);
                return parser.parse(in);
            } catch (Exception e) {
                throw new RuntimeException("Error al descarregar el XML: ", e);
            }
        }

        @Override
        protected void onPostExecute(List<T> result) {

            // Do stuff with the result
            for (T entry : result) {
                Log.w(TAG, entry.toString());
            }

            mCallback.onTaskCompleted(result);

        }
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //conn.setReadTimeout(10000 /* milliseconds */);
        //conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

}
