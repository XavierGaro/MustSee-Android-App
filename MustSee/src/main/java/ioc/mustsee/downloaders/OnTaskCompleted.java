package ioc.mustsee.downloaders;

import java.util.List;

/**
 * Aquesta interfície es fa servir per comunicar-se amb tasques asíncrones que retornaran una
 * llista com a resultat
 */
public interface OnTaskCompleted {

    /**
     * Mètode que es cridat amb el resultat quan la tasca es completa.
     *
     * @param result llista amb el resultat
     */
    public void onTaskCompleted(List result);
}
