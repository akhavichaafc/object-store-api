package ca.gc.aafc.objectstore.api.minio;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
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
public class MinioFileService {

  private final MinioClient minioClient;

  @Inject
  public MinioFileService(
      @Value("${minio.scheme:}") String scheme,
      @Value("${minio.host:}") String host,
      @Value("${minio.port:}") int port,
      @Value("${minio.accessKey:}") String accessKey,
      @Value("${minio.secretKey:}") String secretKey)
      throws InvalidEndpointException, InvalidPortException, URISyntaxException {
    
    URI uri = new URIBuilder().setScheme(scheme).setHost(host).build();
    this.minioClient = new MinioClient(uri.toString(), port, accessKey, secretKey);
  }
  
  public MinioFileService(MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  /**
   * Store a file (received as a InputStream) on Minio into a specific bucket.
   * If the bucket doesn't exist, it will be created.
   * 
   * @param fileName filename to be used in Minio
   * @param iStream inputstream to send through Minio client (won't be closed)
   * @param bucket name of the bucket (will eb created if doesn't exist)
   * @throws NoSuchAlgorithmException
   * @throws IOException
   * @throws InvalidKeyException
   * @throws InvalidBucketNameException
   * @throws NoResponseException
   * @throws ErrorResponseException
   * @throws InternalException
   * @throws InvalidArgumentException
   * @throws InsufficientDataException
   * @throws InvalidResponseException
   * @throws XmlPullParserException
   * @throws RegionConflictException
   * @throws InvalidEndpointException
   * @throws InvalidPortException
   * @throws URISyntaxException
   */
  public void storeFile(String fileName, InputStream iStream, String bucket)
      throws NoSuchAlgorithmException, IOException, InvalidKeyException, InvalidBucketNameException,
      NoResponseException, ErrorResponseException, InternalException, InvalidArgumentException,
      InsufficientDataException, InvalidResponseException, XmlPullParserException,
      RegionConflictException, InvalidEndpointException, InvalidPortException, URISyntaxException {

    boolean isExist = minioClient.bucketExists(bucket);
    if (!isExist) {
      minioClient.makeBucket(bucket);
    }
    
    // Upload the file to the bucket
    minioClient.putObject(bucket, fileName, iStream, null, null, null, ContentType.APPLICATION_OCTET_STREAM.getMimeType());
  }

}
