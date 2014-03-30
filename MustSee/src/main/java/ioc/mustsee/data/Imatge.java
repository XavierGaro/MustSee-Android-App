package ioc.mustsee.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

import ioc.mustsee.activities.MainActivity;

public class Imatge {
    public static int idCounter = 10000; // TODO: contador por defecto

    public final int id;
    public final String tittle;
    public final String fileName;

    public Imatge(String tittle, String fileName) {
        this(idCounter++, tittle, fileName);
    }

    public Imatge(int id, String tittle, String fileName) {
        this.id = id;
        this.tittle = tittle;
        this.fileName = fileName;
    }

    // TODO hace falta un contexto
    private static Bitmap getBitmapFromAssets(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();

        InputStream istr = null;
        try {
            istr = assetManager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeStream(istr);

        return bitmap;
    }

    // TODO: Buscar otra manera para pasar el contexto
    public Bitmap carregarImatge(Context context) {


        return getBitmapFromAssets(context, MainActivity.PICTURES_DIRECTORY + this.fileName);

           /*
        //TODO: Carregar la imagen principal del lloc. Este código es el mismo que en LlocArrayAdapter.
        String filename = lloc.thumnail;
        File file = new File(context.getFilesDir()
                + MainActivity.THUMBNAILS_DIRECTORY, filename);
        Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());
        return image;
        */
    }

}
