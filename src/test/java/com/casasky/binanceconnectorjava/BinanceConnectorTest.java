package com.casasky.binanceconnectorjava;

import com.casasky.binanceconnectorjava.model.AccountSnapshot;
import com.casasky.binanceconnectorjava.model.Asset;
import com.casasky.binanceconnectorjava.model.Price;
import com.casasky.binanceconnectorjava.model.Symbol;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.as;

@SpringBootTest
class BinanceConnectorTest {

    @Autowired
    BinanceConnector binanceConnector;

    @Test
    void price() {
        assertThat(binanceConnector.price(Symbol.BTCEUR))
                .extracting(Price::symbol)
                .isEqualTo(Symbol.BTCEUR);
    }

    @Test
    void walletSnapshot() {
        assertThat(binanceConnector.walletSnapshot(Optional.of(Asset.BTC), Optional.of(1))).satisfies(accountSnapshot -> {
            assertThat(accountSnapshot).extracting(AccountSnapshot::totalAssetOfBtc, as(InstanceOfAssertFactories.BIG_DECIMAL))
                    .isPositive();
            assertThat(accountSnapshot).extracting(AccountSnapshot::balances)
                    .satisfies(accountSnapshot1 -> assertThat(accountSnapshot1).extracting(AccountSnapshot.Balance::asset)
                            .containsExactly(String.valueOf(Asset.BTC)));
        });
    }

    @Test
    void btcToEuro() {
        assertThat(binanceConnector.btcToEuro(new BigDecimal("1"))).isPositive();
    }

}