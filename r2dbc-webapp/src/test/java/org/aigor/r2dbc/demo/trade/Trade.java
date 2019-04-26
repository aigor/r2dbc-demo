package org.aigor.r2dbc.demo.trade;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Trade {
	private String id;
	private long   timestamp;
	private double price;
	private double amount;
	private String currency;
	private String market;

	@Override
	public String toString() {
		return "Trade{" + "id=" + id + ", timestamp=" + timestamp + ", price=" +
			price + ", amount=" + amount + ", currency='" +
			currency + '\'' + ", market='" + market + '\'' + '}';
	}
}
