package ca.gc.aafc.objectstore.api.file;

public class FileUploadResponse {

  private final String fileName;
  private final String mediaType;
  private final String fileExtension;
  private final long size;

  public FileUploadResponse(String fileName, String mediaType, String fileExtension, long size) {
    this.fileName = fileName;
    this.mediaType = mediaType;
    this.fileExtension = fileExtension;
    this.size = size;
  }

  public String getFileName() {
    return fileName;
  }

  public String getMediaType() {
    return mediaType;
  }
  
  public String getFileExtension() {
    return fileExtension;
  }

  public long getSize() {
    return size;
  }

}
