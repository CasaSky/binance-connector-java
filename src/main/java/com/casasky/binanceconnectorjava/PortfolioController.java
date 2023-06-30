package com.casasky.binanceconnectorjava;

import com.casasky.binanceconnectorjava.model.AccountSnapshot;
import com.casasky.binanceconnectorjava.model.Asset;
import com.casasky.binanceconnectorjava.model.Price;
import com.casasky.binanceconnectorjava.model.Symbol;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
class PortfolioController {

    BinanceConnector binanceConnector;

    public PortfolioController(BinanceConnector binanceConnector) {
        this.binanceConnector = binanceConnector;
    }

    @GetMapping("walletSnapshot")
    @ResponseBody
    ResponseEntity<AccountSnapshot> walletSnapshot(@RequestParam Optional<Asset> asset, @RequestParam Optional<Integer> limit) {
       return ResponseEntity.ok(binanceConnector.walletSnapshot(asset, limit));
    }

    @GetMapping("price")
    @ResponseBody
    ResponseEntity<Price> price(@RequestParam Symbol symbol) {
        return ResponseEntity.ok(binanceConnector.price(symbol));
    }

}
