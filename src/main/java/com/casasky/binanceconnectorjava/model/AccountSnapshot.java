package com.casasky.binanceconnectorjava.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record AccountSnapshot(BigDecimal totalAssetOfEur, BigDecimal totalAssetOfBtc, List<Balance> balances) {

    public record Balance(String asset, BigDecimal free, String locked) {

        @JsonIgnore
        public boolean isPositive() {
            return free.signum() > 0;
        }

        @JsonIgnore
        public boolean isAssetEqualTo(Asset assetParam) {
            return Objects.equals(asset, assetParam.name());
        }
    }

}
