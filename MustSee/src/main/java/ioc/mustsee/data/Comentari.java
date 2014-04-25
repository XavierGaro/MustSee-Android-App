package ioc.mustsee.data;

public class Comentari {

    public static int sIdCounter = 10000; // TODO: contador por defecto

    public final int id;
    public final String text;
    public final int llocId;
    public final int usuariId;
    public final String nomUsuari;
    public final String data;

    public Comentari(int id, String text, int usuariId, String nomUsuari, String data, int llocId) {
        this.id = id;
        this.text = text;
        this.usuariId = usuariId;
        this.llocId = llocId;
        this.nomUsuari = nomUsuari;
        this.data = data;
    }

    public Comentari(String text, int usuariId, String nomUsuari, String data, int llocId) {
        this(sIdCounter++, text, usuariId, nomUsuari, data, llocId);
    }

}
