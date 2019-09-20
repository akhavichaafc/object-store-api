package ca.gc.aafc.objectstore.api.fileupload.payload;

public class FileUploadResponse {

  private final String fileName;
  private final String fileType;
  private final long size;

  public FileUploadResponse(String fileName, String fileType, long size) {
    this.fileName = fileName;
    this.fileType = fileType;
    this.size = size;
  }

  public String getFileName() {
    return fileName;
  }

  public String getFileType() {
    return fileType;
  }

  public long getSize() {
    return size;
  }

}
