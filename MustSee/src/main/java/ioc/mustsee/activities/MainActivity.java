package ioc.mustsee.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ioc.mustsee.R;
import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Imatge;
import ioc.mustsee.data.Lloc;
import ioc.mustsee.downloaders.DownloadImageAsyncTask;
import ioc.mustsee.downloaders.DownloadManager;
import ioc.mustsee.downloaders.OnTaskCompleted;
import ioc.mustsee.fragments.DetailFragment;
import ioc.mustsee.fragments.GalleryFragment;
import ioc.mustsee.fragments.LoginFragment;
import ioc.mustsee.fragments.MainFragment;
import ioc.mustsee.fragments.MyListFragment;
import ioc.mustsee.fragments.MyMapFragment;
import ioc.mustsee.fragments.OnFragmentActionListener;
import ioc.mustsee.fragments.PictureFragment;
import ioc.mustsee.parser.RetrieveData;

/**
 * Aquesta es la classe principal de la aplicació. Des de aquí es gestionen les accions i es
 * possibilita la comunicació amb la base de dades i entre els diferents fragments.
 *
 * @author Xavier García
 */
public class MainActivity extends ActionBarActivity implements OnFragmentActionListener, DownloadManager {
    private static final String TAG = "MainActivity";

    // Directoris
    public static final String PICTURES_DIRECTORY = "/pictures";

    // Location Manager
    public static final float MIN_REFRESH_METERS = 100f;
    public static final long MIN_REFRESH_TIME = 1000; // Refresca la informació del GPS cada 1s

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

    // En aquest mBundle s'emmagatzeman les dades que es passen entre fragments
    private Bundle mBundle;

    // Localització del usuari
    LocationManager mLocationManager;
    String locationProvider = LocationManager.GPS_PROVIDER;

    // Gestor de descarregues
    private int descarregues;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Si s'està restaurant un estat previ no fem res per evitar que es superposin els fragments.
        if (savedInstanceState != null) return;

        // Inicialitzem els widgets
        initWidgets();

        // Carreguem les dades
        initCategories();
        initLlocs();
        initLocation();


        // Inicialitzem la localització
        updateLloc();

