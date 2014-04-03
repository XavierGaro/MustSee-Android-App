package ioc.mustsee.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ioc.mustsee.R;
import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Imatge;
import ioc.mustsee.data.Lloc;

/**
 * Classe per gestionar la base de dades.
 *
 * @author Javier García
 */
public class DBMustSee {
    public static final String TAG = "DBMustSee";

    // Base de dades y taules
    public static final String BD_NOM = "DBMustSee";
    public static final String BD_TAULA_LLOCS = "llocs";
    public static final String BD_TAULA_CATEGORIES = "categories";
    public static final String BD_TAULA_IMATGES = "imatges";
    public static final int VERSIO = 1;

    // Camps de la base de dades
    public static final String CLAU_ID = "_id";
    public static final String CLAU_NOM = "nom";
    public static final String CLAU_DESCRIPCIO = "descripcio";
    public static final String CLAU_LATITUD = "latitud";
    public static final String CLAU_LONGITUD = "longitud";
    public static final String CLAU_CATEGORIA = "categoria";
    public static final String CLAU_ICON_RESOURCE = "icon";
    public static final String CLAU_FILE = "fitxer";
    public static final String CLAU_ID_LLOC = "_id_lloc";

    // Consulta per crear les taules
    public static final String BD_CREATE_LLOCS = "CREATE TABLE " + BD_TAULA_LLOCS + "("
            + CLAU_ID + " INTEGER PRIMARY KEY, " + CLAU_NOM + " TEXT NOT NULL, " + CLAU_DESCRIPCIO
            + " TEXT NOT NULL, " + CLAU_LATITUD + " REAL NOT NULL, " + CLAU_LONGITUD
            + " REAL NOT NULL, " + CLAU_CATEGORIA + " INT NOT NULL, " + CLAU_ICON_RESOURCE
            + " INT NOT NULL)";

    public static final String BD_CREATE_CATEGORIES = "CREATE TABLE " + BD_TAULA_CATEGORIES + "("
            + CLAU_ID + " INTEGER PRIMARY KEY, " + CLAU_NOM + " TEXT NOT NULL, " + CLAU_DESCRIPCIO
            + " TEXT NOT NULL)";

    public static final String BD_CREATE_IMATGES = "CREATE TABLE " + BD_TAULA_IMATGES + "("
            + CLAU_ID + " INTEGER PRIMARY KEY, " + CLAU_NOM + " TEXT NOT NULL, " + CLAU_FILE
            + " TEXT NOT NULL, " + CLAU_ID_LLOC + " INTEGER)";

    // Array amb tots els camps per facilitar la creació de consultes
    private String[] mColumnsLlocs = new String[]{CLAU_ID, CLAU_NOM, CLAU_DESCRIPCIO, CLAU_LATITUD,
            CLAU_LONGITUD, CLAU_CATEGORIA, CLAU_ICON_RESOURCE};
    private String[] mColumnsCategories = new String[]{CLAU_ID, CLAU_NOM, CLAU_DESCRIPCIO};
    private String[] mColumnsImatges = new String[]{CLAU_ID, CLAU_NOM, CLAU_FILE, CLAU_ID_LLOC};

    private DataBaseHelper mDataBaseHelper;
    private SQLiteDatabase mDB;
    private Context mContext;

    private static boolean initializeDB;

    /**
     * El constructor requereix passar el context de la activitat i s'instancia l'ajudant.
     *
     * @param context context de la activitat.
     */
    public DBMustSee(Context context) {
        this.mContext = context;
        mDataBaseHelper = new DataBaseHelper(context, this);
    }

    /**
     * Obre la base de dades per poder realitzar consultes.
     *
     * @return aquest mateix objecte.
     * @throws SQLException si hi ha cap error amb la base de dades.
     */
    public DBMustSee open() throws SQLException {
        mDB = mDataBaseHelper.getWritableDatabase();
        if (initializeDB) {
            initImatges();
            initCategories();
            initLlocs();
        }
        return this;
    }

