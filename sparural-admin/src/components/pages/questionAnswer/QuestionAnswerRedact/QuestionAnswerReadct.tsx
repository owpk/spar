import { message } from "antd";
import React, { FC, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import { Routes } from "../../../../config";
import { QuestionAnswerService } from "../../../../services/QuestionAnswerService";
import { CreateQuestonAnswerType } from "../../../../types";
import { MainLayout } from "../../../complexes/MainLayout";
import { EditWrapper } from "../../../simples/EditWrapper";
import { TextAria } from "../../../simples/TextAria";
import styles from "./QuestionAnswerRedact.module.scss";

type Props = {};

const QuestionAnswerRedact: FC<Props> = () => {
  const { t } = useTranslation();
  const id = useLocation().search.split("=")[1];
  const navigation = useNavigate();
  const [question, setQuestion] = useState<string>("");
  const [answer, setAnswer] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);
  const [currentId, setCurrentId] = useState<number>(id ? +id : 0);

  const getOneScreenById = useCallback(async () => {
    try {
      setLoading(true)
      const response = await QuestionAnswerService.getQuestionAnswerById(
        Number(id)
      )
      setQuestion(response.question)
      setAnswer(response.answer)
      setLoading(false)
    } catch (error) {
      message.error(t('errors.get_data'))
      setLoading(false)
    }
  }, [id, t])

  const onHandleSave = useCallback(async () => {
    const sendData: CreateQuestonAnswerType = {
      question: question,
      answer: answer,
      draft: false,
    }
    if (id) {
      try {
        const response = await QuestionAnswerService.updateQuestionAnswer(
          Number(currentId),
          sendData
        )
        message.success(t('suсcess_messages.update_data'))
        navigation(Routes.QUESTION_ANSWER)
      } catch (error: any) {
        message.error(
          t('errors.update_data') + ` (${error.response.data.message})`
        )
      } 
    }else {
      try {
        const response = await QuestionAnswerService.createQuestionAnswer(
          sendData
        )
        message.success(t('suсcess_messages.update_data'))
        navigation(Routes.QUESTION_ANSWER)
      } catch (error: any) {
        message.error(
          t('errors.update_data') + ` (${error.response.data.message})`
        )
      } 
    }

  }, [answer, id, question, t, currentId])

  const onCreateBanner = useCallback(async () => {
    try {
      const sendData: CreateQuestonAnswerType = {
        draft: true,
      }
      const response = await QuestionAnswerService.createQuestionAnswer(
        sendData
      )
      if (response) {
        setCurrentId(response.id)
      }
    } catch (error) { }
  }, [])

  useEffect(() => {
    if (!!id) {
      getOneScreenById().then()
    } else {
      onCreateBanner().then()
    }
  }, [id])

  return (
    <MainLayout title={t("screen_title.question_answer")} isLoading={loading}>
      <EditWrapper
        title={t(!id ? "common.add" : "common.edit_full")}
        onSave={onHandleSave}
      >
        <div className={styles.block}>
          <div className={styles.blockOne}>
            <TextAria
              label={t("forms.question")}
              value={question}
              onChange={setQuestion}
              height={140}
              minRows={6}
            />
          </div>
          <div className={styles.blockOne}>
            <TextAria
              label={t("forms.answer")}
              value={answer}
              onChange={setAnswer}
              height={140}
              minRows={6}
              maxCount={1000}
            />
          </div>
        </div>
      </EditWrapper>
    </MainLayout>
  );
};
export default QuestionAnswerRedact;
