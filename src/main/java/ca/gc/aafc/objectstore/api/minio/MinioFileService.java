package ca.gc.aafc.objectstore.api.minio;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidArgumentException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.NoResponseException;
import io.minio.errors.RegionConflictException;


@Service
public class MinioFileService{
  
  @Value("${minio.protocol:}")
  private String protocol;

  @Value("${minio.host:}")
  private String host; 
  
  @Value("${minio.port:}")
  private int port;
  
  @Value("${minio.accessKey:}")  
  private String accessKey ;
  
  @Value("${minio.secretKey:}")  
  private String secretKey ;
  
  @Value("${minio.bucket:}")  
  private String bucket ;  
  
  private MinioClient minioClient ;
  
  public String getBucket() {
    return bucket;
  }

  public void setBucket(String bucket) {
    this.bucket = bucket;
  }
  
  public MinioClient getMinioClient() {
    return minioClient;
  }

  public void setMinioClient(MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  @PostConstruct
  public void initializeMinioClient() throws MalformedURLException, URISyntaxException,
  InvalidEndpointException, InvalidPortException {
    
      URIBuilder uriBuilder = new URIBuilder();
      uriBuilder.setScheme(protocol);
      uriBuilder.setHost(host);
      uriBuilder.setPort(port);
      
      URL url = uriBuilder.build().toURL();
      minioClient = new MinioClient(url, accessKey, secretKey );
    
  }
  
  @SuppressWarnings("deprecation")
  public void storeFile(MultipartFile file) throws NoSuchAlgorithmException, IOException,
    InvalidKeyException, InvalidBucketNameException, NoResponseException, ErrorResponseException, 
    InternalException, InvalidArgumentException, InsufficientDataException, InvalidResponseException, 
    XmlPullParserException, RegionConflictException, InvalidEndpointException, InvalidPortException, URISyntaxException {
    
    boolean isExist = minioClient.bucketExists(bucket);
    if(!isExist) {
      minioClient.makeBucket(bucket);
    }
    // Upload the file to the bucket      
    minioClient.putObject(bucket,file.getName(),file.getInputStream(), "multipart");
  }

}
