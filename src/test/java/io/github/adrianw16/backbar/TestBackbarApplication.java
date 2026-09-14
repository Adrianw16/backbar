package io.github.adrianw16.backbar;

import org.springframework.boot.SpringApplication;

public class TestBackbarApplication {

	public static void main(String[] args) {
		SpringApplication.from(BackbarApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
