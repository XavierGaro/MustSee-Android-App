package ioc.mustsee.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Widget personalitzar per avisar al listener que s'ha seleccionat un element encara que sigui el
 * mateix que ja estava seleccionat. El comportament del Spinner per defecte es ignorar la selecció
 * si es tracta del mateix element.
 */
public class MySpinner extends Spinner {
    private static final String TAG = "MySpinner";

    OnItemSelectedListener mListener;

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (mListener != null) {
            mListener.onItemSelected(null, null, position, 0);
        }
    }

    /**
     * Listener propi per avisar de la selecció encara que el item seleccionat no hagi canviat.
     *
     * @param listener
     */
    public void setOnItemSelectedEvenIfUnchangedListener(OnItemSelectedListener listener) {
        this.mListener = listener;
    }
}