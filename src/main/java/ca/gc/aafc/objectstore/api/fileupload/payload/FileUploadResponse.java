package ca.gc.aafc.objectstore.api.fileupload.payload;

public class FileUploadResponse {
      private String fileName;
      public String getFileName() {
        return fileName;
      }

      public void setFileName(String fileName) {
        this.fileName = fileName;
      }

      public String getFileType() {
        return fileType;
      }

      public void setFileType(String fileType) {
        this.fileType = fileType;
      }

      public long getSize() {
        return size;
      }

      public void setSize(long size) {
        this.size = size;
      }

      private String fileType;
      private long size;

      public FileUploadResponse(String fileName,String fileType, long size) {
          this.fileName = fileName;
          this.fileType = fileType;
          this.size = size;
      }

}
