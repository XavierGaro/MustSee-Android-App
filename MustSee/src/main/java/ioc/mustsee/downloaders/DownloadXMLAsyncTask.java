package ioc.mustsee.downloaders;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ioc.mustsee.parser.MustSeeXMLParser;

public abstract class DownloadXMLAsyncTask<T> extends AsyncTask<String, Void, List<T>> {
    private final static String TAG = "DownloadXMLAsyncTask";

    final static int URL = 0;
    final static int METHOD = 1;

    final OnTaskCompleted mCallback;
    final String mRoot;
    final Map<String, String> mParams;

    public DownloadXMLAsyncTask(OnTaskCompleted callback, String root, Map<String, String> params) {
        this.mCallback = callback;
        this.mRoot = root;
        this.mParams = params;
    }

    public DownloadXMLAsyncTask(OnTaskCompleted callback, String root) {
        this(callback, root, new HashMap<String, String>());
    }


    @Override
    protected List<T> doInBackground(String... urls) {
        MustSeeXMLParser<T> parser = new MustSeeXMLParser<T>(mRoot);
        try {
            InputStream in;
            in = send(urls[URL]);

            Log.d("Url: " + urls[URL], "Method: " + " Params: " + mParams);
            return parser.parse(in);

        } catch (Exception e) {
            Log.e(TAG, "No s'ha pogut generar el XML: " + e);
            return new ArrayList<T>();
        }
    }

    @Override
    protected void onPostExecute(List<T> result) {
        mCallback.onTaskCompleted(result);
    }

    abstract InputStream send(String urlString) throws IOException, SocketTimeoutException;

    abstract String getQuery() throws UnsupportedEncodingException;


}
