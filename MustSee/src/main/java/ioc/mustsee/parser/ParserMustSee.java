package ioc.mustsee.parser;

import android.os.AsyncTask;
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
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Lloc;
import ioc.mustsee.fragments.DetailFragment;

public class ParserMustSee {
    private static final String TAG = "ParserMustSee";
    private static final String URL_GET_LLOCS = "http://mustseers.hol.es/api/v1/llocs.xml";
    private static final String URL_GET_CATEGORIES = "http://mustseers.hol.es/api/v1/categories.xml";
    private static final String URL_POST_AUTH = "http://mustseers.hol.es/api/v1/auth.xml";
    private static final String URL_POST_COMMENT = "http://mustseers.hol.es/api/v1/comentaris/llocs/";

    public void getLlocs(OnTaskCompleted callback) {
        new DownloadXmlTask<Lloc>(callback, "llocs").execute(URL_GET_LLOCS);
    }

    public void getCategories(OnTaskCompleted callback) {
        new DownloadXmlTask<Categoria>(callback, "categories").execute(URL_GET_CATEGORIES);
    }

    // TODO, s'ha de passar el correu i el password al mètode d'autenticació
    public void getAuth(OnTaskCompleted callback, String correu, String password) throws RuntimeException {
        new PostXmlTask<Boolean>(callback, "auth", correu, password).execute(URL_POST_AUTH);
    }

    public void postComment(OnTaskCompleted callback, String correu, String password, String text, int llocId) {
        String url = URL_POST_COMMENT + llocId + ".xml";
        new PostCommentXmlTask<Boolean>(callback, "auth", correu, password, text).execute(url);
    }


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

    // TODO la tasca per comprovar el pass hauria de ser GET, el mètode hauria de ser variable. Es
    // podria passar un mapa amb els atributs a enviar i el métode per enviar. Canviar el nom de la
    // classe. En aquest cas fusionar la funcionalitat de les dos AsyncTask i comprovar si hi han
    // paràmetres per enviar
    private class PostXmlTask<T> extends AsyncTask<String, Void, List<T>> {
        private final static String TAG = "DownloadXmlTask";

        private OnTaskCompleted mCallback;
        private String mRoot;
        private String mCorreu;
        private String mPassword;

        public PostXmlTask(OnTaskCompleted callback, String root, String correu, String password) {
            this.mCallback = callback;
            this.mRoot = root;
            this.mCorreu = correu;
            this.mPassword = password;
        }

        public PostXmlTask(OnTaskCompleted callback, String root) {
            this.mCallback = callback;
            this.mRoot = root;
        }

        @Override
        protected List<T> doInBackground(String... urls) throws RuntimeException {
            try {
                MustSeeXMLParser parser = new MustSeeXMLParser(mRoot);
                //InputStream in = downloadUrl(urls[0]);

                // No cal fer el parse, si el codi de la connexió no es 200 es que les dades son incorrectes
                List result = new ArrayList<T>();
                if (downloadUrl(urls[0]) == null) {
                    result.add(false);
                } else {
                    result.add(true);
                }
                return result;


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

        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setReadTimeout(10000);
            //conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Data to send
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("correu", mCorreu));
            params.add(new BasicNameValuePair("password", mPassword));

            // Obrim el stream de dades per enviar els paràmetres al servidor
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

            // Si la resposta no es correcte, hi ha hagut un error al autenticar-se
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return conn.getInputStream();
            } else {
                return null;
            }


        }

        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }
    }

    private class PostCommentXmlTask<T> extends AsyncTask<String, Void, List<T>> {
        private final static String TAG = "DownloadXmlTask";

        private OnTaskCompleted mCallback;
        private String mRoot;
        private String mCorreu;
        private String mPassword;
        private String mText;

        public PostCommentXmlTask(OnTaskCompleted callback, String root, String correu, String password, String text) {
            this.mCallback = callback;
            this.mRoot = root;
            this.mCorreu = correu;
            this.mPassword = password;
            this.mText= text;
        }

        @Override
        protected List<T> doInBackground(String... urls) throws RuntimeException {
            try {
                MustSeeXMLParser parser = new MustSeeXMLParser(mRoot);
                //InputStream in = downloadUrl(urls[0]); /// COMPTE, si es posa aquí i a la autenticació es crida dues vegades

                // No cal fer el parse, si el codi de la connexió no es 200 es que les dades son incorrectes
                List result = new ArrayList<T>();
                if (downloadUrl(urls[0]) == null) {
                    result.add(false);
                } else {
                    result.add(true);
                }
                return result;


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

        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setReadTimeout(10000);
            //conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Data to send
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("correu", mCorreu));
            params.add(new BasicNameValuePair("password", mPassword));
            params.add(new BasicNameValuePair("comentari", mText));


            Log.d(TAG, "Correu: "+ mCorreu+ " password: "+ mPassword + "comentari:" + mText + "url: "+url);

            // Obrim el stream de dades per enviar els paràmetres al servidor
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

            // Si la resposta no es correcte, hi ha hagut un error al autenticar-se
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return conn.getInputStream();
            } else {
                return null;
            }


        }

        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }
    }
}
