package ca.gc.aafc.objectstore.api.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

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
import io.crnk.core.exception.ResourceNotFoundException;
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

  private final MinioFileService minioService;

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
    
    UUID uuid = UUID.randomUUID();
    //to do check with the database
    //record original filename

    minioService.storeFile(uuid.toString(), file.getInputStream(), file.getContentType(), bucket);
    
    return new FileUploadResponse(uuid.toString(), file.getContentType(),
        file.getSize());
  }
  
  @GetMapping("/file/{bucket}/{fileId}")
  public ResponseEntity<InputStreamResource> downloadObject(@PathVariable String bucket,
      @PathVariable UUID fileId) throws IOException {
    
    try {
      FileObjectInfo foi = minioService.getFileInfo(fileId.toString(), bucket)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, fileId + " or bucket " + bucket + " Not Found", null));
      
      HttpHeaders respHeaders = new HttpHeaders();
      respHeaders.setContentType(MediaType.parseMediaType(foi.getContentType()));
      respHeaders.setContentLength(foi.getLength());
      respHeaders.setContentDispositionFormData("attachment", "fileNameIwant.jpg");
      
      InputStream is = minioService.getFile(fileId.toString(), bucket);

      InputStreamResource isr = new InputStreamResource(is);
      return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
    }
    catch (IOException e) {
      log.warn("Can't download object", e);
    }
   
    throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, null);
  }

}
