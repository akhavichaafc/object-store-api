package ca.gc.aafc.objectstore.api.file;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FileObjectInfo {
  
  private long length;
  private String contentType;

}
