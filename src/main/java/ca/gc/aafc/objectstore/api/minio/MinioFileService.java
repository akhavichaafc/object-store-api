package ca.gc.aafc.objectstore.api.minio;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
  
  @Inject
  MinioClient minioClient;
 
  private String bucket ;  
  
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
  
  public MinioFileService(){
    
  }
    
  public MinioFileService(MinioClient minioClient) {
    this.minioClient = minioClient;
  }
      
  @SuppressWarnings("deprecation")
  public void storeFile(String fileName, InputStream iStream, String bucket) throws NoSuchAlgorithmException, IOException,
    InvalidKeyException, InvalidBucketNameException, NoResponseException, ErrorResponseException, 
    InternalException, InvalidArgumentException, InsufficientDataException, InvalidResponseException, 
    XmlPullParserException, RegionConflictException, InvalidEndpointException, InvalidPortException, URISyntaxException {
    
    boolean isExist = minioClient.bucketExists(bucket);
    if(!isExist) {
      minioClient.makeBucket(bucket);
    }
    // Upload the file to the bucket      
    minioClient.putObject(bucket,fileName,iStream, "multipart");
  }

}
