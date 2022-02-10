package com.example.eshop.rest.controllers;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.customer.application.query.QueryCustomerService;
import com.example.eshop.customer.application.signup.SignUpCommand;
import com.example.eshop.customer.application.signup.SignUpService;
import com.example.eshop.customer.application.updatecustomer.UpdateCustomerCommand;
import com.example.eshop.customer.application.updatecustomer.UpdateCustomerService;
import com.example.eshop.customer.domain.customer.Customer;
import com.example.eshop.customer.domain.customer.Customer.CustomerId;
import com.example.eshop.customer.domain.customer.EmailAlreadyExistException;
import com.example.eshop.customer.domain.customer.HashedPassword;
import com.example.eshop.customer.domain.customer.PasswordPolicyException;
import com.example.eshop.rest.config.AuthConfig;
import com.example.eshop.rest.config.ControllerTest;
import com.example.eshop.rest.dto.CustomerDto;
import com.example.eshop.rest.dto.NewCustomerDto;
import com.example.eshop.rest.mappers.CustomerMapper;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomerController.class)
@ControllerTest
class CustomerControllerTest {
    private final static CustomerId CUSTOMER_ID = new CustomerId(AuthConfig.CUSTOMER_ID);
    private final static String CUSTOMER_EMAIL = AuthConfig.CUSTOMER_EMAIL;
    private final static String CUSTOMER_PASSWORD = AuthConfig.CUSTOMER_PASSWORD;

    private final static String NEW_FIRSTNAME = "newFirstname";
    private final static String NEW_LASTNAME = "newLastname";
    private final static String NEW_EMAIL = "new-email@example.com";
    private final static LocalDate NEW_BIRTHDAY = LocalDate.of(1999, 9, 19);
    private final static String NEW_PASSWORD = "pass123";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QueryCustomerService queryCustomerService;
    @MockBean
    private UpdateCustomerService updateCustomerService;
    @MockBean
    private SignUpService signUpService;
    @MockBean
    private CustomerMapper customerMapper;

    private Customer customer;
    private final CustomerDto customerDto = new CustomerDto();

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(CUSTOMER_ID)
                .firstname("firstname")
                .lastname("lastname")
                .email(Email.fromString(CUSTOMER_EMAIL))
                .birthday(LocalDate.of(1990, 10, 20))
                .password(HashedPassword.fromHash(CUSTOMER_PASSWORD))
                .build();

