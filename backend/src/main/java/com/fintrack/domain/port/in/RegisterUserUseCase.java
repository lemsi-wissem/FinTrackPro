package com.fintrack.domain.port.in;

import com.fintrack.domain.model.User;

public interface RegisterUserUseCase {

    User register(RegisterUserCommand command);

    record RegisterUserCommand(String email, String password, String firstName, String lastName) {}
}
