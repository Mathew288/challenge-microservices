package com.ntt.ms_movimientos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MsMovimientosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsMovimientosApplication.class, args);
	}

}
