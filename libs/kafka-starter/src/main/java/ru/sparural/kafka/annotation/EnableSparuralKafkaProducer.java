/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sparural.kafka.annotation;

import org.springframework.context.annotation.Import;
import ru.sparural.kafka.KafkaSparuralBaseConfig;
import ru.sparural.kafka.KafkaSparuralProducerAutoconfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Import({KafkaSparuralProducerAutoconfiguration.class, KafkaSparuralBaseConfig.class})
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableSparuralKafkaProducer {

}
