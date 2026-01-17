package com.lastValue.price;

import org.junit.jupiter.api.Test;

import com.lastValue.price.model.PriceRecord;
import com.lastValue.price.service.InMemoryPriceService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

class ConcurrencyTest {

	@Test
	void concurrentReadersAndSingleWriter() throws Exception {
		InMemoryPriceService service = new InMemoryPriceService();
		ExecutorService executor = Executors.newFixedThreadPool(10);

		UUID batchId = service.startBatch();

		Callable<Void> writer = () -> {
			service.upload(batchId, List.of(new PriceRecord("GOOG", Instant.now(), 1000)));
			service.completeBatch(batchId);
			return null;
		};

		Callable<Void> reader = () -> {
			service.getLastPrice("GOOG");
			return null;
		};

		for (int i = 0; i < 5; i++) {
			executor.submit(reader);
		}

		executor.submit(writer).get();

		for (int i = 0; i < 5; i++) {
			executor.submit(reader);
		}

		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.SECONDS);

		assertThat(service.getLastPrice("GOOG")).isPresent();
	}
}
