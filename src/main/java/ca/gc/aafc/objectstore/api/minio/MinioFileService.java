package ca.gc.aafc.objectstore.api.minio;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xmlpull.v1.XmlPullParserException;

import ca.gc.aafc.objectstore.api.file.FileObjectInfo;
import io.minio.ErrorCode;
import io.minio.MinioClient;
import io.minio.ObjectStat;
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
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
  public void storeFile(String fileName, InputStream iStream, String contentType, String bucket)
      throws NoSuchAlgorithmException, IOException, InvalidKeyException, InvalidBucketNameException,
      NoResponseException, ErrorResponseException, InternalException, InvalidArgumentException,
      InsufficientDataException, InvalidResponseException, XmlPullParserException,
      RegionConflictException, InvalidEndpointException, InvalidPortException, URISyntaxException {

    boolean isExist = minioClient.bucketExists(bucket);
    if (!isExist) {
      minioClient.makeBucket(bucket);
    }
    
    // Upload the file to the bucket
    minioClient.putObject(bucket, fileName, iStream, null, null, null, contentType);
  }
  
  public InputStream getFile(String fileName, String bucketName) throws IOException {
    try {
      return minioClient.getObject(bucketName, fileName);
    } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException
        | InsufficientDataException | NoResponseException | ErrorResponseException
        | InternalException | InvalidArgumentException | InvalidResponseException
        | XmlPullParserException e) {
      throw new IOException(e);
    }
  }
  
  /**
   * Get information about a file as {@link FileObjectInfo}.
   * @param fileName
   * @param bucketName
   * @return
   * @throws IOException
   */
  public Optional<FileObjectInfo> getFileInfo(String fileName, String bucketName) throws IOException {

    ObjectStat objectStat;
    try {
      objectStat = minioClient.statObject(bucketName, fileName);
      return Optional.of(
          FileObjectInfo.builder()
          .length(objectStat.length())
          .contentType(objectStat.contentType())
          .build());
    } catch (ErrorResponseException erEx) {
      if (ErrorCode.NO_SUCH_KEY == erEx.errorResponse().errorCode() ||
          ErrorCode.NO_SUCH_BUCKET == erEx.errorResponse().errorCode()) {
        log.debug("file: {}, bucket: {} : not found", fileName, bucketName);
        return Optional.empty();
      }
      throw new IOException(erEx);
    }
    catch(  
        InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException
        | InsufficientDataException | NoResponseException
        | InternalException | InvalidResponseException | InvalidArgumentException
        | XmlPullParserException e) {
      throw new IOException(e);
    }

  }

}
