package com.malotian.okta.api.gateway;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@FeignClient(name = "foo-user-service", url = "http://localhost:8081")
interface FooUserClient {

	@GetMapping("/user-service")
	Map<String, String> userService();
}

@FeignClient(name = "foo-admin-service", url = "http://localhost:8081")
interface FooAdminClient {

	@GetMapping("/admin-service")
	Map<String, String> adminService();
}

@FeignClient(name = "bar-user-service", url = "http://localhost:8082")
interface BarUserClient {

	@GetMapping("/user-service")
	Map<String, String> userService();
}

@FeignClient(name = "bar-admin-service", url = "http://localhost:8082")
interface BarAdminClient {

	@GetMapping("/admin-service")
	Map<String, String> adminService();
}

@RestController
public class ServiceController {
	private final FooUserClient fooUserClient;
	private final FooAdminClient fooAdminClient;
	private final BarUserClient barUserClient;
	private final BarAdminClient barAdminClient;

	public ServiceController(FooUserClient fooUserClient, FooAdminClient fooAdminClient, BarUserClient barUserClient, BarAdminClient barAdminClient) {
		this.fooUserClient = fooUserClient;
		this.fooAdminClient = fooAdminClient;
		this.barUserClient = barUserClient;
		this.barAdminClient = barAdminClient;
	}

	public Map<String, String> fallback() {
		System.out.println("fallback");
		return new HashMap<>();
	}

	@GetMapping(path = "/foo-user-service", produces = MediaType.APPLICATION_JSON_VALUE)
	@HystrixCommand(fallbackMethod = "fallback")
	@CrossOrigin(origins = "*")
	public Map<String, String> fooUserService() {
		return fooUserClient.userService();
	}

	@GetMapping(path = "/foo-admin-service", produces = MediaType.APPLICATION_JSON_VALUE)
	@HystrixCommand(fallbackMethod = "fallback")
	@CrossOrigin(origins = "*")
	public Map<String, String> fooAdminService() {
		return fooAdminClient.adminService();
	}

	@GetMapping(path = "/bar-user-service", produces = MediaType.APPLICATION_JSON_VALUE)
	@HystrixCommand(fallbackMethod = "fallback")
	@CrossOrigin(origins = "*")
	public Map<String, String> barUserService() {
		return barUserClient.userService();
	}

	@GetMapping(path = "/bar-admin-service", produces = MediaType.APPLICATION_JSON_VALUE)
	@HystrixCommand(fallbackMethod = "fallback")
	@CrossOrigin(origins = "*")
	public Map<String, String> barAdminService() {
		return barAdminClient.adminService();
	}
	// @GetMapping(path = "/bar-user-service", produces =
	// MediaType.APPLICATION_JSON_VALUE)
	// @HystrixCommand(fallbackMethod = "fallback")
	// @CrossOrigin(origins = "*")
	// public Map<String, String> barUserService(Principal principal,
	// HttpServletRequest request) {
	// return barUserClient.userService(principal, request);
	// }

}