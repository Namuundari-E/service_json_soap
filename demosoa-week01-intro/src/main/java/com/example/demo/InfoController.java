package com.example.demo;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Builder;
import lombok.Data;

@RestController
public class InfoController {
	@GetMapping("/api/v1/ping")
	public ServiceResponse ping() {
// Statelessness: Сервер дээр ямар нэгэн сешн мэдээлэл хадгалагдахгүй
		return ServiceResponse.builder().status("UP").architecture("SOA").timestamp(LocalDateTime.now())
				.message("Abstraction Successful: You see the JSON, not the Java Logic.").build();
	}
}

@Data
@Builder
class ServiceResponse {
	private String status;
	private String architecture;
	private LocalDateTime timestamp;
	private String message;
}