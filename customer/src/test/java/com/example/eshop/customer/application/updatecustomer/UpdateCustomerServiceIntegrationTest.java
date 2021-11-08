package com.example.eshop.customer.application.updatecustomer;

import com.example.eshop.customer.domain.customer.Customer.CustomerId;
import com.example.eshop.sharedtest.dbtests.DbTest;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;

@DbTest
@SpringBootTest
class UpdateCustomerServiceIntegrationTest {
    @Autowired
    UpdateCustomerService updateCustomerService;

    @Test
    @DataSet("customers.yml")
    @ExpectedDataSet("expectedUpdateCustomers.yml")
    @DBUnit(cacheConnection = false, caseSensitiveTableNames = true)
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
