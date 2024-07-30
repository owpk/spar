import { message } from "antd";
import React, { FC, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { Routes } from "../../../../config";
import { StaticScreensService } from "../../../../services/StaticScreensService";
import { DeleteModal } from "../../../complexes/DeleteModal";
import { MainLayout } from "../../../complexes/MainLayout";
import { MainTable } from "../../../complexes/MainTable";
import { DataStaticType } from "../../../complexes/MainTable/MainTable";
import { ColumnType } from "../../../complexes/MainTable/TableBody";

const StaticScreen: FC = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  const [list, setList] = useState<Array<DataStaticType>>([]);
  const [loading, setLoading] = useState(false);
  const offset = useRef(0);
  const has = useRef(true);

  const [del, setDel] = useState<number>(0);
  const goToEdit = (id: number) => {
    const idAlias = list.find((i) => i.id === id)?.alias;
    navigate(`${Routes.STATIC_SCREEN_EDIT}?id=${idAlias}`);
  };
  const onCreateNew = () => {
    navigate(Routes.STATIC_SCREEN_CREATE);
  };

  /**
   *
   * fetching list
   */
  const load = async () => {
    if (!has.current || loading) {
      return;
    }

    setLoading(true);
    try {
      const result = await StaticScreensService.getStaticScreens({
        offset: offset.current
      });
      if (!result.length) {
        has.current = false;
        setLoading(false);
        return;
      }
      const rows = result.map((item) => {
        return {
          id: item.id,
          alias: item.alias,
          doc_name: item.title
        };
      });

      offset.current = offset.current + result.length;
      setList([...list, ...rows]);
      setLoading(false);
    } catch (error) {
      message.error(t("errors.get_data"));
    }
    setLoading(false);
  };
  useEffect(() => {
    load().then();
  }, []);

  /**
   * column data for table
   */
  const columns: Array<ColumnType> = [
    {
      key: "alias",
      title: t("table.alias"),
      width: 2
    },
    {
      key: "doc_name",
      title: t("table.doc_name"),
      width: 6
    }
  ];

  /**
   * delete static screen
   */
  const onHandleDelete = useCallback(async () => {
    const idAlias = list.find((i) => i.id === del)?.alias;
    try {
      if (idAlias) {
        const response = await StaticScreensService.deleteStaticScreen(idAlias);

        if (response) {
          message.success(t("suсcess_messages.delete_data"));
          setDel(0);
          setList((prev) => prev.filter((i) => i.id !== del));
        }
      } else {
        message.warning(t("errors.delete_data"));
      }
    } catch (error) {
      message.error(t("errors.delete_data"));
    }
  }, [del, list, t]);

  return (
    <MainLayout
      isLoading={loading}
      onAdd={onCreateNew}
      title={t("screen_title.static_screen")}
      onEndReached={load}
    >
      <>
        <MainTable
          columns={columns}
          data={list}
          onEdit={goToEdit}
          onDelete={setDel}
        />
        <DeleteModal
          onSubmit={onHandleDelete}
          onCancel={() => setDel(0)}
          visible={!!del}
        />
      </>
    </MainLayout>
  );
};

export default StaticScreen;
