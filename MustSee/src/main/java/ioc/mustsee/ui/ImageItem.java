package ioc.mustsee.ui;

/**
 * Created by Xavier on 29/03/2014.
 */

import android.graphics.Bitmap;

//TODO: Esta clase se reemplazará con mi clase Imatge
public class ImageItem {
    private Bitmap image;
    private String title;

    public ImageItem(Bitmap image, String title) {
        super(); // esto hace algo?
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}