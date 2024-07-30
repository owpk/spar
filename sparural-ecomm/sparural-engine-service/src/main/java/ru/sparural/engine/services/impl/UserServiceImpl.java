package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.api.dto.UserSearchFilterDto;
import ru.sparural.engine.api.dto.registration.UserProfileUpdateRequest;
import ru.sparural.engine.api.dto.user.RoleDto;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.api.dto.user.UserProfileDto;
import ru.sparural.engine.api.enums.UserGroups;
import ru.sparural.engine.entity.*;
import ru.sparural.engine.entity.enums.Genders;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserInfo;
import ru.sparural.engine.loymax.utils.LoymaxTimeToSparTimeAdapter;
import ru.sparural.engine.repositories.RolesRepository;
import ru.sparural.engine.repositories.UserRepository;
import ru.sparural.engine.repositories.UserUnconfirmedEmailRepository;
import ru.sparural.engine.repositories.impl.UserUnconfirmedPhonesRepository;
import ru.sparural.engine.services.UserGroupService;
import ru.sparural.engine.services.UserService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.StatusException;
import ru.sparural.engine.services.exception.UserNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.utils.ReflectUtils;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final UserUnconfirmedPhonesRepository userUnconfirmedPhonesRepository;
    private final UserUnconfirmedEmailRepository userUnconfirmedEmailRepository;
    private final DtoMapperUtils dtoMapperUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserGroupService userGroupService;

    //TODO: Create photo for user from documents
    @Override
    public UserDto createDtoFromUser(User user) {
        return dtoMapperUtils.convert(user, UserDto.class);
    }

    public User createUserFromDto(UserDto userDto) {
        return dtoMapperUtils.convert(userDto, User.class);
    }

    @Override
    public User findByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException(email));
    }

    @Override
    public User findByPhone(String phone) throws UserNotFoundException {
        return userRepository.findByPhone(phone).orElseThrow(
                () -> new UserNotFoundException(phone));
    }

    @Override
    public User findById(Long userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found"));
    }

    @Override
    public boolean checkIfUserExistsWithPhoneOrEmail(String login) {
        return userRepository.checkIfUserExistsWithPhoneOrEmail(login);
    }

    @Override
    public boolean checkIfUserExistsWithPhoneOrEmail(String phone, String email) {
        return userRepository.checkIfUserExistsWithPhoneOrEmail(phone, email);
    }

    @Override
    public boolean checkIfUserExistsWithPhoneOrEmail(String phone, String email, Long id) {
        return userRepository.checkIfUserExistsWithPhoneOrEmail(phone, email, id);
    }

    @Override
    public User findByEmailOrPhone(String phone, String email) {
        return userRepository.findByEmailOrPhone(phone, email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with login: " + phone + " / " + email));
    }

    @Override
    public List<Long> extract(List<Long> users, String userGroup) {
        return userRepository.extract(users, userGroup);
    }

    @Override
    public Long usersCount(String role) {
        return userRepository.usersCount(role);
    }

    @Override
    public List<UserIdLoymaxIdEntry> findUserIdsByLoymaxUserIds(List<Long> loymaxUserIds) {
        return userRepository.findUserIdsByLoymaxUserIds(loymaxUserIds);
    }

    @Override
    public Long usersCount(UserSearchFilterDto filter) {
        return userRepository.usersCount(filter);
    }

    @Override
    public List<UserDto> usersByFilter(UserFilterDto filter) {
        return userRepository.usersByFilter(filter);
    }

    @Override
    public User findByUserId(Long id) throws UserNotFoundException {
        return userRepository.get(id).orElseThrow(
                () -> new UserNotFoundException(id.toString()));
    }


    @Override
    public User findByFirstNameAndLastName(String firstName, String lastName) throws UserNotFoundException {
        return userRepository.findByFirstNameAndLastName(firstName, lastName)
                .orElseThrow(
                        () -> new UserNotFoundException(firstName + " " + lastName));
    }

    @Transactional
    @Override
    public User saveOrUpdate(User user) {
        log.info("USER_LOG: SAVING USER DATA: " + user);
        return userRepository.saveOrUpdate(user).orElseThrow(
                () -> new StatusException("cannot create user with phone: " + user + " - database exception"));
    }

    @Transactional
    @Override
    public User createAnonymousWithPhone(String phone) {
        var user = new User();
        user.setPhoneNumber(phone);
        user.setRoles(List.of(findAnonymousRole()));
        var anon = saveOrUpdate(user);
        userGroupService.addUserToGroupByCode(anon.getId(), UserGroups.ANON.getCode());
        return anon;
    }

    @Override
    public User createAnonymous() {
        var user = new User();
        user.setRoles(List.of(findAnonymousRole()));
        var anon = saveOrUpdate(user);
        userGroupService.addUserToGroupByCode(anon.getId(), UserGroups.ANON.getCode());
        return anon;
    }

    @Override
    public void createUnconfirmedPhoneRecord(String phone, Long userId) {
        userUnconfirmedPhonesRepository.saveOrUpdate(new UserUnconfirmedPhone(userId, phone))
                .orElseThrow(StatusException::new);
    }

    @Override
    public void createUnconfirmedEmailRecord(Long userId, String email) {
        var rec = new UserUnconfirmedEmail(userId, email);
        userUnconfirmedEmailRepository.createRecord(rec);
    }

    @Override
    public void removeAllUnconfirmedEmailRecordByUser(Long userId) {
        userUnconfirmedEmailRepository.removeByUser(userId);
    }

    @Override
    public UserUnconfirmedEmail getUnconfirmedEmailRecord(Long userId) {
        return userUnconfirmedEmailRepository.getByUserId(userId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public void removeUnconfirmedRecordByEmail(String email) {
        userUnconfirmedEmailRepository.removeByEmail(email);
    }

    @Override
    public UserUnconfirmedPhone getUnconfirmedPhoneRecord(Long userId) {
        return userUnconfirmedPhonesRepository.getByUserId(userId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public UserUnconfirmedPhone getLastUnconfirmedPhoneRecord(Long userId) {
        return userUnconfirmedPhonesRepository.getLastByUserId(userId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public void removeUnconfirmedRecordByPhone(String phoneNumber) {
        userUnconfirmedPhonesRepository.removeByPhone(phoneNumber);
    }

    @Override
    public void deleteRoleForUser(String roleName, Long userId) {
        var role = fetchRoleByName(roleName);
        rolesRepository.deleteRoleForUser(role, userId);
    }

    @Override
    public void addRoleForUser(String name, Long userId) {
        var role = fetchRoleByName(name);
        rolesRepository.addRoleForUser(role, userId);
    }

    @Override
    public void updatePassword(Long userId, String newPassword) {
        User u = findByUserId(userId);
        u.setPassword(bCryptPasswordEncoder.encode(newPassword));
        saveOrUpdate(u);
    }

    private Role fetchRoleByName(String name) {
        return rolesRepository.getByName(name).orElseThrow(
                () -> new ResourceNotFoundException("role not found" + name));
    }

    private Role findAnonymousRole() throws ResourceNotFoundException {
        return rolesRepository.getByName(RoleNames.ANONYMOUS.getName()).orElseThrow(() ->
                new ResourceNotFoundException("unexpected exception: role anonymous not found", 500));
    }

    public User update(User user) throws UserNotFoundException {
        log.info("USER_LOG: UPDATING USER DATA: " + user);
        return userRepository.updateIfNotNull(user)
                .orElseThrow(() -> new UserNotFoundException("Can not update user"));
    }

    public boolean delete(Long id) {
        return userRepository.delete(id);
    }

    @Override
    public void deleteAsync(Long id) {
        userRepository.deleteAsync(id);
    }

    @Override
    public List<Role> getAllRoles() {
        return rolesRepository.getAll();
    }

    public User confirmPhone(User user) throws ResourceNotFoundException {
        UserUnconfirmedPhone entity = userUnconfirmedPhonesRepository.getByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "missed the first step of registration, try to send confirm phone again"));
        user.setPhoneNumber(entity.getPhoneNumber());
        userUnconfirmedPhonesRepository.deleteByUserId(user.getId());
        return update(user);
    }

    public User updateUserData(UserProfileUpdateRequest userRequest, User user) {
        log.info("USER_LOG: UPDATING USER DATA: data: {}, request {}", user, userRequest);
        ReflectUtils.updateNotNullFields(userRequest, user);
        userRepository.updateIfNotNull(user)
                .orElseThrow(StatusException::new);
        return user;
    }

    @Override
    public User updateUserNotificationsSettings(NotificationSetting settings, User user) {
        ReflectUtils.updateNotNullFields(settings, user);
        userRepository.saveOrUpdate(user)
                .orElseThrow(StatusException::new);
        return user;
    }

    @Override
    public User createFromLoymaxData(LoymaxUserInfo loymaxUserInfo, String phoneNumber,
                                     String hashedPassword, List<Role> roles) {
        log.info("USER_LOG: CREATING FROM LOYMAX DATA: " + loymaxUserInfo);
        User user = mapLoymaxInfoToUser(loymaxUserInfo);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(hashedPassword);
        user.setRoles(roles);
        return user;
    }

    @Override
    public User createFromLoymaxDataAndSetClient(LoymaxUserInfo loymaxUserInfo, String phoneNumber,
                                                 String hashedPassword) {
        log.info("USER_LOG: CREATING FROM LOYMAX DATA: " + loymaxUserInfo);
        User user = mapLoymaxInfoToUser(loymaxUserInfo);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(hashedPassword);
        user.setRoles(List.of(rolesRepository.getByName(RoleNames.CLIENT.getName()).orElseThrow()));
        return user;
    }

    @Override
    public User createFromLoymaxDataWithRoleNames(LoymaxUserInfo loymaxUserInfo, String phoneNumber,
                                                  String hashedPassword, List<String> roles) {
        log.info("USER_LOG: CREATING FROM LOYMAX DATA: " + loymaxUserInfo);
        User user = mapLoymaxInfoToUser(loymaxUserInfo);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(hashedPassword);
        user.setRoles(rolesRepository.getListByNames(roles));
        return user;
    }

    public User mapLoymaxInfoToUser(LoymaxUserInfo loymaxUserInfo) {
        User user = new User();
        if (loymaxUserInfo.getBirthDay() != null)
            user.setBirthday(LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(loymaxUserInfo.getBirthDay()));

        user.setFirstName(loymaxUserInfo.getFirstName());
        user.setLastName(loymaxUserInfo.getLastName());
        user.setPatronymicName(loymaxUserInfo.getPatronymicName());

        if (loymaxUserInfo.getRejectPaperChecks() != null) {
            user.setRejectPaperChecks(loymaxUserInfo.getRejectPaperChecks());
        }

        if (loymaxUserInfo.getGender() != null)
            user.setGender(Genders.valueOf(loymaxUserInfo.getGender()));

        user.setEmail(loymaxUserInfo.getEmail());
        return user;
    }

    public UserProfileDto getUserProfileInfo(Long userId) {
        return userRepository.getProfileInfo(userId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public UserProfileDto getUserProfileInfoWithoutEx(Long userId) {
        return userRepository.getProfileInfo(userId)
                .orElse(null);
    }

    @Override
    public List<Long> getAllId() {
        return userRepository.getAllId();
    }

    @Override
    public Long getCityIdByUserId(Long userId) {
        return userRepository.getCityIdByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("City not notified for this user"));
    }

    @Override
    public User findByPushToken(String token) {
        return userRepository.findByUserPushToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("User with given token: " + token + "not found"));
    }

    @Override
    public void updateLastCityId(Long userId, Long cityId) {
        userRepository.updateLastCityId(userId, cityId);
    }

    @Override
    public void updateLastActivity(Long userId) {
        userRepository.updateLastActivity(userId);
    }

    @Override
    public UserDto createDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPassword(user.getPassword());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setIsDraft(user.getDraft());
        if (user.getRoles() != null)
            userDto.setRoles(dtoMapperUtils.convertList(RoleDto.class, user.getRoles()));
        userDto.setPatronymicName(user.getPatronymicName());
        if (user.getGender() != null)
            userDto.setGender(user.getGender().getVal());
        userDto.setBirthday(user.getBirthday());
        userDto.setSmsAllowed(user.getSmsAllowed());
        userDto.setEmailAllowed(user.getEmailAllowed());
        userDto.setDraft(user.getDraft());
        userDto.setViberAllowed(user.getViberAllowed());
        userDto.setWhatsappAllowed(user.getWhatsappAllowed());
        userDto.setPushAllowed(user.getPushAllowed());
        userDto.setRejectPaperChecks(user.getRejectPaperChecks());
        return userDto;
    }

    @Override
    public List<User> list(UserSearchFilterDto filter) {
        return userRepository.list(filter);
    }

    @Override
    public List<UserDto> usersByIds(List<Long> ids) {
        return userRepository.getByIds(ids);
    }
}