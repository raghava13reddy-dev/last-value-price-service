package com.lastValue.price.model;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds staging data for an active batch run.
 *
 * Prices are accumulated here while the batch is in STARTED state. This data is
 * NOT visible to consumers until the batch is completed.
 *
 * Alternative considered: Writing directly to the main store with locks.
 * Rejected because it would expose partial batch data and complicate concurrent
 * reads.
 */
public class BatchContext {

	private final UUID batchId;
	private BatchState state;
	private final Map<String, PriceRecord> stagedPrices = new ConcurrentHashMap<>();

	public BatchContext(UUID batchId) {
		this.batchId = batchId;
		this.state = BatchState.STARTED;
	}

	public UUID getBatchId() {
		return batchId;
	}

	public BatchState getState() {
		return state;
	}

	public void setState(BatchState state) {
		this.state = state;
	}

	public Map<String, PriceRecord> getStagedPrices() {
		return stagedPrices;
	}
}
