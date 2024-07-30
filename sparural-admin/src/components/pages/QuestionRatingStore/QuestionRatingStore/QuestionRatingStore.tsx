import { message } from "antd";
import { FC, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { Routes } from "../../../../config";
import { QuestionRatingStoreServives } from "../../../../services/QuestionRatingStoreServives";
import { QuestionCode, QuestionRatingStoreOptionsType } from "../../../../types";
import { DeleteModal } from "../../../complexes/DeleteModal";
import { MainLayout } from "../../../complexes/MainLayout";
import { MainTable } from "../../../complexes/MainTable";
import { ColumnType } from "../../../complexes/MainTable/TableBody";
type Props = {};
type TableQuestionsType = {
  id: number
  title: string
}
const QuestionRatingStore: FC<Props> = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [del, setDel] = useState<number>();
  const [list, setList] = useState<Array<TableQuestionsType>>([]);
  const [loading, setLoading] = useState(false);
  const offset = useRef(0);
  const has = useRef(true);

  const columns: Array<ColumnType> = [
    {
      key: "title",
      title: t("table.answer"),
      width: 10
    }
  ];


  /**
   * go to edit page
   */
  const goToEditPage = useCallback(
    (id: number) => {
      navigate(`${Routes.QUESTION_RATING_STORE_EDIT}?questionId=${id}`);
    },
    [navigate]
  );

  const load = async () => {
    if (!has.current || loading) {
      return;
    }

    setLoading(true);
    try {
      const result = await QuestionRatingStoreServives.getQuestionRatingStore({
        offset: offset.current
      });
      if (!result.length) {
        has.current = false;
        setLoading(false);
        return;
      }

      const badQuestion = result.find(i => i.code === QuestionCode.BAD)
      
      if(badQuestion){
        const rows = badQuestion.options.map((item: QuestionRatingStoreOptionsType): TableQuestionsType => {
          return {
            id: item.id,
            title: item.answer
          };
        });

        setList([...list, ...rows])
      }

      offset.current = offset.current + result.length;

     
    } catch (error) {
      message.error(t("errors.get_data"));
    }
    setLoading(false);
  };
  useEffect(() => {
    load().then();
  }, []);

/**
 * delete answers
 */
  const onDeleteScreen = useCallback(async () => {
    if(!del) return
    try {
      const response = await QuestionRatingStoreServives.deleteQuestionRating(
        QuestionCode.BAD,
        del
        );

      if (response) {
        message.success(t("suсcess_messages.delete_data"));
        setList((prev) => prev.filter((i) => i.id !== del));
        setDel(undefined);
      } else {
        message.error(t("errors.delete_data"));
      }
    } catch (error) {
      message.error(t("errors.delete_data"));
    }
  }, [del, t]);

  /**
   * navigate to edit page
   */
  const onAddQuestion = useCallback(async () => {
    navigate(Routes.QUESTION_RATING_STORE_CREATE)
  }, [navigate])

  return (
    <MainLayout title={t("screen_title.question_rating_store")}
      onAdd={onAddQuestion}
    >
      <>
        <MainTable
          columns={columns}
          data={list}
          onEdit={(id) => goToEditPage(id)}
          onDelete={(id) => setDel(id)}
        />
        <DeleteModal
          onSubmit={onDeleteScreen}
          onCancel={() => setDel(undefined)}
          visible={!!del}
        />
      </>
    </MainLayout>
  );
};
export default QuestionRatingStore;
