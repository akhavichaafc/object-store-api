package ca.gc.aafc.objectstore.api;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class related to mapping between dcFormat and dcType.
 *
 */
@Configuration
@PropertySource(value = "classpath:MediaTypeToDcType.yml", factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties
public class MediaTypeToDcTypeConfiguration {

  private LinkedHashMap<String, LinkedList<String>> toDcType;
  
  public LinkedHashMap<String, LinkedList<String>> getToDcType() {
    return toDcType;
  }

  public void setToDcType(LinkedHashMap<String, LinkedList<String>> toDcType) {
    this.toDcType = toDcType;
  }
    
}
