package ioc.mustsee.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Classe abstracta per analitzar el contingut de InputStreams amb dades en XML. Aquesta classe
 * conté els mètodes generics que poden ser utilitzats pels parsers concrets.
 *
 * @param <T> tipus de la llista en la que es retornarà el resultat.
 */
public abstract class XmlParser<T> {
    static final String TAG = "XmlParser";

    static final String NAMESPACE = null;

    final String mRoot;
    XmlPullParser mParser;

    /**
     * Al constructor es defineix el nom del element arrel per analitzar.
     *
     * @param root nom del node arrel
     */
    public XmlParser(String root) {
        this.mRoot = root;
    }

    /**
     * Aquest mètode inicia l'anàlisis del flux de dades i prepara el flux per ser llegit.
     *
     * @param in flux de dades per analitzar
     * @return llista del tipus especificat al crear el parser
     * @throws XmlPullParserException si hi ha cap problema al analitzar les dades
     * @throws IOException            si hi ha cap problema al llegir el flux de dades
     */
    public List<T> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            mParser = Xml.newPullParser();
            mParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            mParser.setInput(in, null);
            mParser.nextTag();
            return read();
        } finally {
            in.close();
        }
    }

    /**
     * Aquest es el mètode inicial on es llegeix i analitza el flux de dades, i on s'han de crear
     * els elements per omplir la llista que es retornarà com a resultat.
     *
     * @return llista amb les dades llegides
     * @throws XmlPullParserException si hi ha cap problema al analitzar les dades
     * @throws IOException            si hi ha cap problema al llegir el flux de dades
     */
    abstract List<T> read() throws XmlPullParserException, IOException;

    /**
     * Llegeix un valor i el retorna com a int.
     *
     * @param node nom del node
     * @return el valor llegit com a enter
     * @throws XmlPullParserException si hi ha cap problema al analitzar les dades
     * @throws IOException            si hi ha cap problema al llegir el flux de dades
     */
    int readInt(String node) throws IOException, XmlPullParserException {
        mParser.require(XmlPullParser.START_TAG, NAMESPACE, node);
        int i = Integer.parseInt(readText());
        mParser.require(XmlPullParser.END_TAG, NAMESPACE, node);
        return i;
    }

    /**
     * Llegeix un valor i el retorna com a string.
     *
     * @param node nom del node
     * @return el valor llegit com a string
     * @throws XmlPullParserException si hi ha cap problema al analitzar les dades
     * @throws IOException            si hi ha cap problema al llegir el flux de dades
     */
    String readString(String node) throws IOException, XmlPullParserException {
        mParser.require(XmlPullParser.START_TAG, NAMESPACE, node);
        String s = readText();
        mParser.require(XmlPullParser.END_TAG, NAMESPACE, node);
        return s;
    }

    /**
     * Llegeix un valor i el retorna com a float.
     *
     * @param node nom del node
     * @return el valor llegit com a float
     * @throws XmlPullParserException si hi ha cap problema al analitzar les dades
     * @throws IOException            si hi ha cap problema al llegir el flux de dades
     */

    float readFloat(String node) throws IOException, XmlPullParserException {
        mParser.require(XmlPullParser.START_TAG, NAMESPACE, node);
        float f = Float.parseFloat(readText());
        mParser.require(XmlPullParser.END_TAG, NAMESPACE, node);
        return f;
    }

    /**
     * Llegeix el text del node actual
     *
     * @return cadena amb el contingut textual del node actual
     * @throws XmlPullParserException si hi ha cap problema al analitzar les dades
     * @throws IOException            si hi ha cap problema al llegir el flux de dades
     */
    String readText() throws IOException, XmlPullParserException {
        String result = "";
        if (mParser.next() == XmlPullParser.TEXT) {
            result = mParser.getText();
            mParser.nextTag();
        }
        return result;
    }

    /**
     * Es salta el contingut del node actual
     *
     * @throws XmlPullParserException si hi ha cap problema al analitzar les dades
     * @throws IOException            si hi ha cap problema al llegir el flux de dades
     */
    void skip() throws XmlPullParserException, IOException {
        if (mParser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (mParser.next()) {
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
