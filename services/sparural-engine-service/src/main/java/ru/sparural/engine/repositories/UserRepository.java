package ru.sparural.engine.repositories;

import ru.sparural.engine.api.dto.UserSearchFilterDto;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.api.dto.user.UserProfileDto;
import ru.sparural.engine.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> get(Long id);

    List<User> list(UserSearchFilterDto userSearchFilterDto);

    List<UserDto> getByIds(List<Long> userIds);

    boolean delete(Long id);

    Optional<User> saveOrUpdate(User user);

    Optional<User> updateIfNotNull(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findById(Long userId);

    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);

    Optional<UserProfileDto> getProfileInfo(Long userId);

    boolean checkIfUserExistsWithPhoneOrEmail(String login);

    boolean checkIfUserExistsWithPhoneOrEmail(String phone, String email);

    boolean checkIfUserExistsWithPhoneOrEmail(String phone, String email, Long id);

    List<Long> getAllId();

    Optional<Long> getCityIdByUserId(Long userId);

    Optional<User> findByUserPushToken(String token);

    void updateLastCityId(Long userId, Long cityId);

    void updateDeleteAt(Long userId);

    void updateLastActivity(Long userId);

    Optional<User> findByEmailOrPhone(String phone, String email);

    void deleteAsync(Long id);

    List<Long> extract(List<Long> users, String userGroup);

    List<UserDto> usersByFilter(UserFilterDto filter);

    Long usersCount(String role);

    Long usersCount(UserSearchFilterDto filter);
}
