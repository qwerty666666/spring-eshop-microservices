package com.example.eshop.rest.mappers;

import com.example.eshop.cart.application.usecases.checkout.CheckoutForm;
import com.example.eshop.cart.application.usecases.checkout.Total;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.delivery.ShipmentInfo;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.catalog.domain.file.File;
import com.example.eshop.catalog.domain.product.Attribute;
import com.example.eshop.catalog.domain.product.AttributeValue;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.rest.config.MappersConfig;
import com.example.eshop.rest.dto.CheckoutFormDto;
import com.example.eshop.rest.dto.CheckoutTotalDto;
import com.example.eshop.rest.dto.DeliveryAddressDto;
import com.example.eshop.rest.dto.DeliveryServiceDto;
import com.example.eshop.rest.dto.PaymentServiceDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MappersConfig.class)
class CheckoutMapperTest {
    @Autowired
    private CheckoutMapper checkoutMapper;

    @MockBean
    private ProductCrudService productCrudService;

    private Cart cart = FakeData.cart();
    private Map<Ean, Product> productInfo;

    @BeforeEach
    void setUp() {
        var attribute = new Attribute(1L, "size");

        var ean = cart.getItems().get(0).getEan();

        var product = Product.builder()
                .name("product")
                .addImage(new File("file-1"))
                .addImage(new File("file-2"))
                .addSku(Sku.builder()
                        .ean(ean)
                        .price(Money.USD(10))
                        .availableQuantity(10)
                        .addAttribute(new AttributeValue(attribute, "XL", 1))
                        .build())
                .build();

        productInfo = Map.of(ean, product);

        when(productCrudService.getByEan(anyList())).thenReturn(productInfo);
    }

    @Test
    void toCheckoutFormDtoTest() {
        var deliveryService = new DeliveryServiceStub(new DeliveryServiceId("1"), "delivery");
        var paymentService = new PaymentServiceStub(new PaymentServiceId("1"), "payment");

        var checkoutForm = CheckoutForm.builder()
                .order(new Order(UUID.randomUUID(), FakeData.customerId(), FakeData.cart(), FakeData.deliveryAddress(), deliveryService, paymentService))
                .availableDeliveries(List.of(deliveryService))
                .availablePayments(List.of(paymentService))
                .total(new Total(Money.USD(1), Money.USD(2.3), Money.USD(10)))
                .build();

        var dto = checkoutMapper.toCheckoutFormDto(checkoutForm);

        assertCheckoutFormEquals(checkoutForm, dto);
    }

    private void assertCheckoutFormEquals(CheckoutForm form, CheckoutFormDto dto) {
        // cart
        Assertions.assertCartEquals(form.getOrder().getCart(), productInfo, dto.getCart());
        // address
        assertAddressEquals(form.getOrder().getAddress(), dto.getDeliveryAddress());
        // deliveries
        Assertions.assertListEquals(form.getAvailableDeliveries(), dto.getAvailableDeliveries(), this::assertDeliveryServiceEquals);
        // payments
        Assertions.assertListEquals(form.getAvailablePayments(), dto.getAvailablePayments(), this::assertPaymentServiceEquals);
        // Total
        assertTotalEquals(form.getTotal(), dto.getTotal());
    }

    private void assertAddressEquals(DeliveryAddress address, DeliveryAddressDto dto) {
        assertThat(dto.getCountry()).isEqualTo(address.country());
        assertThat(dto.getCity()).isEqualTo(address.city());
        assertThat(dto.getStreet()).isEqualTo(address.street());
        assertThat(dto.getBuilding()).isEqualTo(address.building());
        assertThat(dto.getFlat()).isEqualTo(address.flat());
        assertThat(dto.getFullname()).isEqualTo(address.fullname());
        assertThat(dto.getPhone()).isEqualTo(address.phone() == null ? null : address.phone().toString());
    }

    private void assertDeliveryServiceEquals(DeliveryService service, DeliveryServiceDto dto) {
        assertThat(dto.getId()).isEqualTo(service.getId() == null ? null : service.getId().toString());
        assertThat(dto.getName()).isEqualTo(service.getName());
    }

    private void assertPaymentServiceEquals(PaymentService service, PaymentServiceDto dto) {
        assertThat(dto.getId()).isEqualTo(service.getId() == null ? null : service.getId().toString());
        assertThat(dto.getName()).isEqualTo(service.getName());
    }

    private void assertTotalEquals(Total total, CheckoutTotalDto dto) {
        Assertions.assertPriceEquals(total.getCartPrice(), dto.getCartPrice());
        Assertions.assertPriceEquals(total.getDeliveryPrice(), dto.getDeliveryPrice());
        Assertions.assertPriceEquals(total.getTotalPrice(), dto.getTotalPrice());
    }

    private static class DeliveryServiceStub extends DeliveryService {
        public DeliveryServiceStub(DeliveryServiceId id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public ShipmentInfo getShipmentInfo(Order order) {
            return null;
        }
    }

    private static class PaymentServiceStub extends PaymentService {
        public PaymentServiceStub(PaymentServiceId id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean canPay(Order order) {
            return true;
        }
    }
}
