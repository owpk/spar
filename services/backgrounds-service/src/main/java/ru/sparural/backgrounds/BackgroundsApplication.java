package ru.sparural.backgrounds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.sparural.kafka.annotation.EnableSparuralKafkaConsumer;
import ru.sparural.kafka.annotation.EnableSparuralKafkaProducer;

@SpringBootApplication
@EnableScheduling
@EnableSparuralKafkaConsumer
@EnableSparuralKafkaProducer
@EnableCaching
public class BackgroundsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackgroundsApplication.class, args);
	}

}
