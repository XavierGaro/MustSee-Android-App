package ioc.mustsee.data;

public class Comentari {

    public static int sIdCounter = 10000; // TODO: contador por defecto

    public final int id;
    public final String text;
    public final int llocId;
    public final int usuariId;
    public final String nomUsuari;

    public Comentari(int id, String text, int usuariId, String nomUsuari, int llocId) {
        this.id = id;
        this.text = text;
        this.usuariId = usuariId;
        this.llocId = llocId;
        this.nomUsuari = nomUsuari;
    }

    public Comentari(String text, int usuariId, String nomUsuari, int llocId) {
        this(sIdCounter++, text, usuariId, nomUsuari, llocId);
    }

}
