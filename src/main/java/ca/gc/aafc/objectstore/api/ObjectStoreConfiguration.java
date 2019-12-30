package ca.gc.aafc.objectstore.api;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "objectstore")
public class ObjectStoreConfiguration {
  
  private String defaultLicenceURL;
  private String defaultCopyright;
  
/*  public ObjectStoreConfiguration(String defaultLicence, String defaultCopyright) {
    this.defaultLicence = defaultLicence;
    this.defaultCopyright = defaultCopyright;
  }*/

  public void setDefaultLicenceURL(String defaultLicenceURL) {
    this.defaultLicenceURL = defaultLicenceURL;
  }

  public void setDefaultCopyright(String defaultCopyright) {
    this.defaultCopyright = defaultCopyright;
  }

  public String getDefaultLicenceURL() {
    return defaultLicenceURL;
  }

  public String getDefaultCopyright() {
    return defaultCopyright;
  }

}
