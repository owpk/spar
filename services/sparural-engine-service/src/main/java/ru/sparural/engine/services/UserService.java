package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.UserSearchFilterDto;
import ru.sparural.engine.api.dto.registration.UserProfileUpdateRequest;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.api.dto.user.UserProfileDto;
import ru.sparural.engine.entity.NotificationSetting;
import ru.sparural.engine.entity.Role;
import ru.sparural.engine.entity.User;
import ru.sparural.engine.entity.UserUnconfirmedEmail;
import ru.sparural.engine.entity.UserUnconfirmedPhone;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserInfo;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface UserService {

    UserDto createDtoFromUser(User user);

    User findByEmail(String email);

    User findByPhone(String phone);

    User findById(Long userId);

    boolean checkIfUserExistsWithPhoneOrEmail(String login);

    boolean checkIfUserExistsWithPhoneOrEmail(String phone, String email);

    Long usersCount(UserSearchFilterDto filter);

    List<UserDto> usersByFilter(UserFilterDto filter);

    List<UserDto> usersByIds(List<Long> ids);

    User findByUserId(Long id);

    User findByFirstNameAndLastName(String firstName, String lastName);

    User saveOrUpdate(User user);

    User createAnonymousWithPhone(String phone);

    void createUnconfirmedPhoneRecord(String phone, Long userId);

    void deleteRoleForUser(String roleName, Long userId);

    void addRoleForUser(String name, Long userId);

    void updatePassword(Long userId, String newPassword);

    User mapLoymaxInfoToUser(LoymaxUserInfo loymaxUserInfo);

    User createUserFromDto(UserDto user);

    User createFromLoymaxData(LoymaxUserInfo loymaxUserInfo, String phone, String encode, List<Role> byName);

    User createAnonymous();

    User confirmPhone(User user);

    User update(User user);

    User updateUserData(UserProfileUpdateRequest userRequest, User user);

    User updateUserNotificationsSettings(NotificationSetting settings, User user);

    UserProfileDto getUserProfileInfo(Long userId);

    List<User> list(UserSearchFilterDto filter);

    boolean delete(Long id);

    void deleteAsync(Long id);

    List<Role> getAllRoles();

    void createUnconfirmedEmailRecord(Long userId, String email);

    void removeAllUnconfirmedEmailRecordByUser(Long userId);

    UserUnconfirmedEmail getUnconfirmedEmailRecord(Long userId);

    void removeUnconfirmedRecordByEmail(String email);

    UserUnconfirmedPhone getUnconfirmedPhoneRecord(Long userId);

    UserUnconfirmedPhone getLastUnconfirmedPhoneRecord(Long userId);

    void removeUnconfirmedRecordByPhone(String phoneNumber);

    UserProfileDto getUserProfileInfoWithoutEx(Long userId);

    List<Long> getAllId();

    Long getCityIdByUserId(Long userId);

    User findByPushToken(String token);

    void updateLastCityId(Long userId, Long cityId);

    void updateLastActivity(Long userId);

    UserDto createDto(User user);

    boolean checkIfUserExistsWithPhoneOrEmail(String phoneNumber, String email, Long id);

    User findByEmailOrPhone(String phone, String email);

    List<Long> extract(List<Long> users, String userGroup);

    Long usersCount(String role);
}
