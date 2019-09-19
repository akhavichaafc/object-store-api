package ca.gc.aafc.objectstore.api.minio;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MinioServiceIT {
  
  @Autowired
  private MinioFileService fileService;
  
   @Test
   public void callingStoreFile_whenSucceeds_bucketShouldHaveTheFile() throws InvalidKeyException,
   NoSuchAlgorithmException, InvalidBucketNameException, NoResponseException, ErrorResponseException, 
   InternalException, InvalidArgumentException, InsufficientDataException, InvalidResponseException, 
   RegionConflictException, InvalidEndpointException, InvalidPortException, IOException, 
   XmlPullParserException, URISyntaxException{
     
     MockMultipartFile mockMultipartFile = new MockMultipartFile("File", "file2.jpg", "img", 
           new byte[] { 1, 2, 3, 4, 5, 66, 7, 7, 8, 9, 77, 8, 9, 0 });
     fileService.storeFile(mockMultipartFile);     
     MinioClient minioClient = fileService.getMinioClient();
     String bucketName = fileService.getBucket();
     int byteSize = minioClient.getObject(bucketName,"File").read();
     assertThat(byteSize).isGreaterThan(0);
   }
   
}
