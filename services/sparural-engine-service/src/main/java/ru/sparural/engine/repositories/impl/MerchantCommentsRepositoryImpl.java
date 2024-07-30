package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Answer;
import ru.sparural.engine.entity.Merchant;
import ru.sparural.engine.entity.MerchantComment;
import ru.sparural.engine.entity.MerchantComments;
import ru.sparural.engine.entity.User;
import ru.sparural.engine.repositories.MerchantCommentsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.MerchantCommentAnswers;
import ru.sparural.tables.MerchantCommentsQuestionOptions;
import ru.sparural.tables.Merchants;
import ru.sparural.tables.Users;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantCommentsRepositoryImpl implements MerchantCommentsRepository {
    private final DSLContext dslContext;
    private final ru.sparural.tables.MerchantComments table = ru.sparural.tables.MerchantComments.MERCHANT_COMMENTS;

    @Override
    public Optional<MerchantComments> create(MerchantComments entity, Long userId, Long cardId) {

        return dslContext.insertInto(table)
                .set(table.MERCHANTID, entity.getMerchantId())
                .set(table.GRADE, entity.getGrade())
                .set(table.COMMENT, entity.getComment())
                .set(table.CARDID, cardId)
                .set(table.USERID, userId)
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(MerchantComments.class);
    }

    @Override
    public List<MerchantComment> list(Integer offset,
                                      Integer limit,
                                      String search,
                                      Integer[] grade,
                                      Long dateTimeStart,
                                      Long dateTimeEnd,
                                      Long[] merchantId) {

        var res = dslContext
                .select()
                .from(table)
                .leftJoin(Users.USERS)
                .on(Users.USERS.ID.eq(table.USERID))
                .leftJoin(Merchants.MERCHANTS)
                .on(Merchants.MERCHANTS.ID.eq(table.MERCHANTID))
                .leftJoin(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS
                        .leftJoin(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS)
                        .on(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS.OPTIONID
                                .eq(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.ID)))
                .on(table.ID.eq(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS.MERCHANTCOMMENTID))
                .where();

//                .from(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS
//                .leftJoin(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS)
//                .on(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.ID
//                        .eq(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS.OPTIONID)))
//                .where(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS.MERCHANTCOMMENTID
//                        .eq(commentsId))
//                .fetchInto(Answer.class);
        if (search != null) {
            res.and(Users.USERS.FIRSTNAME.contains(search)
                    .or(Users.USERS.LASTNAME.contains(search))
                    .or(Users.USERS.PATRONYMICNAME.contains(search))
                    .or(table.COMMENT.contains(search)));
        }

        if (grade != null && grade.length > 0) {
            res.and(table.GRADE.in(grade));
        }

        if (dateTimeStart != null) {
            res.and(table.CREATEDAT.greaterOrEqual(dateTimeStart));
        }

        if (dateTimeEnd != null) {
            res.and(table.CREATEDAT.lessOrEqual(dateTimeEnd));
        }

        if (merchantId != null) {
            res.and(table.MERCHANTID.in(merchantId));
        }

        var step = res
                .orderBy(table.CREATEDAT.desc());

        if (limit == null || limit <= 0) {
            return step
                    .fetch()
                    .intoGroups(table.fields())
                    .values()
                    .stream()
                    .map(this::mapRecordToMerchantComment)
                    .collect(Collectors.toList());
        }

        return step
                .limit(limit)
                .offset(offset)
                .fetch()
                .intoGroups(table.fields())
                .values()
                .stream()
                .map(this::mapRecordToMerchantComment)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MerchantComment> get(Long id) {
        return Optional.of(mapRecordToMerchantComment(dslContext
                .select()
                .from(table)
                .leftJoin(Users.USERS)
                .on(Users.USERS.ID.eq(table.USERID))
                .leftJoin(Merchants.MERCHANTS)
                .on(Merchants.MERCHANTS.ID.eq(table.MERCHANTID))
                .leftJoin(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS
                        .leftJoin(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS)
                        .on(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS.OPTIONID
                                .eq(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.ID)))
                .on(table.ID.eq(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS.MERCHANTCOMMENTID))
                .where(table.ID.eq(id)).fetch()));
    }

    private MerchantComment mapRecordToMerchantComment(Result<Record> r) {
        var user = r.into(Users.USERS.fields()).into(User.class).get(0);
        var merchant = r.into(Merchants.MERCHANTS.fields()).into(Merchant.class).get(0);
        var answers = r.into(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.fields()).into(Answer.class);
        var merchantComment = r.into(table).into(MerchantComment.class).get(0);
        merchantComment.setUser(user);
        merchantComment.setMerchant(merchant);
        merchantComment.setOptions(answers);
        return merchantComment;
    }
}
