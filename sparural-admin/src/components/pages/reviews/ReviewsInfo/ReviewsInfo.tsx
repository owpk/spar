import { FC, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { MainLayout } from "../../../complexes/MainLayout";
import { EditWrapper } from "../../../simples/EditWrapper";
import { InputHolder } from "../../../simples/InputHolder";
import styles from "./ReviewsInfo.module.scss";
import { useLocation } from "react-router-dom";
import { message } from "antd";
import { ReviewsType } from "../../../../types";
import StarIcon from "../../../simples/StarIcon/StarIcon";
import { ReviewsServices } from "../../../../services/ReviewsServices";
import { getTimeFromTimestamp, printDate } from "../../../../utils/helpers";
import { FieldWithLabel } from "../../../simples/FieldWithLabel";
type Props = {};

const ReviewsInfo: FC<Props> = () => {
  const { t } = useTranslation();
  const id = useLocation().search.split("=")[1];
  const [loading, setLoading] = useState<boolean>(false);
  const [currentReview, setCurrentReview] = useState<ReviewsType>();

  const getOneScreenById = useCallback(async () => {
    try {
      setLoading(true);
      const response = await ReviewsServices.getReviewsById(Number(id));
      setCurrentReview(response);

      setLoading(false);
    } catch (error) {
      message.error(t("errors.get_data"));
      setLoading(false);
    }
  }, [id, t]);

  useEffect(() => {
    if (!!id) {
      getOneScreenById().then();
    }
  }, [id]);

  return (
    <MainLayout title={t("screen_title.reviews")}>
      <EditWrapper title={t("common.info")}>
        <>
          <div className={styles.block}>
            <div className={styles.blockOne}>
              <InputHolder>
                <FieldWithLabel label={t("reviews.date")}>
                  <div>{currentReview?.createdAt && printDate(currentReview?.createdAt)}</div>
                </FieldWithLabel>
              </InputHolder>
              <InputHolder>
                <FieldWithLabel label={t("reviews.time")}>
                  <div>{getTimeFromTimestamp(currentReview?.createdAt)}</div>
                </FieldWithLabel>
              </InputHolder>
              <InputHolder>
                <FieldWithLabel label={t("common.userFIO")}>
                  <div>{currentReview?.user.lastName || ''} {currentReview?.user.firstName || ''}</div>
                </FieldWithLabel>
              </InputHolder>
            </div>
            <div className={styles.blockTwo}>
              <InputHolder>
                <FieldWithLabel label={t("table.rate")}>
                  <StarIcon starCount={currentReview?.grade || 0} />
                </FieldWithLabel>
              </InputHolder>
              <InputHolder>
                <FieldWithLabel label={t("table.couse")}>
                  <div>{currentReview?.options.map(i => {
                    return <div>{i.answer}</div>
                  })}</div>
                </FieldWithLabel>
              </InputHolder>
              <InputHolder>
                <FieldWithLabel label={t("table.review")}>
                  <span>{currentReview?.comment || ''}</span>
                </FieldWithLabel>
              </InputHolder>
              <InputHolder>
                <FieldWithLabel label={t("table.shop")}>
                <div>{currentReview?.merchant?.title || ''} {currentReview?.merchant?.address || ''}</div>
                </FieldWithLabel>
              </InputHolder>
            </div>
          </div>
        </>
      </EditWrapper>
    </MainLayout>
  );
};
export default ReviewsInfo;
