package ru.service;

import ru.dto.UserResponse;
import ru.dto.UserRequest;

public interface UserService {

    UserResponse create (UserRequest dto);
    UserResponse get (Long id);
    void delete (Long id);
    UserResponse update (Long id, UserRequest dto);
    Long getUserIdByEmail(String email);
}
