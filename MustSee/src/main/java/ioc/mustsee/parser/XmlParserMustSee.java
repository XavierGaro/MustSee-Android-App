package ioc.mustsee.parser;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Comentari;
import ioc.mustsee.data.Imatge;
import ioc.mustsee.data.Lloc;

/**
 * Aquest es un parser concret per analitzar la informació dels arxius XML obtinguts del web service
 * de la aplicació MustSee.
 *
 * @param <T> tipus dels objectes que formaran la llista a retornar
 * @author Xavier García
 */
public class XmlParserMustSee<T> extends XmlParser<T> {
    private static final String TAG = "XmlParserMustSee";

    public XmlParserMustSee(String root) {
        super(root);
    }

    /**
     * Aquest mètode inicial la lectura d'un flux de dades i els afegeix a la llista. El tipus
     * d'objectes poden ser Llocs, Categories, Imatges o booleans amb el resultat de una acció
     * com autenticar-se o enviar un comentari.
     *
     * @return Llista amb els objectes trobats al flux de dades
     * @throws XmlPullParserException si hi ha cap problema al analitzar les dades
     * @throws IOException            si hi ha cap problema al llegir el flux de dades
     */
    @SuppressWarnings("unchecked")
    @Override
    List<T> read() throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        // Comencem l'anàlisis al element arrel
        mParser.require(XmlPullParser.START_TAG, NAMESPACE, mRoot);

        // Recorrem tots els nodes fins arribar al final
        while (mParser.next() != XmlPullParser.END_TAG) {

            // Si no es troba una etiqueta de obertura continuem
            if (mParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            // Obtenim el nom de la etiqueta
            String name = mParser.getName();


            // Segons el nom, cridem un mètode per generar l'objecte adequat i el afegim a la llista
            if (name.equals("lloc")) {
                entries.add(readLloc());

            } else if (name.equals("categoria")) {
                entries.add(readCategoria());

            } else if (name.equals("imatges")) {
                entries.add(readImatge());

            } else if (name.equals("status")) {
                if (readString("status").equals("OK")) {
                    entries.add(true);
                }

            } else if (name.equals("comentari")) {
                entries.add(readComentari());

            } else {
                // Si no es cap dels que ens interessa, el saltem
                skip();
            }
        }
        // Ens assegurem que retornem una llista del tipus adequat a aquest parser
        return (List<T>) entries;
    }

