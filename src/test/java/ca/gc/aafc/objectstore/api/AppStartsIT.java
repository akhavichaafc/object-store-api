package ca.gc.aafc.objectstore.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ObjectStoreApiLauncher.class)
@ActiveProfiles("test")
public class AppStartsIT {
  
  @Inject
  private ObjectStoreConfiguration config;
  
  @Inject
  private MediaTypeToDcTypeConfiguration mediaTypeToDcTypeConfig;

  /**
   * Tests that the application with embedded Tomcat starts up successfully.
   */
  @Test
  public void startApp_OnStartUp_NoErrorsThrown() {
    
    //Make sure we can load the configuration files
    assertNotNull(config.getDefaultCopyright());
    assertFalse(StringUtils.isBlank(mediaTypeToDcTypeConfig.getToDcType().get("IMAGE").get(0)));
  }

}
