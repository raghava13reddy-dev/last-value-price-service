package com.lastValue.price.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lastValue.price.exception.BatchNotFoundException;
import com.lastValue.price.exception.InvalidBatchStateException;
import com.lastValue.price.model.BatchContext;
import com.lastValue.price.model.BatchState;

/**
 * Manages batch lifecycle and enforces valid state transitions.
 *
 * This component protects the service from incorrect producer usage, such as
 * uploading data after completion or canceling twice.
 */
public class BatchManager {

	private final Map<UUID, BatchContext> batches = new ConcurrentHashMap<>();
	
	private static final Logger log = LoggerFactory.getLogger(BatchManager.class);

	public BatchContext startBatch() {
		UUID id = UUID.randomUUID();
		log.info("Starting batch {}", id);
		BatchContext context = new BatchContext(id);
		batches.put(id, context);
		return context;
	}

	public BatchContext getActiveBatch(UUID batchId) {
		BatchContext ctx = batches.get(batchId);
		if (ctx == null) {
			throw new BatchNotFoundException("Batch not found: " + batchId);
		}
		return ctx;
	}

	public void complete(UUID batchId) {
		log.info("Completing batch {}", batchId);
		BatchContext ctx = getActiveBatch(batchId);
		if (ctx.getState() != BatchState.STARTED) {
			throw new InvalidBatchStateException("Batch not in STARTED state");
		}
		ctx.setState(BatchState.COMPLETED);
		batches.remove(batchId);
	}

	public void cancel(UUID batchId) {
		log.info("Cancelling batch {}", batchId);
		BatchContext ctx = getActiveBatch(batchId);
		ctx.setState(BatchState.CANCELLED);
		batches.remove(batchId);
	}
}
