package ca.gc.aafc.objectstore.api.crud;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.objecstore.api.testsupport.DBBackedIntegrationTest;
import ca.gc.aafc.objectstore.api.test.TestConfig;


/**
 * Base class for CRUD-based Integration tests.
 * The main purpose is to ensure all entities can be saved/loaded/deleted from a database.
 * 
 * This base class with run a single test (see testCRUDOperations) to control to order of testing of save/find/remove.
 *
 */
@SpringBootTest(classes = TestConfig.class)
@Transactional
public abstract class BaseEntityCRUDIT extends DBBackedIntegrationTest {

  /**
   * Runs the three main CRUD methods while performing a transaction for each test.
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
	@Test
	public void testCRUDOperations() throws InstantiationException, IllegalAccessException {
		testSave();
		testFind();
		testRemove();
	}

	public abstract void testSave() ;
	
	public abstract void testFind() ;

	public abstract void testRemove() ;
	
}
