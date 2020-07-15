package com.example.fa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ConfigurationPropertiesScan("com.example.fa.configuration")
public class FoodAggregatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodAggregatorApplication.class, args);
	}

}
