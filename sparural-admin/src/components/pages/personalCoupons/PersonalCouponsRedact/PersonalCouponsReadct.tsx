import { message } from "antd";
import React, { FC, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import { PersonalCouponsService } from "../../../../services/PersonalCouponsService";
import {
  CreatePersonalCouponsType,
  PersonalCouponsType,
  Phototype
} from "../../../../types";
import { MainLayout } from "../../../complexes/MainLayout";
import { DatePickerComponent } from "../../../simples/DatePickerComponent";
import { EditWrapper } from "../../../simples/EditWrapper";
import { PhotoLoader } from "../../../simples/PhotoLoader";
import { TextField } from "../../../simples/TextField";
import moment from "moment";
import { Moment } from "moment";
import styles from "./PersonalCouponsRedact.module.scss";
import { InputHolder } from "../../../simples/InputHolder";
import { EntitiesFieldName, FileSource, Routes } from "../../../../config";
import { useUploadFileMutation, UploadFileDocType } from "../../../../services/FileService";

type Props = {};

const PersonalCouponsRedact: FC<Props> = () => {
  const { t } = useTranslation();

  const id = useLocation().search.split("=")[1];
  const navigate = useNavigate()

  const [name, setName] = useState<string>("");
  const [file, setFile] = useState<File>();
  const [photoUrl, setPhotoUrl] = useState<Phototype>();
  const [dateStart, setDateStart] = useState<number>();
  const [dateEnd, setDateEnd] = useState<number>();
  const [loading, setLoading] = useState(false);
  const [item, setItem] = useState<PersonalCouponsType>();
  const [currentId, setCurrentId] = useState<number>(id ? +id : 0);


  const [sendFile, { error }] = useUploadFileMutation()

  const onHandleDisebleStartDate = (date?: Moment) => {
    let disable = false;
    if (dateEnd && date) {
      if (moment(date).valueOf() > dateEnd) {
        disable = true;
      } else {
        disable = false;
      }
    }
    return disable;
  };

  /**
   * 
   * @param date - day date
   * @returns date is available or not
   */
  const onHandleDisebleEndDate = (date?: Moment) => {
    let disable = false;
    if (dateStart && date) {
      if (moment(date).valueOf() < dateStart) {
        disable = true;
      } else {
        disable = false;
      }
    }
    return disable;
  };

  /**
   * get coupon by Id
   */
  const getOneScreenById = useCallback(async () => {
    try {
      const response = await PersonalCouponsService.getPersonalCouponsById(
        Number(id)
      );

      setName(response.title);
      // we get timestamp in seconds/ so we have to multiple on 1000
      setDateEnd(response.end * 1000);

      setPhotoUrl(response.photo)

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

  /**
   * 
   * save personal coupon
   */
  const onHandleSave = useCallback(async () => {
    if (id) {
      try {
        const redact = await PersonalCouponsService.updatePersonalCoupons(
          +id,
          {
            end: dateEnd && dateEnd / 1000
          }
        );
        
      } catch (error) {
        message.error(t("errors.save_data"));
      }

      if (file) {
        await uploadFile(file)
      }
      navigate(Routes.PERSONAL_COUPUNS)
    }
  }, [dateEnd, file, id, navigate, t]);


  /**
* upload image
*/
  const uploadFile = useCallback(async (image: File) => {
    const sendData: UploadFileDocType = {
      source: FileSource.REQUEST,
      'source-parameters': JSON.stringify({}),
      entities: [{ field: EntitiesFieldName.COUPON_EMISSION_PHOTO, documentId: currentId }],
      file: image
    }
   await sendFile(sendData)

  }, [currentId, sendFile])

  useEffect(() => {
    if (error) {
      message.warning(t("errors.upload_photo"))
    }
  }, [error, t])

  return (
    <MainLayout title={t("screen_title.personal_coupons")}>
      <EditWrapper
        title={t(!id ? "common.add" : "common.edit_full")}
        onSave={onHandleSave}
      >
        <div className={styles.block}>
          <div className={styles.blockOne}>
            <InputHolder classes={styles.holder}>
              <TextField
                disabled
                label={t("forms.name")}
                value={name}
                onChange={setName}
              />
            </InputHolder>
            {/* <div className={styles.blockSpanDate}>
              <span>{t("common.date_end")}</span>
            </div> */}

            <InputHolder classes={styles.holder}>
              <div className={styles.blockDate}>
                <div className={styles.blockDateOne}>
                  <DatePickerComponent
                    label={t("common.date_end")}
                    value={dateEnd}
                    onChange={setDateEnd}
                    placeholder="до"
                    onDisableDate={onHandleDisebleEndDate}
                  />
                </div>
              </div>
            </InputHolder>
          </div>

          <div>
            <PhotoLoader
              image={photoUrl}
              onChange={setFile}
              onDelete={() => setFile(undefined)}
            />
          </div>
        </div>
      </EditWrapper>
    </MainLayout>
  );
};
export default PersonalCouponsRedact;
