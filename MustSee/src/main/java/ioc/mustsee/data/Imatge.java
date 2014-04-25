package ioc.mustsee.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ioc.mustsee.activities.MainActivity;

/**
 * Aquesta classe emmagatzema la informació d'una imatge. Tots els seus atributs son immutables i
 * poden ser llegits directament.
 *
 * @author Xavier García
 */
public class Imatge {
    public static final String DEFAULT_PICTURE = "test.jpg";

    public final int id;
    public final String titol;
    public final String nomFitxer;
    public final int llocId;

    /**
     * Constructor sense id, es fa servir una id per defecte que sempre es trobarà per sobre de 10000.
     *
     * @param titol     títol de la imatge.
     * @param nomFitxer nom del fitxer on es troba la imatge.
     */
    public Imatge(String titol, String nomFitxer, int llocId) {
        this(-1, titol, nomFitxer, llocId);
    }

    /**
     * Constructor complet de la imatge.
     *
     * @param id        identificador de la imatge.
     * @param titol     títol de la imatge.
     * @param nomFitxer nom del fitxer on es troba la imatge.
     */
    public Imatge(int id, String titol, String nomFitxer, int llocId) {
        this.id = id;
        this.titol = titol;
        this.nomFitxer = nomFitxer;
        this.llocId = llocId;
    }

    /**
     * Carrega la imatge corresponent a aquesta instància i la retorna com a Bitmap.
     *
     * @param context context de la aplicació principal
     * @return bitmap de la imatge.
     */
    public Bitmap loadImatge(Context context) {
        Bitmap image;

        try {
            String filename = Uri.parse(nomFitxer).getLastPathSegment();
            File file = new File(Environment.getExternalStorageDirectory() + MainActivity.PICTURES_DIRECTORY, filename);
            image = BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (Exception e) {
            // Si hi ha un error al carregar la imatge es mostra la imatge per defecte
            image = getBitmapFromAssets(context, DEFAULT_PICTURE);
        }
        return image;
    }

    /**
     * Mètode per carregar imatges des de el directori d'assets i convertir-la en un bitmap.
     *
     * @param context  context de la aplicació principal.
     * @param fileName nom del fitxer on es troba la imatge.
     * @return bitmap de la imatge.
     */
    private static Bitmap getBitmapFromAssets(Context context, String fileName) {
        InputStream in = null;
        try {
            in = context.getAssets().open(fileName);
        } catch (IOException e) {
            // Si hi ha un error al carregar la imatge es mostra la imatge per defecte
            return getBitmapFromAssets(context, DEFAULT_PICTURE);
        }

        return BitmapFactory.decodeStream(in);
    }

    /**
     * Aquest mètode fa servir una BitmapFactory per encongir la imatge abans de afegir-la a la UI.
     *
     * @param nomFitxer nom del fitxer on es troba la imatge
     * @param width     amplada que volem
     * @param height    alçada que volem
     * @return Bitmap amb les mides especificades
     */
    public static Bitmap ShrinkBitmap(String nomFitxer, int width, int height) {
        // Obtenim la ruta al fitxer
        String filename = Uri.parse(nomFitxer).getLastPathSegment();
        String file = Environment.getExternalStorageDirectory() + MainActivity.PICTURES_DIRECTORY + "/" + filename;

        // Creem les opcions de la factoria
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;

        // Ajustem els ratios d'alçada i amplada
        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        // Si algun dels dos es superior a 1 els apliquem
        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }
        bmpFactoryOptions.inJustDecodeBounds = false;

        // Generem el bitmap i el retornem
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }
}
