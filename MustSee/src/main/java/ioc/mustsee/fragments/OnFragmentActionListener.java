package ioc.mustsee.fragments;


import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Lloc;

/**
 * Aquesta interfície l'ha de implementar l'activitat que adjunta els fragments.
 *
 * @author Javier García
 */
public interface OnFragmentActionListener {

    // Constants per definir les accions a realitzar a la hora de comunicar-se desde el fragment.
    public static final int ACTION_MAIN = 0;
    public static final int ACTION_LOG = 1;
    public static final int ACTION_SEARCH = 2;
    public static final int ACTION_EXPLORE = 3;
    public static final int ACTION_REGISTER = 4;
    public static final int ACTION_BACK = 5;
    public static final int ACTION_DETAIL = 6;
    public static final int ACTION_GALLERY = 7;
    public static final int ACTION_PICTURE = 8;

    /**
     * Aquest mètode es cridat pels fragments quan necesiten realitzar alguna acció.
     *
     * @param action acció a realitzar, que ha de correspondre amb una de les constants
     * @param bundle dades extres per realitzar la acció.
     */
    void OnActionDetected(int action, Bundle bundle);

    /**
     * Aquest mètode es cridat pels fragments quan necesiten realitzar alguna acció i no cal cap
     * informació extra.
     *
     * @param action acció a realitzar, que ha de correspondre amb una de les constants
     */
    void OnActionDetected(int action);

    /**
     * Estableix el lloc com a lloc actual.
     * TODO: Això s'ha de implementar com a acció
     *
     * @param lloc lloc per establir com actual.
     */
    void setCurrentLloc(Lloc lloc);

    /**
     * Retorna el lloc actual.
     *
     * @return lloc actual.
     */
    Lloc getCurrentLloc();

    /**
     * Retorna la llista completa de llocs.
     * TODO: Això s'ha de implementar a la base de dades.
     *
     * @return llista completa de llocs.
     */
    List<Lloc> getLlocs();


    /**
     * Retorna la llista completa de categories.
     * TODO: Això s'ha de implementar a la base de dades.
     *
     * @return llista completa de categories.
     */
    List<Categoria> getCategories();

    /**
     * Estableix la llista de llocs filtrada.
     * TODO: Això s'ha de implementar a la base de dades.
     *
     * @param filteredLlocs llista de llocs filtrada.
     */
    void setFilteredLlocs(List<Lloc> filteredLlocs);

    /**
     * Retorna la llista de llocs filtrada.
     * TODO: Això s'ha de implementar a la base de dades.
     *
     * @return llista de llocs filtrada.
     */
    List<Lloc> getFilteredLlocs();

    LatLng getLastKnownPosition();
}
