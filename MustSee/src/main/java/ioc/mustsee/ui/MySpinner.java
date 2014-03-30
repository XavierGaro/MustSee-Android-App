package ioc.mustsee.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Spinner;

public class MySpinner extends Spinner {
    private static final String TAG = "MySpinner";
    OnItemSelectedListener listener;

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelection(int position) {

        // Item seleccionado antes del click: TODO: borrar, no hace falta
        int oldPosition = getSelectedItemPosition();
        Log.d(TAG, "Antes del click item en posición: " + oldPosition);

        super.setSelection(position);
        if (listener != null) {
            listener.onItemSelected(null, null, position, 0);
            Log.d(TAG, "Seleccionado item en posición: " + position);
        } else {
            Log.d(TAG, "No hay listener: " + position);
        }
    }


    public void setOnItemSelectedEvenIfUnchangedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

}