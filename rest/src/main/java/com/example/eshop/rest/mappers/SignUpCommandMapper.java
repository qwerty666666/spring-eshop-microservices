package com.example.eshop.rest.mappers;

import com.example.eshop.customer.application.signup.SignUpCommand;
import com.example.eshop.rest.requests.SignUpRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SignUpCommandMapper {
    default SignUpCommand toSignUpCommand(SignUpRequest signUpRequest) {
        // can't use MapStruct for java records until JDK-17
        // https://bugs.openjdk.java.net/browse/JDK-8258535
        return new SignUpCommand(
                signUpRequest.firstname(),
                signUpRequest.lastname(),
                signUpRequest.email(),
                signUpRequest.birthday(),
                signUpRequest.password()
        );
    }
}
