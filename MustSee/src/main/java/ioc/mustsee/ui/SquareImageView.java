package ioc.mustsee.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Clase personalitzada per forçar la creació de ImageViews quadrades.
 */
public class SquareImageView extends ImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Sobreescriu el mètode per establir sempre les dimensions del ImageView com a quadreades.
     * La alçada es ignorada, sempre es fa servir l'amplada per totes dues dimensions.
     *
     * @param widthMeasureSpec  amplada mesurada.
     * @param heightMeasureSpec llargària mesurada (es ignorada)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
