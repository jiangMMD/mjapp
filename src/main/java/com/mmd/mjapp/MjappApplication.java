package com.mmd.mjapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com/mmd/mjapp/dao")
public class MjappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MjappApplication.class, args);
	}
}
