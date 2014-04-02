package ioc.mustsee.data;

import junit.framework.TestCase;

/**
 * @author Javier García
 */
public class ImatgeTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testConstructor_complet() throws Exception {
        Imatge imatge = new Imatge(42, "titol", "picture.jpg", 1);
        assertEquals(42, imatge.id);
        assertEquals("titol", imatge.titol);
        assertEquals("picture.jpg", imatge.nomFitxer);
        assertEquals(1, imatge.llocId);
    }

    public void testConstructor_senseId() throws Exception {
        Imatge imatge = new Imatge("titol", "picture.jpg", 1);
        assertEquals(Imatge.sIdCounter - 1, imatge.id);
        assertEquals("titol", imatge.titol);
        assertEquals("picture.jpg", imatge.nomFitxer);
        assertEquals(1, imatge.llocId);
    }

    public void testConstructor_senseIdSequencial() throws Exception {
        Imatge imatge1 = new Imatge("titol1", "picture1.jpg", 1);
        Imatge imatge2 = new Imatge("titol2", "picture2.jpg", 2);
        assertNotSame(imatge1.id, imatge2.id);
    }

}