        when(queryCustomerService.getByEmail(CUSTOMER_EMAIL)).thenReturn(customer);
        when(customerMapper.toCustomerDto(customer)).thenReturn(customerDto);
    }

    @Nested
    class GetAuthenticatedTests {
        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void givenAuthenticatedUser_whenGetAuthenticated_thenReturnThatUser() throws Exception {
            var expectedJson = objectMapper.writeValueAsString(customerDto);

            performGetAuthenticatedRequest()
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(queryCustomerService).getByEmail(CUSTOMER_EMAIL);
        }

        @Test
        void givenUnauthorizedUser_whenGetAuthenticated_thenReturn401() throws Exception {
            performGetAuthenticatedRequest()
                    .andExpect(status().isUnauthorized());
        }

        private ResultActions performGetAuthenticatedRequest() throws Exception {
            return mockMvc.perform(get("/api/customers"));
        }
    }

    @Nested
    class UpdateCurrentTests {
        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void givenAuthenticatedCustomer_whenUpdateCustomer_thenReturnOk() throws Exception {
            // Given
            var expectedCommand = new UpdateCustomerCommand(customer.getId(), NEW_FIRSTNAME, NEW_LASTNAME, NEW_EMAIL, NEW_BIRTHDAY);

            // When + Then
            performUpdateCurrentCustomerRequest()
                    .andExpect(status().isOk());

            verify(updateCustomerService).updateCustomer(expectedCommand);
        }

        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void givenAuthenticatedCustomer_whenUpdateCurrentCustomerWithAlreadyUsedEmail_thenReturn400() throws Exception {
            // Given
            var expectedCommand = new UpdateCustomerCommand(customer.getId(), NEW_FIRSTNAME, NEW_LASTNAME, NEW_EMAIL, NEW_BIRTHDAY);

            doThrow(EmailAlreadyExistException.class).when(updateCustomerService).updateCustomer(any());

            // When + Then
            var response = performUpdateCurrentCustomerRequest();
            assertEmailAlreadyUsedResponse(response);

            verify(updateCustomerService).updateCustomer(expectedCommand);
        }

        @Test
        void givenUnauthorizedUser_whenUpdateCurrentCustomer_thenReturn401() throws Exception {
            performUpdateCurrentCustomerRequest()
                    .andExpect(status().isUnauthorized());
        }

        private ResultActions performUpdateCurrentCustomerRequest() throws Exception {
            var json = """
                    {
                        "firstname": "%s",
                        "lastname": "%s",
                        "email": "%s",
                        "birthday": "%s"
                    }
                    """.formatted(NEW_FIRSTNAME, NEW_LASTNAME, NEW_EMAIL, NEW_BIRTHDAY.format(DateTimeFormatter.ISO_DATE));

            return mockMvc.perform(put("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json));
        }
    }

    @Nested
    class CreateCustomerTests {
        private final NewCustomerDto newCustomerDto = new NewCustomerDto()
                .firstname(NEW_FIRSTNAME)
                .lastname(NEW_LASTNAME)
                .email(NEW_EMAIL)
                .birthday(NEW_BIRTHDAY)
                .password(NEW_PASSWORD);
        private final SignUpCommand signUpCommand = new SignUpCommand(NEW_FIRSTNAME, NEW_LASTNAME, NEW_EMAIL, NEW_BIRTHDAY, NEW_PASSWORD);
        private final Customer newCustomer = Customer.builder()
                .firstname(NEW_FIRSTNAME)
                .lastname(NEW_LASTNAME)
                .email(Email.fromString(NEW_EMAIL))
                .birthday(NEW_BIRTHDAY)
                .password(HashedPassword.fromHash(NEW_PASSWORD))
                .build();

        @BeforeEach
        void setUp() {
            when(customerMapper.toSignUpCommand(newCustomerDto)).thenReturn(signUpCommand);
        }

        @Test
        void whenCreateCustomer_thenReturnNewCustomer() throws Exception {
            // Given
            when(signUpService.signUp(signUpCommand)).thenReturn(newCustomer);

            var expectedCustomerDto = new CustomerDto();
            when(customerMapper.toCustomerDto(newCustomer)).thenReturn(expectedCustomerDto);

            var expectedJson = objectMapper.writeValueAsString(expectedCustomerDto);

            // When + Then
            performSignUpRequest()
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(signUpService).signUp(signUpCommand);
        }

        @Test
        void givenAlreadyUsedEmail_whenCreateCustomer_thenReturn400() throws Exception {
            // Given
            when(signUpService.signUp(signUpCommand)).thenThrow(EmailAlreadyExistException.class);

            // When + Then
            var response = performSignUpRequest();
            assertEmailAlreadyUsedResponse(response);

            verify(signUpService).signUp(signUpCommand);
        }

        @Test
        void givenInvalidPassword_whenCreateCustomer_thenReturn400() throws Exception {
            // Given
            var policyViolationMessage = "Test policy violation message";
            when(signUpService.signUp(signUpCommand)).thenThrow(new PasswordPolicyException(List.of(policyViolationMessage)));

            // When + Then
            var response = performSignUpRequest();
            assertPasswordPolicyViolationResponse(response, policyViolationMessage);

            verify(signUpService).signUp(signUpCommand);
        }

        private ResultActions performSignUpRequest() throws Exception {
            var json = """
                    {
                        "firstname": "%s",
                        "lastname": "%s",
                        "email": "%s",
                        "birthday": "%s",
                        "password": "%s"
                    }
                    """.formatted(NEW_FIRSTNAME, NEW_LASTNAME, NEW_EMAIL, NEW_BIRTHDAY.format(DateTimeFormatter.ISO_DATE), NEW_PASSWORD);

            return mockMvc.perform(post("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json));
        }
    }

    private void assertEmailAlreadyUsedResponse(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("This email address already used by another user"));
    }

    private void assertPasswordPolicyViolationResponse(ResultActions resultActions, String errorMessage) throws Exception {
        resultActions
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field").value("password"))
                .andExpect(jsonPath("$.errors[0].message").value(containsString(errorMessage)));
    }
}
