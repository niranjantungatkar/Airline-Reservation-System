package edu.sjsu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@EnableWebMvc
//@Configuration
@SpringBootApplication
public class AirlineReservationSystemApplication extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(AirlineReservationSystemApplication.class, args);
	}
	
	 @Override
	 public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		 configurer.favorParameter(true)
		 	.parameterName("xml")
		 	.defaultContentType(MediaType.APPLICATION_JSON)
	        .mediaType("true", MediaType.APPLICATION_XML)
		 	.ignoreAcceptHeader(false);
	        //.ignoreUnknownPathExtensions(false)
	        //
	        //.useJaf(true);
	 }
}
