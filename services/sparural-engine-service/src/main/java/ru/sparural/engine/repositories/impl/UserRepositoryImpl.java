package ru.sparural.engine.repositories.impl;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateSetStep;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SortField;
import org.jooq.UpdateSetStep;
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
import ru.sparural.engine.repositories.UserRepository;
import ru.sparural.engine.repositories.impl.tools.ConditionBuilder;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.enums.Genders;
import ru.sparural.tables.*;
import ru.sparural.tables.records.UsersRecord;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.sparural.engine.repositories.impl.tools.SearchOperators.LIKE;
import static ru.sparural.engine.repositories.impl.tools.SearchOperators.MAX;
import static ru.sparural.engine.repositories.impl.tools.SearchOperators.MIN;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final DSLContext dslContext;

    public Optional<User> findWhere(Condition condition) {
        var rec = dslContext.select()
                .from(Users.USERS)
                .leftJoin(RoleUser.ROLE_USER)
                .on(Users.USERS.ID.eq(RoleUser.ROLE_USER.USERID))
                .leftJoin(Roles.ROLES)
                .on(RoleUser.ROLE_USER.ROLEID.eq(Roles.ROLES.ID))
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
        return findWhere(Users.USERS.PHONENUMBER.eq(firstName).and(Users.USERS.LASTNAME.eq(lastName)));
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return findWhere(Users.USERS.PHONENUMBER.eq(phone));
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
                .addCondition(Users.USERS.FIRSTNAME.getName(), filter.getSearch(), LIKE)
                .addCondition(Users.USERS.LASTNAME.getName(), filter.getSearch(), LIKE)
                .addCondition(Users.USERS.PATRONYMICNAME.getName(), filter.getSearch(), LIKE)
                .addCondition(Users.USERS.BIRTHDAY.getName(), searchMinAge, MAX)
                .addCondition(Users.USERS.BIRTHDAY.getName(), searchMaxAge, MIN)
                .addCondition(Users.USERS.CREATEDAT.getName(), minRegistrationDate, MIN, "users")
                .addCondition(Users.USERS.CREATEDAT.getName(), maxRegistrationDate, MAX, "users")
                .buildCondition();

        var group = filter.getGroup();
        if (group != null) {
            var usersInUserGroup = dslContext.select(UsersGroupUser.USERS_GROUP_USER.USERID)
                    .from(UsersGroupUser.USERS_GROUP_USER)
                    .where(UsersGroupUser.USERS_GROUP_USER.USERSGROUPID.eq(group))
                    .fetchInto(Long.class);
            res = res.and(Users.USERS.ID.in(usersInUserGroup));
        }

        var noGroup = filter.getNotinGroup();
        if (noGroup != null) {
            var usersInUserGroupNo = dslContext.select(UsersGroupUser.USERS_GROUP_USER.USERID)
                    .from(UsersGroupUser.USERS_GROUP_USER)
                    .where(UsersGroupUser.USERS_GROUP_USER.USERSGROUPID.eq(noGroup))
                    .fetchInto(Long.class);
            res = res.and(Users.USERS.ID.notIn(usersInUserGroupNo));
        }

        var role = filter.getRole();
        if (role != null && !role.isEmpty()) {
            var usersWithRoles = dslContext.select(RoleUser.ROLE_USER.USERID)
                    .from(RoleUser.ROLE_USER)
                    .where(RoleUser.ROLE_USER.ROLEID.in(role))
                    .fetchInto(Long.class);
            res = res.and(Users.USERS.ID.in(usersWithRoles));
        }

        var neRole = filter.getRole_ne();
        if (neRole != null && !neRole.isEmpty()) {
            var usersWithoutRole = dslContext.select(RoleUser.ROLE_USER.USERID)
                    .from(RoleUser.ROLE_USER
                            .leftJoin(Roles.ROLES)
                            .on(Roles.ROLES.ID.eq(RoleUser.ROLE_USER.ROLEID)))
                    .where(Roles.ROLES.ID.in(neRole))
                    .fetchInto(Long.class);
            res = res.and(Users.USERS.ID.notIn(usersWithoutRole));
        }

        var gender = filter.getGender();
        if (gender != null) {
            Genders genderEnum = Genders.other;
            if (gender.equals("female")) genderEnum = Genders.female;
            if (gender.equals("male")) genderEnum = Genders.male;
            res = res.and(Users.USERS.GENDER.eq(genderEnum));
        }

        return res;
    }

    @Override
    public List<User> list(UserSearchFilterDto filter) {
        SelectConditionStep<Record> userList = dslContext
                .select()
                .from(Users.USERS)
                .leftJoin(RoleUser.ROLE_USER).on(Users.USERS.ID.eq(RoleUser.ROLE_USER.USERID))
                .leftJoin(Roles.ROLES).on(Roles.ROLES.ID.eq(RoleUser.ROLE_USER.ROLEID))
                .where();

        return buildWithFilters(userList, filter)
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
                    Users.USERS.LASTNAME.desc(),
                    Users.USERS.FIRSTNAME.desc(),
                    Users.USERS.PATRONYMICNAME.desc());
        }

        return List.of(
                Users.USERS.LASTNAME.asc(),
                Users.USERS.FIRSTNAME.asc(),
                Users.USERS.PATRONYMICNAME.asc());
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
                .set(table.FIRSTNAME, user.getFirstName())
                .set(table.PASSWORD, user.getPassword())
                .set(table.LASTNAME, user.getLastName())
                .set(table.PATRONYMICNAME, user.getPatronymicName())
                .set(table.EMAIL, user.getEmail())
                .set(table.PHONENUMBER, user.getPhoneNumber())
                .set(table.GENDER, user.getGender() != null ?
                        Genders.valueOf(user.getGender().getGender()) : Genders.other)
                .set(table.BIRTHDAY, user.getBirthday())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .set(table.REJECTPAPERCHECKS, user.getRejectPaperChecks())
                .set(table.WHATSAPPALLOWED, true)
                .set(table.VIBERALLOWED, true)
                .set(table.EMAILALLOWED, true)
                .set(table.SMSALLOWED, true)
                .set(table.PUSHALLOWED, true);

        InsertOnDuplicateSetStep<UsersRecord> onConflictClause = insertStep.onConflict(Users.USERS.PHONENUMBER).doUpdate();

        var record = onConflictClause
                .set(table.FIRSTNAME, user.getFirstName())
                .set(table.LASTNAME, user.getLastName())
                .set(table.PASSWORD, user.getPassword())
                .set(table.PATRONYMICNAME, user.getPatronymicName())
                .set(table.GENDER, user.getGender() != null ?
                        Genders.valueOf(user.getGender().getGender()) : Genders.other)
                .set(table.BIRTHDAY, user.getBirthday())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .set(table.REJECTPAPERCHECKS, user.getRejectPaperChecks())
                .set(table.WHATSAPPALLOWED, user.getWhatsappAllowed())
                .set(table.VIBERALLOWED, user.getViberAllowed())
                .set(table.EMAILALLOWED, user.getEmailAllowed())
                .set(table.SMSALLOWED, user.getSmsAllowed())
                .set(table.PUSHALLOWED, user.getPushAllowed()).returningResult()
                .fetchOne();


        if (record != null) {
            Long id = record.getValue(Users.USERS.ID);
            List<Role> roleList = user.getRoles();

            if (roleList != null)
                roleList.forEach(role ->
                        dslContext.insertInto(RoleUser.ROLE_USER)
                                .set(RoleUser.ROLE_USER.USERID, id)
                                .set(RoleUser.ROLE_USER.ROLEID, role.getId())
                                .onConflict(RoleUser.ROLE_USER.USERID, RoleUser.ROLE_USER.ROLEID)
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
                    .where(RoleUser.ROLE_USER.USERID.eq(user.getId()))
                    .execute();
            log.trace("USER_LOG: User update request. Deleted user roles records: " + deleted);
            roleList.forEach(role ->
                    dslContext.insertInto(RoleUser.ROLE_USER)
                            .set(RoleUser.ROLE_USER.USERID, user.getId())
                            .set(RoleUser.ROLE_USER.ROLEID, role.getId())
                            .onConflict(RoleUser.ROLE_USER.USERID, RoleUser.ROLE_USER.ROLEID)
                            .doNothing().execute());
        }

        UpdateSetStep<UsersRecord> res = dslContext.update(Users.USERS);

        if (user.getFirstName() != null)
            res = res.set(Users.USERS.FIRSTNAME, user.getFirstName());
        if (user.getLastName() != null)
            res = res.set(Users.USERS.LASTNAME, user.getLastName());
        if (user.getEmail() != null)
            res = res.set(Users.USERS.EMAIL, user.getEmail());
        if (user.getPhoneNumber() != null)
            res = res.set(Users.USERS.PHONENUMBER, user.getPhoneNumber());
        if (user.getPatronymicName() != null)
            res = res.set(Users.USERS.PATRONYMICNAME, user.getPatronymicName());
        if (user.getGender() != null)
            res = res.set(Users.USERS.GENDER, Genders.valueOf(user.getGender().getGender()));
        if (user.getBirthday() != null)
            res = res.set(Users.USERS.BIRTHDAY, user.getBirthday());
        if (user.getRejectPaperChecks() != null)
            res = res.set(Users.USERS.REJECTPAPERCHECKS, user.getRejectPaperChecks());

        return res.set(Users.USERS.UPDATEDAT, TimeHelper.currentTime())
                .where(Users.USERS.ID.eq(user.getId()))
                .returningResult()
                .fetchOptionalInto(User.class);
    }

    @Override
    public Optional<UserProfileDto> getProfileInfo(Long userId) {
        var rec = dslContext.select()
                .from(Users.USERS)
                .leftJoin(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES)
                .on(Users.USERS.ID.eq(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USERID))
                .leftJoin(UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS)
                .on(Users.USERS.ID.eq(UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS.USERID))
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
                .where(Users.USERS.PHONENUMBER.eq(login)).or(Users.USERS.EMAIL.eq(login))
                .fetchOptional().isPresent();
    }

    @Override
    public boolean checkIfUserExistsWithPhoneOrEmail(String phone, String email) {
        return dslContext.selectFrom(Users.USERS)
                .where(Users.USERS.PHONENUMBER.eq(phone)).or(Users.USERS.EMAIL.eq(email))
                .fetchOptional().isPresent();
    }

    @Override
    public boolean checkIfUserExistsWithPhoneOrEmail(String phone, String email, Long id) {
        return dslContext.selectFrom(Users.USERS)
                .where((Users.USERS.PHONENUMBER.eq(phone)).or(Users.USERS.EMAIL.eq(email))
                        .and(Users.USERS.ID.notEqual(id)))
                .fetchOptional().isPresent();
    }

    @Override
    public List<Long> getAllId() {
        return dslContext.select(Users.USERS.ID).from(Users.USERS).fetchInto(Long.class);
    }

    @Override
    public Optional<Long> getCityIdByUserId(Long userId) {
        return dslContext.select(Users.USERS.LASTCITYID)
                .from(Users.USERS)
                .where(Users.USERS.ID.eq(userId))
                .fetchOptionalInto(Long.class);
    }

    @Override
    public Optional<User> findByUserPushToken(String token) {
        return dslContext.select()
                .from(Users.USERS)
                .leftJoin(UserPushTokens.USER_PUSH_TOKENS)
                .on(Users.USERS.ID.eq(UserPushTokens.USER_PUSH_TOKENS.USERID))
                .where(UserPushTokens.USER_PUSH_TOKENS.TOKEN.eq(token))
                .fetchOptionalInto(User.class);
    }

    @Override
    public void updateLastCityId(Long userId, Long cityId) {
        dslContext
                .update(Users.USERS)
                .set(Users.USERS.LASTCITYID, cityId)
                .set(Users.USERS.UPDATEDAT, TimeHelper.currentTime())
                .where(Users.USERS.ID.eq(userId))
                .execute();
    }

    @Override
    public void updateDeleteAt(Long userId) {
        dslContext
                .update(Users.USERS)
                .set(Users.USERS.DELETEDAT, TimeHelper.currentTime())
                .where(Users.USERS.ID.eq(userId))
                .execute();
    }

    @Override
    public void updateLastActivity(Long userId) {
        dslContext
                .update(Users.USERS)
                .set(Users.USERS.LASTACTIVITY, TimeHelper.currentTime())
                .where(Users.USERS.ID.eq(userId))
                .execute();
    }

    @Override
    public Optional<User> findByEmailOrPhone(String phone, String email) {
        return findWhere(Users.USERS.EMAIL.eq(email).or(Users.USERS.PHONENUMBER.eq(phone)));
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
                .on(Users.USERS.ID.eq(UsersGroupUser.USERS_GROUP_USER.USERID))
                .leftJoin(UsersGroups.USERS_GROUPS)
                .on(UsersGroupUser.USERS_GROUP_USER.USERSGROUPID.eq(UsersGroups.USERS_GROUPS.ID))
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
                        .on(Users.USERS.ID.eq(UsersGroupUser.USERS_GROUP_USER.USERID));
                conditions.add(UsersGroupUser.USERS_GROUP_USER.USERSGROUPID.in(filter.getGroupIds()));
            }

            if (UserFilterRegistrationTypes.isNeedFilter(filter.getRegistrationType())) {
                query = query.leftJoin(Registrations.REGISTRATIONS)
                        .on(Users.USERS.ID.eq(Registrations.REGISTRATIONS.USERID));

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
                .leftJoin(RoleUser.ROLE_USER).on(Users.USERS.ID.eq(RoleUser.ROLE_USER.USERID))
                .leftJoin(Roles.ROLES).on(Roles.ROLES.ID.eq(RoleUser.ROLE_USER.ROLEID))
                .where(whereCondition)
                .fetchOneInto(Long.class);
    }

    @Override
    public Long usersCount(UserSearchFilterDto filter) {
        var selectStep = dslContext.select()
                .from(Users.USERS)
                .where();
        return (long) buildWithFilters(selectStep, filter).fetch().size();
    }

    private User mapRecordToUserAndAddRoles(Result<Record> r) {
        var user = mapRecordToUserWithoutRoles(r);
        var roles = dslContext.select()
                .from(Roles.ROLES.leftJoin(RoleUser.ROLE_USER)
                        .on(RoleUser.ROLE_USER.ROLEID.eq(Roles.ROLES.ID)))
                .where(RoleUser.ROLE_USER.USERID.eq(user.getId()))
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
        user.setFirstName(rec.get(Users.USERS.FIRSTNAME));
        user.setLastName(rec.get(Users.USERS.LASTNAME));
        user.setPatronymicName(rec.get(Users.USERS.PATRONYMICNAME));
        user.setPhoneNumber(rec.get(Users.USERS.PHONENUMBER));
        user.setEmail(rec.get(Users.USERS.EMAIL));
        user.setPushAllowed(rec.get(Users.USERS.PUSHALLOWED));
        user.setViberAllowed(rec.get(Users.USERS.VIBERALLOWED));
        user.setWhatsappAllowed(rec.get(Users.USERS.WHATSAPPALLOWED));
        user.setEmailAllowed(rec.get(Users.USERS.EMAILALLOWED));
        var gender = rec.get(Users.USERS.GENDER);
        if (gender != null)
            user.setGender(ru.sparural.engine.api.enums.Genders.of(gender.getLiteral()));
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