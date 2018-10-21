package com.malotian.okta.api.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class UserFeignClientInterceptor implements RequestInterceptor {
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_TOKEN_TYPE = "Bearer";

	@Value("${okta.oauth2.issuer}")
	private String issuer;

	Logger logger = LoggerFactory.getLogger(UserFeignClientInterceptor.class);

	@Override
	public void apply(RequestTemplate template) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();

		if (authentication != null && authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
			OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();

			RestTemplate restTemplate = new RestTemplate();
			MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
			form.add("client_id", "0oadve4kd8K5o0r3x0h7");
			form.add("token", details.getTokenValue());
			form.add("token_type_hint", "access_token");

			String response = restTemplate.postForObject(issuer + "/v1/introspect", form, String.class);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonFactory factory = mapper.getFactory();
				JsonParser parser = factory.createParser(response);
				JsonNode tokenIntrospectResult = mapper.readTree(parser);
				logger.info("tokenIntrospectResult: {}", tokenIntrospectResult);
				if (!tokenIntrospectResult.get("active").asBoolean()) {
					logger.error("quitting, token is not active anymore");
					return;
				}
			} catch (Exception e) {
				logger.error("quitting, token introspection failed.", e);
				return;
			}

			template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, details.getTokenValue()));
			logger.info("for request: {}, added AUTHORIZATION_HEADER, {} {}", template.url(), BEARER_TOKEN_TYPE, details.getTokenValue());
		}
	}
}