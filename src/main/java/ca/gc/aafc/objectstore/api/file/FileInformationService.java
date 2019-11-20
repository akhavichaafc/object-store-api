package ca.gc.aafc.objectstore.api.file;

import java.io.IOException;
import java.util.Optional;

/**
 * 
 * Service allowing read access to file information. 
 *
 */
public interface FileInformationService {

  /**
   * Check if a bucket exists.
   * This method doesn't throw exception and simply return false if there is an Exception.
   * Should be changed to at least throw IOException.
   * @param bucketName
   * @return
   */
  boolean bucketExists(String bucketName);

  /**
   * Temporary method to be replaced by fileExists after #17825
   * @param bucketName
   * @param fileNamePrefix
   * @return
   */
  boolean isFileWithPrefixExists(String bucketName, String fileNamePrefix);
  
  /**
   * Get information about a file as {@link FileObjectInfo}.
   * 
   * @param fileName
   * @param bucketName
   * @return {@link FileObjectInfo} instance or {@link Optional#empty} if the filename or the bucket
   *         don't exist
   * @throws IOException
   */
  Optional<FileObjectInfo> getFileInfo(String fileName, String bucketName) throws IOException;
  
  /**
   * Read and return a json file as an instance of the provided class.
   * @param bucketName
   * @param filename
   * @param clazz
   * @return
   * @throws IOException
   */
  <T> T getJsonFileContentAs(String bucketName, String filename, Class<T> clazz) throws IOException;

}
