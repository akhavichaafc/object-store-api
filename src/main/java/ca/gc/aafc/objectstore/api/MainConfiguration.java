package ca.gc.aafc.objectstore.api;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.crnk.operations.server.OperationsModule;
import io.crnk.operations.server.TransactionOperationFilter;
import io.crnk.spring.jpa.SpringTransactionRunner;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;

@Configuration
@EntityScan("ca.gc.aafc.objectstore.api.entities")
public class MainConfiguration {
  
  @Bean
  @Profile("!test")
  public MinioClient initMinioClient(
      @Value("${minio.scheme:}") String protocol, 
      @Value("${minio.host:}") String host,
      @Value("${minio.port:}") int port, 
      @Value("${minio.accessKey:}") String accessKey, 
      @Value("${minio.secretKey:}") String secretKey)
      throws InvalidEndpointException, InvalidPortException {
    String endpoint = protocol + "://"+host;
    return new MinioClient(endpoint, port, accessKey, secretKey);
  }

  /**
   * Registers the transaction filter that executes a transaction around bulk
   * jsonpatch operations.
   * 
   * @param module the Crnk operations module.
   */
  @Inject
  public void initTransactionOperationFilter(OperationsModule module) {
    module.addFilter(new TransactionOperationFilter());
  }

  /**
   * Provides Crnk's SpringTransactionRunner that implements transactions around
   * bulk jsonpatch operations using Spring's transaction management.
   * 
   * @return the transaction runner.
   */
  @Bean
  public SpringTransactionRunner crnkSpringTransactionRunner() {
    return new SpringTransactionRunner();
  }

}
