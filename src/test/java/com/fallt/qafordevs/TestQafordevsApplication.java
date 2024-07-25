package com.fallt.qafordevs;

import org.springframework.boot.SpringApplication;

public class TestQafordevsApplication {

	public static void main(String[] args) {
		SpringApplication.from(QafordevsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
