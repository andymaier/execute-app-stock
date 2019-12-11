package com.predic8.stock;

import static org.springframework.boot.SpringApplication.run;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.predic8.stock.event.NullAwareBeanUtilsBean;
import com.predic8.stock.model.Stock;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

//@EnableDiscoveryClient
@SpringBootApplication
public class StockApplication {

	@Bean
	public Map<String, Stock> stocks() {
		return new ConcurrentHashMap<>();
	}

	@Bean
	public NullAwareBeanUtilsBean beanUtils() {
		return new NullAwareBeanUtilsBean();
	}

	public static void main(String[] args) {
		run(StockApplication.class, args);
	}
}