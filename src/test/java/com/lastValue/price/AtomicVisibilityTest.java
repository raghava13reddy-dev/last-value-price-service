package com.lastValue.price;

import org.junit.jupiter.api.Test;

import com.lastValue.price.model.PriceRecord;
import com.lastValue.price.service.InMemoryPriceService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class AtomicVisibilityTest {

	@Test
	void consumerNeverSeesPartialBatch() {
		InMemoryPriceService service = new InMemoryPriceService();
		UUID batchId = service.startBatch();

		service.upload(batchId, List.of(new PriceRecord("AAPL", Instant.now(), 100)));

		// before commit
		assertThat(service.getLastPrice("AAPL")).isEmpty();

		service.completeBatch(batchId);

		// after commit
		assertThat(service.getLastPrice("AAPL")).isPresent();
	}
}
