package ru.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.model.Role;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    String firstName;
    String lastName;
    String email;
    String password;
    Role role;
}