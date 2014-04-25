package ioc.mustsee.parser;

import android.util.Log;
import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Comentari;
import ioc.mustsee.data.Imatge;
import ioc.mustsee.data.Lloc;

public class MustSeeXMLParser {
    private static final String TAG = "MustSeeXMLParser";
    private static final String NAMESPACE = null;

    private final String mRoot;

    public MustSeeXMLParser(String root) {
        this.mRoot = root;
    }

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readRoot(parser, mRoot);
        } finally {
            in.close();
        }
    }

    private List readRoot(XmlPullParser parser, String node) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, NAMESPACE, node); // Elemento raíz
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            Log.d(TAG, "Name a parsear: "+name);
            // Starts by looking for the entry tag
            if (name.equals("lloc")) { // Elemento que buscamos
                entries.add(readLloc(parser));
                Log.d(TAG, "Lloc afegit");
            } else if (name.equals("categoria")) {
                entries.add(readCategoria(parser));
                Log.d(TAG, "Categoria afegida");
            } else if (name.equals("imatges")) {
                entries.add(readImatge(parser));
                Log.d(TAG, "Imatge afegida");
            } else if (name.equals("auth")) {
                entries.add(readAuth(parser));
                Log.d(TAG, "Comprovant auth");
            } else if (name.equals("comentari")) {
                entries.add(readComentari(parser));
                Log.d(TAG, "Comentari llegit");
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private boolean readAuth(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "auth");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Si es troba alguna etiqueta d'error, no s'ha autenticat correctament
            if (name.equals("error")) {
                return false;
            } else {
                skip(parser);
            }
        }

        return true;
    }

    private Lloc readLloc(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "lloc");
        int id = -1, categoria = -1;
        String nom = null, descripcio = null;
        float latitud = -1f, longitud = -1f;
        List<Imatge> imatges = new ArrayList<Imatge>();
        List<Comentari> comentaris = new ArrayList<Comentari>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("id")) {
                id = readInt(parser, "id");
            } else if (name.equals("nom")) {
                nom = readString(parser, "nom");
            } else if (name.equals("descripcio")) {
                descripcio = readString(parser, "descripcio");
            } else if (name.equals("latitud")) {
                latitud = readFloat(parser, "latitud");
            } else if (name.equals("longitud")) {
                longitud = readFloat(parser, "longitud");
            } else if (name.equals("categoriaid")) {
                categoria = readInt(parser, "categoriaid");
            } else if (name.equals("imatges")) {
                imatges = readImatges(parser);
            } else if (name.equals("comentaris")) {
                comentaris = readComentaris(parser);
            } else {
                skip(parser);
            }
        }

        Lloc lloc = new Lloc.LlocBuilder(nom, new LatLng(latitud, longitud))
                .id(id)
                .descripcio(descripcio)
                .categoria(categoria)
                .build();

        lloc.addImatges(imatges);
        lloc.addComentaris(comentaris);

        return lloc;
    }

    private Categoria readCategoria(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "categoria");
        int id = -1;
        String nom = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("id")) {
                id = readInt(parser, "id");
            } else if (name.equals("descripcio")) {
                nom = readString(parser, "descripcio");
            } else {
                skip(parser);
            }
        }

        return new Categoria(id, nom, null);
    }

    private List<Imatge> readImatges(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Imatge> entries = new ArrayList<Imatge>();
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "imatges"); // Elemento raíz
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("imatge")) { // Elemento que buscamos
                entries.add(readImatge(parser));
                Log.d(TAG, "Imatge Afegida");
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private Imatge readImatge(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "imatge");
        int id = -1, llocId = -1;
        String titol = null, url = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("id")) {
                id = readInt(parser, "id");
            } else if (name.equals("llocid")) {
                llocId = readInt(parser, "llocid");
            } else if (name.equals("titol")) {
                titol = readString(parser, "titol");
            } else if (name.equals("url")) {
                url = readString(parser, url);
            } else {
                skip(parser);
            }
        }
        return new Imatge(id, titol, url, llocId);
    }

    private List<Comentari> readComentaris(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d(TAG, "Llegint comentaris....");

        List<Comentari> entries = new ArrayList<Comentari>();
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "comentaris"); // Elemento raíz
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("comentari")) { // Elemento que buscamos
                entries.add(readComentari(parser));
                Log.d(TAG, "Comentari Afegit");
            } else {
                skip(parser);
            }
        }

        for (Comentari comentari : entries) {
            Log.d(TAG, comentari.text + " per " + comentari.nomUsuari);
        }

        return entries;
    }

    private Comentari readComentari(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "comentari");
        int id = -1, usuariId = -1, llocId = -1;
        String text = null, nomUsuari = null, data = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("id")) {
                id = readInt(parser, "id");
            } else if (name.equals("usuariid")) {
                usuariId = readInt(parser, "usuariid");
            } else if (name.equals("llocid")) {
                llocId = readInt(parser, "llocid");
            } else if (name.equals("text")) {
                text = readString(parser, "text");
            } else if (name.equals("nomusuari")) {
                nomUsuari = readString(parser, "nomusuari");
            } else if (name.equals("data")) {
                data = readString(parser, "data");
            } else {
                skip(parser);
            }
        }
        return new Comentari(id, text, usuariId, nomUsuari, data, llocId);
    }


    // Processes id (int Node)
    private int readInt(XmlPullParser parser, String node) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, node);
        int i = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, NAMESPACE, node);
        return i;
    }

    // Process nom (String Node)
    // Processes title tags in the feed.
    private String readString(XmlPullParser parser, String node) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, node);
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG, NAMESPACE, node);
        return s;
    }

    private float readFloat(XmlPullParser parser, String node) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, node);
        float f = Float.parseFloat(readText(parser));
        parser.require(XmlPullParser.END_TAG, NAMESPACE, node);
        return f;
    }


    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }


    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
