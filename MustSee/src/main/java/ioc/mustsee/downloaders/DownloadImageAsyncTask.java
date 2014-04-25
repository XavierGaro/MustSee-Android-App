package ioc.mustsee.downloaders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Aquesta classe s'encarrega de descarregar imatges i emmagatzemar-les en el disc en la carpeta
 * indicada al constructor. La llista de imatges a descarregar es passa com un array de Strings amb
 * la URL de cada imatge. Aquesta classe comunicarà al DownloadManager passat com argument al
 * constructor quan inicia i quan finalitza les descarregues.
 *
 * @author Xavier García
 */
public class DownloadImageAsyncTask extends AsyncTask<String, Void, Void> {
    public static final String TAG = "DownloadImageAsyncTask";

    public static final int TIMEOUT = 10000;

    private File folder;
    private DownloadManager gestor;

    /**
     * Aquest constructor accepta que la carpeta de destí sigui una cadena de text.
     *
     * @param gestor Activitat que implementi la interfície DownloadManager.
     * @param folder cadena de text amb la carpeta de destí.
     * @see DownloadManager
     */
    public DownloadImageAsyncTask(DownloadManager gestor, File folder) {
        this(gestor, folder.toString());
    }

    /**
     * Aquest constructor requereix que la carpeta sigui un File
     *
     * @param gestor Activitat que implementi la interfície DownloadManager.
     * @param folder objecte File amb la carpeta de destí.
     * @see DownloadManager
     */
    public DownloadImageAsyncTask(DownloadManager gestor, String folder) {
        this.folder = new File(Environment.getExternalStorageDirectory() + folder);
        this.gestor = gestor;
        comprovarDirectori();
    }

    /**
     * Comprova si existeix el directori emmagatzemat a folder, i si no existeix el crea.
     */
    private void comprovarDirectori() {
        if (!folder.exists()) {
            Log.d(TAG, "No existe el directorio");
            folder.mkdirs();
        }

        if (!folder.exists()) {
            Log.d(TAG, "Sigue sin existir");
        }
    }

    @Override
    protected void onPreExecute() {
        gestor.donwloadInProgress(true);
    }

    @Override
    protected Void doInBackground(String... urls) {
        Bitmap image;
        File file;
        String filename = null;
        InputStream in;
        FileOutputStream out = null;

        // Recorrem la llista d'URLs i descarreguem cada una de les imatges
        for (String url : urls) {
            filename = Uri.parse(url).getLastPathSegment();

            // Si aquesta imatge ja la tenim en cache, no la descarreguem
            if (new File(folder, filename).exists()) {
                continue;
            }

            // Si hi ha cap error al descarregar una imatge la resta continua
            // descarregant.
            try {
                // Descarreguem la imatge, si triga massa en connectar passem a la següent
                URLConnection con = new URL(url).openConnection();
                con.setConnectTimeout(TIMEOUT);
                in = con.getInputStream();
                image = BitmapFactory.decodeStream(in);

                // Guardem la imatge en un fitxer
                filename = Uri.parse(url).getLastPathSegment();
                file = new File(folder, filename);
                out = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
            } catch (IOException e) {
                // Mostrem l'error pel log
                Log.e(TAG, "error al guardar el fitxer: " + e.getMessage());
            } finally {
                tancar(out);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        gestor.donwloadInProgress(false);
    }

    /**
     * Tanca un flux de sortida passat com argument.
     *
     * @param out flux a tancar.
     */
    private void tancar(FileOutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
                // Si hi ha cap error l'ignorem.
            }
        }
    }
}
