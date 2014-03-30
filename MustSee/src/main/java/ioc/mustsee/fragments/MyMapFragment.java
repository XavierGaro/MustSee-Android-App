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


// TODO: Al tocar el mapa se pierde el focus de la lista y la selección
public class MyMapFragment extends MustSeeFragment implements GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "MyMapFragment";

    private static final int VELOCITAT_CAMARA_FOCUS = 500;
    HashMap<Marker, Lloc> mMarkersToLloc = new HashMap<Marker, Lloc>();
    private GoogleMap mMap;
    private View view;
    private SupportMapFragment mapFragment;
    private List<Lloc> llocs;

    public MyMapFragment(int fragmentId) {
        super(fragmentId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Log.d(TAG, "onCreateView para MyMapFragment, hay view? " + view);

        if (view == null) {
            Log.d(TAG, "No hay view, la creamos" + view);
            view = inflater.inflate(R.layout.fragment_map, container, false);
        }

        Log.d(TAG, "Eliminamos las views del grupo, childs: " + container.getChildCount());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Estamos dentro de onResume");
        initMap();
    }


    private void initMap() {
        Log.d(TAG, "Entrando en initMap");

        /*
        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.mapFragment);
*/
        Log.d(TAG, "Hay mapFragment? " + mapFragment);

        mMap = mapFragment.getMap();
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMyLocationEnabled(true);

        llocs = mCallback.getLlocs();

        Log.d(TAG, "Existe mMap antes de afegirMarcadors?" + mMap);

        afegirMarcadors(llocs);


        fixZoom(true);

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
            //mLlocToMarker.put(lloc,marcador);
            Log.d(TAG, "afegit marcador: " + mMarkersToLloc);
        }
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

        Log.d(TAG, "Añadiendo marcador para LLoc: " + lloc.nom);
        Log.d(TAG, "Existe mMap? " + mMap);

        return mMap.addMarker(markerOptions);
    }

    private void fixZoom(boolean animar) {
        Log.d(TAG, "Entrando en fixZoom");
        LatLngBounds.Builder bc = new LatLngBounds.Builder();

        for (Lloc lloc : llocs) {
            bc.include(lloc.posicio);
        }

        int height = this.getResources().getDisplayMetrics().heightPixels;
        int width2 = this.getResources().getDisplayMetrics().widthPixels;
        // TODO: Si es pantalla completa la ocupa entera, si es parcial ocupa 3/4
        int width = width2 * 3 / 4;
        //Log.d(TAG," Width mapa: "+width);
        //Log.d(TAG," Width2 mapa: "+width);

        if (animar) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(),
                    width, height, 100), 2000, null);
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(),
                    width, height, 100));
        }


    }

    public void setFocus(Lloc lloc) {
        Log.d(TAG, "Entrando en setFocus:" + lloc.nom);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(lloc.posicio), VELOCITAT_CAMARA_FOCUS, null);
        Marker marker = getMarcador(lloc);
        if (marker != null) marker.showInfoWindow();

        Toast.makeText(getActivity(), "Has cliclado en el marcador de: " + mMarkersToLloc.get(marker).nom, Toast.LENGTH_SHORT).show();
    }


    private Marker getMarcador(Lloc lloc) {
        Log.d(TAG, "Entrando en getMarcador: " + lloc.nom);
        for (Map.Entry<Marker, Lloc> entry : mMarkersToLloc.entrySet()) {
            if (entry.getValue() == lloc) {
                return entry.getKey();
            }
        }
        return null;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "Entrando en onMarkerClick");
        Log.d(TAG, "Marcadores: " + mMarkersToLloc);
        Log.d(TAG, "Has cliclado en el marcador de: " + mMarkersToLloc.get(marker).nom);

        // Comprobamos si ya està seleccionado
        if (mMarkersToLloc.get(marker) == mCallback.getLlocActual()) {
            // Es el mismo, abrimos detalle
            mCallback.OnActionDetected(ACTION_DETAIL);
            return true; // Consumimos el click, no se ejecuta el comportamiento normal.
        } else {
            // Llamamos a main activity para que lo procese, despues pasarà a setFocus o mostrar detalle.
            mCallback.setLlocActual(mMarkersToLloc.get(marker));
            // Devolvemos false, asi que se  ejecuta el efecto normal de clicar, que es mostrar el detalle y centrar (no consumimos el evento).
            return false;
        }


    }


    public void updateMarkers(List<Lloc> llocsFiltrats) {
        Log.d(TAG, "Entrando en updateMarkers");
        // Añadimos los nuevos
        llocs = llocsFiltrats;
        removeMarkersNotInList(llocsFiltrats);
        afegirMarcadors(llocs);
        Log.d(TAG, "Actualitzant la llista de markers, punts: " + llocs.size());
        fixZoom(true);
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
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "Entrando en onInfoWindoclick: " + marker);
        mCallback.OnActionDetected(ACTION_DETAIL);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "Entrando en onDestroyView: " + fragmentId);
        super.onDestroyView();

        /*
        Fragment fragment = (getChildFragmentManager().findFragmentById(R.id.mapFragment));
        android.support.v4.app.FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        if (fragment != null) {
            Log.d(TAG, "El fragmento NO es null");
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
*/

        /*
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.mapFragment));

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
        if (view != null) {
            ViewGroup parentViewGroup = (ViewGroup) view.getParent();
            Log.d(TAG, "parentViewGroup es null?: " + parentViewGroup);
            if (parentViewGroup != null) {
                Log.d(TAG, "Numero de childs antes de eliminar: " + parentViewGroup.getChildCount());
                parentViewGroup.removeAllViews();
                Log.d(TAG, "Numero de childs despues de eliminar: " + parentViewGroup.getChildCount());
            }

        }

        // TODO: mMap es un fragmento nested en otro fragmento, por eso da tantos problemas
        mCallback.deleteMapFragment();
        mMap = null;
        mapFragment = null;
        mMarkersToLloc.clear();

        return;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "Dentro de onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        //android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mapFragment);

        FragmentManager fragmentManager = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mapFragment);
        if (mapFragment == null) {
            Log.d(TAG, "No hay fragmento, nueva instancia");
            mapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.mapFragment, mapFragment).commit();
        }
    }


}