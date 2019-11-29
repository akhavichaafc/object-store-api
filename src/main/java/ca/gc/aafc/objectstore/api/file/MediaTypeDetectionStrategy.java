package ca.gc.aafc.objectstore.api.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;

import lombok.Builder;
import lombok.Getter;

/**
 * A MediaTypeDetectionStrategy allows to determine the media type based on an InputStream and information
 * provided by the user.
 * 
 * This class could be turned into an interface later if more than 1 strategy is implemented.
 * 
 * The current strategy will always detect the MediaType from the InputStream and the file extension will be changed 
 * accordingly unless the detected media type is too generic (OCTET_STREAM or TEXT_PLAIN). In such case, the provided MediaType/file extension will
 * be used (if provided).
 *
 */
public class MediaTypeDetectionStrategy {
  
  private static final TikaConfig TIKA_CONFIG = TikaConfig.getDefaultConfig();
  private static final Detector TIKA_DETECTOR = new DefaultDetector();
  
  @Builder
  @Getter
  public static class MediaTypeDetectionResult {
    private InputStream inputStream;
    private org.apache.tika.mime.MediaType mediaType;
    private String fileExtension;
  }
  
  /**
   * Detect the MediaType and MimeType of an InputStream by reading the beginning of the stream.
   * A new InputStream is returned to make sure the caller can read the entire stream from the beginning.
   * 
   * @param is
   * @return
   * @throws IOException
   * @throws MimeTypeException
   */
  public MediaTypeDetectionResult detectMediaType(InputStream is,
      @Nullable String receivedMediaType, @Nullable String originalFilename)
      throws IOException, MimeTypeException {
    
    Objects.requireNonNull(is);

    // Read the beginning of the Stream to allow Tika to detect the mediaType
    byte[] buffer = new byte[10 * 1024];
    int lenght = is.read(buffer);
    ByteArrayInputStream bais = new ByteArrayInputStream(buffer, 0, lenght);

    MediaType mediaType = TIKA_DETECTOR.detect(bais, new Metadata());
    MimeType mimeType = TIKA_CONFIG.getMimeRepository().forName(mediaType.toString());

    MediaType parsedReceivedMediaType = StringUtils.isNotBlank(receivedMediaType)
        ? MediaType.parse(receivedMediaType)
        : null;

    boolean isGenericMediaType = (MediaType.OCTET_STREAM == mediaType
        || MediaType.TEXT_PLAIN == mediaType);

    MediaTypeDetectionResult.MediaTypeDetectionResultBuilder mtdrBldr = MediaTypeDetectionResult
        .builder().inputStream(new SequenceInputStream(bais, is));
    
    // If the detected type is too generic, try to use what was provided by the user
    if (isGenericMediaType) {
      mtdrBldr.mediaType(ObjectUtils.defaultIfNull(parsedReceivedMediaType, mediaType));
      mtdrBldr.fileExtension(originalFilename != null ? "." + FilenameUtils.getExtension(originalFilename) : mimeType.getExtension());
    } else {
      mtdrBldr.mediaType(mediaType);
      mtdrBldr.fileExtension(mimeType.getExtension());
    }
    
    return mtdrBldr.build();
  }

}
