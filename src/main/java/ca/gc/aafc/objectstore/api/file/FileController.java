package ca.gc.aafc.objectstore.api.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
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

import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.minio.MinioFileService;
import ca.gc.aafc.objectstore.api.service.ObjectStoreMetadataReadService;
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
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class FileController {

  private static final int MAX_NUMBER_OF_ATTEMPT_RANDOM_UUID = 5;
  private static final TikaConfig TIKA_CONFIG = TikaConfig.getDefaultConfig();

  public static final String HEADER_ORIGINAL_FILENAME = "original-filename";
  public static final String MEDIA_TYPE = "media-type";
  public static final String FILE_EXTENSION = "file-extension";

  private final MinioFileService minioService;
  private final ObjectStoreMetadataReadService objectStoreMetadataReadService;

  @Inject
  public FileController(MinioFileService minioService, ObjectStoreMetadataReadService objectStoreMetadataReadService) {
    this.minioService = minioService;
    this.objectStoreMetadataReadService = objectStoreMetadataReadService;
  }
  
  @Builder
  @Getter
  private static class MediaTypeDetectionResult {
    private InputStream inputStream;
    private org.apache.tika.mime.MediaType mediaType;
    private MimeType mimeType;
  }

  @PostMapping("/file/{bucket}")
  public FileUploadResponse handleFileUpload(@RequestParam("file") MultipartFile file,
      @PathVariable String bucket) throws InvalidKeyException, NoSuchAlgorithmException,
      InvalidBucketNameException, NoResponseException, ErrorResponseException, InternalException,
      InvalidArgumentException, InsufficientDataException, InvalidResponseException,
      RegionConflictException, InvalidEndpointException, InvalidPortException, IOException,
      XmlPullParserException, URISyntaxException, MimeTypeException {
    
    // Temporary, we will need to check if the user is an admin
    minioService.ensureBucketExists(bucket);
    
    MediaTypeDetectionResult mtdr = detectMediaType(file.getInputStream());
    
    // Check that the UUID is not already assigned. It is very unlikely but not impossible
    UUID uuid = getNewUUID(bucket);

    Map<String, String> headerMap = new HashMap<>();
    headerMap.put(HEADER_ORIGINAL_FILENAME, file.getOriginalFilename());
    headerMap.put(MEDIA_TYPE, mtdr.getMediaType().toString());
    headerMap.put(FILE_EXTENSION, mtdr.getMimeType().getExtension());
    
    minioService.storeFile(uuid.toString() + mtdr.getMimeType().getExtension(), mtdr.getInputStream(),
        mtdr.getMediaType().toString(), bucket, headerMap);

    return new FileUploadResponse(uuid.toString(), mtdr.getMediaType().toString(),
        file.getSize());
  }
  
  @GetMapping("/file/{bucket}/{fileId}")
  public ResponseEntity<InputStreamResource> downloadObject(@PathVariable String bucket,
      @PathVariable UUID fileId) throws IOException {
    
    try {

      Optional<ObjectStoreMetadata> loadedMetadata = objectStoreMetadataReadService.loadObjectStoreMetadataByFileId(fileId);
      ObjectStoreMetadata metadata = loadedMetadata.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
          "FileIdentifier " + fileId + " or bucket " + bucket + " Not Found", null));

      FileObjectInfo foi = minioService.getFileInfo(metadata.getFilename(), bucket)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
              fileId + " or bucket " + bucket + " Not Found", null));
      
      HttpHeaders respHeaders = new HttpHeaders();
      respHeaders.setContentType(MediaType.parseMediaType(metadata.getDcFormat()));
      respHeaders.setContentLength(foi.getLength());
      respHeaders.setContentDispositionFormData("attachment", metadata.getFilename());

      InputStream is = minioService.getFile(metadata.getFilename(), bucket);

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
  
  /**
   * Detect the MediaType and MimeType of an InputStream by reading the beginning of the stream.
   * A new InputStream is returned to make sure the caller can read the entire stream from the beginning.
   * 
   * TODO: take the media type submitted by the user in case we can not detect it.
   * 
   * @param is
   * @return
   * @throws IOException
   * @throws MimeTypeException
   */
  private MediaTypeDetectionResult detectMediaType(InputStream is) throws IOException, MimeTypeException {
    
    // Read the beginning of the Stream to allow Tika to detect the mediaType
    byte[] buffer = new byte[10 * 1024];
    int lenght = is.read(buffer);
    ByteArrayInputStream bais = new ByteArrayInputStream(buffer, 0, lenght);

    Detector detector = new DefaultDetector();
    org.apache.tika.mime.MediaType mediaType = detector.detect(bais, new Metadata());
    MimeType mimeType = TIKA_CONFIG.getMimeRepository().forName(mediaType.toString());
    
    return MediaTypeDetectionResult.builder()
      .inputStream(new SequenceInputStream(bais, is))
      .mediaType(mediaType)
      .mimeType(mimeType)
      .build();
  }

}
