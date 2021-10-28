package com.example.eshop.customer.application.updatecustomer;

import com.example.eshop.customer.domain.customer.Customer.CustomerId;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;

@SpringBootTest
@DBRider
class UpdateCustomerServiceIntegrationTest {
    @Autowired
    UpdateCustomerService updateCustomerService;

    @Test
    @DataSet("customers.yml")
    @ExpectedDataSet("expectedUpdateCustomers.yml")
    @DBUnit(cacheConnection = false)
    void givenUpdateCustomerCommand_whenUpdateCustomer_thenDbUpdated() {
        // Given
        var id = new CustomerId("1");
        var firstname = "firstname";
        var lastname = "lastname";
        var email = "test@test.test";
        var birthday = LocalDate.of(1990, 10, 25);

        var command = new UpdateCustomerCommand(id, firstname, lastname, email, birthday);

        // When
        updateCustomerService.updateCustomer(command);
    }
}
