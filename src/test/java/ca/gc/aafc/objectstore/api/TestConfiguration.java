package ca.gc.aafc.objectstore.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.entity.ContentType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.xmlpull.v1.XmlPullParserException;

import ca.gc.aafc.objectstore.api.file.FileController;
import ca.gc.aafc.objectstore.api.file.FileObjectInfo;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.ResponseHeader;
import io.minio.Result;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.messages.Item;

/**
 * 
 * Configuration used to override bean in the context of Integration testing.
 *
 */
@Configuration
public class TestConfiguration {
  
  @Primary
  @Bean
  public MinioClient initMinioClient() {
    try {
      return new MinioClientStub();
    } catch (InvalidEndpointException | InvalidPortException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * Stub used to replace MinioClient for testing.
   *
   */
  public static class MinioClientStub extends MinioClient {
    
    public static final UUID TEST_FILE_IDENTIFIER = UUID.randomUUID();
    public static final String TEST_FILE_NAME = TEST_FILE_IDENTIFIER.toString() + ".jpg";
    public static final String TEST_ORIGINAL_FILENAME = "myfile.jpg";

    public MinioClientStub() throws InvalidEndpointException, InvalidPortException {
      super("localhost");
    }
    
    @Override
    public boolean bucketExists(String bucketName){
      return true;
    }
    
    @Override
    public Iterable<Result<Item>> listObjects(String bucketName, String prefix) {
      Item item;
      try {
        item = new Item(TEST_FILE_NAME, false);
        Result<Item> result = new Result<Item>(item, null);
        Iterator<Result<Item>> iterator = Collections.singletonList(result).iterator();
        return () -> iterator;
      } catch (XmlPullParserException e) {
        e.printStackTrace();
      }
      return null;
    }
    
    @Override
    public ObjectStat statObject (String bucketName, String fileName) {
      ResponseHeader rh = new ResponseHeader();
      rh.setContentType(ContentType.IMAGE_JPEG.toString());
      rh.setLastModified("Tue, 15 Nov 1994 12:45:26 GMT");
      
      Map<String, List<String>> httpHeaders = new HashMap<>();
      httpHeaders.put(FileObjectInfo.CUSTOM_HEADER_PREFIX + FileController.HEADER_ORIGINAL_FILENAME, 
          Collections.singletonList(TEST_ORIGINAL_FILENAME));
      
      return new ObjectStat(bucketName, fileName, rh, httpHeaders);
    }
    
  }

}
