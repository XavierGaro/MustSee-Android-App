package ioc.mustsee.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ioc.mustsee.R;
import ioc.mustsee.data.Lloc;

import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_DETAIL;

/**
 * A aquest fragment es on es gestiona el mapa, tant en la vista a pantalla completa com parcial.
 *
 * @author Javier García
 */
public class MyMapFragment extends MustSeeFragment implements GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "MyMapFragment";

    private static final int VELOCITAT_CAMARA = 500;

    // TODO: aquest valor serà 1 per pantalla completa o ajustat a la proporicó visible del mapa.
    private float screen_factor = 0.75f;

    // Objectes del mapa
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    // Informació dels llocs i marcadors
    private HashMap<Marker, Lloc> mMarkersToLloc = new HashMap<Marker, Lloc>();
    private List<Lloc> mLlocs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Si la vista no existeix la inflem i inicialitzem els widgets
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_map, null);
            initWidgets();
        }
        return mView;
    }

    /**
     * TODO: Aquí s'inicialitzaran els botons del mapa
     */
    @Override
    void initWidgets() {
        // S'ha de implementar obligatòriament
    }

    /**
     * Si al restaurar el fragment no existeix el mapa l'inicialitzem. Al contrari que amb altres
     * widgets, el mapa es un fragment niuat i no es pot inicialitzar en el mètode onCreateView().
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mMap == null) initMap();
    }

    /**
     * Inicialitza el mapa, carrega la llista de llocs completa, els afegeix com a marcadors i fa
     * focus amb la càmera sobre ells.
     */
    private void initMap() {
        mMap = mMapFragment.getMap();
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);

        mLlocs = mCallback.getLlocs();
        addMarkers(mLlocs);
        fixZoom(true);
    }

    /**
     * Afegeix la llista de llocs pasada per argument al mapa de marcadors.
     *
     * @param llocs llista de llocs a afegir com marcadors.
     */
    private void addMarkers(List<Lloc> llocs) {
        for (Lloc lloc : llocs) {
            // Si ja hi ha un marcador al mapa per aquest lloc continuem.
            if (mMarkersToLloc.containsValue(lloc)) continue;
            mMarkersToLloc.put(addMarker(lloc), lloc);
        }
    }

    /**
     * Afegeix el lloc passat com argument al mapa de marcadors.
     *
     * @param lloc lloc del que volem afegir el marcador.
     * @return marcador creat.
     */
    private Marker addMarker(Lloc lloc) {
        // Afegim les dades del marcador.
        MarkerOptions markerOptions = new MarkerOptions()
                .position(lloc.posicio)
                .title(lloc.nom)
                .snippet(lloc.getShortDescripcio());

        // Si te un icon associat l'afegim al marcador.
        if (lloc.iconResource != Lloc.NO_ICON) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(lloc.iconResource));
        }

        // Retornem el marcador creat
        return mMap.addMarker(markerOptions);
    }

    /**
     * Centra la càmera amb o sense animació sobre un requadre de mapa ajustat a les coordenades dels llocs a la llista.
     *
     * @param animate true si volem animar el mapa o false en cas contrari.
     */
    private void fixZoom(boolean animate) {
        // Si no hi ha cap lloc, no cal fer res
        if (mLlocs.isEmpty()) return;

        LatLngBounds.Builder bc = new LatLngBounds.Builder();

        for (Lloc lloc : mLlocs) {
            bc.include(lloc.posicio);
        }

        int height = this.getResources().getDisplayMetrics().heightPixels;
        int width = (int) (this.getResources().getDisplayMetrics().widthPixels * screen_factor);

        if (animate) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), width, height, 100), 2000, null);
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), width, height, 100));
        }
    }

    /**
     * Estableix el focus al lloc passat com argument. El que que la imatge es centri en la posició
     * del lloc i si existeix un marcador mostra la seva informació. Aquest moviment es sempre animat.
     *
     * @param lloc lloc en el que centrar la càmera.
     */
    public void setFocus(Lloc lloc) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(lloc.posicio), VELOCITAT_CAMARA, null);
        Marker marker = getMarker(lloc);
        if (marker != null) marker.showInfoWindow();
    }

    /**
     * Retorna el marcador corresponent al lloc passat com argument.
     *
     * @param lloc lloc del que volem obtenir el marcador.
     * @return marcador corresponent al lloc o null si no s'ha trobat cap.
     */
    private Marker getMarker(Lloc lloc) {
        for (Map.Entry<Marker, Lloc> entry : mMarkersToLloc.entrySet()) {
            if (entry.getValue() == lloc) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Listener per l'event de clicar a sobre d'un marcador.
     *
     * @param marker marcador sobre el que s'ha fet click.
     * @return true si s'ha consumit l'event o false en cas contrari.
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        // Comprovem si ya estava seleccionat previament
        if (mMarkersToLloc.get(marker) == mCallback.getCurrentLloc()) {
            // Cridem a la acció de detall i consumim el event.
            mCallback.OnActionDetected(ACTION_DETAIL);
            return true;
        } else {
            // Establim aquest lloc com l'actual i no consumim el click.
            mCallback.setCurrentLloc(mMarkersToLloc.get(marker));
            return false;
        }
    }

    /**
     * Listener per l'event de clicar a sobre d'una finestra d'informació. En aquest cas el lloc
     * sempre està seleccionat anteriorment.
     *
     * @param marker marcador al que pertany la finestra sobre els que s'ha fet click.
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        mCallback.OnActionDetected(ACTION_DETAIL);
    }

    /**
     * Actualitza la llista de llocs a la llista passada com argument. Eliminant els marcadors que
     * no es trobin a la llista, afegint la resta i fent zoom sobre el conjunt.
     *
     * @param filteredLlocs llista de llocs per reemplaçar la llista actual.
     */
    public void updateMarkers(List<Lloc> filteredLlocs) {
        mLlocs = filteredLlocs;
        removeMarkersNotInList(filteredLlocs);
        addMarkers(mLlocs);
        fixZoom(true);
    }

    /**
     * Itera sobre el mapa de marcadors i elimina totes les entrades que no es trobin la la llista
     * passada com argument
     *
     * @param filteredLlocs llista de llocs per conservar al mapa de marcadors.
     */
    private void removeMarkersNotInList(List<Lloc> filteredLlocs) {
        Map.Entry<Marker, Lloc> entry;
        Marker marker;
        Lloc lloc;

        for (Iterator<Map.Entry<Marker, Lloc>> it = mMarkersToLloc.entrySet().iterator(); it.hasNext(); ) {
            entry = it.next();
            marker = entry.getKey();
            lloc = entry.getValue();

            if (!filteredLlocs.contains(lloc)) {
                // Ho eliminem del mapa, ocultem la finestra de informació i eliminem el marcador.
                it.remove();
                marker.hideInfoWindow();
                marker.remove();
            }
        }
    }

    /**
     * Al crear-se la activitat comprovem si ja existeix un fragment niuat amb el mapa i si no es així
     * generem una nova instància.
     *
     * @param savedInstanceState bundle
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtenim un FragmentManager per fragments niuats
        FragmentManager fragmentManager = getChildFragmentManager();

        // Cerquem el fragment de mapa
        mMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        // Si no existeix instanciem un de nou.
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.mapFragment, mMapFragment).commit();
        }
    }
}