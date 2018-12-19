package com.mmd.mjapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@MapperScan("com/mmd/mjapp/dao")
@EnableScheduling
public class MjappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MjappApplication.class, args);
	}
}
