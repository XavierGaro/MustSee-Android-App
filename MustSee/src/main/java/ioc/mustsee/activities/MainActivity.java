package ioc.mustsee.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import ioc.mustsee.fragments.PhotoFragment;


public class MainActivity extends ActionBarActivity implements OnFragmentActionListener {
    // Directoris
    public static final String PICTURES_DIRECTORY = "galerias/";
    private static final String TAG = "MainActivity";
    // Contenidors de Fragments
    private static final int CONTAINER_ONE = R.id.containerOne;
    private static final int CONTAINER_TWO = R.id.containerTwo;


    // Referencia per llençar Fragments
    private static final int MAIN = 0;
    private static final int LOGIN = 1;
    private static final int REGISTER = 2;
    private static final int SEARCH = 3;
    private static final int LIST = 4;
    private static final int DETAIL = 5;
    private static final int FULL_MAP = 6;
    private static final int GALLERY = 7;
    private static final int PHOTO = 8;
    private static final int HALF_MAP = 9;
    // Fragments per controlar
    public MyMapFragment fragmentAmbMapa;
    FragmentTransaction transaction;
    boolean isMapFragmentVisible = false;
    boolean isListFragmentVisible = false;
    private FrameLayout containerOne;
    private FrameLayout containerTwo;
    private FrameLayout containerThree;
    private MyListFragment fragmentAmbLlista;
    private LinkedList<Integer> actions = new LinkedList<Integer>();
    private LinkedList<Boolean> isFrameTwoVisible = new LinkedList<Boolean>();


    // Locs
    private List<Lloc> llocs;

    // Categories
    private List<Categoria> categories;

    // Gestio de llocs
    private Lloc current;
    private List<Lloc> llocsFiltrats;

    // En quest bundle s'emmagatzemma la informació que es pasa entre fragments
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity onCreate");


        // If we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }

        initWidgets();

        // TODO: Això es només per les proves
        // Afegim els llocs
        initCategories();
        initLlocs();
        initImatges();

