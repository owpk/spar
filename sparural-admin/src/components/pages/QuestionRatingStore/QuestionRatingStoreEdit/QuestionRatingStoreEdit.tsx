import { message } from "antd";
import { FC, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import { Routes } from "../../../../config";
import { QuestionRatingStoreServives } from "../../../../services/QuestionRatingStoreServives";
import { QuestionCode } from "../../../../types";
import { MainLayout } from "../../../complexes/MainLayout";
import { EditWrapper } from "../../../simples/EditWrapper";
import { TextAria } from "../../../simples/TextAria";
import styles from "./QuestionRatingStoreEdit.module.scss";

type Props = {};

const QuestionRatingStoreEdit: FC<Props> = () => {
  const { t } = useTranslation();
  const id = useLocation().search.split("=")[1];
  const navigation = useNavigate();
  const [answer, setAnswer] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);

  const getOneScreenById = useCallback(async () => {
    setLoading(true)
    const result = await QuestionRatingStoreServives.getQuestionRatingStore({
      offset: 0,
      limit: 30
    });

    const badQuestion = result.find(i => i.code === QuestionCode.BAD)

    if (badQuestion) {
      const option = badQuestion.options.find(i => i.id === +id)
      setAnswer(option?.answer || '')
    }
    setLoading(false)
  }, [id]);


  const onHandleSave = useCallback(async () => {

    try {
      if (id) {
        const response = await QuestionRatingStoreServives.updateQuestionRatingStore(
          QuestionCode.BAD,
          Number(+id),
          { answer }
        );
      } else {
        const response = await QuestionRatingStoreServives.createQuestionRatingStore(
          QuestionCode.BAD,
          { answer }
        );
      }
      message.success(t("suсcess_messages.update_data"));
      navigation(Routes.QUESTION_RATING_STORE);
    } catch (error: any) {
      message.error(
        t("errors.update_data") + ` (${error.response.data.message || error.response.status})`
      );
    }
  }, [id, t, navigation, answer]);

  useEffect(() => {
    if (!!id) {
      getOneScreenById().then();
    }
  }, [id]);

  return (
    <MainLayout
      title={t("screen_title.question_rating_store")}
      isLoading={loading}
    >
      <EditWrapper
        title={t("common.edit_full")}
        onSave={onHandleSave}
        disabled={answer.length === 0}
      >
        <div className={styles.block}>
          <div className={styles.blockOne}>
            <TextAria
              label={t("forms.answer")}
              value={answer}
              onChange={setAnswer}
              height={140}
            />
          </div>
        </div>
      </EditWrapper>
    </MainLayout>
  );
};
export default QuestionRatingStoreEdit;
