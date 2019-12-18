package com.predic8.stock.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.predic8.stock.model.Basket;
import com.predic8.stock.model.Stock;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopListener {

	private final ObjectMapper mapper;
	private final Map<String, Stock> repo;
	private final NullAwareBeanUtilsBean beanUtils;
	private KafkaTemplate<String, Operation> kafka;

	public ShopListener(ObjectMapper mapper, Map<String, Stock> repo, NullAwareBeanUtilsBean beanUtils, KafkaTemplate<String, Operation> kafka) {
		this.mapper = mapper;
		this.repo = repo;
		this.beanUtils = beanUtils;
		this.kafka = kafka;
	}

	@KafkaListener(topics = "shop")
	public void listen(Operation op) throws Exception {
		System.out.println("op = " + op);

		switch (op.getBo()) {
			case "article":
				handleArticle(op);
				break;
			case "stock":
				handleStock(op);
				break;
			case "basket":
				Basket basket = mapper.treeToValue(op.getObject(), Basket.class);
				handleBasket(basket);
				break;
		}
	}

	private void handleArticle(Operation op) throws com.fasterxml.jackson.core.JsonProcessingException {

		Stock stock = mapper.treeToValue(op.getObject(), Stock.class);
		switch (op.getAction()) {
			case "upsert":
				repo.put(stock.getUuid(), stock);
				break;
			case "remove":
				repo.remove(stock.getUuid());
				break;
		}
	}

	private void handleStock(Operation op) throws JsonProcessingException {
		Stock stock = mapper.treeToValue(op.getObject(), Stock.class);
		switch (op.getAction()) {
			case "upsert":
				repo.put(stock.getUuid(), stock);
				break;
		}
	}

	private void handleBasket(Basket basket) {
		basket
				.getItems()
				.stream()
				.map(item ->
						new Operation("stock", "upsert", mapper.valueToTree(
								new Stock(item.getArticleId(), repo.get(item.getArticleId()).getQuantity() - item.getQuantity())))
				)
				.forEach(op -> {
					try {
						op.logSend();

						kafka.send("shop", op).get(100, TimeUnit.MILLISECONDS);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
	}
}