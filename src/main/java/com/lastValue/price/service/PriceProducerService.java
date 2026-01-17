package com.lastValue.price.service;

import java.util.List;
import java.util.UUID;

import com.lastValue.price.model.PriceRecord;

/**
 * JVM-local API for producers publishing price data.
 *
 * This interface is intentionally defined as a plain Java API (not REST or
 * messaging) to allow producers to run in the same JVM and interact via direct
 * method calls.
 *
 * This keeps the core service independent of any transport layer.
 */

public interface PriceProducerService {

	UUID startBatch();

	void upload(UUID batchId, List<PriceRecord> records);

	void completeBatch(UUID batchId);

	void cancelBatch(UUID batchId);
}
