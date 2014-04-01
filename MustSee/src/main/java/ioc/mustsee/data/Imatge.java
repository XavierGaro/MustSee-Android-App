package ioc.mustsee.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
    public Bitmap carregarImatge(Context context) {
        return getBitmapFromAssets(context, MainActivity.PICTURES_DIRECTORY + this.nomFitxer);
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

}
