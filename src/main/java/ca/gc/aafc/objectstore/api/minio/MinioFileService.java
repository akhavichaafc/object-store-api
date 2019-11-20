package ca.gc.aafc.objectstore.api.minio;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.xmlpull.v1.XmlPullParserException;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.objectstore.api.file.FileInformationService;
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
public class MinioFileService implements FileInformationService {

  private final MinioClient minioClient;
  private final ObjectMapper objectMapper;
  
  @Inject
  public MinioFileService(MinioClient minioClient,  Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
    this.minioClient = minioClient;
    this.objectMapper = jackson2ObjectMapperBuilder.build();
  }

  /**
   * Store a file (received as an InputStream) on Minio into a specific bucket.
   * The bucket is expected to exist.
   * 
   * @param fileName filename to be used in Minio
   * @param iStream inputstream to send to Minio (won't be closed)
   * @param bucket name of the bucket (will NOT be created if doesn't exist)
   * @param headersMap optional, null if none
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
  public void storeFile(String fileName, InputStream iStream, String contentType, String bucket, Map<String, String> headersMap)
      throws NoSuchAlgorithmException, IOException, InvalidKeyException, InvalidBucketNameException,
      NoResponseException, ErrorResponseException, InternalException, InvalidArgumentException,
      InsufficientDataException, InvalidResponseException, XmlPullParserException,
      RegionConflictException, InvalidEndpointException, InvalidPortException, URISyntaxException {

    // Upload the file to the bucket
    minioClient.putObject(bucket, fileName, iStream, null, headersMap, null, contentType);
  }
  
  public void ensureBucketExists(String bucketName) throws IOException {
    try {
      if (!minioClient.bucketExists(bucketName)) {
        minioClient.makeBucket(bucketName);
      }
    } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException
        | InsufficientDataException | NoResponseException | ErrorResponseException
        | InternalException | InvalidResponseException | RegionConflictException
        | XmlPullParserException e) {
      throw new IOException(e);
    }
  }
  
  @Override
  public boolean bucketExists(String bucketName) {
    try {
      return minioClient.bucketExists(bucketName);
    } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException
        | InsufficientDataException | NoResponseException | ErrorResponseException
        | InternalException | InvalidResponseException | IOException | XmlPullParserException e) {
      log.info("bucketExists exception:", e);
    }
    return false;
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
  

  public <T> T getJsonFileContentAs(String bucketName, String filename, Class<T> clazz)
      throws IOException {
    return objectMapper.readValue(getFile(filename, bucketName), clazz);
  }

  /**
   * See {@link FileInformationService#getFileInfo(String, String)}
   */
  public Optional<FileObjectInfo> getFileInfo(String fileName, String bucketName) throws IOException {
    ObjectStat objectStat;
    try {
      objectStat = minioClient.statObject(bucketName, fileName);
      
      return Optional.of(FileObjectInfo.builder()
          .length(objectStat.length())
          .contentType(objectStat.contentType())
          .headerMap(objectStat.httpHeaders())
          .build());
    } catch (ErrorResponseException erEx) {
      if (ErrorCode.NO_SUCH_KEY == erEx.errorResponse().errorCode()
          || ErrorCode.NO_SUCH_BUCKET == erEx.errorResponse().errorCode()) {
        log.debug("file: {}, bucket: {} : not found", fileName, bucketName);
        return Optional.empty();
      }
      throw new IOException(erEx);
    } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException
        | InsufficientDataException | NoResponseException | InternalException
        | InvalidResponseException | InvalidArgumentException | XmlPullParserException e) {
      throw new IOException(e);
    }
  }
  
  /**
   * Check if at least 1 object on the provided bucket starts with a specific prefix.
   * 
   * @param bucketName
   * @param prefix
   * @return at least 1 object with the provided prefix exists
   */
  @Override
  public boolean isFileWithPrefixExists(String bucketName, String prefix) {
    try {
      return minioClient.listObjects(bucketName, prefix).iterator().hasNext();
    } catch (XmlPullParserException e) {
      log.info("listObjects exception:", e);
    }
    return false;
  }

}
