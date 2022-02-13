package com.example.eshop.rest.controllers;

import com.example.eshop.customer.application.query.QueryCustomerService;
import com.example.eshop.customer.application.signup.SignUpService;
import com.example.eshop.customer.application.updatecustomer.UpdateCustomerCommand;
import com.example.eshop.customer.application.updatecustomer.UpdateCustomerService;
import com.example.eshop.customer.domain.customer.EmailAlreadyExistException;
import com.example.eshop.customer.domain.customer.PasswordPolicyException;
import com.example.eshop.rest.api.CustomerApi;
import com.example.eshop.rest.controllers.base.BaseController;
import com.example.eshop.rest.controllers.base.ValidationErrorBuilder;
import com.example.eshop.rest.dto.CustomerDto;
import com.example.eshop.rest.dto.CustomerFieldsDto;
import com.example.eshop.rest.dto.NewCustomerDto;
import com.example.eshop.rest.dto.ValidationErrorDto;
import com.example.eshop.rest.mappers.CustomerMapper;
import com.example.eshop.rest.utils.UriUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.Locale;

@RestController
@RequestMapping(UriUtils.API_BASE_PATH_PROPERTY)
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)  // for access to autowired fields from @ExceptionHandler
public class CustomerController extends BaseController implements CustomerApi {
    private final MessageSource messageSource;
    private final QueryCustomerService queryCustomerService;
    private final UpdateCustomerService updateCustomerService;
    private final SignUpService signUpService;
    private final CustomerMapper customerMapper;

    /**
     * @return validation error if email already in use by another customer
     */
    @ExceptionHandler(EmailAlreadyExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ValidationErrorDto handleEmailAlreadyExistException(Locale locale) {
        return ValidationErrorBuilder.newInstance()
                .addError("email", getMessageSource().getMessage("emailAlreadyInUse", null, locale))
                .build();
    }

    /**
     * @return validation error if password is not comply to policies
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ValidationErrorDto handlePasswordPolicyException(PasswordPolicyException e) {
        return ValidationErrorBuilder.newInstance()
                .addError("password", String.join(" ", e.getPolicyViolationMessages()))
                .build();
    }

    @Override
    public ResponseEntity<CustomerDto> createCustomer(NewCustomerDto newCustomerDto) {
        var command = customerMapper.toSignUpCommand(newCustomerDto);

        var customer = signUpService.signUp(command);

        return ResponseEntity.ok(customerMapper.toCustomerDto(customer));
    }

    @Override
    public ResponseEntity<CustomerDto> getAuthenticatedCustomer() {
        var userDetails = getCurrentAuthenticationOrFail();
        var customer = queryCustomerService.getByEmail(userDetails.getEmail());

        return ResponseEntity.ok(customerMapper.toCustomerDto(customer));
    }

    @Override
    public ResponseEntity<Void> updateAuthenticatedCustomer(CustomerFieldsDto customerFieldsDto) {
        var userDetails = getCurrentAuthenticationOrFail();
        var customer = queryCustomerService.getByEmail(userDetails.getEmail());

        var command = new UpdateCustomerCommand(
                customer.getId(),
                customerFieldsDto.getFirstname(),
                customerFieldsDto.getLastname(),
                customerFieldsDto.getEmail(),
                customerFieldsDto.getBirthday()
        );

        updateCustomerService.updateCustomer(command);

        return ResponseEntity.ok().build();
    }
}
