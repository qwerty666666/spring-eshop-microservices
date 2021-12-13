package com.example.eshop.warehouse.application.services.reserve;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedtest.dbtests.DbTest;
import com.example.eshop.warehouse.application.services.reserve.ReserveResult.InsufficientQuantityError;
import com.example.eshop.warehouse.application.services.reserve.ReserveResult.StockItemNotFoundError;
import com.example.eshop.warehouse.domain.StockQuantity;
import com.example.eshop.warehouse.domain.events.ProductStockChangedEvent;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DbTest
class ReserveStockItemServiceImplIT {
    private final static Ean STOCK_ITEM_1_EAN = Ean.fromString("0000000000001");
    private final static StockQuantity STOCK_ITEM_1_QUANTITY = StockQuantity.of(10);

    private final static Ean STOCK_ITEM_2_EAN = Ean.fromString("0000000000002");
    private final static StockQuantity STOCK_ITEM_2_QUANTITY = StockQuantity.of(10);

    private final static Ean NON_EXISTING_EAN = Ean.fromString("0000000000333");

    @TestConfiguration
    public static class Config {
        // Spring can't mock ApplicationEventPublisher. So use this
        // weird workaround to do it.
        // TODO may be it is better to wrap ApplicationEventPublished in our own interface? (but we'll lose IDEA navigation support for events)
        @Bean
        @Primary
        ApplicationEventPublisher eventPublisher() {
            return mock(ApplicationEventPublisher.class);
        }
    }

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    ReserveStockItemService reserveStockItemService;

    @Test
    @DataSet("stock_items.yml")
    @ExpectedDataSet("stock_items.yml")
    void givenNonExistingEan_whenReserve_thenReturnErrorAndNoStockItemShouldBeUpdated() {
        // When
        var result = reserveStockItemService.reserve(Map.of(
                STOCK_ITEM_1_EAN, STOCK_ITEM_1_QUANTITY,
                NON_EXISTING_EAN, StockQuantity.of(1)
        ));

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(NON_EXISTING_EAN)).isInstanceOf(StockItemNotFoundError.class);
    }

    @Test
    @DataSet("stock_items.yml")
    @ExpectedDataSet("stock_items.yml")
    void whenReserveWithExceededQuantity_thenReturnErrorsAndNoStockItemShouldBeUpdated() {
        // When
        var result = reserveStockItemService.reserve(Map.of(
                STOCK_ITEM_1_EAN, STOCK_ITEM_1_QUANTITY,
                STOCK_ITEM_2_EAN, STOCK_ITEM_2_QUANTITY.add(StockQuantity.of(1))
        ));

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(STOCK_ITEM_2_EAN)).isInstanceOf(InsufficientQuantityError.class);
    }

    @Test
    @DataSet("stock_items.yml")
    @ExpectedDataSet("reserved_stock_items.yml")
    void whenReserve_thenDatabaseIsUpdatedAndDomainEventsArePublished() {
        // When
        var result = reserveStockItemService.reserve(Map.of(
                STOCK_ITEM_1_EAN, StockQuantity.of(5)
        ));

        // Then
        assertThat(result.isSuccess()).isTrue();
        verify(eventPublisher).publishEvent(isA(ProductStockChangedEvent.class));
    }
}
