package com.lastValue.price.store;

import java.util.Collections;
import java.util.Map;

import com.lastValue.price.model.PriceRecord;

public class PriceSnapshot {

    private final Map<String, PriceRecord> prices;

    public PriceSnapshot(Map<String, PriceRecord> prices) {
        this.prices = Collections.unmodifiableMap(prices);
    }

    public Map<String, PriceRecord> getPrices() {
        return prices;
    }
}
