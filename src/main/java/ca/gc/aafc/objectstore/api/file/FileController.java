package ca.gc.aafc.objectstore.api.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.xmlpull.v1.XmlPullParserException;

import ca.gc.aafc.objectstore.api.minio.MinioFileService;
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

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class FileController {

  public static final String HEADER_ORIGINAL_FILENAME = "original-filename";
  
  private static final int MAX_NUMBER_OF_ATTEMPT_RANDOM_UUID = 5;

  private final MinioFileService minioService;

  @Inject
  public FileController(MinioFileService minioService) {
    this.minioService = minioService;
  }

  @PostMapping("/file/{bucket}")
  public FileUploadResponse handleFileUpload(@RequestParam("file") MultipartFile file,
      @PathVariable String bucket) throws InvalidKeyException, NoSuchAlgorithmException,
      InvalidBucketNameException, NoResponseException, ErrorResponseException, InternalException,
      InvalidArgumentException, InsufficientDataException, InvalidResponseException,
      RegionConflictException, InvalidEndpointException, InvalidPortException, IOException,
      XmlPullParserException, URISyntaxException {
    
    // Temporary, we will need to check if the user is an admin
    minioService.ensureBucketExists(bucket);

    // Check that the UUID is not already assigned. It is very unlikely but not impossible
    UUID uuid = getNewUUID(bucket);

    // Detect media type and file extension with a library instead of relying on the provided filename (#17825)
    String ext = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
    
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put(HEADER_ORIGINAL_FILENAME, file.getOriginalFilename());

    minioService.storeFile(uuid.toString() + "." + ext, file.getInputStream(), file.getContentType(), bucket, headerMap);
    
    return new FileUploadResponse(uuid.toString(), file.getContentType(),
        file.getSize());
  }
  
  @GetMapping("/file/{bucket}/{fileId}")
  public ResponseEntity<InputStreamResource> downloadObject(@PathVariable String bucket,
      @PathVariable UUID fileId) throws IOException {
    
    try {
      // We should get the extension from the database, not scanning files in Minio by prefix
      Optional<String> possibleFileName = minioService.getFileNameByPrefix(bucket,
          fileId.toString());

      String fileName = possibleFileName
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
              possibleFileName + " or bucket " + bucket + " Not Found", null));

      FileObjectInfo foi = minioService.getFileInfo(fileName, bucket)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
              fileId + " or bucket " + bucket + " Not Found", null));
      
      HttpHeaders respHeaders = new HttpHeaders();
      respHeaders.setContentType(MediaType.parseMediaType(foi.getContentType()));
      respHeaders.setContentLength(foi.getLength());
      respHeaders.setContentDispositionFormData("attachment", fileName);

      InputStream is = minioService.getFile(fileName, bucket);

      InputStreamResource isr = new InputStreamResource(is);
      return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
    } catch (IOException e) {
      log.warn("Can't download object", e);
    }
   
    throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, null);
  }
  
  private UUID getNewUUID(String bucketName) throws IllegalStateException {
    int numberOfAttempt = 0;
    while (numberOfAttempt < MAX_NUMBER_OF_ATTEMPT_RANDOM_UUID) {
      UUID uuid = UUID.randomUUID();
      if(!minioService.isFileWithPrefixExists(bucketName, uuid.toString())) {
        return uuid;
      }
      numberOfAttempt++;
    }
    throw new IllegalStateException("Can't assign unique UUID. Giving up.");
  }

}
