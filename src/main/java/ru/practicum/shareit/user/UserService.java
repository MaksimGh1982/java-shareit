package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<UserDto> findAll() {
        log.info("Список пользователей");
        return userStorage.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto findUserById(long id) {
        log.info("Пользователь id = {}", id);
        return UserMapper.toUserDto(userStorage.findUserById(id));
    }

    public UserDto create(@Valid UserDto userDto) {
        log.info("Создать пользователя  {}", userDto.getName());
        validate(userDto);
        return UserMapper.toUserDto(userStorage.create(UserMapper.toUser(userDto)));
    }

    public UserDto update(UserDto newUserDto, long userId) {
        if (newUserDto.getEmail() != null) {
            validate(newUserDto);
        }

        log.info("Обновить пользователя id = {}", userId);
        UserDto oldUserDto = findUserById(userId);
        if (oldUserDto != null) {
            newUserDto.setId(userId);
            return UserMapper.toUserDto(userStorage.update(UserMapper.toUser(newUserDto)));
        } else {
            throw new NotFoundException(MessageFormat.format("Пользователь с id = {0} не найден", userId));
        }
    }

    public void validate(UserDto userDto) {
        long cnt = findAll()
                .stream()
                .filter(u -> u.getEmail().equals(userDto.getEmail()))
                .count();
        if (cnt > 0) {
            throw new RuntimeException("Не допустимы два пользователья с одинаковыми email");
        }
    }

    public void deleteUser(long id) {
        log.info("Удалить пользователя id = {}", id);
        userStorage.deleteUser(id);
    }
}
