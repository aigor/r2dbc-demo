package org.aigor.r2dbc.demo.trade;

import org.aigor.r2dbc.demo.trade.H2TradeRepository;
import org.aigor.r2dbc.demo.trade.Trade;
import org.junit.Test;
import reactor.core.publisher.Flux;

public class H2R2dbcRepoTest {

    @Test
    public void useR2dbcForH2() {
        H2TradeRepository tradeRepository = new H2TradeRepository();

        tradeRepository.saveAll(
            Flux.just(
                Trade.builder().id("1").amount(10).currency("USD").market("TX").price(2.0).build(),
                Trade.builder().id("2").amount(20).currency("UAH").market("TX").price(2.2).build(),
                Trade.builder().id("3").amount(15).currency("USD").market("TD").price(0.2).build()
            )
        ).then(
            tradeRepository
                .showRepoAnalytics()
        ).subscribe();

    }
}
