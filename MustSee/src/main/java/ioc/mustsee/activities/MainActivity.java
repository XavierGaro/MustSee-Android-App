package ioc.mustsee.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ioc.mustsee.R;
import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Imatge;
import ioc.mustsee.data.Lloc;
import ioc.mustsee.fragments.DetailFragment;
import ioc.mustsee.fragments.GalleryFragment;
import ioc.mustsee.fragments.LoginFragment;
import ioc.mustsee.fragments.MainFragment;
import ioc.mustsee.fragments.MyListFragment;
import ioc.mustsee.fragments.MyMapFragment;
import ioc.mustsee.fragments.OnFragmentActionListener;
import ioc.mustsee.fragments.PictureFragment;

/**
 * Aquesta es la classe principal de la aplicació. Des de aquí es gestionen les accions i es
 * posibilita la comunicació amb la base de dades i entre els diferents fragments.
 *
 * @author Javier García
 */
public class MainActivity extends ActionBarActivity implements OnFragmentActionListener {
    // Directoris
    public static final String PICTURES_DIRECTORY = "galerias/";
    private static final String TAG = "MainActivity";

    // Referencia per llençar Fragments
    private static final int MAIN = 0;
    private static final int LOGIN = 1;
    private static final int REGISTER = 2;
    private static final int SEARCH = 3;
    private static final int LIST = 4;
    private static final int DETAIL = 5;
    private static final int FULL_MAP = 6;
    private static final int GALLERY = 7;
    private static final int PICTURE = 8;
    private static final int HALF_MAP = 9;
    private static final int MAP = 10;

    // Per controlar els fragments
    private FrameLayout mContainerOne;
    private FrameLayout mContainerTwo;
    private MyListFragment mListFragment;
    private MyMapFragment mMapFragment;
    private FragmentTransaction mTransaction;
    private LinkedList<Integer[]> mActionHistory = new LinkedList<Integer[]>();
    private boolean mFullScreen = false;

    // Dades
    private List<Lloc> mLlocs;
    private List<Categoria> mCategories;

    // Dades seleccionades
    private Lloc mCurrentLloc;
    private Categoria mCurrentCategoria;
    private List<Lloc> mFilteredLlocs;

    // En aquest mBundle s'emmagatzeman les dades que es pasen entre fragments
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Si s'esta restaurant un estat previ no fem res per evitar que es superposin els fragments.
        if (savedInstanceState != null) return;

        // Inicialitzem els widgets
        initWidgets();

        // TODO: Això es només per les proves. Carreguem les dades
        initCategories();
        initLlocs();
        initImatges();

