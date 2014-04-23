package ioc.mustsee.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ioc.mustsee.activities.MainActivity;

/**
 * Aquesta classe emmagatzema la informació d'una imatge. Tots els seus atributs son immutables i
 * poden ser llegits directament.
 *
 * @author Javier García
 */
public class Imatge {
    public static final String DEFAULT_PICTURE = "test.jpg";

    public static int sIdCounter = 10000; // TODO: contador por defecto

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
        this(sIdCounter++, titol, nomFitxer, llocId);
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
     * Carrega la imatge corresponent a aquesta instancia i la retorna com a Bitmap.
     * TODO: Això no es farà des de aquí, hi haurà una classe especial per gestionar els fitxers.
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
            Log.e("Imatge", "Error al carregar la imatge: " + e);
            image = getBitmapFromAssets(context, DEFAULT_PICTURE);
        }
        return image;
    }

    /**
     * Mètode temporal per carregar imatges des de el directori d'assets i convertir-la en un
     * bitmap.
     * TODO: Això no es farà des de aquí, hi haurà una classe especial per gestionar els fitxers.
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
     * Uses Bitmapfactory to shrink the given image to the expected size.
     *
     * @param file   Path to the image.
     * @param width  expected width of the image.
     * @param height expected height of the image.
     * @return a Bitmap with the new size.
     */
    public static Bitmap ShrinkBitmap(String nomFitxer, int width, int height) {
        String filename = Uri.parse(nomFitxer).getLastPathSegment();
        String file = Environment.getExternalStorageDirectory() + MainActivity.PICTURES_DIRECTORY + "/" + filename;
        Log.d("ShrinkBitmap", "Trying to shrink " + file + " to " + width + "x" + height);
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        Log.d("ShrinkBitmap", "Original size: " + bmpFactoryOptions.outWidth + "x" + bmpFactoryOptions.outHeight);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);
        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
                Log.d("ShrinkBitmap", "Shrink ratio: " + heightRatio);
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
                Log.d("ShrinkBitmap", "Shrink ratio: " + widthRatio);
            }
        }
        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }
}
