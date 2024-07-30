import { message } from "antd";
import React, { FC, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { Routes } from "../../../../config";
import { QuestionAnswerService } from "../../../../services/QuestionAnswerService";
import { QuestionAnswerType } from "../../../../types";
import { DeleteModal } from "../../../complexes/DeleteModal";
import { MainLayout } from "../../../complexes/MainLayout";
import { MainTable } from "../../../complexes/MainTable";
import { ColumnType } from "../../../complexes/MainTable/TableBody";

type Props = {};

const QuestionAnswer: FC<Props> = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [del, setDel] = useState<number>(0);
  const [loading, setLoading] = useState(false);
  const offset = useRef(0);
  const has = useRef(true);
  const [list, setList] = useState<Array<QuestionAnswerType>>([]);
  const QuestionAnswerRef = useRef<Array<QuestionAnswerType>>([]);

  const columns: Array<ColumnType> = [
    {
      key: "question",
      title: t("forms.question"),
      width: 6
    },
    {
      key: "answer",
      title: t("forms.answer"),
      width: 6
    }
  ];

  const load = async () => {
    if (!has.current || loading) {
      return;
    }

    setLoading(true);
    try {
      const result = await QuestionAnswerService.getQuestionAnswer({
        offset: offset.current
      });
      if (!result.length) {
        has.current = false;
        setLoading(false);
        return;
      }
      try {
        const rows = result
          .map((item) => {
            return {
              id: item.id,
              question: item.question || "",

              answer: item.answer || ""
            };
          })
          // времено
          .sort((a, b) => b.id - a.id);

        setList([...list, ...rows]);
        QuestionAnswerRef.current = [...QuestionAnswerRef.current, ...result];
      } catch (error) {
        message.error("wrong data");
      }

      offset.current = offset.current + result.length;
    } catch (error: any) {
      message.error(t("errors.get_data"));
    }
    setLoading(false);
  };
  useEffect(() => {
    load().then();
  }, []);

  const handleEndReached = async () => {
    await load();
  };

  const onDeleteScreen = useCallback(async () => {
    const response = await QuestionAnswerService.deleteQuestionAnswer(del);
    if (response) {
      setList((prev) => prev.filter((i) => i.id !== del));
      setDel(0);
    } else {
      message.error(t("errors.delete"));
    }
  }, [del, t]);

  const goToEdit = (id: number) => {
    navigate(`${Routes.QUESTION_ANSWER_EDIT}?id=${id}`);
  };
  const onCreateNew = () => {
    navigate(Routes.QUESTION_ANSWER_CREATE);
  };
  const data = [
    {
      id: 1,
      question: "sdfsdfsadfasdfasdfsdfsadfsadfasdfasfsadfasdf",
      answer: "qwqwqwqwwqweqwqweqweqeqweqweqweqweqwe"
    }
  ];
  return (
    <MainLayout
      title={t("screen_title.question_answer")}
      onAdd={onCreateNew}
      onEndReached={handleEndReached}
      isLoading={loading}
    >
      <>
        <MainTable
          columns={columns}
          onEdit={goToEdit}
          data={list}
          onDelete={setDel}
        />
        <DeleteModal
          onSubmit={onDeleteScreen}
          onCancel={() => setDel(0)}
          visible={!!del}
        />
      </>
    </MainLayout>
  );
};
export default QuestionAnswer;
