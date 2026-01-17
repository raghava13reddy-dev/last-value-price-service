package com.lastValue.price.service;

import org.springframework.stereotype.Service;

import com.lastValue.price.exception.InvalidBatchStateException;
import com.lastValue.price.model.BatchContext;
import com.lastValue.price.model.BatchState;
import com.lastValue.price.model.PriceRecord;
import com.lastValue.price.store.PriceStore;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InMemoryPriceService implements PriceProducerService, PriceConsumerService {

	private final BatchManager batchManager = new BatchManager();
	private final PriceStore priceStore = new PriceStore();

	@Override
	public UUID startBatch() {
		return batchManager.startBatch().getBatchId();
	}

	@Override
	public void upload(UUID batchId, List<PriceRecord> records) {
		BatchContext ctx = batchManager.getActiveBatch(batchId);

		if (ctx.getState() != BatchState.STARTED) {
			throw new InvalidBatchStateException("Cannot upload to non-started batch");
		}

		records.forEach(record -> ctx.getStagedPrices().merge(record.getId(), record,
				(oldVal, newVal) -> newVal.getAsOf().isAfter(oldVal.getAsOf()) ? newVal : oldVal));
	}

	/**
	 * Completes a batch and publishes all prices atomically.
	 *
	 * Synchronization is used only during commit to ensure that concurrent batch
	 * completions do not interleave.
	 *
	 * Reads remain completely lock-free.
	 */
	@Override
	public synchronized void completeBatch(UUID batchId) {
		BatchContext ctx = batchManager.getActiveBatch(batchId);
		priceStore.commit(ctx.getStagedPrices());
		batchManager.complete(batchId);
	}

	@Override
	public void cancelBatch(UUID batchId) {
		batchManager.cancel(batchId);
	}

	@Override
	public Optional<PriceRecord> getLastPrice(String instrumentId) {
		return Optional.ofNullable(priceStore.getSnapshot().getPrices().get(instrumentId));
	}
}
