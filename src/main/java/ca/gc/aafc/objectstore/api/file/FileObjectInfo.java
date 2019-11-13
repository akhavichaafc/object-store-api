package ca.gc.aafc.objectstore.api.file;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FileObjectInfo {
  
  public static final String CUSTOM_HEADER_PREFIX = "x-amz-meta-";
  
  private long length;
  private String contentType;
  private Map<String, List<String>> headerMap;
  
  public List<String> extractHeader(String header) {
    if(headerMap == null || headerMap.isEmpty()) {
      return Collections.emptyList();
    }
    
    for(String key : headerMap.keySet()) {
      if(key.equalsIgnoreCase(header)) {
         return headerMap.get(key);
      }
    }
    return Collections.emptyList();
  }

}
