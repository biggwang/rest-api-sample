package com.ryu.rest;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WhiteshipRestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhiteshipRestApiApplication.class, args);
	}

	/**
	 * 공통으로 사용할 테니 빈으로 등록하여 편하게 사용하자
	 */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
