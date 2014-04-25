package ioc.mustsee.downloaders;

/**
 * Aquesta interfície la implementen objectes que porten el control del nombre de descarregues en
 * curs.
 */
public interface DownloadManager {

    /**
     * Es crida al començar i finalitzar una descarrega.
     *
     * @param descarrega cert al iniciar la descarrega i false al finalitzar.
     * @see DownloadImageAsyncTask
     */
    void donwloadInProgress(boolean descarrega);
}
