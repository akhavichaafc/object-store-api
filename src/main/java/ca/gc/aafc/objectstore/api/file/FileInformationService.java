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
   * Check if at least 1 object in the provided bucket starts with a specific prefix. 
   * 
   * @param bucketName
   * @param fileNamePrefix
   * @return at least 1 object with the provided prefix exists
   * @throws IllegalStateException if something wrong with the request or response. For example, invalid bucket name.
   * @throws IOException
   */
  boolean isFileWithPrefixExists(String bucketName, String fileNamePrefix)
      throws IllegalStateException, IOException;
  
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
  <T> Optional<T> getJsonFileContentAs(String bucketName, String filename, Class<T> clazz) throws IOException;

}
