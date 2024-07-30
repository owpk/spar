import { message } from "antd";
import FileSaver from "file-saver";
import { FC, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { Routes } from "../../../../config";
import { Endpoints } from "../../../../constants";
import { ReviewsServices } from "../../../../services/ReviewsServices";
import { FeedBackType, ReviewsType } from "../../../../types";
import { toQueryString } from "../../../../utils/helpers";
import { DeleteModal } from "../../../complexes/DeleteModal";
import { MainLayout } from "../../../complexes/MainLayout";
import { MainTable } from "../../../complexes/MainTable";
import { Celltype, ColumnType } from "../../../complexes/MainTable/TableBody";
import { ReviewFilter } from "../../../complexes/ReviewFilter";
import { FilterContext, ReviewFilterType } from "../../../complexes/ReviewFilter/parts/contexts";
import { Button } from "../../../simples/Button";
import { ButtonType } from "../../../simples/Button/Button";
import styles from "./Reviews.module.scss";

type Props = {};

const FeedBack: FC<Props> = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [del, setDel] = useState<number>(0);
  const [filter, setFilter] = useState<ReviewFilterType>({} as ReviewFilterType);

  const [list, setList] = useState<any>([]);

  const [loading, setLoading] = useState(false);
  const offset = useRef(0);
  const has = useRef(true);
  const feedBackRef = useRef<Array<FeedBackType>>([]);

  const columns: Array<ColumnType> = [
    {
      key: "data",
      title: t("table.date"),
      width: 0.7,
      type: Celltype.DATE_SHORT
    },
    {
      key: "user",
      title: t("table.user"),
      width: 1.5
    },
    {
      key: "rate",
      title: t("table.rate"),
      width: 2,
      type: Celltype.RATE
    },
    {
      key: "cause",
      title: t("table.couse"),
      width: 2
    },
    {
      key: "reviews",
      title: t("table.reviews"),
      width: 2
    },
    {
      key: "shop",
      title: t("table.shop"),
      width: 2,
      type: Celltype.PLACE_SHOP
    }
  ];

  const goToRedact = (id: number) => {
    navigate(`${Routes.REVIEWS_INFO}?id=${id}`);
  };

  const load = async (isFilter?: boolean) => {
    if (!has.current || loading) {
      return;
    }

    setLoading(true);

    try {
      const result = await ReviewsServices.getReviews({
        offset: offset.current,
        limit: 30,
        merchantId: filter.merchantId && filter.merchantId?.length > 0 ? (filter.merchantId?.join('&merchantId=')) : undefined,
        grade: filter.grade && filter.grade?.length > 0 ? (filter.grade?.join('&grade=')) : undefined,
        dateTimeEnd: filter.dateTimeEnd,
        dateTimeStart: filter.dateTimeStart,
        search: filter.search || undefined,
      });

      if (!result.length) {
        has.current = false;
        setLoading(false);
      }

      const rows = result.map((item: ReviewsType) => {
        return {
          id: item.id,
          data: item?.createdAt || "",
          user: item?.user.firstName + " " + item?.user.lastName || "",
          rate: item?.grade || "",
          cause: item?.options[0].answer || "",
          reviews: item?.comment || "",
          shop: `${item?.merchant.title}, ${item?.merchant.address}` || ""
        };
      });

      // setList([...list, ...rows]);

      setList((prev: any) => isFilter ? rows : [...prev, ...rows]);

      offset.current = offset.current + result.length;

      // feedBackRef.current = [...feedBackRef.current, ...result];
    } catch (error) {
      message.error(t("errors.get_data"));
    }
    setLoading(false);
  };
  useEffect(() => {
    load().then();
  }, []);

  //   const onDeleteScreen = useCallback(async () => {
  //     try {
  //       const response = await FeedBackService.deleteFeedBack(del);

  //       if (response) {
  //         message.success(t("suсcess_messages.delete_data"));
  //         setList((prev) => prev.filter((i) => i.id !== del));
  //         setDel(0);
  //       } else {
  //         message.error(t("errors.delete_data"));
  //       }
  //     } catch (error) {
  //       message.error(t("errors.delete_data"));
  //     }
  //   }, [del, t]);

  const handleEndReached = async () => {
    await load();
  };

  const onChangeFilter = useCallback(() => {

  }, [])

  const clearFunction = async () => {
    has.current = true
    // setLoading(true)
    offset.current = 0
    // setList([])
    await load(true)
  }

  // dowload report
  const onHandleDowload = useCallback(async () => {
    try {
      const result = await fetch(`/api/v1/${Endpoints.REVIEWS_REPORT_EXPORT}${toQueryString({
        merchantId: filter.merchantId && filter.merchantId?.length > 0 ? (filter.merchantId?.join('&merchantId=')) : undefined,
        grade: filter.grade && filter.grade?.length > 0 ? (filter.grade?.join('&grade=')) : undefined,
        dateTimeEnd: filter.dateTimeEnd,
        dateTimeStart: filter.dateTimeStart,
        search: filter.search || undefined,
      })}`, {
        method: 'GET',
        headers: {
          "accept": "text/csv",
          "x-client-type": "web",
        },
      })
      if (!result.ok) {
        // throw new Error('Ответ сети был не ok.');
      }
      const myBlob = await result.blob();
      const objectURL = URL.createObjectURL(myBlob);

      await FileSaver.saveAs(
        objectURL,
        'report.csv'
      )
    } catch (error) {

    }


  }, [filter.dateTimeEnd, filter.dateTimeStart, filter.grade, filter.merchantId, filter.search])


  useEffect(() => {
    clearFunction().then()
  }, [filter.dateTimeEnd, filter.dateTimeStart, filter.grade, filter.merchantId, filter.search])

  return (
    <FilterContext.Provider value={[filter, setFilter]}>
      <MainLayout
        onEndReached={handleEndReached}
        title={t("screen_title.reviews")}
        customFilter={<ReviewFilter />}
      >
        <>
          <div className={styles.table}>
            <MainTable
              columns={columns}
              data={list}
              onEdit={(id) => goToRedact(id)}
              onDelete={setDel}
              withoutEdit={false}
              iconsInfo={true}
            />
            <DeleteModal
              onSubmit={() => { }}
              onCancel={() => setDel(0)}
              visible={!!del}
            />
          </div>
          <div className={styles.btnUpload}>
            <Button
              classes={{
                root: styles.btn
              }}
              label={t("reviews.outload")}
              onClick={onHandleDowload}
              typeStyle={ButtonType.SECOND}
              textUp="uppercase"
            />
          </div>
        </>
      </MainLayout>
    </FilterContext.Provider>
  );
};
export default FeedBack;
