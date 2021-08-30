package com.example.eshop.rest.controllers;

import com.example.eshop.customer.application.query.QueryCustomerService;
import com.example.eshop.customer.application.signup.SignUpService;
import com.example.eshop.customer.application.updatecustomer.UpdateCustomerCommand;
import com.example.eshop.customer.application.updatecustomer.UpdateCustomerService;
import com.example.eshop.customer.domain.customer.EmailAlreadyExistException;
import com.example.eshop.rest.mappers.SignUpCommandMapper;
import com.example.eshop.rest.requests.SignUpRequest;
import com.example.eshop.rest.requests.UpdateCustomerRequest;
import com.example.eshop.rest.resources.customer.CustomerResource;
import com.example.eshop.rest.resources.shared.ValidationErrorResponse;
import com.example.eshop.rest.resources.shared.ValidationErrorResponse.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Locale;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private QueryCustomerService queryCustomerService;

    @Autowired
    private UpdateCustomerService updateCustomerService;

    @Autowired
    private SignUpService signUpService;

    @Autowired
    SignUpCommandMapper signUpCommandMapper;

    /**
     * @return validation error if email already in use by another customer
     */
    @ExceptionHandler(EmailAlreadyExistException.class)
    private ResponseEntity<ValidationErrorResponse> handleEmailAlreadyInSUeException(Locale locale) {
        var emailError = messageSource.getMessage("emailAlreadyInUse", null, locale);

        return ResponseEntity
                .status(400)
                .body(new ValidationErrorResponse()
                        .addError(new Error("email", emailError))
                );
    }

    /**
     * @return the authenticated customer
     */
    @GetMapping("/current")
    public CustomerResource getCurrent(Authentication authentication) {
        var customer = queryCustomerService.getByEmail(authentication.getName());

        return new CustomerResource(customer);
    }

    /**
     * Updates the authenticated customer
     */
    @PutMapping("/current")
    public void updateCurrent(@RequestBody @Valid UpdateCustomerRequest request, Authentication authentication) {
        var customer = queryCustomerService.getByEmail(authentication.getName());

        var command = new UpdateCustomerCommand(
                customer.getId(),
                request.firstname(),
                request.lastname(),
                request.email(),
                request.birthday()
        );

        updateCustomerService.updateCustomer(command);
    }

    /**
     * Register new customer
     *
     * @return new customer
     */
    @PostMapping("")
    public CustomerResource singUp(@RequestBody @Valid SignUpRequest request) {
        var command = signUpCommandMapper.toSignUpCommand(request);

        var customer = signUpService.signUp(command);

        return new CustomerResource(customer);
    }
}