        // Cridem a la acció principal per carregar el primer fragment.
        OnActionDetected(ACTION_MAIN);
    }

    /**
     * Inicialitza els contenidors on carregarem els fragments.
     */
    private void initWidgets() {
        mContainerOne = (FrameLayout) findViewById(R.id.containerOne);
        mContainerTwo = (FrameLayout) findViewById(R.id.containerTwo);
    }

    /**
     * Estableix quin fragment s'ha de carregar segons si la interficie esta composada per un panell
     * o per dos. TODO: Afegir el control per i la crida al mètode per quan només hi hagi un panell.
     *
     * @param reference referencia del tipus de panell que volem carregar.
     */
    private void loadFragment(int reference) {
        loadFragmentDuePane(reference);
    }

    /**
     * Carrega el fragment en la posició adecuada per la configuració de dos panells. En el cas del
     * mapa i la llista també els emmagatzema per poder cridar-los des de altres punts de la
     * activitat.
     *
     * @param reference referencia del tipus de panell que volem carregar.
     */
    private void loadFragmentDuePane(int reference) {
        Fragment fragment = null;
        int container = R.id.containerOne;
        mFullScreen = true;

        switch (reference) {
            case MAIN:
                fragment = new MainFragment();
                break;

            case LOGIN:
                fragment = new LoginFragment();
                break;

            case FULL_MAP:
                fragment = new MyMapFragment();
                mMapFragment = (MyMapFragment) fragment;
                break;

            case HALF_MAP:
                fragment = new MyMapFragment();
                mFullScreen = false;
                container = R.id.containerTwo;
                mMapFragment = (MyMapFragment) fragment;
                break;

            case LIST:
                fragment = new MyListFragment();
                mFullScreen = false;
                mListFragment = (MyListFragment) fragment;
                break;

            case DETAIL:
                fragment = new DetailFragment();
                mFullScreen = false;
                container = R.id.containerTwo;
                break;

            case GALLERY:
                fragment = new GalleryFragment();
                break;

            case PICTURE:
                fragment = new PictureFragment();
                fragment.setArguments(mBundle);
                break;
        }

        // Reemplacem el fragment al contenidor apropiat.
        mTransaction.replace(container, fragment);
    }

    /**
     * Infla el menú de la ActionBar.
     *
     * @param menu menu a inflar.
     * @return true sempre.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Listener pels items de la ActionBar.
     *
     * @param item item seleccionat
     * @return true si s'ha consumit la selecció o fals en cas contrari.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) return true;
        return super.onOptionsItemSelected(item);
    }

    /**
     * Métode cridat pels fragments adjuntats a aquesta activitat per comunicar-se amb ella. Accepta
     * un bundle amb la informació extra necessaria per portar a terme l'acció.
     *
     * @param action acció a portar a terme.
     * @param bundle dades necesaries per portar a terme la acció.
     */
    @Override
    public void OnActionDetected(int action, Bundle bundle) {
        this.mBundle = bundle;
        OnActionDetected(action);
    }

    /**
     * Métode cridat pels fragments adjuntats a aquesta activitat per comunicar-se amb ella. En
     * aquest cas no accepta informació extra.
     *
     * @param action acció a portar a terme.
     */
    @Override
    public void OnActionDetected(int action) {
        // TODO: Per el moment sempre es guarden totes les accions al BackStack
        boolean addToBackStack = true;


        // Iniciem la transacció per realitzar la acció.
        mTransaction = getSupportFragmentManager().beginTransaction();

        // Carreguem els fragments segons la acció
        switch (action) {
            case OnFragmentActionListener.ACTION_MAIN:
                loadFragment(MAIN);
                addActionHistory(MAIN);
                break;

            case OnFragmentActionListener.ACTION_LOG:
                loadFragment(LOGIN);
                addActionHistory(LOGIN);
                break;


            case OnFragmentActionListener.ACTION_EXPLORE:
                loadFragment(FULL_MAP);
                addActionHistory(MAP);
                break;

            case OnFragmentActionListener.ACTION_SEARCH:
                loadFragment(LIST);
                loadFragment(HALF_MAP);
                addActionHistory(LIST, MAP);
                break;

            case OnFragmentActionListener.ACTION_DETAIL:
                if (!checkActionHistory(LIST)) loadFragment(LIST);
                loadFragment(DETAIL);
                addActionHistory(LIST, DETAIL);
                break;

            case OnFragmentActionListener.ACTION_GALLERY:
                loadFragment(GALLERY);
                addActionHistory(GALLERY);
                break;

            case OnFragmentActionListener.ACTION_PICTURE:
                loadFragment(PICTURE);
                addActionHistory(PICTURE);
                break;
        }

        //Finalitzem la transacció
        finishTransaction(addToBackStack);
    }

    /**
     * Afegeix les dades al historial per poder restaurar les vistes i accions.
     *
     * @param actions llista de enters amb les accions.
     */
    private void addActionHistory(Integer... actions) {
        mActionHistory.add(actions);
    }

    /**
     * Comprova si la acció es troba a la última posició del historial.
     *
     * @param action acció per comprovar.
     * @return true si la acció es troba a la última posició o false en cas contrari.
     */
    private boolean checkActionHistory(int action) {
        for (Integer checkedAction : mActionHistory.getLast()) {
            if (action == checkedAction) return true;
        }
        return false;
    }

    /**
     * Elimina la última posició del stack d'accions i restaura la visibilitat del segon fragment
     * si troba més d'un fragment.
     * TODO: Hi ha que adaptar-lo quan només hi hagi un panell.
     *
     * @return true si s'ha pogut restaurar o false si no quedan més accions..
     */
    private boolean restoreActionHistory() {
        // Eliminem la última acció
        mActionHistory.removeLast();

        // Si el historial esta buit finalitzem la activitat.
        if (mActionHistory.isEmpty()) return false;

        // Comprovem si la acció actual requereix el segon fragment i si es així el mostrem.
        Integer[] actions = mActionHistory.getLast();
        if (actions.length > 1) {
            mContainerTwo.setVisibility(View.VISIBLE);
        } else {
            mContainerTwo.setVisibility(View.GONE);
        }
        return true;
    }

    /**
     * TODO: Això s'ha de obtenir de la base de dades
     * Retorna la llista completa de Llocs.
     *
     * @return llista completa de llocs.
     */
    @Override
    public List<Lloc> getLlocs() {
        return mLlocs;
    }

    /**
     * TODO: Això s'ha de obtenir de la base de dades
     * Retorna la llista de llocs filtrats, o la llista completa si no s'ha aplicat cap filtre
     *
     * @return llista filtrada o llista completa de llocs.
     */
    @Override
    public List<Lloc> getFilteredLlocs() {
        if (mFilteredLlocs == null) {
            return mLlocs;
        } else {
            return mFilteredLlocs;
        }
    }

    /**
     * TODO: La llista s'ha de filtrar a la base de dades
     * Estableix la llista de llocs filtrada i actualitza els marcadors si hi ha el fragment de
     * mapa carregat.
     *
     * @param filteredLlocs llista de llocs filtrats.
     */
    @Override
    public void setFilteredLlocs(List<Lloc> filteredLlocs) {
        this.mFilteredLlocs = filteredLlocs;
        if (mMapFragment != null) {
            mMapFragment.updateMarkers(filteredLlocs);
        }
    }

    /**
     * TODO: Això s'ha de obtenir de la base de dades
     * Retorna la llista completa de categories.
     *
     * @return llista completa de categories.
     */
    @Override
    public List<Categoria> getCategories() {
        return mCategories;
    }

    /**
     * Retorna el lloc actual seleccionat.
     *
     * @return lloc actual
     */
    @Override
    public Lloc getCurrentLloc() {
        return mCurrentLloc;
    }

    /**
     * Estableix el lloc actual, i segons la acció actual actualiza els fragments amb aquesta informació.
     *
     * @param lloc lloc per establir com actual.
     */
    @Override
    public void setCurrentLloc(Lloc lloc) {
        // Establim el lloc com el actual
        mCurrentLloc = lloc;

        // Comprovem les accions que poden fer servir aquesta informació
        if (checkActionHistory(MAP)) mMapFragment.setFocus(lloc);
        if (checkActionHistory(DETAIL)) OnActionDetected(ACTION_DETAIL);
        if (checkActionHistory(LIST)) mListFragment.setSelected(lloc);
    }

    /**
     * Finalitza la transacció de fragments i la guarda al BackStack segons el valor pasat com
     * argument.
     *
     * @param saveToBackStack true si es vol guardar o false en cas contrari.
     */
    private void finishTransaction(boolean saveToBackStack) {
        if (saveToBackStack) {
            mTransaction.addToBackStack(null);
        }

        mTransaction.commit();

        // Ocultem el segon contenidor si no es necessari
        if (mFullScreen) {
            mContainerTwo.setVisibility(View.GONE);
        } else {
            mContainerTwo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!restoreActionHistory()) finish();
    }

    /**
     * TODO: Esborrar despres de les proves. Aquesta informació s'extrau de la base de dades
     * Llocs de prova
     */
    private void initLlocs() {
        mLlocs = new ArrayList<Lloc>();

        mLlocs.add(new Lloc("Playa de Punta Prima", 1, 37.94017f, -0.711672f, "Esta es la playa de Punta Prima. Distintivo Q de calidad turística. Bandera azul."));
        mLlocs.add(new Lloc("Cala Mosca", 1, 37.932554f, -0.718925f, "Esta es la playa de Cala Mosca."));
        mLlocs.add(new Lloc("Playa Mil Palmeras", 1, 37.885557f, -0.752352f, "Esta es la playa Mil Palmeras. Distintivo Q de calidad turística."));

        mLlocs.add(new Lloc("Teatro Circo", 2, 38.085333f, -0.943217f, "En su origen fue una construcción de las llamadas \"semipermanentes\" dedicada en principio a espectáculos circenses, acrobáticos, boxeo, etc."));
        mLlocs.add(new Lloc("La Lonja", 2, 38.082335f, -0.947454f, "Debido a la construcción de una nueva lonja en el Polígono Industrial Puente Alto, la antigua edificación quedo en desuso, y en la actualidad se ha reformado para destinarla al actual Conservatorio de Música y Auditorio. Inagurado el 24 de octubre de 2008, como \"Conservatorio Pedro Terol\"."));

        mLlocs.add(new Lloc("Museo Arqueológico Comarcal de Orihuela", 3, 38.086431f, -0.950500f, "Ubicado en parte de las antiguas dependencias del Hospital San Juan de Dios (Hospital Municipal), restauradas en 1997. Ocupa la antigua iglesia de estilo barroco de planta en cruz latina."));
        mLlocs.add(new Lloc("Casa Museo Miguel Hernandez", 3, 38.089488f, -0.942205f, "Situada en la Calle de Arriba, próxima al Colegio de Santo Domingo, en el “Rincón Hernandiano”. Sus dependencias son las típicas de una casa con explotación ganadera de principios de siglo pasado, cuenta además con un pequeño huerto situado junto a la sierra."));
        mLlocs.add(new Lloc("Museo de la Reconquista", 3, 38.08714f, -0.950126f, "El Museo de la Reconquista fue creado en 1.985, por la Asociación de Fiestas de Moros y Cristianos \"Santas Justa y Rufina\", con sede en los bajos del Palacio de Rubalcava, con el objeto de conservar y mostrar al público trajes festeros de las distintas comparsas, carteles de la fiesta, fotografías, armamento, instrumentos musicales y demás objetos relacionados con la fiesta."));
    }

    /**
     * TODO: Esborrar despres de les proves. Aquesta informació s'extrau de la base de dades
     * Imatges de prova
     */
    private void initImatges() {
        mLlocs.get(0).addImatge(new Imatge("Playa de Punta Prima", "punta_prima_01.jpg"));
        mLlocs.get(0).addImatge(new Imatge("Playa de Punta Prima", "punta_prima_02.jpg"));
        mLlocs.get(0).addImatge(new Imatge("Test 1", "test.jpg"));
        mLlocs.get(0).addImatge(new Imatge("Test 2", "test.jpg"));
        mLlocs.get(0).addImatge(new Imatge("Test 3", "test.jpg"));
        mLlocs.get(0).addImatge(new Imatge("Test 4", "test.jpg"));
        mLlocs.get(0).addImatge(new Imatge("Test 5", "test.jpg"));
        mLlocs.get(0).addImatge(new Imatge("Test 6", "test.jpg"));
        mLlocs.get(0).addImatge(new Imatge("Test 7", "test.jpg"));
        mLlocs.get(0).addImatge(new Imatge("Test 8", "test.jpg"));

        mLlocs.get(1).addImatge(new Imatge("Cala Mosca", "cala_mosca_01.jpg"));
        mLlocs.get(2).addImatge(new Imatge("Playa Mil Palmeras", "mil_palmeras_01.jpg"));
        mLlocs.get(2).addImatge(new Imatge("Playa Mil Palmeras", "mil_palmeras_02.jpg"));

        mLlocs.get(3).addImatge(new Imatge("Teatro Circo", "teatro_circo_01.jpg"));
        mLlocs.get(4).addImatge(new Imatge("La Lonja", "la_lonja_02.jpg"));

        mLlocs.get(5).addImatge(new Imatge("Museo Arqueológico Comarcal de Orihuela", "museo_arqueologico_orihuela_01.jpg"));
        mLlocs.get(6).addImatge(new Imatge("Casa Museo Miguel Hernandez", "casa_museo_miguel_hernandez_01.jpg"));
        mLlocs.get(7).addImatge(new Imatge("Museo de la Reqconquista", "museo_de_la_reconquista_01.jpg"));
    }

    /**
     * TODO: Esborrar despres de les proves. Aquesta informació s'extrau de la base de dades
     * Categories de proves
     */
    private void initCategories() {
        mCategories = new ArrayList<Categoria>();

        // Categorias de prueba TODO: Esta información se extrae del web service
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
    }
}
