package com.example.eshop.warehouse.application.services.reserve;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.pg14test.DbTest;
import com.example.eshop.transactionaloutbox.OutboxMessage;
import com.example.eshop.transactionaloutbox.TransactionalOutbox;
import com.example.eshop.warehouse.client.reservationresult.InsufficientQuantityError;
import com.example.eshop.warehouse.client.reservationresult.StockItemNotFoundError;
import com.example.eshop.warehouse.domain.StockQuantity;
import com.example.eshop.warehouse.client.events.ProductStockChangedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@DbTest
class ReserveStockItemServiceImplIntegrationTest {
    private final static Ean STOCK_ITEM_1_EAN = Ean.fromString("0000000000001");
    private final static StockQuantity STOCK_ITEM_1_QUANTITY = StockQuantity.of(10);

    private final static Ean STOCK_ITEM_2_EAN = Ean.fromString("0000000000002");
    private final static StockQuantity STOCK_ITEM_2_QUANTITY = StockQuantity.of(10);

    private final static Ean NON_EXISTING_EAN = Ean.fromString("0000000000333");

    @MockBean
    TransactionalOutbox transactionalOutbox;

    @Autowired
    ReserveStockItemService reserveStockItemService;

    @Autowired
    ObjectMapper objectMapper;

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
        // Given
        var reservingQuantity = StockQuantity.of(5);
        var expectedEvent = new ProductStockChangedEvent(
                STOCK_ITEM_1_EAN,
                STOCK_ITEM_1_QUANTITY.subtract(reservingQuantity).toInt()
        );


        // When
        var result = reserveStockItemService.reserve(Map.of(
                STOCK_ITEM_1_EAN, reservingQuantity
        ));

        // Then
        assertThat(result.isSuccess()).isTrue();

        ArgumentCaptor<List<OutboxMessage>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionalOutbox).add(captor.capture());

        assertThat(captor.getValue()).hasSize(1);
        assertOutboxMessageEqualsTo(captor.getValue().get(0), expectedEvent);
    }

    @SneakyThrows
    private void assertOutboxMessageEqualsTo(OutboxMessage message, Object expectedPayloadObject) {
        assertThat(message.getType()).isEqualTo(expectedPayloadObject.getClass().getName());

        var event = objectMapper.readValue(message.getPayload(), expectedPayloadObject.getClass());
        assertThat(event).isEqualTo(expectedPayloadObject);
    }
}
