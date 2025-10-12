package com.innowise.authservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Evgeniy Zaleshchenok
 */
@Configuration
@Profile("local")
@PropertySource("file:.env")
public class EnvConfig {
}
