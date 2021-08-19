package com.example.eshop.rest.controllers;

import com.example.eshop.customer.application.query.QueryCustomerService;
import com.example.eshop.customer.application.signup.SignUpService;
import com.example.eshop.customer.application.updatecustomer.UpdateCustomerCommand;
import com.example.eshop.customer.application.updatecustomer.UpdateCustomerService;
import com.example.eshop.customer.domain.customer.EmailAlreadyExistException;
import com.example.eshop.rest.mappers.SignUpCommandMapper;
import com.example.eshop.rest.requests.SignUpRequest;
import com.example.eshop.rest.requests.UpdateCustomerRequest;
import com.example.eshop.rest.resources.CustomerResource;
import com.example.eshop.rest.resources.ValidationErrorResponse;
import com.example.eshop.rest.resources.ValidationErrorResponse.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Locale;

@RestController
@RequestMapping("/customers")
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

    @GetMapping("/current")
    public CustomerResource getCurrent(Authentication authentication) {
        var customer = queryCustomerService.getByEmail(authentication.getName());

        return new CustomerResource(customer);
    }

    @PutMapping("/current")
    public ResponseEntity<Object> updateCurrent(
            @RequestBody @Valid UpdateCustomerRequest request,
            Authentication authentication,
            Locale locale
    ) {
        var customer = queryCustomerService.getByEmail(authentication.getName());

        var command = new UpdateCustomerCommand(
                customer.getId(),
                request.firstname(),
                request.lastname(),
                request.email(),
                request.birthday()
        );

        try {
            updateCustomerService.updateCustomer(command);

            return ResponseEntity.ok().build();
        } catch (EmailAlreadyExistException e) {
            return ResponseEntity
                    .status(400)
                    .body(new ValidationErrorResponse()
                            .addError(new Error("email", messageSource.getMessage("emailAlreadyInUse", null, locale)))
                    );
        }
    }

    @PostMapping("")
    public ResponseEntity<Object> singUp(@RequestBody @Valid SignUpRequest request, Locale locale) {
        try {
            var command = signUpCommandMapper.toSignUpCommand(request);
            var customer = signUpService.signUp(command);

            return ResponseEntity
                    .ok()
                    .body(new CustomerResource(customer));
        } catch (EmailAlreadyExistException e) {
            return ResponseEntity
                    .status(400)
                    .body(new ValidationErrorResponse()
                            .addError(new Error("email", messageSource.getMessage("emailAlreadyInUse", null, locale)))
                    );
        }
    }
}
