package com.app.blooddonor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

// Enables @Async annotation — used in EmailService
// so emails are sent in background thread
// and the user gets search results immediately
@Configuration
@EnableAsync
public class AsyncConfig {
}
