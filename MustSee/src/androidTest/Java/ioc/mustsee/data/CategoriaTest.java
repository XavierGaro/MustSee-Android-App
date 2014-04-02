package ioc.mustsee.data;

import junit.framework.TestCase;

/**
 * @author Javier García
 */
public class CategoriaTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test P01-And
     *
     * @throws Exception
     */
    public void testConstructor_completa() throws Exception {
        Categoria categoria = new Categoria(42, "nom", "descripcio");
        assertEquals("nom", categoria.nom);
        assertEquals("descripcio", categoria.descripcio);
        assertEquals(42, categoria.id);
    }

    /**
     * Test P02-And
     *
     * @throws Exception
     */
    public void testConstructor_senseId() throws Exception {
        Categoria categoria = new Categoria("nom", "descripcio");
        assertEquals("nom", categoria.nom);
        assertEquals("descripcio", categoria.descripcio);
        assertEquals(Categoria.sIdCounter - 1, categoria.id);
    }

    /**
     * Test P03-And
     *
     * @throws Exception
     */
    public void testConstructor_senseIdSequencial() throws Exception {
        Categoria categoria1 = new Categoria("nom1", "descripcio1");
        Categoria categoria2 = new Categoria("nom2", "descripcio2");
        assertNotSame(categoria1.id, categoria2.id);
    }
}