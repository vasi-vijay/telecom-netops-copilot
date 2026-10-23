package com.telecom.netops.netops_copilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.*")
public class NetopsCopilotApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetopsCopilotApplication.class, args);
	}

}
