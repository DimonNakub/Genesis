package com.stephen_oosthuizen.genesis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GenesisApplication {

	public static void main(String[] args) {
		SpringApplication.run(GenesisApplication.class, args);
		System.out.println("---------------------------------------------------------------------");
		System.out.println("Access the front-end here: http://localhost:8080/");
		System.out.println("---------------------------------------------------------------------");
	}
}