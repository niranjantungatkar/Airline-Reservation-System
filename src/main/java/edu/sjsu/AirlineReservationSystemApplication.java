package edu.sjsu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableWebMvc
@Configuration
@SpringBootApplication
@EntityScan(basePackages={"edu.sjsu.models"})
public class AirlineReservationSystemApplication extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(AirlineReservationSystemApplication.class, args);
	}
	
	 /*@Override
	 public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		 configurer
		 .favorParameter(true)
		 .parameterName("xml")
		 	.defaultContentType(MediaType.APPLICATION_JSON)
	        .ignoreAcceptHeader(true)
	        .mediaType("true", MediaType.APPLICATION_XML);
	 }*/
}
