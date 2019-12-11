package com.predic8.stock.api;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.predic8.stock.event.Operation;
import com.predic8.stock.model.Stock;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/stocks")
@RestController
public class StockRestController {
	
	private final Map<String, Stock> stocks;

	private ObjectMapper mapper;
	private KafkaTemplate<String, Operation> kafka;

	public StockRestController(Map<String, Stock> articles, ObjectMapper mapper, KafkaTemplate<String, Operation> kafka) {
		this.stocks = articles;
		this.mapper = mapper;
		this.kafka = kafka;
	}

	@GetMapping
	public Collection<Stock> index() {
		return stocks.values();
	}


	@GetMapping("/{id}")
	public Stock get(@PathVariable String id) {
		return stocks.get(id);
	}


	@PutMapping("/{id}")
	public Stock put( @PathVariable String id, @RequestBody Stock stock) throws Exception {

		Stock old = stocks.get(id);

		old.setQuantity(stock.getQuantity());

		Operation op = new Operation("article","upsert",mapper.valueToTree(old));

		op.logSend();

		kafka.send("shop", op).get(200, MILLISECONDS);

		return old;
	}

	@GetMapping("/count")
	public long count() {
		return stocks.size();
	}
}