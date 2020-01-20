package ca.gc.aafc.objectstore.api.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ThumbnailService {

  public InputStream generateThumbnail(InputStream sourceImage) throws IOException {
    // Copy to a temp file; The source image must be a file for thumbnailator to
    // generate the thumbnail.
    File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
    FileUtils.copyToFile(sourceImage, tempFile);

    BufferedImage thumbnailImg = Thumbnails.of(tempFile)
      .size(200, 200)
      .outputFormat("jpg")
      .asBufferedImage();

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ImageIO.write(thumbnailImg, "jpg", os);
    InputStream thumbnailInputStream = new ByteArrayInputStream(os.toByteArray());

    return thumbnailInputStream;

    // PipedInputStream thumbnail = new PipedInputStream();
    // new Thread(() -> {
    //   try (PipedOutputStream pipe = new PipedOutputStream(thumbnail)) {
    //     Thumbnails.of(tempFile)
    //       .size(200, 200)
    //       .outputFormat("jpg")
    //       .toOutputStream(pipe);
    //   } catch(IOException e) {
        
    //   } finally {
    //     // tempFile.delete();
    //   }
    // }).start();
    // return thumbnail;
  }

  public boolean isSupported(String extension) {
    return ImageIO.getImageReadersByMIMEType(extension).hasNext();
  }

}
