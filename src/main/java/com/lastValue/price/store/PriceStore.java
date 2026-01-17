package com.lastValue.price.store;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import com.lastValue.price.model.PriceRecord;

/**
 * In-memory committed price store.
 *
 * Uses an atomic snapshot (copy-on-write) strategy to guarantee: - Atomic
 * visibility of completed batches - Lock-free reads for consumers
 *
 * Alternative considered: Fine-grained locking on a shared map Rejected due to
 * higher contention and risk of consumers observing partially updated state.
 */

public class PriceStore {

	private final AtomicReference<PriceSnapshot> snapshot = new AtomicReference<>(new PriceSnapshot(new HashMap<>()));

	public PriceSnapshot getSnapshot() {
		return snapshot.get();
	}

	/**
	 * Atomically publishes a completed batch.
	 *
	 * A new immutable snapshot is created by merging the current snapshot with the
	 * batch data. The AtomicReference swap guarantees that consumers see either the
	 * old snapshot or the new one, never a mix.
	 */
	public void commit(Map<String, PriceRecord> batchData) {
		PriceSnapshot current = snapshot.get();
		Map<String, PriceRecord> merged = new HashMap<>(current.getPrices());

		batchData.forEach((id, record) -> merged.merge(id, record,
				(oldVal, newVal) -> newVal.getAsOf().isAfter(oldVal.getAsOf()) ? newVal : oldVal));

		snapshot.set(new PriceSnapshot(merged));
	}
}
