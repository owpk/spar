package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.ConfirmCodesNotifier;
import ru.sparural.engine.entity.RecoveryPassword;
import ru.sparural.engine.repositories.RecoveryPasswordRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.ConfirmCodesNotifiers;
import ru.sparural.tables.RecoveryPasswordRequests;

import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class RecoveryPasswordRepositoryImpl implements RecoveryPasswordRepository {

    private final DSLContext dslContext;

    public Optional<ConfirmCodesNotifier> getConfirmCodeNotifierByName(String name) {
        return dslContext.select(
                        ConfirmCodesNotifiers.CONFIRM_CODES_NOTIFIERS.ID,
                        ConfirmCodesNotifiers.CONFIRM_CODES_NOTIFIERS.NAME)
                .from(ConfirmCodesNotifiers.CONFIRM_CODES_NOTIFIERS)
                .where(ConfirmCodesNotifiers.CONFIRM_CODES_NOTIFIERS.NAME.eq(name))
                .fetchOptionalInto(ConfirmCodesNotifier.class);
    }

    @Override
    public Optional<RecoveryPassword> create(RecoveryPassword data) {
        return dslContext.insertInto(RecoveryPasswordRequests.RECOVERY_PASSWORD_REQUESTS)
                .set(RecoveryPasswordRequests.RECOVERY_PASSWORD_REQUESTS.CODE, data.getCode())
                .set(RecoveryPasswordRequests.RECOVERY_PASSWORD_REQUESTS.NOTIFIERID, data.getNotifier().getId())
                .set(RecoveryPasswordRequests.RECOVERY_PASSWORD_REQUESTS.NOTIFIERIDENTITY, data.getNotifierIdentity())
                .set(RecoveryPasswordRequests.RECOVERY_PASSWORD_REQUESTS.EXPIRED, data.getExpired())
                .set(RecoveryPasswordRequests.RECOVERY_PASSWORD_REQUESTS.USERID, data.getUserId())
                .set(RecoveryPasswordRequests.RECOVERY_PASSWORD_REQUESTS.TOKEN, data.getToken())
                .set(RecoveryPasswordRequests.RECOVERY_PASSWORD_REQUESTS.CREATEDAT, TimeHelper.currentTime())
                .returning().fetchOptionalInto(RecoveryPassword.class);
    }

    @Override
    public Optional<RecoveryPassword> getByToken(String token) {
        return dslContext.select()
                .from(RecoveryPasswordRequests.RECOVERY_PASSWORD_REQUESTS)
                .leftJoin(ConfirmCodesNotifiers.CONFIRM_CODES_NOTIFIERS)
                .on(ConfirmCodesNotifiers.CONFIRM_CODES_NOTIFIERS.ID.eq(
                        RecoveryPasswordRequests.RECOVERY_PASSWORD_REQUESTS.NOTIFIERID))
                .where(RecoveryPasswordRequests.RECOVERY_PASSWORD_REQUESTS.TOKEN.eq(token))
                .fetchOptionalInto(RecoveryPassword.class);
    }

    public Optional<ConfirmCodesNotifier> getNotifier(Long id) {
        return dslContext
                .selectFrom(ConfirmCodesNotifiers.CONFIRM_CODES_NOTIFIERS)
                .where(ConfirmCodesNotifiers.CONFIRM_CODES_NOTIFIERS.ID.eq(id))
                .fetchOptionalInto(ConfirmCodesNotifier.class);
    }
}
