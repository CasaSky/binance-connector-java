package com.casasky.binanceconnectorjava.model;

import java.math.BigDecimal;

public record Price(Symbol symbol, BigDecimal price) {
}
