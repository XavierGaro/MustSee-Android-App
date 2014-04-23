package ioc.mustsee.parser;

public interface DownloadManager {

    /**
     * Es crida al començar i finalitzar una descarrega.
     *
     * @param descarrega cert al iniciar la descarrega i false al finalitzar.
     * @see DownloadImageAsyncTask
     */
    void descarregaEnCurs(boolean descarrega);
}
