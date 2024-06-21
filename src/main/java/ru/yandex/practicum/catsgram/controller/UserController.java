package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        users.values().stream().filter(value -> value.getEmail().equals(user.getEmail())).forEach(value -> {
            throw new DuplicatedDataException("Этот имейл уже используется");
        });
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            users.values().stream().filter(value -> value.getEmail().equals(newUser.getEmail())).forEach(value -> {
                throw new DuplicatedDataException("Этот имейл уже используется");
            });
            User oldUser = users.get(newUser.getId());
            if (newUser.getUserName() != null) {
                oldUser.setUserName(newUser.getUserName());
            }
            if (newUser.getEmail() != null) {
                oldUser.setUserName(newUser.getEmail());
            }
            if (newUser.getPassword() != null) {
                oldUser.setUserName(newUser.getPassword());
            }
            return oldUser;
        } else {
            throw new NotFoundException("Пост с id = " + newUser.getId() + " не найден");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
