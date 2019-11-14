package ca.gc.aafc.objectstore.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(classes = ObjectStoreApiLauncher.class)
@ActiveProfiles("test")
public class AppStartsIT {

  /**
   * Tests that the application with embedded Tomcat starts up successfully.
   */
  @Test
  public void startApp_OnStartUp_NoErrorsThrown() {
  }

}
