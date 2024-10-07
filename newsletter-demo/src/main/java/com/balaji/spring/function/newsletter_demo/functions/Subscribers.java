package com.balaji.spring.function.newsletter_demo.functions;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.balaji.spring.function.newsletter_demo.model.Subscriber;
import com.balaji.spring.function.newsletter_demo.service.SubscriberService;

@Configuration
public class Subscribers {
	
	@Autowired
	SubscriberService subscriberService;
	
	@Bean
	public Supplier<List<Subscriber>> findAll() {
		return () -> subscriberService.findAll();
	}
	
	@Bean
	public Consumer<String> create() {
		return (email) -> subscriberService.create(email);
	}

}
