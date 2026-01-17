package com.lastValue.price.service;

import java.util.Optional;

/**
 * JVM-local API for consumers retrieving the latest price.
 *
 * Consumers access committed price snapshots only.
 * Partial batch data is never visible.
 */

import com.lastValue.price.model.PriceRecord;

public interface PriceConsumerService {

	Optional<PriceRecord> getLastPrice(String instrumentId);
}
