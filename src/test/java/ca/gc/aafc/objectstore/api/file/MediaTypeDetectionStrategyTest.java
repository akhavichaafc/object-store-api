package ca.gc.aafc.objectstore.api.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nullable;

import org.apache.tika.mime.MimeTypeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.google.common.io.Resources;

public class MediaTypeDetectionStrategyTest {

  private static final MediaTypeDetectionStrategy MTDS = new MediaTypeDetectionStrategy();
 
  @Test
  public void detectMediaType_onWrongSpecificMediaType_mediaTypeIsChanged() throws FileNotFoundException, URISyntaxException {
    try (FileInputStream fis = new FileInputStream(
        Resources.getResource("drawing.png").toURI().getPath())) {

      assertDetectedMediaType(fis, MediaType.TEXT_PLAIN_VALUE, "my_file.ab1",
          MediaType.IMAGE_PNG_VALUE, ".png");
    } catch (IOException e) {
      fail(e);
    }
  }
  
  @Test
  public void detectMediaType_onNoMediaTypeProvided_typeDetected() {
    ByteArrayInputStream bais = new ByteArrayInputStream(
        "Test content".getBytes(StandardCharsets.UTF_8));

    assertDetectedMediaType(bais, null, null,
        MediaType.TEXT_PLAIN_VALUE, ".txt");
  }
  
  @Test
  public void detectMediaType_onGenericMediaType_extensionIsPreserved() {
    ByteArrayInputStream bais = new ByteArrayInputStream(
        "Test content".getBytes(StandardCharsets.UTF_8));

    assertDetectedMediaType(bais, MediaType.TEXT_PLAIN_VALUE, "my_file.ab1",
        MediaType.TEXT_PLAIN_VALUE, ".ab1");
  }
  
  @Test
  public void detectMediaType_onUnknownMediaType_extensionAndMediaTypeIsPreserved() {
    ByteArrayInputStream bais = new ByteArrayInputStream(new byte[] { 1, 0, 10, 23 });
    assertDetectedMediaType(bais, "special/binary", "my_file.bin", "special/binary", ".bin");
  }
  
  @Test
  public void detectMediaType_onUnknownTextMediaType_extensionAndMediaTypeIsPreserved() {
    ByteArrayInputStream bais = new ByteArrayInputStream(
        "Test content".getBytes(StandardCharsets.UTF_8));
    assertDetectedMediaType(bais, "special/text", "my_file.zzz", "special/text", ".zzz");
  }
  
  @Test
  public void detectMediaType_onUnknownMediaType_extensionAndMediaTypeSet() {
    ByteArrayInputStream bais = new ByteArrayInputStream(new byte[] { 1, 0, 10, 23 });
    assertDetectedMediaType(bais, null, null, MediaType.APPLICATION_OCTET_STREAM_VALUE, ".bin");
  }
  
  /**
   * Method responsible to detect the mediatype/extension and assert the result against the provided
   * values.
   * 
   * @param is
   * @param receivedMediaType
   * @param originalFilename
   * @param expectedMediaType
   * @param expectedExt
   */
  private void assertDetectedMediaType(InputStream is, @Nullable String receivedMediaType,
      @Nullable String originalFilename, String expectedMediaType, String expectedExt) {
    try {
      MediaTypeDetectionStrategy.MediaTypeDetectionResult mtdr = MTDS.detectMediaType(is,
          receivedMediaType, originalFilename);
      assertEquals(expectedMediaType, mtdr.getMediaType().toString());
      assertEquals(expectedExt, mtdr.getFileExtension());
    } catch (MimeTypeException | IOException e) {
      fail(e);
    }
  }

}
