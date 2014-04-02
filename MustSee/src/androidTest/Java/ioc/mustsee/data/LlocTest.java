package ioc.mustsee.data;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

/**
 * @author Javier García
 */
public class LlocTest extends TestCase {
    private static final float DELTA = 0.0001f;

    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test P07-And
     *
     * @throws Exception
     */
    public void testBuilder_minimLatLng() throws Exception {
        Lloc lloc = new Lloc.LlocBuilder("test", new LatLng(1, 2)).build();
        assertEquals(Lloc.sIdCounter - 1, lloc.id);
        assertEquals("test", lloc.nom);
        assertEquals("", lloc.descripcio);
        assertEquals(1f, lloc.posicio.latitude, DELTA);
        assertEquals(2f, lloc.posicio.longitude, DELTA);
        assertEquals(Lloc.NO_ICON, lloc.iconResource);
        assertEquals(0, lloc.categoriaId);
    }

    /**
     * Test P08-And
     * @throws Exception
     */
    public void testBuilder_minimLatitudILongitud() throws Exception {
        Lloc lloc = new Lloc.LlocBuilder("test", 1f, 2f).build();
        assertEquals(Lloc.sIdCounter - 1, lloc.id);
        assertEquals("test", lloc.nom);
        assertEquals("", lloc.descripcio);
        assertEquals(1f, lloc.posicio.latitude, DELTA);
        assertEquals(2f, lloc.posicio.longitude, DELTA);
        assertEquals(Lloc.NO_ICON, lloc.iconResource);
        assertEquals(0, lloc.categoriaId);
    }

    /**
     * Test P09-And
     * @throws Exception
     */
    public void testBuilder_complet() throws Exception {
        Lloc lloc = new Lloc.LlocBuilder("test", 1f, 2f)
                .id(42)
                .descripcio("descripcio")
                .categoria(100)
                .icon(1)
                .build();

        assertEquals(42, lloc.id);
        assertEquals("test", lloc.nom);
        assertEquals("descripcio", lloc.descripcio);
        assertEquals(1f, lloc.posicio.latitude, DELTA);
        assertEquals(2f, lloc.posicio.longitude, DELTA);
        assertEquals(1, lloc.iconResource);
        assertEquals(100, lloc.categoriaId);
    }

    /**
     * Test P10-And
     * @throws Exception
     */
    public void testConstructor_senseIdSequencial() throws Exception {
        Lloc lloc1 = new Lloc.LlocBuilder("test1", 1f, 2f).build();
        Lloc lloc2 = new Lloc.LlocBuilder("test2", 2f, 1f).build();
        assertNotSame(lloc1.id, lloc2.id);
    }

    /**
     * Test P11-And
     *
     * @throws Exception
     */
    public void testGetShortDescripcio_curta() throws Exception {
        Lloc lloc = new Lloc.LlocBuilder("test", 1f, 2f)
                .id(42)
                .descripcio("descripció curta").build();
        assertEquals("descripció curta", lloc.getShortDescripcio());
    }

    /**
     * Test P12-And
     * @throws Exception
     */
    public void testGetShortDescricio_llarga() throws Exception {
        Lloc lloc = new Lloc.LlocBuilder("test", 1f, 2f)
                .id(42)
                .descripcio("1234 1234 1234 1234 1234 1234 1234 1234 1234 ").build();
        assertEquals("1234 1234 1234 1234 1234 12...", lloc.getShortDescripcio());
    }

    /**
     * Test P13-And
     * @throws Exception
     */
    public void testAddImatge_noNula() throws Exception {
        Lloc lloc = new Lloc.LlocBuilder("test", 1f, 2f).build();
        Imatge imatge = new Imatge("titol", "picture.jpg", lloc.id);
        lloc.addImatge(imatge);
        assertNotNull(lloc.getImatgePrincipal());
        assertEquals(imatge, lloc.getImatgePrincipal());
    }

