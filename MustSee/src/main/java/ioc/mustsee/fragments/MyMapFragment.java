package ioc.mustsee.fragments;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ioc.mustsee.R;
import ioc.mustsee.data.Lloc;

import static ioc.mustsee.fragments.OnFragmentActionListener.ACTION_DETAIL;


public class MyMapFragment extends MustSeeFragment implements GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "MyMapFragment";

    private static final int VELOCITAT_CAMARA_FOCUS = 500;
    HashMap<Marker, Lloc> mMarkersToLloc = new HashMap<Marker, Lloc>();
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private List<Lloc> mLlocs;

    public MyMapFragment(int fragmentId) {
        super(fragmentId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "Entrando en onCreateView");
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        Log.d(TAG, "Saliendo de onCreateView");
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Entrando en onResume()");

        if (mMap == null) {
            initMap();
        }
        Log.d(TAG, "Saliendo de onResume()");
    }


    private void initMap() {
        Log.d(TAG, "Entrando en initMap: " + mMap);

        /*
        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        SupportMapFragment mMapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.mMapFragment);
        */

        mMap = mMapFragment.getMap();
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mLlocs = mCallback.getLlocs();
        afegirMarcadors(mLlocs);
        fixZoom(true);

        Log.d(TAG, "Saliendo de en initMap: " + mMap);
    }

    private List<Marker> afegirMarcadors(List<Lloc> llocs) {
        Log.d(TAG, "Entrando en afegirMarcadors");
        List<Marker> marcadors = new ArrayList<Marker>();
        Marker marcador;
        for (Lloc lloc : llocs) {
            // Si el lloc ja te marcador, continuem
            if (mMarkersToLloc.containsValue(lloc)) {
                continue;
            }
            marcador = afegirMarcador(lloc);
            marcadors.add(marcador);
            mMarkersToLloc.put(marcador, lloc);
            Log.d(TAG, "Afegit marcador: " + mMarkersToLloc);
        }
        Log.d(TAG, "Saliendo de afegirMarcadors");
        return marcadors;
    }

    private Marker afegirMarcador(Lloc lloc) {
        Log.d(TAG, "Entrando en afegirMarcador: " + lloc.nom);
        // Retallem la descripció
        String shortDescripcio = lloc.descripcio;
        if (shortDescripcio.length() > 30) {
            shortDescripcio = shortDescripcio.substring(0, 30) + "...";
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(lloc.posicio)
                .title(lloc.nom)
                .snippet(shortDescripcio);
        if (lloc.iconResource != Lloc.DEFAULT_ICON) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(lloc.iconResource));
        }

        Log.d(TAG, "Saliendo de afegirMarcador: " + lloc.nom);
        return mMap.addMarker(markerOptions);
    }

    private void fixZoom(boolean animar) {
        Log.d(TAG, "Entrando en fixZoom");
        LatLngBounds.Builder bc = new LatLngBounds.Builder();

        for (Lloc lloc : mLlocs) {
            bc.include(lloc.posicio);
        }

        int height = this.getResources().getDisplayMetrics().heightPixels;
        int width2 = this.getResources().getDisplayMetrics().widthPixels;
        // TODO: Si es pantalla completa la ocupa entera, si es parcial ocupa 3/4
        int width = width2 * 3 / 4;

        if (animar) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(),
                    width, height, 100), 2000, null);
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(),
                    width, height, 100));
        }
        Log.d(TAG, "Saliendo de fixZoom");
    }

    public void setFocus(Lloc lloc) {
        Log.d(TAG, "Entrando en setFocus:" + lloc.nom);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(lloc.posicio), VELOCITAT_CAMARA_FOCUS, null);
        Marker marker = getMarcador(lloc);
        if (marker != null) marker.showInfoWindow();

        Toast.makeText(getActivity(), "Has cliclado en el marcador de: " + mMarkersToLloc.get(marker).nom, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Entrando de setFocus:" + lloc.nom);
    }


    private Marker getMarcador(Lloc lloc) {
        Log.d(TAG, "Entrando en getMarcador: " + lloc.nom);
        for (Map.Entry<Marker, Lloc> entry : mMarkersToLloc.entrySet()) {
            if (entry.getValue() == lloc) {
                Log.d(TAG, "Saliendo de getMarcador.");
                return entry.getKey();
            }
        }
        Log.d(TAG, "Saliendo de getMarcador.");
        return null;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "Entrando en onMarkerClick");

        // Comprobamos si ya està seleccionado
        if (mMarkersToLloc.get(marker) == mCallback.getLlocActual()) {
            // Es el mismo, abrimos detalle
            mCallback.OnActionDetected(ACTION_DETAIL);
            Log.d(TAG, "Saliendo de onMarkerClick.");
            return true; // Consumimos el click, no se ejecuta el comportamiento normal.
        } else {
            // Llamamos a main activity para que lo procese, despues pasarà a setFocus o mostrar detalle.
            mCallback.setLlocActual(mMarkersToLloc.get(marker));
            // Devolvemos false, asi que se  ejecuta el efecto normal de clicar, que es mostrar el detalle y centrar (no consumimos el evento).
            Log.d(TAG, "Saliendo de onMarkerClick.");
            return false;
        }
    }


    public void updateMarkers(List<Lloc> llocsFiltrats) {
        Log.d(TAG, "Entrando en updateMarkers");
        // Añadimos los nuevos
        mLlocs = llocsFiltrats;
        removeMarkersNotInList(llocsFiltrats);
        afegirMarcadors(mLlocs);
        fixZoom(true);
        Log.d(TAG, "Saliendo de updateMarkers.");
    }

    private void removeMarkersNotInList(List<Lloc> llocsFiltrats) {
        Log.d(TAG, "Entrando en removeMarkersNotInList");
        Marker marker;
        Lloc lloc;


        // Iterator
        Iterator<Map.Entry<Marker, Lloc>> it = mMarkersToLloc.entrySet().iterator();
        Map.Entry<Marker, Lloc> entry;
        while (it.hasNext()) {
            entry = it.next();
            marker = entry.getKey();
            lloc = entry.getValue();

            if (llocsFiltrats.contains(lloc)) {
                // El lloc continua a la llista
            } else {
                // Cal eliminar-lo
                it.remove();
                marker.hideInfoWindow(); // Per si de cas
                marker.remove();
            }


        }
        Log.d(TAG, "Saliendo de removeMarkersNotInList");
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "Entrando en onInfoWindoclick: " + marker);
        mCallback.OnActionDetected(ACTION_DETAIL);
    }

    /*
    @Override
    public void onDestroyView() {

        Fragment fragment = (getChildFragmentManager().findFragmentById(R.id.mapFragment));
        android.support.v4.app.FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        Log.d(TAG, "Existe el fragmento de mapa? "+fragment);

        if (fragment != null) {
            Log.d(TAG, "Existe el fragmento de mapa, los eliminamos");
            try {
                //FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                ft.commit();
                //ft.commit(); // No se puede hacer el commit aquí, lo dejamos pendiente hasta que lo envie la actividad principal


            } catch (Exception e) {
                // TODO esto no se puede dejar así
                Log.e(TAG, "Error al destruir el fragmento: " + e);
            }
        } else {
            Log.d(TAG, "El fragmento es null");

        }

        super.onDestroyView();

    }


        /*
        Fragment fragment = (getChildFragmentManager().findFragmentById(R.id.mapFragment));
        android.support.v4.app.FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        if (fragment != null) {
            Log.d(TAG, "Existe el fragmento de mapa, los eliminamos");
            try {
                //FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                //ft.commit(); // No se puede hacer el commit aquí, lo dejamos pendiente hasta que lo envie la actividad principal


            } catch (Exception e) {
                // TODO esto no se puede dejar así
                Log.e(TAG, "Error al destruir el fragmento: " + e);
            }
        } else {
            Log.d(TAG, "El fragmento es null");
        }


        /*
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.mMapFragment));

        if (fragment != null) {
            Log.d(TAG, "El fragmento NO es null");
            try {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                ft.commit();
                Log.d(TAG, "Fragmento destruido");
            } catch (Exception e) {
                // TODO esto no se puede dejar así
                Log.e(TAG, "Error al destruir el fragmento");
            }
        } else {
            Log.d(TAG, "El fragmento es null");
        }

        Log.d(TAG, "Saliendo de onDestroyView");
*/
        /*



        // TODO: mMap es un fragmento nested en otro fragmento, por eso da tantos problemas
        mCallback.deleteMapFragment();
        mMap = null;
        mMapFragment = null;
        mMarkersToLloc.clear();
*/


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "Dentro de onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        //android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //SupportMapFragment mMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mMapFragment);

        FragmentManager fragmentManager = getChildFragmentManager();
        mMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mapFragment);
        if (mMapFragment == null) {
            Log.d(TAG, "No hay fragmento, nueva instancia");
            mMapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.mapFragment, mMapFragment).commit();
        }
    }


}