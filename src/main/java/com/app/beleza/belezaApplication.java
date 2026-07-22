package com.app.beleza;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class belezaApplication implements WebMvcConfigurer {

	@Value("${app.upload.dir}")
	private String uploadDir;

	public static void main(String[] args) {
		SpringApplication.run(belezaApplication.class, args);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
		String uploadUri = uploadPath.toUri().toString();

		registry.addResourceHandler("/uploads/depoimentos/**")
				.addResourceLocations(uploadUri);
	}
}