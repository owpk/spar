import { message } from "antd";
import { FC, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { Routes } from "../../../../config";
import { DeliveryType } from "../../../../types";
import { DeleteModal } from "../../../complexes/DeleteModal";
import { MainLayout } from "../../../complexes/MainLayout";
import { MainTable } from "../../../complexes/MainTable";
import { Celltype, ColumnType } from "../../../complexes/MainTable/TableBody";
import { DeliveryOptionsServices } from "../../../../services/DeliveryOptionsServices";
import produce from "immer";

type Props = {};

const DeliveryOptions: FC<Props> = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [del, setDel] = useState<number>(0);
  const [list, setList] = useState<{
    id: number
    header: string
    shortDescription: string
    link: string
    image: string
    button?: boolean
  }[]>([]);

  const has = useRef(true);
  const offset = useRef(0);

  const [loading, setLoading] = useState<boolean>(false);

  // const [loading, setLoading] = useState(false);


  const columns: Array<ColumnType> = [
    {
      key: "header",
      title: t("forms.header"),
      width: 1.5
    },
    {
      key: "shortDescription",
      title: t("forms.short_descrption"),
      width: 2
    },
    {
      key: "link",
      title: t("forms.link"),
      width: 2
    },
    {
      key: "image",
      title: t("common.iz-nei"),
      type: Celltype.IMAGE,
      width: 1.5
    },
    {
      key: "button",
      title: t("forms.button"),
      type: Celltype.BUTTON,
      
      width: 2
    }
  ];

  // fetching delivey
  const load = async () => {
    if (!has.current || loading) {
      return;
    }
    setLoading(true);
    try {
      const result = await DeliveryOptionsServices.getDelivery({
        offset: offset.current
      });

      if (!result.length) {
        has.current = false;
        setLoading(false);
        return;
      }

      const rows = result.map((item: DeliveryType) => {
        return {
          id: item.id,
          header: item?.title || "",
          shortDescription: item?.shortDescription || "",
          link: item?.url || "",
          image:item?.photo?.uuid  || "",
          button: item.isPublic || false
        };
      });

      offset.current = offset.current + result.length;
      setList([...list, ...rows]);

      // bannersRef.current = [...bannersRef.current, ...result];
    } catch (error) {
      message.error(t("errors.get_data"));
    }

    setLoading(false);
  };

  useEffect(() => {
    load().then();
  }, []);

  const goToEdit = (id: number) => {
    navigate(`${Routes.DELIVERY_OPTIONS_EDIT}?id=${id}`);
  };

  const onCreateNew = () => {
    navigate(Routes.DELIVERY_OPTIONS_CREATE);
  };

  const handleEndReached = async () => {
    await load();
  };

  /**
   * del delivery
   */
  const onDeleteDelivery = useCallback(async () => {

    const response = await DeliveryOptionsServices.deleteBannerPlace(del);
    if (response) {
      setDel(0);
      setList((prev) => prev.filter((i) => i.id !== del))
    } else {
      message.error(t("errors.del_data"))
    }
  }, [del, t])

  /**
   * change publick status
   */
  const onHandleChangePublick = useCallback( async(id: number, publick: boolean) => {
    try {
      const res = await DeliveryOptionsServices.updateDeliveryPlace(id, {
          isPublic: publick
      })
      setList(produce(
          draft => {
              const find = draft.find(i => i.id === id)
              if(find)
              find.button = res.isPublic
          }
      ))
      message.success(t("suсcess_messages.update_data"))
  } catch (error) {
      message.error(t("errors.update_data"))
  }
  },[t])

  return (
    <MainLayout
      onEndReached={handleEndReached}
      title={t("screen_title.deliveryOptions")}
      onAdd={onCreateNew}
    >
      <>
        <MainTable
          columns={columns}
          onEdit={goToEdit}
          data={list}
          onDelete={setDel}
          onBtnClick={onHandleChangePublick}
        />
        <DeleteModal
          onSubmit={onDeleteDelivery}
          onCancel={() => setDel(0)}
          visible={!!del}
        />
      </>
    </MainLayout>
  );
};
export default DeliveryOptions;