        // Cridem a la acció principal
        OnActionDetected(ACTION_MAIN);
    }

    private void initWidgets() {
        containerOne = (FrameLayout) findViewById(R.id.containerOne);
        containerTwo = (FrameLayout) findViewById(R.id.containerTwo);
    }

    private void setFragment(int id) {
        if (containerTwo == null) {
            setFragmentSinglePane(id);
        } else {
            setFragmentDuePane(id);
        }


    }

    private void setFragmentSinglePane(int id) {
                    /* TODO ignoramos el modo SinglePane

        Log.d(TAG, "Un Fragment");
        Fragment fragment = null;

        switch (id) {

            case MAIN:
                fragment = new MainFragment();
                fragment.setArguments(getIntent().getExtras());
                break;

            case LOGIN:
                fragment = new LoginFragment();
                fragment.setArguments(getIntent().getExtras());
                break;

            case FULL_MAP:
                fragment = new MyMapFragment();
                fragment.setArguments(getIntent().getExtras());
                break;

        }

        // Reemplacem el fragment
        // TODO: afegir animació al canviar els fragments setCustomAnimations()
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(CONTAINER_ONE, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

                */

    }

    private void setFragmentDuePane(int id) {
        Log.d(TAG, "Dos Fragments");
        Fragment fragment = null;
        boolean pantallaCompleta = false;
        int container = 0;


        switch (id) {
            case MAIN:
                fragment = new MainFragment(CONTAINER_ONE);
                //fragment.setArguments(getIntent().getExtras());
                pantallaCompleta = true;
                container = CONTAINER_ONE;
                break;

            case LOGIN:
                fragment = new LoginFragment(CONTAINER_ONE);
                //fragment.setArguments(getIntent().getExtras());
                pantallaCompleta = true;
                container = CONTAINER_ONE;
                break;

            case FULL_MAP:
                // TODO mover esto al final del mètodo
                fragment = new MyMapFragment(R.id.mapFragment);
                //fragment = new MyMapFragment(CONTAINER_ONE);
                pantallaCompleta = true;
                container = CONTAINER_ONE;
                fragmentAmbMapa = (MyMapFragment) fragment;
                Log.d(TAG, "Añadido el fragmento con mapa: " + fragmentAmbMapa);
                break;

            case HALF_MAP:
                fragment = new MyMapFragment(R.id.mapFragment);
                //fragment = new MyMapFragment(CONTAINER_TWO);
                pantallaCompleta = false;
                container = CONTAINER_TWO;
                fragmentAmbMapa = (MyMapFragment) fragment;
                Log.d(TAG, "Añadido el fragmento con mapa: " + fragmentAmbMapa);
                break;


            case LIST:
                fragment = new MyListFragment(CONTAINER_ONE);
                //fragment.setArguments(getIntent().getExtras());
                pantallaCompleta = false;
                container = CONTAINER_ONE;
                fragmentAmbLlista = (MyListFragment) fragment;
                // fragmentAmbMapa =null; lo mantiene si lo hay
                break;

            case DETAIL:
                fragment = new DetailFragment(CONTAINER_TWO);
                //fragment.setArguments(getIntent().getExtras());
                pantallaCompleta = false;
                container = CONTAINER_TWO;
                Log.d(TAG, "Abriendo detalle para: " + current.nom);
                break;

            case GALLERY:
                Log.d(TAG, "Estableciendo fragmento galería:");
                fragment = new GalleryFragment(CONTAINER_ONE);
                //fragment.setArguments(getIntent().getExtras());
                pantallaCompleta = true;
                container = CONTAINER_ONE;
                break;

            case PHOTO:
                Log.d(TAG, "Estableciendo fragmento photo");
                fragment = new PhotoFragment(CONTAINER_ONE);
                //fragment.setArguments(getIntent().getExtras());
                fragment.setArguments(bundle);
                pantallaCompleta = true;
                container = CONTAINER_ONE;
                break;
        }

        // Ocultem el segon contenidor si no es necessari
        if (pantallaCompleta) {
            containerTwo.setVisibility(View.GONE);
        } else {
            containerTwo.setVisibility(View.VISIBLE);
        }

        // Reemplacem el fragment
        //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Log.d(TAG, "Transaction: " + transaction);
        Log.d(TAG, "Container: " + container);
        Log.d(TAG, "Fragment: " + fragment);
        transaction.replace(container, fragment);
        //transaction.addToBackStack(null);
        //transaction.commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Al iniciar las transacciones de fragmentos hacer el commit para todas a la vez, comprovar si es posible.
    @Override
    public void OnActionDetected(int action, Bundle bundle) {
        //Actualitzem el bundle
        this.bundle = bundle;
        OnActionDetected(action);
    }

    @Override
    public void OnActionDetected(int action) {
        // Inciem la transacció per canviar els fragments
        transaction = getSupportFragmentManager().beginTransaction();
        boolean guardarAlBackStack = true;

        isMapFragmentVisible = false;
        isListFragmentVisible = false;
        // Reaccionamos según la acción recibida

        Log.d(TAG, "Eligiendo acción: " + action);
        switch (action) {
            case OnFragmentActionListener.ACTION_MAIN:
                actions.add(action);
                setFragment(MAIN);
                isFrameTwoVisible.add(false);
                break;

            case OnFragmentActionListener.ACTION_LOG:
                actions.add(action);
                setFragment(LOGIN);
                isFrameTwoVisible.add(false);
                break;

            case OnFragmentActionListener.ACTION_BACK:
                actions.removeLast();
                System.out.println(actions);
                isFrameTwoVisible.removeLast();
                setFragment(actions.getLast());
                Log.d(TAG, "Volviendo al fragmento anterior");
                break;

            case OnFragmentActionListener.ACTION_EXPLORE:
                // actions.add(action); // No afegim l'acció
                Log.d(TAG, "Cargando mapa");
                isFrameTwoVisible.add(false);
                isMapFragmentVisible = true;
                setFragment(FULL_MAP);

                break;

            case OnFragmentActionListener.ACTION_SEARCH:
                //actions.add(action);
                isMapFragmentVisible = true;
                isListFragmentVisible = true;
                isFrameTwoVisible.add(true);
                setFragment(LIST);
                setFragment(HALF_MAP);
                break;

            case OnFragmentActionListener.ACTION_DETAIL:
                actions.add(action);
                isListFragmentVisible = true;
                isFrameTwoVisible.add(true);
                setFragment(DETAIL);
                break;

            case OnFragmentActionListener.ACTION_GALLERY:
                Log.d(TAG, "Acción: Gallery");
                actions.add(action);
                setFragment(GALLERY);
                isFrameTwoVisible.add(false);
                break;

            case OnFragmentActionListener.ACTION_PHOTO:
                Log.d(TAG, "Acción: Photo");
                actions.add(action);
                setFragment(PHOTO);
                isFrameTwoVisible.add(false);
                break;
        }

        //Finalitzem la transacció

        finishTransaction(guardarAlBackStack);
    }

    @Override
    public List<Lloc> getLlocs() {

        Log.d(TAG, "Devolviendo el lloc actual: " + llocs);
        return llocs;
    }


    @Override
    public List<Lloc> getLlocsFiltrats() {
        if (llocsFiltrats == null) {
            return llocs;
        } else {
            return llocsFiltrats;
        }
    }

    @Override
    public void setLlocsFiltrats(List<Lloc> llocsFiltrats) {
        this.llocsFiltrats = llocsFiltrats;
        if (fragmentAmbMapa != null && isMapFragmentVisible) {
            Log.d(TAG, "Pasando de Main al fragmento de mapa la lista: " + llocsFiltrats.size());
            fragmentAmbMapa.updateMarkers(llocsFiltrats);
        } else {
            Log.d(TAG, "No hay fragmento de mapa");
        }

    }

    @Override
    public List<Categoria> getCategories() {
        return categories;
    }

    @Override
    public Lloc getLlocActual() {
        Log.d(TAG, "Devolviendo el lloc actual: " + current);
        return current;
    }

    @Override
    public void setLlocActual(Lloc lloc) {
        Log.d(TAG, "Estableciendo lloc actual, es correcto?");

        current = lloc;
        // Si hay abierto un fragmento de mapa centra el mapa en este lloc
        if (fragmentAmbMapa != null && isMapFragmentVisible) {
            Log.d(TAG, "Existe un fragmento de mapa, haciendo setFocus a: " + lloc.nom);
            Log.d(TAG, "Existe un fragmento de mapa, es visible? isMapFragmentVisible");
            fragmentAmbMapa.setFocus(lloc);
        } else if (fragmentAmbLlista != null && isListFragmentVisible) {
            Log.d(TAG, "Existe un fragmento de lista y no hay mapa, debe haber fragmento de detalle. ");
            OnActionDetected(ACTION_DETAIL);
        }

        if (fragmentAmbLlista != null && isListFragmentVisible) {
            Log.d(TAG, "Existe un fragmento de lista, haciendo setSelected a: " + lloc.nom);
            fragmentAmbLlista.setSelected(lloc);
        }
    }

    @Override
    public void refreshLlocActual() {
        setLlocActual(current);
    }

    // Esborrar despres de les proves
    private void initLlocs() {
        // Llocs de prueba TODO: Esta información se extrae del web service
        llocs = new ArrayList<Lloc>();


        llocs.add(new Lloc("Playa de Punta Prima", 1, 37.94017f, -0.711672f, "Esta es la playa de Punta Prima. Distintivo Q de calidad turística. Bandera azul."));
        llocs.add(new Lloc("Cala Mosca", 1, 37.932554f, -0.718925f, "Esta es la playa de Cala Mosca."));
        llocs.add(new Lloc("Playa Mil Palmeras", 1, 37.885557f, -0.752352f, "Esta es la playa Mil Palmeras. Distintivo Q de calidad turística."));

        llocs.add(new Lloc("Teatro Circo", 2, 38.085333f, -0.943217f, "En su origen fue una construcción de las llamadas \"semipermanentes\" dedicada en principio a espectáculos circenses, acrobáticos, boxeo, etc."));
        llocs.add(new Lloc("La Lonja", 2, 38.082335f, -0.947454f, "Debido a la construcción de una nueva lonja en el Polígono Industrial Puente Alto, la antigua edificación quedo en desuso, y en la actualidad se ha reformado para destinarla al actual Conservatorio de Música y Auditorio. Inagurado el 24 de octubre de 2008, como \"Conservatorio Pedro Terol\"."));

        llocs.add(new Lloc("Museo Arqueológico Comarcal de Orihuela", 3, 38.086431f, -0.950500f, "Ubicado en parte de las antiguas dependencias del Hospital San Juan de Dios (Hospital Municipal), restauradas en 1997. Ocupa la antigua iglesia de estilo barroco de planta en cruz latina."));
        llocs.add(new Lloc("Casa Museo Miguel Hernandez", 3, 38.089488f, -0.942205f, "Situada en la Calle de Arriba, próxima al Colegio de Santo Domingo, en el “Rincón Hernandiano”. Sus dependencias son las típicas de una casa con explotación ganadera de principios de siglo pasado, cuenta además con un pequeño huerto situado junto a la sierra."));
        llocs.add(new Lloc("Museo de la Reconquista", 3, 38.08714f, -0.950126f, "El Museo de la Reconquista fue creado en 1.985, por la Asociación de Fiestas de Moros y Cristianos \"Santas Justa y Rufina\", con sede en los bajos del Palacio de Rubalcava, con el objeto de conservar y mostrar al público trajes festeros de las distintas comparsas, carteles de la fiesta, fotografías, armamento, instrumentos musicales y demás objetos relacionados con la fiesta."));
    }

    private void initImatges() {
        // Imatges de prova TODO: Esta informació se extrae del web service

        llocs.get(0).addImatge(new Imatge("Playa de Punta Prima", "punta_prima_01.jpg"));
        llocs.get(0).addImatge(new Imatge("Playa de Punta Prima", "punta_prima_02.jpg"));
        llocs.get(0).addImatge(new Imatge("Test 1", "test.jpg"));
        llocs.get(0).addImatge(new Imatge("Test 2", "test.jpg"));
        llocs.get(0).addImatge(new Imatge("Test 3", "test.jpg"));
        llocs.get(0).addImatge(new Imatge("Test 4", "test.jpg"));
        llocs.get(0).addImatge(new Imatge("Test 5", "test.jpg"));
        llocs.get(0).addImatge(new Imatge("Test 6", "test.jpg"));
        llocs.get(0).addImatge(new Imatge("Test 7", "test.jpg"));
        llocs.get(0).addImatge(new Imatge("Test 8", "test.jpg"));


        llocs.get(1).addImatge(new Imatge("Cala Mosca", "cala_mosca_01.jpg"));
        llocs.get(2).addImatge(new Imatge("Playa Mil Palmeras", "mil_palmeras_01.jpg"));
        llocs.get(2).addImatge(new Imatge("Playa Mil Palmeras", "mil_palmeras_02.jpg"));

        llocs.get(3).addImatge(new Imatge("Teatro Circo", "teatro_circo_01.jpg"));
        llocs.get(4).addImatge(new Imatge("La Lonja", "la_lonja_02.jpg"));

        llocs.get(5).addImatge(new Imatge("Museo Arqueológico Comarcal de Orihuela", "museo_arqueologico_orihuela_01.jpg"));
        llocs.get(6).addImatge(new Imatge("Casa Museo Miguel Hernandez", "casa_museo_miguel_hernandez_01.jpg"));
        llocs.get(7).addImatge(new Imatge("Museo de la Reqconquista", "museo_de_la_reconquista_01.jpg"));

    }


    private void initCategories() {
        categories = new ArrayList<Categoria>();

        // Categorias de prueba TODO: Esta información se extrae del web service
        Categoria tot = new Categoria(0, "Seleccionar Todo", "Selecciona todas las categorías.");
        Categoria playas = new Categoria(1, "Playas", "En esta categoría hay playas.");
        Categoria poi = new Categoria(2, "Puntos de interes", "En esta categoría hay puntos de interes.");
        Categoria museos = new Categoria(3, "Museos", "En esta categoría hay museos.");

        categories.add(tot);
        categories.add(playas);
        categories.add(poi);
        categories.add(museos);
    }

    private void finishTransaction(boolean guardarAlBackStack) {
        // Según la acción hay que cargar 1 o 2 fragmentos y ocultar los demás

        // Ocultamos los layouts que no se van a utilizar

        // Iniciamos la transacción

        // Cargamos los fragmentos necesarios

        // Guardamos en el backstack si es necesario
        Log.d(TAG, "Dentro de finishTransaction");
        if (guardarAlBackStack) {
            Log.d(TAG, "Guardando BackStack");
            transaction.addToBackStack(null);
        }

        // Commit de la transacción
        transaction.commit();
        Log.d(TAG, "Saliendo de finishTransaction");
    }


    @Override
    public void deleteMapFragment() {
        fragmentAmbMapa = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "Llamado onResume");
    }

    private void restoreLastVisible() {
        if (isFrameTwoVisible.isEmpty()) return;

        isFrameTwoVisible.removeLast();
        if (isFrameTwoVisible.getLast()) {
            containerTwo.setVisibility(View.VISIBLE);
        } else {
            containerTwo.setVisibility(View.GONE);
        }

        Log.d(TAG, "isMapFragmentVisible?" + isMapFragmentVisible);
        Log.d(TAG, "isListFragmentVisible?" + isListFragmentVisible);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        restoreLastVisible();
        Log.d(TAG, "pulsado ATRAS");
    }

}
