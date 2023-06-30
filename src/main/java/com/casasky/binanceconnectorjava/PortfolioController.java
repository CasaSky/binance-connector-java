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

    @GetMapping("wallet/snapshot/asset")
    @ResponseBody
    ResponseEntity<AccountSnapshot> walletSnapshotByAsset(@RequestParam Asset asset) {
       return ResponseEntity.ok(binanceConnector.retrieveWalletSnapshotByAsset(asset));
    }

    @GetMapping("wallet/snapshot")
    @ResponseBody
    ResponseEntity<AccountSnapshot> walletSnapshot() {
        return ResponseEntity.ok(binanceConnector.retrieveWalletSnapshot());
    }

    @GetMapping("wallet/snapshot/compact")
    @ResponseBody
    ResponseEntity<AccountSnapshot> walletSnapshotCompact(@RequestParam Integer limit) {
        return ResponseEntity.ok(binanceConnector.retrieveWalletSnapshotCompact(limit));
    }

    @GetMapping("price")
    @ResponseBody
    ResponseEntity<Price> price(@RequestParam Symbol symbol) {
        return ResponseEntity.ok(binanceConnector.price(symbol));
    }

}
