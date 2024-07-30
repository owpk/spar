package ru.sparural.engine.repositories.impl;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sparural.engine.api.dto.UserSearchFilterDto;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.api.dto.user.UserProfileDto;
import ru.sparural.engine.api.enums.UserFilterRegistrationTypes;
import ru.sparural.engine.entity.Role;
import ru.sparural.engine.entity.User;
import ru.sparural.engine.entity.UserIdLoymaxIdEntry;
import ru.sparural.engine.entity.enums.Genders;
import ru.sparural.engine.repositories.UserRepository;
import ru.sparural.engine.repositories.impl.tools.ConditionBuilder;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.*;
import ru.sparural.tables.records.UsersRecord;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.sparural.engine.repositories.impl.tools.SearchOperators.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private static final String LIMIT_USER_TABLE = "limited_users_tbl";
    private final CountersValues countersValuesTable = CountersValues.COUNTERS_VALUES;
    private final Accounts accountsTable = Accounts.ACCOUNTS;
    private final DSLContext dslContext;

    public Optional<User> findWhere(Condition condition) {
        var rec = dslContext.select()
                .from(Users.USERS)
                .leftJoin(RoleUser.ROLE_USER)
                .on(Users.USERS.ID.eq(RoleUser.ROLE_USER.USER_ID))
                .leftJoin(Roles.ROLES)
                .on(RoleUser.ROLE_USER.ROLE_ID.eq(Roles.ROLES.ID))
                .where(condition)
                .orderBy(Users.USERS.ID.desc())
                .fetch();

        return rec.intoGroups(Users.USERS.fields())
                .values()
                .stream()
                .map(this::mapRecordToUser)
                .findFirst();
    }

    @Override
    public Optional<User> findByFirstNameAndLastName(String firstName, String lastName) {
        return findWhere(Users.USERS.PHONE_NUMBER.eq(firstName).and(Users.USERS.LAST_NAME.eq(lastName)));
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return findWhere(Users.USERS.PHONE_NUMBER.eq(phone));
    }

    @Override
    public Optional<User> findById(Long userId) {
        return findWhere(Users.USERS.ID.eq(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findWhere(Users.USERS.EMAIL.eq(email));
    }

    @Override
    public Optional<User> get(Long id) {
        return findWhere(Users.USERS.ID.eq(id));
    }

    private SelectConditionStep<Record> buildWithFilters(
            SelectConditionStep<Record> conditionStep, UserSearchFilterDto filter) {

        var conditionBuilder = new ConditionBuilder(conditionStep);

        Function<Integer, Long> calcAgeSearch = x -> x != null ?
                TimeHelper.currentTime() - TimeUnit.DAYS.toSeconds(365) * x : null;

        var searchMinAge = calcAgeSearch.apply(filter.getMinAge());
        var searchMaxAge = calcAgeSearch.apply(filter.getMaxAge());

        var minRegistrationDate = filter.getMinRegistrationDate();
        if (minRegistrationDate != null)
            minRegistrationDate = minRegistrationDate / 1000;

        var maxRegistrationDate = filter.getMaxRegistrationDate();
        if (maxRegistrationDate != null)
            maxRegistrationDate = maxRegistrationDate / 1000;

        if (maxRegistrationDate != null &&
                minRegistrationDate != null &&
                maxRegistrationDate - minRegistrationDate <= 86399)
            maxRegistrationDate += 86399;

        var res = conditionBuilder
                .addCondition(Users.USERS.FIRST_NAME.getName(), filter.getSearch(), LIKE)
                .addCondition(Users.USERS.LAST_NAME.getName(), filter.getSearch(), LIKE)
                .addCondition(Users.USERS.PATRONYMIC_NAME.getName(), filter.getSearch(), LIKE)
                .addCondition(Users.USERS.BIRTHDAY.getName(), searchMinAge, MAX)
                .addCondition(Users.USERS.BIRTHDAY.getName(), searchMaxAge, MIN)
                .addCondition(Users.USERS.CREATED_AT.getName(), minRegistrationDate, MIN, "users")
                .addCondition(Users.USERS.CREATED_AT.getName(), maxRegistrationDate, MAX, "users")
                .buildCondition();

        var group = filter.getGroup();
        if (Objects.nonNull(group)) {
            var usersInUserGroup = dslContext.select(UsersGroupUser.USERS_GROUP_USER.USER_ID)
                    .from(UsersGroupUser.USERS_GROUP_USER)
                    .where(UsersGroupUser.USERS_GROUP_USER.USERS_GROUP_ID.eq(group));
            res = res.and(Users.USERS.ID.in(usersInUserGroup));
        }

        var noGroup = filter.getNotinGroup();
        if (Objects.nonNull(noGroup)) {
            var usersInUserGroupNo = dslContext.select(UsersGroupUser.USERS_GROUP_USER.USER_ID)
                    .from(UsersGroupUser.USERS_GROUP_USER)
                    .where(UsersGroupUser.USERS_GROUP_USER.USERS_GROUP_ID.eq(noGroup));
            res = res.and(Users.USERS.ID.notIn(usersInUserGroupNo));
        }

        var role = filter.getRole();
        if (Objects.nonNull(role) && !role.isEmpty()) {
            var usersWithRoles = dslContext.select(RoleUser.ROLE_USER.USER_ID)
                    .from(RoleUser.ROLE_USER)
                    .where(RoleUser.ROLE_USER.ROLE_ID.in(role));
            res = res.and(Users.USERS.ID.in(usersWithRoles));
        }

        var neRole = filter.getRole_ne();
        if (Objects.nonNull(neRole) && !neRole.isEmpty()) {
            var usersWithoutRole = dslContext.select(RoleUser.ROLE_USER.USER_ID)
                    .from(RoleUser.ROLE_USER
                            .leftJoin(Roles.ROLES)
                            .on(Roles.ROLES.ID.eq(RoleUser.ROLE_USER.ROLE_ID)))
                    .where(Roles.ROLES.ID.in(neRole));
            res = res.and(Users.USERS.ID.notIn(usersWithoutRole));
        }

        var gender = filter.getGender();
        if (Objects.nonNull(gender)) {
            var genderEnum = Genders.other;
            if (gender.equals(Genders.female.getVal())) genderEnum = Genders.female;
            if (gender.equals("male")) genderEnum = Genders.male;
            res = res.and(Users.USERS.GENDER.eq(genderEnum.getVal()));
        }

        var attributeId = filter.getAttributeId();
        if (Objects.nonNull(attributeId)) {
            var userAttrTable = UsersAttributeUser.USERS_ATTRIBUTE_USER;
            var usersWithAttribute = dslContext.select(userAttrTable.USER_ID)
                    .from(userAttrTable)
                    .leftJoin(UsersAttributes.USERS_ATTRIBUTES)
                    .on(UsersAttributes.USERS_ATTRIBUTES.ID.eq(userAttrTable.USER_ATTRIBUTE_ID))
                    .where(UsersAttributes.USERS_ATTRIBUTES.ID.eq(attributeId));
            res = res.and(Users.USERS.ID.in(usersWithAttribute));
        }

        var hasEmail = filter.getHasEmail();
        if (Objects.nonNull(hasEmail))
            res = res.and(Users.USERS.EMAIL.isNotNull());

        var counterId = filter.getCounterId();
        if (Objects.nonNull(counterId)) {
            var usersWithCounterId = DSL.select(countersValuesTable.USER_ID)
                    .from(countersValuesTable)
                    .where(countersValuesTable.COUNTER_ID.eq(counterId));
            res = res.and(Users.USERS.ID.in(usersWithCounterId));
        }

        var counterMin = filter.getCounterMin();
        if (Objects.nonNull(counterId) &&
                Objects.nonNull(counterMin)) {
            var targetUsers = DSL.select(countersValuesTable.USER_ID)
                    .from(countersValuesTable)
                    .where(countersValuesTable.VALUE.greaterOrEqual(Math.toIntExact(counterMin)));
            res = res.and(Users.USERS.ID.in(targetUsers));
        }

        var counterMax = filter.getCounterMax();
        if (Objects.nonNull(counterId) &&
                Objects.nonNull(counterMax)) {
            var targetUsers = DSL.select(countersValuesTable.USER_ID)
                    .from(countersValuesTable)
                    .where(countersValuesTable.VALUE.lessThan(Math.toIntExact(counterMax)));
            res = res.and(Users.USERS.ID.in(targetUsers));
        }

        var currencyId = filter.getCurrencyId();
        if (Objects.nonNull(currencyId)) {
            var usersWithAccounts = DSL.select(accountsTable.USER_ID)
                    .from(accountsTable)
                    .where(accountsTable.CURRENCY_ID.eq(currencyId));
            res = res.and(Users.USERS.ID.in(usersWithAccounts));
        }

        var currencyMin = filter.getCurrencyMin();
        if (Objects.nonNull(currencyId) &&
                Objects.nonNull(currencyMin)) {
            var usersWithAccounts = DSL.select(accountsTable.USER_ID)
                    .from(accountsTable)
                    .where(accountsTable.CURRENCY_ID.eq(currencyId)
                            .and(accountsTable.AMOUNT.greaterThan(Double.valueOf(currencyMin))));
            res = res.and(Users.USERS.ID.in(usersWithAccounts));
        }

        var currencyMax = filter.getCurrencyMax();
        if (Objects.nonNull(currencyId) &&
                Objects.nonNull(currencyMax)) {
            var usersWithAccounts = DSL.select(accountsTable.USER_ID)
                    .from(accountsTable)
                    .where(accountsTable.CURRENCY_ID.eq(currencyId)
                            .and(accountsTable.AMOUNT.lessThan(Double.valueOf(currencyMax))));
            res = res.and(Users.USERS.ID.in(usersWithAccounts));
        }

        var statusId = filter.getStatusId();
        if (Objects.nonNull(statusId)) {
            var csu = ClientStatusUser.CLIENT_STATUS_USER;
            var usersWithStatus = DSL.select(csu.USER_ID)
                    .from(csu)
                    .where(csu.CLIENT_STATUS_ID.eq(statusId));
            res = res.and(Users.USERS.ID.in(usersWithStatus));
        }

        return res;
    }

    @Override
    public List<User> list(UserSearchFilterDto filter) {
        var selectOnWhere = dslContext
                .select()
                .from(Users.USERS)
                .leftJoin(RoleUser.ROLE_USER).on(Users.USERS.ID.eq(RoleUser.ROLE_USER.USER_ID))
                .leftJoin(Roles.ROLES).on(Roles.ROLES.ID.eq(RoleUser.ROLE_USER.ROLE_ID))
                .where();
        return buildWithFilters(selectOnWhere, filter)
                .orderBy(getSortFieldFromFilter(filter))
                .offset(filter.getOffset())
                .limit(filter.getLimit())
                .fetch()
                .intoGroups(Users.USERS.ID)
                .values()
                .stream().map(this::mapRecordToUser)
                .collect(Collectors.toList());
    }

    private List<SortField<?>> getSortFieldFromFilter(UserSearchFilterDto filter) {
        if ("desc".equalsIgnoreCase(filter.getAlphabetSort())) {
            return List.of(
                    Users.USERS.LAST_NAME.desc(),
                    Users.USERS.FIRST_NAME.desc(),
                    Users.USERS.PATRONYMIC_NAME.desc());
        }

        return List.of(
                Users.USERS.LAST_NAME.asc(),
                Users.USERS.FIRST_NAME.asc(),
                Users.USERS.PATRONYMIC_NAME.asc());
    }

    @Transactional
    @Override
    public boolean delete(Long id) {
        return dslContext.deleteFrom(Users.USERS)
                .where(Users.USERS.ID.eq(id))
                .execute() == 1;
    }

    @Transactional
    @Override
    public Optional<User> saveOrUpdate(User user) {
        var table = Users.USERS;
        var insertStep = dslContext.insertInto(table)
                .set(table.FIRST_NAME, user.getFirstName())
                .set(table.PASSWORD, user.getPassword())
                .set(table.LAST_NAME, user.getLastName())
                .set(table.PATRONYMIC_NAME, user.getPatronymicName())
                .set(table.EMAIL, user.getEmail())
                .set(table.PHONE_NUMBER, user.getPhoneNumber())
                .set(table.GENDER, user.getGender() != null ?
                        user.getGender().getVal() : Genders.other.getVal())
                .set(table.BIRTHDAY, user.getBirthday())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .set(table.REJECT_PAPER_CHECKS, user.getRejectPaperChecks())
                .set(table.WHATSAPP_ALLOWED, true)
                .set(table.VIBER_ALLOWED, true)
                .set(table.EMAIL_ALLOWED, true)
                .set(table.SMS_ALLOWED, true)
                .set(table.PUSH_ALLOWED, true);

        InsertOnDuplicateSetStep<UsersRecord> onConflictClause = insertStep.onConflict(Users.USERS.PHONE_NUMBER).doUpdate();

        var record = onConflictClause
                .set(table.FIRST_NAME, user.getFirstName())
                .set(table.LAST_NAME, user.getLastName())
                .set(table.PASSWORD, user.getPassword())
                .set(table.PATRONYMIC_NAME, user.getPatronymicName())
                .set(table.GENDER, user.getGender() != null ?
                        user.getGender().getVal() : Genders.other.getVal())
                .set(table.BIRTHDAY, user.getBirthday())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .set(table.REJECT_PAPER_CHECKS, user.getRejectPaperChecks())
                .set(table.WHATSAPP_ALLOWED, user.getWhatsappAllowed())
                .set(table.VIBER_ALLOWED, user.getViberAllowed())
                .set(table.EMAIL_ALLOWED, user.getEmailAllowed())
                .set(table.SMS_ALLOWED, user.getSmsAllowed())
                .set(table.PUSH_ALLOWED, user.getPushAllowed()).returningResult()
                .fetchOne();


        if (record != null) {
            Long id = record.getValue(Users.USERS.ID);
            List<Role> roleList = user.getRoles();

            if (roleList != null)
                roleList.forEach(role ->
                        dslContext.insertInto(RoleUser.ROLE_USER)
                                .set(RoleUser.ROLE_USER.USER_ID, id)
                                .set(RoleUser.ROLE_USER.ROLE_ID, role.getId())
                                .onConflict(RoleUser.ROLE_USER.USER_ID, RoleUser.ROLE_USER.ROLE_ID)
                                .doNothing().execute());
            user.setId(id);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> updateIfNotNull(User user) {
        List<Role> roleList = user.getRoles();
        if (roleList != null && !roleList.isEmpty() && user.getId() != null) {
            int deleted = dslContext.delete(RoleUser.ROLE_USER)
                    .where(RoleUser.ROLE_USER.USER_ID.eq(user.getId()))
                    .execute();
            log.trace("USER_LOG: User update request. Deleted user roles records: " + deleted);
            roleList.forEach(role ->
                    dslContext.insertInto(RoleUser.ROLE_USER)
                            .set(RoleUser.ROLE_USER.USER_ID, user.getId())
                            .set(RoleUser.ROLE_USER.ROLE_ID, role.getId())
                            .onConflict(RoleUser.ROLE_USER.USER_ID, RoleUser.ROLE_USER.ROLE_ID)
                            .doNothing().execute());
        }

        UpdateSetStep<UsersRecord> res = dslContext.update(Users.USERS);

        if (user.getFirstName() != null)
            res = res.set(Users.USERS.FIRST_NAME, user.getFirstName());
        if (user.getLastName() != null)
            res = res.set(Users.USERS.LAST_NAME, user.getLastName());
        if (user.getEmail() != null)
            res = res.set(Users.USERS.EMAIL, user.getEmail());
        if (user.getPhoneNumber() != null)
            res = res.set(Users.USERS.PHONE_NUMBER, user.getPhoneNumber());
        if (user.getPatronymicName() != null)
            res = res.set(Users.USERS.PATRONYMIC_NAME, user.getPatronymicName());
        if (user.getGender() != null)
            res = res.set(Users.USERS.GENDER, user.getGender().getVal());
        if (user.getBirthday() != null)
            res = res.set(Users.USERS.BIRTHDAY, user.getBirthday());
        if (user.getRejectPaperChecks() != null)
            res = res.set(Users.USERS.REJECT_PAPER_CHECKS, user.getRejectPaperChecks());

        return res.set(Users.USERS.UPDATED_AT, TimeHelper.currentTime())
                .where(Users.USERS.ID.eq(user.getId()))
                .returningResult()
                .fetchOptionalInto(User.class);
    }

    @Override
    public Optional<UserProfileDto> getProfileInfo(Long userId) {
        var rec = dslContext.select()
                .from(Users.USERS)
                .leftJoin(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES)
                .on(Users.USERS.ID.eq(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USER_ID))
                .leftJoin(UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS)
                .on(Users.USERS.ID.eq(UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS.USER_ID))
                .where(Users.USERS.ID.eq(userId)).fetch();
        return rec
                .intoGroups(Users.USERS.fields())
                .values()
                .stream()
                .map(r -> r.into(Users.USERS.fields())
                        .into(UserProfileDto.class).get(0)).findFirst();
    }

    @Override
    public boolean checkIfUserExistsWithPhoneOrEmail(String login) {
        return dslContext.selectFrom(Users.USERS)
                .where(Users.USERS.PHONE_NUMBER.eq(login)).or(Users.USERS.EMAIL.eq(login))
                .fetchOptional().isPresent();
    }

    @Override
    public boolean checkIfUserExistsWithPhoneOrEmail(String phone, String email) {
        return dslContext.selectFrom(Users.USERS)
                .where(Users.USERS.PHONE_NUMBER.eq(phone)).or(Users.USERS.EMAIL.eq(email))
                .fetchOptional().isPresent();
    }

    @Override
    public boolean checkIfUserExistsWithPhoneOrEmail(String phone, String email, Long id) {
        return dslContext.selectFrom(Users.USERS)
                .where((Users.USERS.PHONE_NUMBER.eq(phone)).or(Users.USERS.EMAIL.eq(email))
                        .and(Users.USERS.ID.notEqual(id)))
                .fetchOptional().isPresent();
    }

    @Override
    public List<Long> getAllId() {
        return dslContext.select(Users.USERS.ID).from(Users.USERS).fetchInto(Long.class);
    }

    @Override
    public Optional<Long> getCityIdByUserId(Long userId) {
        return dslContext.select(Users.USERS.LAST_CITY_ID)
                .from(Users.USERS)
                .where(Users.USERS.ID.eq(userId))
                .fetchOptionalInto(Long.class);
    }

    @Override
    public Optional<User> findByUserPushToken(String token) {
        return dslContext.select()
                .from(Users.USERS)
                .leftJoin(UserPushTokens.USER_PUSH_TOKENS)
                .on(Users.USERS.ID.eq(UserPushTokens.USER_PUSH_TOKENS.USER_ID))
                .where(UserPushTokens.USER_PUSH_TOKENS.TOKEN.eq(token))
                .fetchOptionalInto(User.class);
    }

    @Override
    public void updateLastCityId(Long userId, Long cityId) {
        dslContext
                .update(Users.USERS)
                .set(Users.USERS.LAST_CITY_ID, cityId)
                .set(Users.USERS.UPDATED_AT, TimeHelper.currentTime())
                .where(Users.USERS.ID.eq(userId))
                .execute();
    }

    @Override
    public void updateDeleteAt(Long userId) {
        dslContext
                .update(Users.USERS)
                .set(Users.USERS.DELETED_AT, TimeHelper.currentTime())
                .where(Users.USERS.ID.eq(userId))
                .execute();
    }

    @Override
    public void updateLastActivity(Long userId) {
        dslContext
                .update(Users.USERS)
                .set(Users.USERS.LAST_ACTIVITY, TimeHelper.currentTime())
                .where(Users.USERS.ID.eq(userId))
                .execute();
    }

    @Override
    public Optional<User> findByEmailOrPhone(String phone, String email) {
        return findWhere(Users.USERS.EMAIL.eq(email).or(Users.USERS.PHONE_NUMBER.eq(phone)));
    }

    @Override
    @Transactional
    public void deleteAsync(Long id) {
        dslContext.deleteFrom(Users.USERS)
                .where(Users.USERS.ID.eq(id))
                .executeAsync();
    }

    @Override
    public List<Long> extract(List<Long> users, String userGroup) {
        Condition condition = null;
        if (users != null)
            condition = Users.USERS.ID.in(users);
        if (userGroup != null)
            if (condition != null)
                condition = condition.and(UsersGroups.USERS_GROUPS.NAME.eq(userGroup));
            else
                condition = UsersGroups.USERS_GROUPS.NAME.eq(userGroup);
        return dslContext.select()
                .from(Users.USERS)
                .leftJoin(UsersGroupUser.USERS_GROUP_USER)
                .on(Users.USERS.ID.eq(UsersGroupUser.USERS_GROUP_USER.USER_ID))
                .leftJoin(UsersGroups.USERS_GROUPS)
                .on(UsersGroupUser.USERS_GROUP_USER.USERS_GROUP_ID.eq(UsersGroups.USERS_GROUPS.ID))
                .where(condition)
                .fetchInto(Long.class);
    }

    @Override
    public List<UserDto> usersByFilter(UserFilterDto filter) {
        var query = dslContext.select(Users.USERS.fields())
                .from(Users.USERS);

        List<Condition> conditions = new LinkedList<>();
        if (filter != null) {
            if (!CollectionUtils.isEmpty(filter.getUserIds())) {
                conditions.add(Users.USERS.ID.in(filter.getUserIds()));
            }

            if (!CollectionUtils.isEmpty(filter.getGroupIds())) {
                query = query.leftJoin(UsersGroupUser.USERS_GROUP_USER)
                        .on(Users.USERS.ID.eq(UsersGroupUser.USERS_GROUP_USER.USER_ID));
                conditions.add(UsersGroupUser.USERS_GROUP_USER.USERS_GROUP_ID.in(filter.getGroupIds()));
            }

            if (UserFilterRegistrationTypes.isNeedFilter(filter.getRegistrationType())) {
                query = query.leftJoin(Registrations.REGISTRATIONS)
                        .on(Users.USERS.ID.eq(Registrations.REGISTRATIONS.USER_ID));

                switch (filter.getRegistrationType()) {
                    case NO_REGISTRED:
                        conditions.add(Registrations.REGISTRATIONS.STEP.lessThan(5));
                        break;
                    case REGISTRED:
                        conditions.add(Registrations.REGISTRATIONS.STEP.greaterOrEqual(5));
                        break;
                }
            }
        }
        return query
                .where(conditions)
                .fetchInto(UserDto.class);
    }

    @Override
    public Long usersCount(String role) {
        var field = DSL.field("count(*)", SQLDataType.BIGINT);
        var whereCondition = DSL.noCondition();
        if (role != null)
            whereCondition = whereCondition.and(Roles.ROLES.CODE.eq(role));
        return dslContext.select(field)
                .from(Users.USERS)
                .leftJoin(RoleUser.ROLE_USER).on(Users.USERS.ID.eq(RoleUser.ROLE_USER.USER_ID))
                .leftJoin(Roles.ROLES).on(Roles.ROLES.ID.eq(RoleUser.ROLE_USER.ROLE_ID))
                .where(whereCondition)
                .fetchOneInto(Long.class);
    }

    @Override
    public Long usersCount(UserSearchFilterDto filter) {
        var selectStep = dslContext.select()
                .from(Users.USERS)
                .where();
        return (long) buildWithFilters(selectStep, filter)
                .fetch().size();
    }

    @Override
    public List<UserIdLoymaxIdEntry> findUserIdsByLoymaxUserIds(List<Long> loymaxUserIds) {
        return dslContext.select(Users.USERS.ID.as("userId"), LoymaxUsers.LOYMAX_USERS.LOYMAX_USER_ID.as("loymaxUserId"))
                .from(Users.USERS).leftJoin(LoymaxUsers.LOYMAX_USERS)
                .on(LoymaxUsers.LOYMAX_USERS.USER_ID.eq(Users.USERS.ID))
                .where(LoymaxUsers.LOYMAX_USERS.LOYMAX_USER_ID.in(loymaxUserIds))
                .fetchInto(UserIdLoymaxIdEntry.class);
    }

    private User mapRecordToUserAndAddRoles(Result<Record> r) {
        var user = mapRecordToUserWithoutRoles(r);
        var roles = dslContext.select()
                .from(Roles.ROLES.leftJoin(RoleUser.ROLE_USER)
                        .on(RoleUser.ROLE_USER.ROLE_ID.eq(Roles.ROLES.ID)))
                .where(RoleUser.ROLE_USER.USER_ID.eq(user.getId()))
                .fetchInto(Role.class);
        user.setRoles(roles);
        return user;
    }

    private User mapRecordToUser(Result<Record> result) {
        var user = mapRecordToUserWithoutRoles(result);
        var roles = result.into(Roles.ROLES.fields()).into(Role.class);
        user.setRoles(roles);
        return user;
    }

    private User mapRecordToUserWithoutRoles(Result<Record> r) {
        var rec = r.get(0);
        User user = new User();
        user.setPassword(rec.get(Users.USERS.PASSWORD));
        user.setId(rec.get(Users.USERS.ID));
        user.setFirstName(rec.get(Users.USERS.FIRST_NAME));
        user.setLastName(rec.get(Users.USERS.LAST_NAME));
        user.setPatronymicName(rec.get(Users.USERS.PATRONYMIC_NAME));
        user.setPhoneNumber(rec.get(Users.USERS.PHONE_NUMBER));
        user.setEmail(rec.get(Users.USERS.EMAIL));
        user.setPushAllowed(rec.get(Users.USERS.PUSH_ALLOWED));
        user.setViberAllowed(rec.get(Users.USERS.VIBER_ALLOWED));
        user.setWhatsappAllowed(rec.get(Users.USERS.WHATSAPP_ALLOWED));
        user.setEmailAllowed(rec.get(Users.USERS.EMAIL_ALLOWED));
        var gender = rec.get(Users.USERS.GENDER);
        if (gender != null)
            user.setGender(Genders.valueOf(gender));
        user.setBirthday(rec.get(Users.USERS.BIRTHDAY));
        user.setDraft(rec.get(Users.USERS.DRAFT));
        return user;
    }

    @Override
    public List<UserDto> getByIds(List<Long> userIds) {
        return Lists.partition(userIds, 1000).parallelStream()
                .map(part -> dslContext
                        .select()
                        .from(Users.USERS)
                        .where(Users.USERS.ID.in(part))
                        .fetchInto(UserDto.class))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}