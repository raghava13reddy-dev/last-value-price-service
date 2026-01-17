package com.lastValue.price;

import org.junit.jupiter.api.Test;

import com.lastValue.price.model.PriceRecord;
import com.lastValue.price.service.InMemoryPriceService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PriceServiceIntegrationTest {

    private final InMemoryPriceService service = new InMemoryPriceService();

    @Test
    void batchIsVisibleOnlyAfterCompletion() {
        UUID batchId = service.startBatch();

        service.upload(batchId, List.of(
                new PriceRecord("AAPL", Instant.now(), 100)
        ));

        assertThat(service.getLastPrice("AAPL")).isEmpty();

        service.completeBatch(batchId);

        assertThat(service.getLastPrice("AAPL")).isPresent();
    }
}
