package ca.gc.aafc.objectstore.api.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Configuration used for loading the Application Context.
 */
@SpringBootApplication
@EntityScan("ca.gc.aafc.objectstore.api.entities")
public class TestConfig { }
