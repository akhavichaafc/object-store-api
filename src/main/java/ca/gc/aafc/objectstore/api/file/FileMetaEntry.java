package ca.gc.aafc.objectstore.api.file;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents metadata about an uploaded file.
 * This class could be versioned in the future. 
 *
 */
@Getter
public class FileMetaEntry {
  
  public static final String VERSION_1_0 = "1.0";
  public static final String SUFFIX = "_meta.json";
  
  private String version = VERSION_1_0;
  
  @Setter
  private String originalFilename;
  
  @Setter
  private String sha1Hex;
  
  @Setter
  private String mediaType;
  
  @Setter
  private String fileExtension;

}
