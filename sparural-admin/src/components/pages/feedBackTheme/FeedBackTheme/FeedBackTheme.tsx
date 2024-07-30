import { message } from "antd";
import React, { FC, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { Routes } from "../../../../config";
import { FeedBackThemeService } from "../../../../services/FeedBackThemeService";
import { FeedBackThemeType } from "../../../../types";
import { DeleteModal } from "../../../complexes/DeleteModal";
import { MainLayout } from "../../../complexes/MainLayout";
import { MainTable } from "../../../complexes/MainTable";
import { ColumnType } from "../../../complexes/MainTable/TableBody";
type Props = {};

const FeedBackTheme: FC<Props> = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [del, setDel] = useState<number>(0);
  const [list, setList] = useState<Array<FeedBackThemeType>>([]);
  const [loading, setLoading] = useState(false);
  const offset = useRef(0);
  const has = useRef(true);

  const columns: Array<ColumnType> = [
    {
      key: "name",
      title: t("common.temaMessage"),
      width: 10
    }
  ];


  const goToEditPage = useCallback(
    (id: number) => {
      navigate(`${Routes.FEEDBACK_THEME_EDIT}?userId=${id}`);
    },
    [navigate]
  );
  const goToCreatePage = useCallback(() => {
    navigate(Routes.FEEDBACK_THEME_CREATE);
  }, [navigate]);

  const load = async () => {
    if (!has.current || loading) {
      return;
    }

    setLoading(true);
    try {
      const result = await FeedBackThemeService.getFeedBackTheme({
        offset: offset.current
      });
      if (!result.length) {
        has.current = false;
        setLoading(false);
        return;
      }

      const rows = result.map((item: FeedBackThemeType) => {
        return {
          id: item.id,
          name: item.name
        };
      });

      offset.current = offset.current + result.length;

      setList([...list, ...rows]);
    } catch (error) {
      message.error(t("errors.get_data"));
    }
    setLoading(false);
  };
  useEffect(() => {
    load().then();
  }, []);

  const onDeleteScreen = useCallback(async () => {
    try {
      const response = await FeedBackThemeService.deleteFeedBackThemen(del);

      if (response) {
        message.success(t("suсcess_messages.delete_data"));
        setList((prev) => prev.filter((i) => i.id !== del));
        setDel(0);
      } else {
        message.error(t("errors.delete_data"));
      }
    } catch (error) {
      message.error(t("errors.delete_data"));
    }
  }, [del, t]);

  return (
    <MainLayout title={t("screen_title.feedbacktheme")} onAdd={goToCreatePage}>
      <>
        <MainTable
          columns={columns}
          data={list}
          onEdit={(id) => goToEditPage(id)}
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
export default FeedBackTheme;
