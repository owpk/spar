package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.RoleUser;
import ru.sparural.tables.records.RoleUserRecord;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RolesUsersRepository extends
        CrudRepositoryImpl<Long, ru.sparural.tables.records.RoleUserRecord, RoleUser> {

    @PostConstruct
    public void init() {
        this.table = RoleUser.ROLE_USER;
        this.idFieldName = RoleUser.ROLE_USER.ID.getName();
    }

    public void insertUserRoleRecord(Long userId, Long roleId) {
        insertUserRoleRecord(userId, List.of(roleId));
    }

    @Transactional
    public void insertUserRoleRecord(Long userId, List<Long> roleIds) {
        Result<RoleUserRecord> records = dsl.fetch(table);
        for (Long roleId : roleIds) {
            var rec = dsl.fetchOne(
                    table, table.ROLEID.eq(roleId).and(table.USERID.eq(userId)));
            if (rec == null) {
                rec = createRecord();
                rec.setUserid(userId);
                rec.setRoleid(roleId);
                rec.setCreatedat(TimeHelper.currentTime());
                records.add(rec);
            }
        }
        dsl.batchStore(records).execute();
    }
}