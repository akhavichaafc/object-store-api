package ca.gc.aafc.objectstore.api.security.keycloak;

import javax.inject.Inject;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import lombok.extern.log4j.Log4j2;

@Configuration
@ConditionalOnProperty(value = "keycloak.enabled", matchIfMissing = true)
@Log4j2
public class KeycloakAuthConfig extends KeycloakWebSecurityConfigurerAdapter {
	
	public KeycloakAuthConfig() {
		super();
		log.debug("KeycloakAuthConfig created");
	}

	// TODO  
//	@Inject
//	private AutowireCapableBeanFactory beanFactory;
	
	@Inject
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		KeycloakAuthenticationProvider keycloakAuthProvider = keycloakAuthenticationProvider();
		keycloakAuthProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
		auth.authenticationProvider(keycloakAuthProvider);
	}
	
	@Bean
	public KeycloakConfigResolver keycloakConfigResolver() {
		log.debug("Creating KeycloakSpringBootConfigResolver bean");
		return new KeycloakSpringBootConfigResolver();
	}
	
	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		http
			.authorizeRequests()
			.anyRequest().authenticated();
		// TODO implement KeycloakAccountRegistrationFilter (see seqdb-api) if necessary
//			.and()
//			.addFilterAfter(
//				beanFactory.createBean(KeycloakAccountRegistrationFilter.class), 
//				KeycloakAuthenticationProcessingFilter.class
//			);
		
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web
			.ignoring()
			.antMatchers("/json-schema/**");
	}

}
