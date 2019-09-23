package ca.gc.aafc.objectstore.api.minio;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

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

  public MinioFileService(MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  @Inject
  public MinioFileService(@Value("${minio.scheme:}") String protocol,
      @Value("${minio.host:}") String host, @Value("${minio.port:}") int port,
      @Value("${minio.accessKey:}") String accessKey,
      @Value("${minio.secretKey:}") String secretKey)
      throws InvalidEndpointException, InvalidPortException {
    String endpoint = protocol + "://" + host;
    this.minioClient = new MinioClient(endpoint, port, accessKey, secretKey);
  }

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
    // minioClient.putObject("my-bucketname", "my-objectname", bais, Long.valueOf(bais.available()), null, null, "application/octet-stream");
    minioClient.putObject(bucket, fileName, iStream, "multipart");
  }

}
