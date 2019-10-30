package ca.gc.aafc.objectstore.api.file;

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

}