    /**
     * Tanca la base de dades. Si hi ha cap error o mostra al log i continua.
     */
    public void close() {
        try {
            mDataBaseHelper.close();
        } catch (SQLException e) {
            // Si hi ha un error al tancar la base de dades només el mostrem al log.
            Log.e(TAG, mContext.getResources().getString(R.string.error_db), e);
        }
    }

    /**
     * Insereix un lloc a la base de dades
     *
     * @param lloc lloc a inserir
     * @return aquest mateix objecte.
     * @throws SQLException si hi ha cap error amb la base de dades.
     */
    public DBMustSee insertLloc(Lloc lloc) throws SQLException {
        // Preparem els valors per inserir
        ContentValues initialValues = new ContentValues();
        initialValues.put(CLAU_ID, lloc.id);
        initialValues.put(CLAU_NOM, lloc.nom);
        initialValues.put(CLAU_DESCRIPCIO, lloc.descripcio);
        initialValues.put(CLAU_LATITUD, lloc.posicio.latitude);
        initialValues.put(CLAU_LONGITUD, lloc.posicio.longitude);
        initialValues.put(CLAU_CATEGORIA, lloc.categoriaId);
        initialValues.put(CLAU_ICON_RESOURCE, lloc.iconResource);

        // Inserim el registre
        mDB.insert(BD_TAULA_LLOCS, null, initialValues);
        return this;
    }

    /**
     * Retorna el lloc emmagatzemat a la base de dades amb la id passada com argument.
     *
     * @param id id del lloc.
     * @return lloc al que correspon la id o null si no existeix.
     * @throws SQLException si hi ha cap error amb la base de dades.
     */
    public Lloc getLloc(int id) throws SQLException {
        Lloc lloc = null;

        Cursor cursor = mDB.query(true, BD_TAULA_LLOCS, mColumnsLlocs, CLAU_ID + " = " + id, null,
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            lloc = cursorToLloc(cursor);
        }
        return lloc;
    }

    /**
     * Retorna una llista amb tots els llocs emamgatzemats a la base de dades amb totes les imatges
     * corresponents afegides.
     *
     * @return llista completa de llocs emmagatzemats a la base de dades o buida si no hi ha cap.
     * @throws SQLException si hi ha cap error amb la base de dades.
     */
    public List<Lloc> getLlocs() throws SQLException {
        List<Lloc> llocs = new ArrayList<Lloc>();
        Cursor mCursor = mDB.query(BD_TAULA_LLOCS, mColumnsLlocs, null, null, null, null, null);

        // Recorrem el cursor i els afegim a la llista
        if (mCursor.moveToFirst()) {
            do {
                llocs.add(cursorToLloc(mCursor));
            } while (mCursor.moveToNext());
        }
        return llocs;
    }

    /**
     * Extreu la informació del cursor passat com argument i crea un lloc amb aquesta informació.
     *
     * @param cursor amb la informació del lloc.
     * @return Lloc creat.
     */
    private Lloc cursorToLloc(Cursor cursor) {
        Lloc lloc = null;

        try {
            lloc = new Lloc.LlocBuilder(cursor.getString(1), cursor.getFloat(3), cursor.getFloat(4))
                    .id(cursor.getInt(0))
                    .descripcio(cursor.getString(2))
                    .categoria(cursor.getInt(5))
                    .icon(cursor.getInt(6))
                    .build();

            // Afegim la llista de imatges
            lloc.addImatges(getImatgesFromLloc(lloc));

        } catch (Exception e) {
            // Si hi ha un error al obtenir les dades del cursor enregistrem l'error al log
            Log.e(TAG, mContext.getResources().getString(R.string.error_db), e);
        }
        return lloc;
    }

    /**
     * Insereix la categoria passada com argument a la base de dades.
     *
     * @param categoria a inserir
     * @return aquest mateix objecte.
     * @throws SQLException si hi ha cap error amb la base de dades.
     */
    public DBMustSee insertCategoria(Categoria categoria) throws SQLException {
        // Preparem els valors per inserir
        ContentValues initialValues = new ContentValues();
        initialValues.put(CLAU_ID, categoria.id);
        initialValues.put(CLAU_NOM, categoria.nom);
        initialValues.put(CLAU_DESCRIPCIO, categoria.descripcio);

        // Inserim el registre
        mDB.insert(BD_TAULA_CATEGORIES, null, initialValues);
        return this;
    }

