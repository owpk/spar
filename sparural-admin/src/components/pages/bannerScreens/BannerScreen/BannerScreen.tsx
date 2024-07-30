import { FC, useCallback, useEffect, useRef, useState } from "react";
import MainLayout from "../../../complexes/MainLayout/MainLayout";
import styles from "./BannerScreen.module.scss";

import { useNavigate } from "react-router";
import { Routes } from "../../../../config";
import { MainTable } from "../../../complexes/MainTable";
import { useTranslation } from "react-i18next";
import { DeleteModal } from "../../../complexes/DeleteModal";
import { Celltype, ColumnType } from "../../../complexes/MainTable/TableBody";
import { BannerPlaceService } from "../../../../services/BannerPlaceService";
import { message } from "antd";
import { DataBannerType } from "../../../complexes/MainTable/MainTable";
import {
  BannerScreenType,
  CreateBannerPlaceType
} from "../../../../types";
import produce from "immer";

/**
 *
 * @returns banners screen
 */
const BannerScreen: FC = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [del, setDel] = useState<number>(0);
  const [list, setList] = useState<Array<DataBannerType>>([]);
  const [loading, setLoading] = useState(false);
  const offset = useRef(0);
  const has = useRef(true);
  const bannersRef = useRef<Array<BannerScreenType>>([]);

  // data for table
  const columns: Array<ColumnType> = [
    {
      key: "banner",
      title: t("common.banner"),
      type: Celltype.IMAGE,
      width: 1
    },
    {
      key: "name",
      title: t("common.name"),
      width: 2
    },
    { key: "places", title: t("common.places"), width: 1 },
    {
      key: "link",
      title: t("common.link"),
      width: 3
    },
    {
      key: "button",
      title: t("common.button"),
      type: Celltype.BUTTON,
      width: 2
    }
  ];

  const goToRedact = (id: number) => {
    navigate(`${Routes.BANNER_SCREEN_REDACT}?id=${id}`);
  };

  const goToCreate = () => {
    navigate(Routes.BANNER_SCREEN_ADD);
  };

  // fetching banners
  const load = async () => {
    if (!has.current || loading) {
      return;
    }

    setLoading(true);
    try {
      const result = await BannerPlaceService.getBannerPlaces({
        offset: offset.current
      });
      if (!result.length) {
        has.current = false;
        setLoading(false);
        return;
      }

      const rows = result.map((item: BannerScreenType) => {
        return {
          id: item.id,
          banner:item?.photo?.uuid  || "",
          button: !!item?.isPublic || false,
          name: item?.title || "",
          link: item?.url || item.mobileNavigateTarget?.name || "",
          places: `${item?.order}` || ""
        };
      });

      offset.current = offset.current + result.length;

      setList([...list, ...rows]);
      bannersRef.current = [...bannersRef.current, ...result];
    } catch (error) {
      message.error(t("errors.get_data"));
    }
    setLoading(false);
  };

  useEffect(() => {
    load().then();
  }, []);

  // fetching new banners when scroll down

  const onPublish = useCallback(
    async (id: number, Publick: boolean) => {
      try {
        const updateBanner = bannersRef.current.find(
          (i) => i.id === id
        ) as CreateBannerPlaceType;
        if (updateBanner) {
          const response = await BannerPlaceService.updateBannerPlace(id, {
            // ...updateBanner,
            // mobileNavigateTargetId: null,
            
            isPublic: Publick
          });
          setList(
            produce((draft) => {
              const find = draft.find((i) => i.id === id);
              if (find) {
                find.button = !find.button;
              }
            })
          );
        }
      } catch (error) {
        message.warning(t("errors.update_data"));
      }
    },
    [t]
  );

  const onDeleteScreen = useCallback(async () => {
    try {
      const response = await BannerPlaceService.deleteBannerPlace(del);

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

  const handleEndReached = async () => {
    await load();
  };

  return (
    <MainLayout
      onEndReached={handleEndReached}
      onAdd={goToCreate}
      isLoading={false}
      title={t("screen_title.banner_place")}
    >
      <>
        <div className={styles.BannerScreen}>
          <MainTable
            columns={columns}
            data={list}
            onDelete={setDel}
            onEdit={(id) => goToRedact(id)}
            onBtnClick={onPublish}
          />
        </div>
        <DeleteModal
          onSubmit={onDeleteScreen}
          onCancel={() => setDel(0)}
          visible={!!del}
        />
      </>
    </MainLayout>
  );
};
export default BannerScreen;
