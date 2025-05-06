package ru.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.dto.UserRequest;
import ru.dto.UserResponse;
import ru.exception.ConflictException;
import ru.exception.NotFoundException;
import ru.mapper.UserMapper;
import ru.model.User;
import ru.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public UserResponse create(UserRequest dto) {
        existsByEmail(dto.getEmail());
        User user = userMapper.toEntity(dto);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse get(Long id) {
        User user = checkUserExists(id);
        return userMapper.toUserResponse(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserResponse update(Long id, UserRequest dto) {
        User user = checkUserExists(id);

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        if (!user.getEmail().equals(dto.getEmail())) {
            existsByEmail(dto.getEmail());
            user.setEmail(dto.getEmail());
        }

        user.setRole(dto.getRole());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public Long getUserIdByEmail(String email) {
        User user = getByEmail(email);
        return user.getId();
    }

    private void existsByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Пользователь с email=" + email + " уже существует");
        }
    }

    private User checkUserExists(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден или недоступен"));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь с email=" + email + " не найден"));
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new NotFoundException("Не удалось определить текущего пользователя");
        }
        String email = authentication.getName();
        return getByEmail(email);
    }
}