    /**
     * Test P14-And
     * @throws Exception
     */
    public void testAddImatge_nula() throws Exception {
        Lloc lloc = new Lloc.LlocBuilder("test", 1f, 2f).build();
        lloc.addImatge(null);
        assertNull(lloc.getImatgePrincipal());
    }

    /**
     * Test P15-And
     * @throws Exception
     */
    public void testGetImatgePrincipal_senseAfegirImatge() throws Exception {
        Lloc lloc = new Lloc.LlocBuilder("test", 1f, 2f).build();
        assertNull(lloc.getImatgePrincipal());
    }

    /**
     * Test P16-And
     * @throws Exception
     */
    public void testGetImatgePrincipal_afegintUnaImatge() throws Exception {
        Lloc lloc = new Lloc.LlocBuilder("test", 1f, 2f).build();
        Imatge imatge = new Imatge("titol", "picture.jpg", lloc.id);
        lloc.addImatge(imatge);
        assertEquals(imatge, lloc.getImatgePrincipal());
    }

    /**
     * Test P17-And
     * @throws Exception
     */
    public void testGetImatgePrincipal_afegintMesDeUnaImatge() {
        Lloc lloc = new Lloc.LlocBuilder("test", 1f, 2f).build();
        Imatge imatge1 = new Imatge("titol1", "picture1.jpg", lloc.id);
        Imatge imatge2 = new Imatge("titol2", "picture2.jpg", lloc.id);
        Imatge imatge3 = new Imatge("titol3", "picture3.jpg", lloc.id);
        lloc.addImatge(imatge1);
        lloc.addImatge(imatge2);
        lloc.addImatge(imatge3);
        assertEquals(imatge1, lloc.getImatgePrincipal());
    }

    /**
     * Test P18-And
     * @throws Exception
     */
    public void testGetImatges_senseImatges() throws Exception {
        Lloc lloc = new Lloc.LlocBuilder("test", 1f, 2f).build();
        assertTrue(lloc.getImages().isEmpty());
    }

    /**
     * Test P19-And
     * @throws Exception
     */
    public void testGetImatges_ambUnaImatge() throws Exception {
        Lloc lloc = new Lloc.LlocBuilder("test", 1f, 2f).build();
        Imatge imatge = new Imatge("titol", "picture.jpg", lloc.id);
        lloc.addImatge(imatge);
        assertEquals(1, lloc.getImages().size());
    }

    /**
     * Test P20-And
     * @throws Exception
     */
    public void testGetImatges_ambMesDeUnaImatge() throws Exception {
        Lloc lloc = new Lloc.LlocBuilder("test", 1f, 2f).build();
        Imatge imatge1 = new Imatge("titol1", "picture1.jpg", lloc.id);
        Imatge imatge2 = new Imatge("titol2", "picture2.jpg", lloc.id);
        Imatge imatge3 = new Imatge("titol3", "picture3.jpg", lloc.id);
        lloc.addImatge(imatge1);
        lloc.addImatge(imatge2);
        lloc.addImatge(imatge3);
        assertEquals(3, lloc.getImages().size());
    }

    /**
     * Test P21-And
     * @throws Exception
     */
    public void testGetImatges_esConservaElOrdre() throws Exception {
        Lloc lloc = new Lloc.LlocBuilder("test", 1f, 2f).build();
        Imatge imatge1 = new Imatge("titol1", "picture1.jpg", lloc.id);
        Imatge imatge2 = new Imatge("titol2", "picture2.jpg", lloc.id);
        Imatge imatge3 = new Imatge("titol3", "picture3.jpg", lloc.id);
        lloc.addImatge(imatge1);
        lloc.addImatge(imatge2);
        lloc.addImatge(imatge3);
        assertEquals(imatge1, lloc.getImages().get(0));
        assertEquals(imatge2, lloc.getImages().get(1));
        assertEquals(imatge3, lloc.getImages().get(2));
    }
}
