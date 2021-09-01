package com.example.eshop.rest.controllers;

import com.example.eshop.customer.application.query.QueryCustomerService;
import com.example.eshop.customer.application.signup.SignUpCommand;
import com.example.eshop.customer.application.signup.SignUpService;
import com.example.eshop.customer.application.updatecustomer.UpdateCustomerCommand;
import com.example.eshop.customer.application.updatecustomer.UpdateCustomerService;
import com.example.eshop.customer.domain.customer.Customer;
import com.example.eshop.customer.domain.customer.Customer.CustomerId;
import com.example.eshop.customer.domain.customer.EmailAlreadyExistException;
import com.example.eshop.customer.domain.customer.HashedPassword;
import com.example.eshop.customer.infrastructure.auth.UserDetailsImpl;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerTest {
    private final static String CUSTOMER_EMAIL = "test@test.test";
    private final static CustomerId CUSTOMER_ID = new CustomerId("1");
    private final static Customer CUSTOMER = Customer.builder()
            .id(CUSTOMER_ID)
            .firstname("firstname")
            .lastname("lastname")
            .email(Email.fromString(CUSTOMER_EMAIL))
            .birthday(LocalDate.of(1990, 10, 20))
            .password(HashedPassword.fromHash("pass"))
            .build();

    private final static String NEW_FIRSTNAME = "newFirstname";
    private final static String NEW_LASTNAME = "newLastname";
    private final static String NEW_EMAIL = "new-email@example.com";
    private final static LocalDate NEW_BIRTHDAY = LocalDate.of(1999, 9, 19);
    private final static String NEW_PASSWORD = "pass123";

    @TestConfiguration
    @ComponentScan(basePackages = "com.example.eshop.rest.mappers")
    public static class Config {
        @Bean
        @Primary
        public UserDetailsService userDetailsService() {
            return new InMemoryUserDetailsManager(List.of(
                    new UserDetailsImpl(CUSTOMER_EMAIL, "pass", CUSTOMER_ID.toString())
            ));
        }
    }

    @Autowired
    MockMvc mockMvc;

    @MockBean
    QueryCustomerService queryCustomerService;

    @MockBean
    UpdateCustomerService updateCustomerService;

    @MockBean
    SignUpService signUpService;

    @BeforeEach
    void setUp() {
        when(queryCustomerService.getByEmail(eq(CUSTOMER_EMAIL))).thenReturn(CUSTOMER);
    }

    //--------------------------
    // getCurrent()
    //--------------------------
    
    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    void givenAuthenticatedUser_whenGetCurrent_thenReturnThatUser() throws Exception {
        performGetCurrentCustomerRequest()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CUSTOMER.getId().toString()))
                .andExpect(jsonPath("$.firstname").value(CUSTOMER.getFirstname()))
                .andExpect(jsonPath("$.lastname").value(CUSTOMER.getLastname()))
                .andExpect(jsonPath("$.birthday").value(
                        CUSTOMER.getBirthday() != null ? CUSTOMER.getBirthday().format(DateTimeFormatter.ISO_DATE) : "null"
                ))
                .andExpect(jsonPath("$.email").value(CUSTOMER.getEmail().toString()));

        verify(queryCustomerService).getByEmail(CUSTOMER_EMAIL);
    }

    @Test
    void givenUnauthorizedUser_whenGetCurrent_thenReturn401() throws Exception {
        performGetCurrentCustomerRequest()
                .andExpect(status().isUnauthorized());
    }

    private ResultActions performGetCurrentCustomerRequest() throws Exception {
        return mockMvc.perform(get("/api/customers/current"));
    }

    //--------------------------
    // updateCurrent()
    //--------------------------

    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    void givenAuthenticatedCustomer_whenUpdateCustomer_thenReturnOk() throws Exception {
        // Given
        var expectedCommand = new UpdateCustomerCommand(CUSTOMER.getId(), NEW_FIRSTNAME, NEW_LASTNAME, NEW_EMAIL, NEW_BIRTHDAY);

        // When + Then
        performUpdateCurrentCustomerRequest()
                .andExpect(status().isOk());

        verify(updateCustomerService).updateCustomer(eq(expectedCommand));
    }

    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    void givenAuthenticatedCustomer_whenUpdateCurrentCustomerWithAlreadyUsedEmail_thenReturn400() throws Exception {
        // Given
        var expectedCommand = new UpdateCustomerCommand(CUSTOMER.getId(), NEW_FIRSTNAME, NEW_LASTNAME, NEW_EMAIL, NEW_BIRTHDAY);

        doThrow(EmailAlreadyExistException.class).when(updateCustomerService).updateCustomer(any());

        // When + Then
        performUpdateCurrentCustomerRequest()
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("This email address already used by another user"));

        verify(updateCustomerService).updateCustomer(eq(expectedCommand));
    }

    @Test
    void givenUnauthorizedUser_whenUpdateCurrentCustomer_thenReturn401() throws Exception {
        performUpdateCurrentCustomerRequest()
                .andExpect(status().isUnauthorized());
    }

    private ResultActions performUpdateCurrentCustomerRequest() throws Exception {
        return mockMvc.perform(
                put("/api/customers/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "firstname": "%s",
                            "lastname": "%s",
                            "email": "%s",
                            "birthday": "%s"
                        }
                        """.formatted(NEW_FIRSTNAME, NEW_LASTNAME, NEW_EMAIL,
                                NEW_BIRTHDAY.format(DateTimeFormatter.ISO_DATE))
                        )
        );
    }

    //--------------------------
    // signUp()
    //--------------------------

    @Test
    void whenSignUp_thenReturnNewCustomer() throws Exception {
        // Given
        var command = new SignUpCommand(NEW_FIRSTNAME, NEW_LASTNAME, NEW_EMAIL, NEW_BIRTHDAY, NEW_PASSWORD);
        when(signUpService.signUp(eq(command))).thenReturn(Customer.builder()
                .firstname(NEW_FIRSTNAME)
                .lastname(NEW_LASTNAME)
                .email(Email.fromString(NEW_EMAIL))
                .birthday(NEW_BIRTHDAY)
                .password(HashedPassword.fromHash(""))
                .build()
        );

        // When + Then
        performSignUpRequest()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstname").value(NEW_FIRSTNAME))
                .andExpect(jsonPath("$.lastname").value(NEW_LASTNAME))
                .andExpect(jsonPath("$.birthday").value(NEW_BIRTHDAY.format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.email").value(NEW_EMAIL));

        verify(signUpService).signUp(eq(command));
    }

    @Test
    void givenAlreadyUsedEmail_whenSignUp_thenReturn400() throws Exception {
        // Given
        var command = new SignUpCommand(NEW_FIRSTNAME, NEW_LASTNAME, NEW_EMAIL, NEW_BIRTHDAY, NEW_PASSWORD);
        when(signUpService.signUp(eq(command))).thenThrow(EmailAlreadyExistException.class);

        // When + Then
        performSignUpRequest()
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("This email address already used by another user"));

        verify(signUpService).signUp(eq(command));
    }

    private ResultActions performSignUpRequest() throws Exception {
        return mockMvc.perform(
                post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstname": "%s",
                                    "lastname": "%s",
                                    "email": "%s",
                                    "birthday": "%s",
                                    "password": "%s"
                                }
                                """.formatted(NEW_FIRSTNAME, NEW_LASTNAME, NEW_EMAIL,
                                        NEW_BIRTHDAY.format(DateTimeFormatter.ISO_DATE), NEW_PASSWORD)
                        )
        );
    }
}