    /**
     * Obté la categoria amb la id passada per argument de la base de dades.
     *
     * @param id id de la categoria que volem obtenir.
     * @return la categoria corresponent a la id passada o null si no s'ha trobat.
     * @throws SQLException si hi ha cap error amb la base de dades.
     */
    public Categoria getCategoria(int id) throws SQLException {
        Categoria categoria = null;

        Cursor cursor = mDB.query(true, BD_TAULA_CATEGORIES, mColumnsCategories, CLAU_ID + " = "
                + id, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            categoria = cursorToCategoria(cursor);
        }
        return categoria;
    }

    /**
     * Retorna una llista amb totes les categories emmagatzemades a la base de dades.
     *
     * @return llista amb totes les categories o buida si no s'ha trobat cap.
     * @throws SQLException si hi ha cap error amb la base de dades.
     */
    public List<Categoria> getCategories() throws SQLException {
        List<Categoria> categories = new ArrayList<Categoria>();
        Cursor mCursor = mDB.query(BD_TAULA_CATEGORIES, mColumnsCategories, null, null, null, null, null);

        // Recorrem el cursor i els afegim a la llista
        if (mCursor.moveToFirst()) {
            do {
                categories.add(cursorToCategoria(mCursor));
            } while (mCursor.moveToNext());
        }
        return categories;
    }

