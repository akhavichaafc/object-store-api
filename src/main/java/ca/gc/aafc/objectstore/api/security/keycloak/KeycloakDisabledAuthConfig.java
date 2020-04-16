package ca.gc.aafc.objectstore.api.security.keycloak;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import lombok.extern.log4j.Log4j2;

@Configuration
@ConditionalOnProperty(value = "keycloak.enabled", havingValue = "false")
@Log4j2
/**
 * A dummy security configuration file in order to prevent Spring Boot from 
 * automatically configuring its own security when Keycloak is disabled.
 * In production, Keycloak should always be enabled.
 * @author Alex Khavich
 */
public class KeycloakDisabledAuthConfig extends WebSecurityConfigurerAdapter {
  
  public KeycloakDisabledAuthConfig() {
    super();
    log.info("KeycloakDisabledAuthConfig created");
  }
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();
    
    http.authorizeRequests().antMatchers("/**").permitAll();
  }
  
  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/**");
  }

}
