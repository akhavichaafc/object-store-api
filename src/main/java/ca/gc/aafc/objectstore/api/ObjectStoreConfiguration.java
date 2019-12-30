package ca.gc.aafc.objectstore.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "objectstore")
public class ObjectStoreConfiguration {
  
  private String defaultLicenceURL;
  private String defaultCopyright;
  
  public ObjectStoreConfiguration(String defaultLicenceURL, String defaultCopyright) {
    this.defaultLicenceURL = defaultLicenceURL;
    this.defaultCopyright = defaultCopyright;
  }

  public String getDefaultLicenceURL() {
    return defaultLicenceURL;
  }

  public String getDefaultCopyright() {
    return defaultCopyright;
  }

}