    /**
     * Llegeix les dades de la etiqueta actual i crea un lloc a partir de elles.
     *
     * @return lloc generat a partir de les dades
     * @throws XmlPullParserException si hi ha cap problema al analitzar les dades
     * @throws IOException            si hi ha cap problema al llegir el flux de dades
     */
    @SuppressWarnings("ConstantConditions")
    private Lloc readLloc() throws XmlPullParserException, IOException {
        mParser.require(XmlPullParser.START_TAG, NAMESPACE, "lloc");

        // Inicalitzem les dades
        int id = -1, categoria = -1;
        String nom = null, descripcio = null;
        float latitud = -1f, longitud = -1f;
        List<Imatge> imatges = new ArrayList<Imatge>();
        List<Comentari> comentaris = new ArrayList<Comentari>();

        // Recorrem els nodes
        while (mParser.next() != XmlPullParser.END_TAG) {
            if (mParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = mParser.getName();
            if (name.equals("id")) {
                id = readInt("id");

            } else if (name.equals("nom")) {
                nom = readString("nom");

            } else if (name.equals("descripcio")) {
                descripcio = readString("descripcio");

            } else if (name.equals("latitud")) {
                latitud = readFloat("latitud");

            } else if (name.equals("longitud")) {
                longitud = readFloat("longitud");

            } else if (name.equals("categoriaid")) {
                categoria = readInt("categoriaid");

            } else if (name.equals("imatges")) {
                imatges = readImatges();

            } else if (name.equals("comentaris")) {
                comentaris = readComentaris();

            } else {
                skip();
            }
        }

        // Creem el Lloc
        Lloc lloc = new Lloc.LlocBuilder(nom, new LatLng(latitud, longitud))
                .id(id)
                .descripcio(descripcio)
                .categoria(categoria)
                .build();

        lloc.addImatges(imatges);
        lloc.addComentaris(comentaris);

        return lloc;
    }

    /**
     * Llegeix les dades de la etiqueta actual i crea una categoria a partir de elles.
     *
     * @return categoria generada a partir de les dades
     * @throws XmlPullParserException si hi ha cap problema al analitzar les dades
     * @throws IOException            si hi ha cap problema al llegir el flux de dades
     */
    private Categoria readCategoria() throws XmlPullParserException, IOException {
        mParser.require(XmlPullParser.START_TAG, NAMESPACE, "categoria");

        // Inicalitzem les dades
        int id = -1;
        String nom = null;

        // Recorrem els nodes
        while (mParser.next() != XmlPullParser.END_TAG) {
            if (mParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = mParser.getName();
            if (name.equals("id")) {
                id = readInt("id");

            } else if (name.equals("descripcio")) {
                nom = readString("descripcio");

            } else {
                skip();
            }
        }

        // Creem la categoria i la retornem
        return new Categoria(id, nom, null);
    }

    /**
     * Llegeix una llista d'imatges del parser.
     *
     * @return llista amb les imatges obtingudes de les dades
     * @throws XmlPullParserException si hi ha cap problema al analitzar les dades
     * @throws IOException            si hi ha cap problema al llegir el flux de dades
     */
    private List<Imatge> readImatges() throws XmlPullParserException, IOException {
        List<Imatge> imatges = new ArrayList<Imatge>();
        mParser.require(XmlPullParser.START_TAG, NAMESPACE, "imatges");

        // Recorrem els nodes
        while (mParser.next() != XmlPullParser.END_TAG) {
            if (mParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = mParser.getName();
            if (name.equals("imatge")) {
                imatges.add(readImatge());

            } else {
                skip();
            }
        }

        return imatges;
    }

    /**
     * Obté una imatge a partir de les dades del parser.
     *
     * @return Imatge obtinguda a partir de les dades
     * @throws XmlPullParserException si hi ha cap problema al analitzar les dades
     * @throws IOException            si hi ha cap problema al llegir el flux de dades
     */
    private Imatge readImatge() throws XmlPullParserException, IOException {
        mParser.require(XmlPullParser.START_TAG, NAMESPACE, "imatge");

        // Inicalitzem les dades
        int id = -1, llocId = -1;
        String titol = null, url = null;

        // Recorrem els nodes
        while (mParser.next() != XmlPullParser.END_TAG) {
            if (mParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = mParser.getName();
            if (name.equals("id")) {
                id = readInt("id");

            } else if (name.equals("llocid")) {
                llocId = readInt("llocid");

            } else if (name.equals("titol")) {
                titol = readString("titol");

            } else if (name.equals("url")) {
                url = readString(url);

            } else {
                skip();
            }
        }

        return new Imatge(id, titol, url, llocId);
    }

    /**
     * Obté una llista de comentaris a partir de les dades del parser.
     *
     * @return Llista dels comentaris obtinguts del parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List<Comentari> readComentaris() throws XmlPullParserException, IOException {
        List<Comentari> comentaris = new ArrayList<Comentari>();
        mParser.require(XmlPullParser.START_TAG, NAMESPACE, "comentaris");

        // Recorrem els nodes
        while (mParser.next() != XmlPullParser.END_TAG) {
            if (mParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = mParser.getName();
            if (name.equals("comentari")) {
                comentaris.add(readComentari());

            } else {
                skip();
            }
        }

        return comentaris;
    }

    /**
     * Obté un comentari a partir de les dades del parser.
     *
     * @return Comentari obtingut del parser
     * @throws XmlPullParserException si hi ha cap problema al analitzar les dades
     * @throws IOException            si hi ha cap problema al llegir el flux de dades
     */
    private Comentari readComentari() throws XmlPullParserException, IOException {
        mParser.require(XmlPullParser.START_TAG, NAMESPACE, "comentari");

        // Inicalitzem les dades
        int id = -1, usuariId = -1, llocId = -1;
        String text = null, nomUsuari = null, data = null;

        // Recorrem els nodes
        while (mParser.next() != XmlPullParser.END_TAG) {
            if (mParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = mParser.getName();
            if (name.equals("id")) {
                id = readInt("id");

            } else if (name.equals("usuariid")) {
                usuariId = readInt("usuariid");

            } else if (name.equals("llocid")) {
                llocId = readInt("llocid");

            } else if (name.equals("text")) {
                text = readString("text");

            } else if (name.equals("nomusuari")) {
                nomUsuari = readString("nomusuari");

            } else if (name.equals("data")) {
                data = readString("data");

            } else {
                skip();
            }
        }

        // Construeix el comentari i el retorna
        return new Comentari(id, text, usuariId, nomUsuari, data, llocId);
    }
}