        // Cridem a la acció principal per carregar el primer fragment.
        OnActionDetected(ACTION_MAIN);
    }

    /**
     * Inicialitza els contenidors on carregarem els fragments.
     */
    private void initWidgets() {
        // Ocultem la Actionbar perquè no la fem servir
        getSupportActionBar().hide();

        mContainerOne = (FrameLayout) findViewById(R.id.containerOne);
        mContainerTwo = (FrameLayout) findViewById(R.id.containerTwo);
    }

    /**
     * Estableix quin fragment s'ha de carregar segons si la interfície esta composada per un panell
     * o per dos.
     *
     * @param reference referència del tipus de panell que volem carregar.
     */
    private void loadFragment(int reference) {
        loadFragmentDuePane(reference);
    }

    /**
     * Carrega el fragment en la posició adecuada per la configuració de dos panells. En el cas del
     * mapa i la llista també els emmagatzema per poder cridar-los des de altres punts de la
     * activitat.
     *
     * @param reference referència del tipus de panell que volem carregar.
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
                eliminarMapa();
                fragment = new MyMapFragment();
                mMapFragment = (MyMapFragment) fragment;
                break;

            case HALF_MAP:
                eliminarMapa();
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
     * Ens assegurem que s'ha eliminat el fragment del mapa abans de tornar a obrir-lo.
     */
    private void eliminarMapa() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        // Si s'ha trobat el fragment l'eliminem
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }
    }

    /**
     * Mètode cridat pels fragments adjuntats a aquesta activitat per comunicar-se amb ella. Accepta
     * un bundle amb la informació extra necessària per portar a terme l'acció.
     *
     * @param action acció a portar a terme.
     * @param bundle dades necessàries per portar a terme la acció.
     */
    @Override
    public void OnActionDetected(int action, Bundle bundle) {
        this.mBundle = bundle;
        OnActionDetected(action);
    }

    /**
     * Mètode cridat pels fragments adjuntats a aquesta activitat per comunicar-se amb ella. En
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
     * Retorna la llista completa de Llocs.
     *
     * @return llista completa de llocs.
     */
    @Override
    public List<Lloc> getLlocs() {
        return mLlocs;
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
     * Estableix el lloc actual, i segons la acció actual actualitza els fragments amb aquesta
     * informació.
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
     * Finalitza la transacció de fragments i la guarda al BackStack segons el valor passat com
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
     * Connecta amb el web service i recupera la llista complea de llocs.
     */
    private void initLlocs() {
        donwloadInProgress(true);

        new RetrieveData().getLlocs(new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(List result) {
                mLlocs = result;
                donwloadInProgress(false);

                // Recorrem tots els llocs i comprovem si hi ha cap imatge per descarregar
                List<String> urlImatges = new ArrayList<String>();

                for (Lloc lloc : mLlocs) {
                    for (Imatge imatge : lloc.getImages()) {
                        urlImatges.add(imatge.nomFitxer);
                    }
                }

                // Llencem una nova tasca per descarregar les imatges que siguin necessaries
                new DownloadImageAsyncTask(MainActivity.this, PICTURES_DIRECTORY).execute(urlImatges.toArray(new String[urlImatges.size()]));

            }
        });
    }

    /**
     * Connecta amb el web service i obté la llista completa de categories.
     */
    private void initCategories() {
        mCategories = new ArrayList<Categoria>();
        mCategories.add(new Categoria(0, getResources().getString(R.string.show_all_llocs), null));

        donwloadInProgress(true);

        new RetrieveData().getCategories(new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(List result) {
                // Ocultem el dialog
                //dialog.dismiss();
                mCategories.addAll(result);
                donwloadInProgress(false);
            }
        });
    }

    /**
     * Inicialitza el gestor de localització i el listener que respondrà als canvis de localització.
     */
    public void initLocation() {
        // Obtenim la referència al Location Manager del sistema
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Definim el listener que respondrà a les actualitzacions
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Cridat quan una nova localització es trobada pel proveïdor
                updateLloc(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Cridat quan canvia l'estatus
                updateLloc();
            }

            @Override
            public void onProviderEnabled(String provider) {
                // Cridat quan s'activa el proveïdor
                updateLloc();
            }

            @Override
            public void onProviderDisabled(String provider) {
                // Cridat quan es desactiva el proveïdor
            }
        };

        // Enregistrem el listener per rebre les actualitzacions.
        mLocationManager.requestLocationUpdates(locationProvider, MIN_REFRESH_TIME, MIN_REFRESH_METERS, locationListener);
    }

    /**
     * Retorna la ultima posició coneguda pel LocationManager.
     *
     * @return ultima posició coneguda o la localització per defecte de Lloc si no hi ha cap.
     */
    @Override
    public LatLng getLastKnownPosition() {
        Location lastKnownLocation = mLocationManager.getLastKnownLocation(locationProvider);
        return lastKnownLocation == null ? Lloc.NO_LOCATION :
                new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
    }

    /**
     * Actualitza la posició del usuari a la classe Lloc amb la localització passada com argument.
     *
     * @param location localització que volem establir.
     */
    public void updateLloc(Location location) {
        Lloc.sPosition = new LatLng(location.getLatitude(), location.getLongitude());

        // Si està visible la llista la actualitzem.
        if (checkActionHistory(LIST)) mListFragment.updateListView();
    }

    /**
     * Actualitza la posició del usuari a la classe Lloc per la última localització coneguda.
     */
    public void updateLloc() {
        Lloc.sPosition = getLastKnownPosition();
    }

    /**
     * Aquest mètode es crida al començar i finalitzar la descarrega de dades i imatges. Mentre hi
     * ha descarregues en curs es mostra el dialog de progress i es bloqueja la pantalla.
     *
     * @param descarrega si es true indica que inicia la descarrega en cas contrari s'indica que
     *                   finalitza la descarrega.
     */
    @Override
    public synchronized void donwloadInProgress(boolean descarrega) {
        // Actualitzem el comptador
        if (descarrega) {
            descarregues++;
        } else {
            descarregues--;
        }

        if (descarregues > 0) {
            // Hi han descarregues pendents
            if (dialog == null) {
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    dialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
                } else {
                    dialog = new ProgressDialog(this);
                }

                dialog.setCancelable(false);
            }

            if (!dialog.isShowing()) {
                dialog.setMessage(getResources().getString(R.string.dialog_wait));
                dialog.show();
            }

        } else {
            // No queden més descarregues pendents, ocultem la barra de progrés
            if (dialog.isShowing()) dialog.dismiss();
            dialog = null;
        }
    }
}
