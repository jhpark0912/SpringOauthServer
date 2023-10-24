package com.springauthorization.demo.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.PasswordLookup;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.springauthorization.demo.repository.JpaOAuth2AuthorizationConsentService;
import com.springauthorization.demo.repository.JpaRegisteredClientRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}


	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
			throws Exception {

		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
				.oidc(Customizer.withDefaults()) // Enable OpenID Connect 1.0
				.tokenEndpoint(tokenEndpoint ->
				tokenEndpoint.accessTokenRequestConverter(new AuthenticationConverter() {
					@Override
					public Authentication convert(HttpServletRequest request) {

						System.out.println("hello");

						return null;
					}
				})
		);
		http
				// Redirect to the login page when not authenticated from the
				// authorization endpoint
				.exceptionHandling((exceptions) -> exceptions
						.defaultAuthenticationEntryPointFor(
								new LoginUrlAuthenticationEntryPoint("/login"),
								new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
				// Accept access tokens for User Info and/or Client Registration
				.oauth2ResourceServer((resourceServer) -> resourceServer
						.jwt(Customizer.withDefaults()));

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
			throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.securityMatcher("/oauth2/**", "/login")
				.authorizeHttpRequests((authorize) -> authorize
						.anyRequest().authenticated())
				// Form login handles the redirect to the login page from the
				// authorization server filter chain
				.formLogin(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, JpaRegisteredClientRepository registeredClientRepository) {
		return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource(@Value("${keystore.path}") String path, @Value("${keystore.pass}") String password) throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException {
		File ksFile = new File(path);
		if (ksFile.exists()) {
			KeyStore keyStore = KeyStore.getInstance("pkcs12");
			try (FileInputStream fis = new FileInputStream(ksFile)) {
				keyStore.load(fis, password.toCharArray());
			}

			JWKSet jwkSet = JWKSet.load(keyStore, new PasswordLookup() {
				@Override
				public char[] lookupPassword(String name) {
					return password.toCharArray();
				}
			});

			return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
		} else {
			KeyPair keyPair = generateRsaKey();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			RSAKey rsaKey = new RSAKey.Builder(publicKey)
					.privateKey(privateKey)
					.keyID(UUID.randomUUID().toString())
					.build();
			JWKSet jwkSet = new JWKSet(rsaKey);
			return new ImmutableJWKSet<>(jwkSet);
		}
	}

	private static KeyPair generateRsaKey() {
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}

	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}

	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
		return (context) -> {
			if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
				context.getClaims().claims((claims) -> {
					Set<String> roles = AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
							.stream()
							.map(c -> c.replaceFirst("^ROLE_", ""))
							.collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
					claims.put("roles", roles);
				});
			}
		};
	}
}
