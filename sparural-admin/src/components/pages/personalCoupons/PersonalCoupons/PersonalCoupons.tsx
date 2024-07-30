import { message } from "antd";
import list from "antd/lib/list";
import produce from "immer";
import React, { FC, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { Routes } from "../../../../config";
import { BannerPlaceService } from "../../../../services/BannerPlaceService";
import { PersonalCouponsService } from "../../../../services/PersonalCouponsService";
import {
  BannerScreenType,
  PersonalCouponsType,
  PersonalOffersType
} from "../../../../types";
import { printDate } from "../../../../utils/helpers";
import { DeleteModal } from "../../../complexes/DeleteModal";
import { MainLayout } from "../../../complexes/MainLayout";
import { MainTable } from "../../../complexes/MainTable";
import { Celltype, ColumnType } from "../../../complexes/MainTable/TableBody";

type Props = {};

type PersonalCouponDataType = {
  id:number
  name: string
  action :string
  button: boolean
  image: string

}
const PersonalCoupons: FC<Props> = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [del, setDel] = useState<number>(0);
  const [loading, setLoading] = useState(false);
  const [list, setList] = useState<Array<PersonalCouponDataType>>([]);
  const offset = useRef(0);
  const has = useRef(true);

  const columns: Array<ColumnType> = [
    {
      key: "name",
      title: t("common.name"),
      width: 5
    },
    {
      key: "action",
      title: t("common.action"),
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
      title: t("common.button"),
      type: Celltype.BUTTON,
      width: 2
    }
  ];
  const data = [
    {
      id: 1,
      name: "TestTestTest",
      action: "21.02.2021",
      image: ""
    }
  ];

  const goToRedact = (id: number) => {
    navigate(`${Routes.PERSONAL_COUPUNS_EDIT}?id=${id}`);
  };

  const load = async () => {
    if (!has.current || loading) {
      return;
    }

    setLoading(true);
    try {
      const result = await PersonalCouponsService.getPersonalCoupons({
        offset: offset.current
      });
      if (!result.length) {
        has.current = false;
        setLoading(false);
        return;
      }


      const rows: PersonalCouponDataType[] = result.map((item: PersonalCouponsType) => {
          return {
              id: item.id,
              name: item?.title || '',
              action: printDate(item.end) ||'',
              button: !!item?.isPublic || false,
              image:item?.photo?.uuid ||''

          } as PersonalCouponDataType
      })

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
      const response = await PersonalCouponsService.deletePersonalCoupons(del);

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


  const onCouponPublic = useCallback( async(id: number) => {
    const coupon = list.find((i) => i.id === id)
    const response = await PersonalCouponsService.updatePersonalCoupons(id,{
      isPublic: !!!coupon?.button
    });
    setList(produce(
      draft => {
        const find = draft.find((i) => i.id === id)
        if(find)
        find.button =  !!!coupon?.button
      }
    ))
  },[list])

  return (
    <MainLayout
      title={t("screen_title.personal_coupons")}
      isLoading={loading}
      onEndReached={load}
    >
      <>
        <MainTable
          columns={columns}
          data={list}
          onDelete={setDel}
          onBtnClick={onCouponPublic}
          onEdit={(id) => goToRedact(id)}
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
export default PersonalCoupons;
