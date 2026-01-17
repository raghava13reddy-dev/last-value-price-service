package com.lastValue.price;

import org.junit.jupiter.api.Test;

import com.lastValue.price.model.PriceRecord;
import com.lastValue.price.service.InMemoryPriceService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class BatchLifecycleTest {

	private final InMemoryPriceService service = new InMemoryPriceService();

	@Test
	void cancelledBatchIsDiscarded() {
		UUID batchId = service.startBatch();

		service.upload(batchId, List.of(new PriceRecord("IBM", Instant.now(), 10)));

		service.cancelBatch(batchId);

		assertThat(service.getLastPrice("IBM")).isEmpty();
	}

	@Test
	void latestAsOfWinsInsideBatch() {
		UUID batchId = service.startBatch();

		service.upload(batchId, List.of(new PriceRecord("IBM", Instant.parse("2024-01-01T10:00:00Z"), 10),
				new PriceRecord("IBM", Instant.parse("2024-01-01T11:00:00Z"), 20)));

		service.completeBatch(batchId);

		assertThat(service.getLastPrice("IBM").get().getPayload()).isEqualTo(20);
	}
}
