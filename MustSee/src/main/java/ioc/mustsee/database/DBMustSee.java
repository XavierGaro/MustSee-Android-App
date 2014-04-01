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
    public static final int VERSIO = 5;

    // Camps de la base de dades
    public static final String CLAU_ID = "_id";
    public static final String CLAU_NOM = "nom";
    public static final String CLAU_DESCRIPCIO = "descripcio";
    public static final String CLAU_LATITUD = "latitud";
    public static final String CLAU_LONGITUD = "longitud";
    public static final String CLAU_CATEGORIA = "categoria";
    public static final String CLAU_ICON_RESOURCE = "icon";

    // Consulta per crear la taula
    public static final String BD_CREATE_LLOCS = "CREATE TABLE " + BD_TAULA_LLOCS + "("
            + CLAU_ID + " INTEGER PRIMARY KEY, " + CLAU_NOM + " TEXT NOT NULL, " + CLAU_DESCRIPCIO
            + " TEXT NOT NULL, " + CLAU_LATITUD + " REAL NOT NULL, " + CLAU_LONGITUD
            + " REAL NOT NULL, " + CLAU_CATEGORIA + " INT NOT NULL, " + CLAU_ICON_RESOURCE
            + " INT NOT NULL)";

    // Array amb tots els camps per facilitar la creació de consultes
    private String[] columns = new String[]{CLAU_ID, CLAU_NOM, CLAU_DESCRIPCIO, CLAU_LATITUD,
            CLAU_LONGITUD, CLAU_CATEGORIA, CLAU_ICON_RESOURCE};

    private AjudaBD ajuda;
    private SQLiteDatabase bd;
    private Context context;


    public DBMustSee(Context context) {
        this.context = context;
        ajuda = new AjudaBD(context);
    }

    public DBMustSee open() throws SQLException {
        bd = ajuda.getWritableDatabase();
        return this;
    }

    public void close() {
        try {
            ajuda.close();
        } catch (SQLException e) {
            // Si hi ha un error al tancar la base de dades només el mostrem al log.
            Log.e(TAG, context.getResources().getString(R.string.error_db), e);
        }
    }

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
        bd.insert(BD_TAULA_LLOCS, null, initialValues);

        return this;
    }

    public Lloc getLloc(int id) throws SQLException {
        Lloc lloc = null;

        Cursor cursor = bd.query(true, BD_TAULA_LLOCS, columns, CLAU_ID + " = " + id, null, null,
                null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            lloc = cursorToLloc(cursor);
        }

        return lloc;
    }

    public List<Lloc> getLlocs() throws SQLException {
        List<Lloc> llocs = new ArrayList<Lloc>();
        Cursor mCursor = bd.query(BD_TAULA_LLOCS, columns, null, null, null, null, null);

        // Recorrem el cursor i els afegim a la llista
        if (mCursor.moveToFirst()) {
            do {
                llocs.add(cursorToLloc(mCursor));
            } while (mCursor.moveToNext());
        }
        return llocs;
    }

    private Lloc cursorToLloc(Cursor cursor) {
        Lloc lloc = null;

        try {
            lloc = new Lloc.LlocBuilder(cursor.getString(1), cursor.getFloat(3), cursor.getFloat(4))
                    .id(cursor.getInt(0))
                    .descripcio(cursor.getString(2))
                    .categoria(cursor.getInt(5))
                    .icon(cursor.getInt(6))
                    .build();
        } catch (Exception e) {
            // Si hi ha un error al obtenir les dades del cursor enregistrem l'error al log
            Log.e(TAG, context.getResources().getString(R.string.error_db), e);
        }

        return lloc;
    }

    private static class AjudaBD extends SQLiteOpenHelper {
        Context mContext;

        AjudaBD(Context context) {
            super(context, BD_NOM, null, VERSIO);
            this.mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                // Creem les taules
                db.execSQL(BD_CREATE_LLOCS);
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
}
    /* PER FER TESTS: Inserta una llista de llocs a la base de dades
    public void testCreateLlocs() {
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

        for (Object[] obj : objs) {
            mLlocs.add(new Lloc.LlocBuilder((String) obj[0], (Float) obj[2], (Float) obj[3])
                    .categoria((Integer) obj[1])
                    .descripcio((String) obj[4])
                    .build());

        }

        // Recorrem la llista de llocs i els afegim a la base de dades
        try {
            open();
            for (Lloc lloc : mLlocs) {
                insertLloc(lloc);
            }
        } catch (SQLException e) {
            // Si trobem cap error ho mostrem al log
            Log.e(TAG, context.getResources().getString(R.string.error_db), e);
        } finally {
            close();
        }
    }*/
