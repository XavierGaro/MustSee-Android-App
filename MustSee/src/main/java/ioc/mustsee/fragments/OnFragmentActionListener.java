package ioc.mustsee.fragments;


import android.os.Bundle;

import java.util.List;

import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Lloc;

public interface OnFragmentActionListener {
    public static final int ACTION_MAIN = 0;
    public static final int ACTION_LOG = 1;
    public static final int ACTION_SEARCH = 2;
    public static final int ACTION_EXPLORE = 3;
    public static final int ACTION_REGISTER = 4;
    public static final int ACTION_BACK = 5;
    public static final int ACTION_DETAIL = 6;
    public static final int ACTION_GALLERY = 7;
    public static final int ACTION_PHOTO = 8;

    void OnActionDetected(int action, Bundle bundle);

    public void OnActionDetected(int action);

    List<Lloc> getLlocs();

    List<Categoria> getCategories();

    Lloc getLlocActual();

    void setLlocActual(Lloc lloc);

    List<Lloc> getLlocsFiltrats();

    void setLlocsFiltrats(List<Lloc> llocs);

    void refreshLlocActual();

    void deleteMapFragment();

}
