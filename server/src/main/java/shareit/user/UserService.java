package shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shareit.exception.NotFoundException;
import shareit.user.dto.UserDto;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Collection<UserDto> findAll() {
        log.info("Список пользователей");
        return repository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto findUserById(long id) {
        log.info("Пользователь id = {}", id);
        User user = repository.findById(id).orElse(null);
        if (user == null) {
            throw new NotFoundException(MessageFormat.format("Пользователь с id = {0} не найден", id));
        }
        return UserMapper.toUserDto(repository.findById(id).orElse(null));
    }

    public UserDto create(UserDto userDto) {
        log.info("Создать пользователя  {}", userDto.getName());
        validate(userDto);
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(userDto)));
    }

    public UserDto update(UserDto newUserDto, long userId) {
        if (newUserDto.getEmail() != null) {
            validate(newUserDto);
        }

        log.info("Обновить пользователя id = {}", userId);
        User oldUser = repository.findById(userId).orElse(null);
        if (oldUser != null) {
            if (newUserDto.getName() != null) {
                oldUser.setName(newUserDto.getName());
            }
            if (newUserDto.getEmail() != null) {
                oldUser.setEmail(newUserDto.getEmail());
            }
            return UserMapper.toUserDto(repository.save(oldUser));
        } else {
            throw new NotFoundException(MessageFormat.format("Пользователь с id = {0} не найден", userId));
        }
    }

    public void validate(UserDto userDto) {

        if (repository.сountEmail(userDto.getEmail(), userDto.getId() == null ? 0 : userDto.getId()) > 0) {
            throw new RuntimeException("Не допустимы два пользователья с одинаковыми email");
        }
    }

    public void deleteUser(long id) {
        log.info("Удалить пользователя id = {}", id);
        repository.deleteById(id);
    }
}