    /**
     * Crea una categoria a partir de les dades del cursor passat com argument.
     *
     * @param cursor cursor per extreure la informació.
     * @return categoria creada a partir del cursor.
     */
    private Categoria cursorToCategoria(Cursor cursor) {
        Categoria categoria = null;

        try {
            categoria = new Categoria(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
        } catch (Exception e) {
            // Si hi ha un error al obtenir les dades del cursor enregistrem l'error al log
            Log.e(TAG, mContext.getResources().getString(R.string.error_db), e);
        }
        return categoria;
    }

    /**
     * Insereix la informació d'una imatge a la base de dades
     *
     * @param imatge imatge a emmagatzemar.
     * @return aquest mateix objecte.
     * @throws SQLException si hi ha cap error amb la base de dades.
     */
    public DBMustSee insertImatge(Imatge imatge) throws SQLException {
        // Preparem els valors per inserir
        ContentValues initialValues = new ContentValues();
        initialValues.put(CLAU_ID, imatge.id);
        initialValues.put(CLAU_NOM, imatge.titol);
        initialValues.put(CLAU_FILE, imatge.nomFitxer);
        initialValues.put(CLAU_ID_LLOC, imatge.llocId);

        // Inserim el registre
        mDB.insert(BD_TAULA_IMATGES, null, initialValues);
        return this;
    }

    /**
     * Obté la llista de totes les imatges corresponents al lloc passat com argument.
     *
     * @param lloc lloc del que volem obtenir la llista d'imatges.
     * @return llista de imatges corresponent.
     */
    public List<Imatge> getImatgesFromLloc(Lloc lloc) {
        List<Imatge> imatges = new ArrayList<Imatge>();
        Cursor cursor = mDB.query(true, BD_TAULA_IMATGES, mColumnsImatges, CLAU_ID_LLOC + " = "
                + lloc.id, null, null, null, null, null);

        // Recorrem el cursor i els afegim a la llista
        if (cursor.moveToFirst()) {
            do {
                imatges.add(cursorToImatge(cursor));
            } while (cursor.moveToNext());
        }
        Log.d(TAG, "Devueltas " + imatges.size() + " imatges");
        return imatges;
    }

    /**
     * Crea una imatge a partir de la informació del cursor passat com argument.
     *
     * @param cursor amb la informació de la imatge.
     * @return imatge creada a partir del cursor.
     */
    private Imatge cursorToImatge(Cursor cursor) {
        Imatge imatge = null;

        try {
            imatge = new Imatge(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
        } catch (Exception e) {
            // Si hi ha un error al obtenir les dades del cursor enregistrem l'error al log
            Log.e(TAG, mContext.getResources().getString(R.string.error_db), e);
        }
        return imatge;
    }

    /**
     * Classe d'ajuda per gestionar la basse de dades
     */
    private static class DataBaseHelper extends SQLiteOpenHelper {
        Context mContext;
        DBMustSee database; // Això es necessari per poder reiniciar les dades de prova

        DataBaseHelper(Context context, DBMustSee database) {
            super(context, BD_NOM, null, VERSIO);
            this.mContext = context;
            this.database = database;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                // Creem les taules i afegim les dades de prova després de crear la base e dades
                //db.execSQL(BD_CREATE_LLOCS);
                //db.execSQL(BD_CREATE_CATEGORIES);
                //db.execSQL(BD_CREATE_IMATGES);

                initializeDB = true;

            } catch (SQLException e) {
                Log.e(TAG, mContext.getResources().getString(R.string.error_db), e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int versioAntiga,
                              int versioNova) {
            Log.w(TAG, R.string.update_from + " " + versioAntiga + " " + R.string.update_to + ".");
            // Eliminem totes les taules
            db.execSQL("DROP TABLE IF EXISTS " + BD_TAULA_LLOCS);

            // Recreem la base de dades
            onCreate(db);
        }
    }

    /**
     * TODO: Mètode per realitzar proves, moure al packet de tests
     */
    public void initCategories() {
        mDB.execSQL("DROP TABLE IF EXISTS " + BD_TAULA_CATEGORIES);
        mDB.execSQL(BD_CREATE_CATEGORIES);

        List<Categoria> mCategories = new ArrayList<Categoria>();

        Categoria tot = new Categoria(0, "Seleccionar Todo", "Selecciona todas las categorías.");
        Categoria platjes = new Categoria(1, "Playas", "En esta categoría hay playas.");
        Categoria poi = new Categoria(2, "Puntos de interes", "En esta categoría hay puntos de interes.");
        Categoria museus = new Categoria(3, "Museos", "En esta categoría hay museos.");
        Categoria buida = new Categoria(4, "Vacía", "En esta categoría no hay nada.");

        mCategories.add(tot);
        mCategories.add(platjes);
        mCategories.add(poi);
        mCategories.add(museus);
        mCategories.add(buida);

        // Recorrem la llista de llocs i els afegim a la base de dades
        try {
            for (Categoria categoria : mCategories) {
                insertCategoria(categoria);
            }
        } catch (SQLException e) {
            // Si trobem cap error ho mostrem al log
            Log.e(TAG, mContext.getResources().getString(R.string.error_db), e);
        }
    }

    /**
     * TODO: Mètode per realitzar proves, moure al packet de tests
     */
    public void initLlocs() {
        mDB.execSQL("DROP TABLE IF EXISTS " + BD_TAULA_LLOCS);
        mDB.execSQL(BD_CREATE_LLOCS);

        List<Lloc> mLlocs = new ArrayList<Lloc>();
        // Crea lloc a partir de array Obj[]
        Object[][] objs = {
                {"Playa de Punta Prima", 1, 37.94017f, -0.711672f, "Esta es la playa de Punta Prima. Distintivo Q de calidad turística. Bandera azul."},
                {"Cala Mosca", 1, 37.932554f, -0.718925f, "Esta es la playa de Cala Mosca."},
                {"Playa Mil Palmeras", 1, 37.885557f, -0.752352f, "Esta es la playa Mil Palmeras. Distintivo Q de calidad turística."},
                {"Teatro Circo", 2, 38.085333f, -0.943217f, "En su origen fue una construcción de las llamadas \"semipermanentes\" dedicada en principio a espectáculos circenses, acrobáticos, boxeo, etc."},
                {"La Lonja", 2, 38.082335f, -0.947454f, "Debido a la construcción de una nueva lonja en el Polígono Industrial Puente Alto, la antigua edificación quedo en desuso, y en la actualidad se ha reformado para destinarla al actual Conservatorio de Música y Auditorio. Inagurado el 24 de octubre de 2008, como \"Conservatorio Pedro Terol\"."},
                {"Museo Arqueológico Comarcal de Orihuela", 3, 38.086431f, -0.950500f, "Ubicado en parte de las antiguas dependencias del Hospital San Juan de Dios (Hospital Municipal), restauradas en 1997. Ocupa la antigua iglesia de estilo barroco de planta en cruz latina."},
                {"Casa Museo Miguel Hernandez", 3, 38.089488f, -0.942205f, "Situada en la Calle de Arriba, próxima al Colegio de Santo Domingo, en el “Rincón Hernandiano”. Sus dependencias son las típicas de una casa con explotación ganadera de principios de siglo pasado, cuenta además con un pequeño huerto situado junto a la sierra."},
                {"Museo de la Reconquista", 3, 38.08714f, -0.950126f, "El Museo de la Reconquista fue creado en 1.985, por la Asociación de Fiestas de Moros y Cristianos \"Santas Justa y Rufina\", con sede en los bajos del Palacio de Rubalcava, con el objeto de conservar y mostrar al público trajes festeros de las distintas comparsas, carteles de la fiesta, fotografías, armamento, instrumentos musicales y demás objetos relacionados con la fiesta.\n" +
                        "En 1995 un equipo de trabajo dirigido por José Luis LOPEZ IBAÑEZ, redactó un interesante \"Proyecto para rehabilitación del Museo Festero de la Reconquista\" que no llegó a ejecutarse.\n" +
                        "Al trasladarse el 3 de julio de 2.003, la Asociación de Fiestas a su sede actual, su presidente encargó a D. Emilio DIZ la redacción de un nuevo proyecto museográfico, inaugurado en 2006. Consta de dos espacios principales, dedicados respectivamente al bando moro y al bando cristiano. En ellos se pasa revista a distintos aspectos relacionados con la fiesta como son la historia, la leyenda, las comparsas, los cargos festeros, la música, etc." +
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus interdum sem non odio posuere, vitae facilisis nisi posuere. Curabitur gravida imperdiet eros, in congue sem egestas fermentum. In mauris lectus, placerat nec neque vitae, pharetra consequat sapien. Nullam eget ullamcorper est, non molestie nisl. Nunc non pulvinar ante. Integer quis lacus faucibus, venenatis nibh ut, iaculis elit. Ut vulputate, ante sed semper consectetur, est nulla auctor ante, a mollis arcu sapien vel risus.\n" +
                        "\n" +
                        "Integer vel pellentesque odio. Integer eleifend risus eget mauris egestas pretium. Vivamus sodales, lacus eget eleifend lacinia, nisi nulla ullamcorper ligula, ac rutrum justo tellus sit amet odio. Phasellus luctus nisl ac porta suscipit. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aliquam id accumsan nisl. Sed gravida sapien a sodales tempor. Duis at dui risus. Etiam sed elit id mi vestibulum egestas quis at turpis. Donec imperdiet massa faucibus, sodales orci facilisis, mattis mauris. Integer vestibulum tincidunt blandit. Etiam laoreet, odio sit amet pellentesque dictum, dolor tortor tincidunt augue, ut vulputate lorem lectus id nisi. Nullam consectetur lacinia augue eu sollicitudin.\n" +
                        "\n" +
                        "Nunc lacus arcu, laoreet vel tempor nec, eleifend ac elit. In dictum lorem sed viverra varius. Morbi ultrices at libero sit amet volutpat. Duis pulvinar, risus non varius consequat, leo turpis elementum enim, vel aliquam orci turpis quis felis. Vivamus fringilla vitae mauris sit amet auctor. Curabitur eu ultricies eros, ut egestas lacus. Vivamus leo nisi, tempor id egestas vitae, sollicitudin vel odio. Sed sit amet porta neque.\n" +
                        "\n" +
                        "Nullam sollicitudin ante turpis, et blandit erat auctor vitae. Proin at metus lorem. Suspendisse tempus sit amet est eleifend varius. Suspendisse porttitor rutrum dolor, ultricies vestibulum nisi viverra nec. Nam sagittis, libero vel ullamcorper fringilla, nisi massa semper lacus, in suscipit neque tellus eu ante. Nulla facilisi. Duis sit amet pretium est, vel condimentum arcu. Nullam vitae aliquam orci. Ut non condimentum neque, sed varius nisi. Nullam consectetur suscipit lacus, eu condimentum nisi venenatis nec. Praesent vehicula massa tempus dui semper, eu tempor sem porttitor. Ut viverra vehicula auctor.\n" +
                        "\n" +
                        "In porttitor tortor vehicula leo consequat viverra. Quisque vitae nisi pharetra libero elementum aliquet vel eu tellus. Nulla adipiscing magna eget laoreet iaculis. In hac habitasse platea dictumst. Pellentesque elementum pretium erat ac semper. Sed porttitor lectus tincidunt ante tempus, et pharetra sem sagittis. Nulla tempus sapien semper risus dignissim, eu placerat ligula iaculis. Nunc rhoncus diam enim, nec adipiscing ipsum pharetra in. Quisque sit amet tempus velit, vel scelerisque lorem. Donec tristique sodales nisl adipiscing auctor. Aliquam vitae ligula id velit malesuada pellentesque vel in augue. Duis nec dignissim diam. Donec at tincidunt urna. Proin vitae nibh posuere lorem egestas varius. Cras sit amet mi eget mi bibendum posuere varius a urna. Mauris placerat libero at aliquet semper."
                }
        };

        int counter = 0;
        for (Object[] obj : objs) {
            mLlocs.add(new Lloc.LlocBuilder((String) obj[0], (Float) obj[2], (Float) obj[3])
                    .categoria((Integer) obj[1])
                    .descripcio((String) obj[4])
                    .id(counter++)
                    .build());
        }

        // Recorrem la llista de llocs i els afegim a la base de dades
        try {
            for (Lloc lloc : mLlocs) {
                insertLloc(lloc);
            }
        } catch (SQLException e) {
            // Si trobem cap error ho mostrem al log
            Log.e(TAG, mContext.getResources().getString(R.string.error_db), e);
        }
    }

    /**
     * TODO: Mètode per realitzar proves, moure al packet de tests
     */
    public void initImatges() {
        mDB.execSQL("DROP TABLE IF EXISTS " + BD_TAULA_IMATGES);
        mDB.execSQL(BD_CREATE_IMATGES);

        List<Imatge> imatges = new ArrayList<Imatge>();

        imatges.add(new Imatge("Playa de Punta Prima", "punta_prima_01.jpg", 0));
        imatges.add(new Imatge("Playa de Punta Prima", "punta_prima_02.jpg", 0));
        imatges.add(new Imatge("Test 1", "test.jpg", 0));
        imatges.add(new Imatge("Test 2", "test.jpg", 0));
        imatges.add(new Imatge("Test 3", "test.jpg", 0));
        imatges.add(new Imatge("Test 4", "test.jpg", 0));
        imatges.add(new Imatge("Test 5", "test.jpg", 0));
        imatges.add(new Imatge("Test 6", "test.jpg", 0));
        imatges.add(new Imatge("Test 7", "test.jpg", 0));
        imatges.add(new Imatge("Test 8", "test.jpg", 0));

        //imatges.add(new Imatge("Cala Mosca", "cala_mosca_01.jpg",1)); // Aquest lloc no tindrà imatge associada
        imatges.add(new Imatge("Playa Mil Palmeras", "mil_palmeras_01.jpg", 2));
        imatges.add(new Imatge("Playa Mil Palmeras", "mil_palmeras_02.jpg", 2));

        imatges.add(new Imatge("Teatro Circo", "teatro_circo_01.jpg", 3));
        imatges.add(new Imatge("La Lonja", "la_lonja_02.jpg", 4));

        imatges.add(new Imatge("Museo Arqueológico Comarcal de Orihuela", "museo_arqueologico_orihuela_01.jpg", 5));
        imatges.add(new Imatge("Casa Museo Miguel Hernandez", "casa_museo_miguel_hernandez_01.jpg", 6));
        imatges.add(new Imatge("Museo de la Reconquista", "museo_de_la_reconquista_01.jpg", 7));

        // Recorrem la llista de llocs i els afegim a la base de dades
        try {
            for (Imatge imatge : imatges) {
                insertImatge(imatge);
            }
        } catch (SQLException e) {
            // Si trobem cap error ho mostrem al log
            Log.e(TAG, mContext.getResources().getString(R.string.error_db), e);
        }
    }
}
