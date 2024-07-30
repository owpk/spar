import { Col, message, Row } from "antd";
import moment from "moment";
import { Moment } from "moment";
import { FC, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import { EntitiesFieldName, FileSource, Routes } from "../../../../config";
import { UploadFileDocType, useUploadFileMutation } from "../../../../services/FileService";
import { CreatePersonalOffersType, PersonalOffersService } from "../../../../services/PersonalOffersService";
import {
  PersonalOffersType, Phototype
} from "../../../../types";
import { MainLayout } from "../../../complexes/MainLayout";
import { DatePickerComponent } from "../../../simples/DatePickerComponent";
import { EditWrapper } from "../../../simples/EditWrapper";
import { InputHolder } from "../../../simples/InputHolder";
import { Label } from "../../../simples/Label";
import { PhotoLoader } from "../../../simples/PhotoLoader";
import { TextAria } from "../../../simples/TextAria";
import { TextField } from "../../../simples/TextField";
import styles from "./PersonalOffersRedact.module.scss";

type Props = {};

enum ImageType {
  PREVIEW = 'preview',
  PHOTO = 'photo'
}

const PersonalOffersRedact: FC<Props> = () => {
  const { t } = useTranslation();
  const navigate = useNavigate()
  const id = useLocation().search.split("=")[1];
  const [nameAtribut, setNameAtribut] = useState<string>("");
  const [name, setName] = useState<string>("");
  const [descr, setDescr] = useState<string>("");
  const [dateStart, setDateStart] = useState<number>();
  const [dateEnd, setDateEnd] = useState<number>();

  const [file, setFile] = useState<File>();
  const [preview, setPreview] = useState<File>();
  const [photoUrl, setPhotoUrl] = useState<Phototype>();
  const [photoUUID, setPhotoUUID] = useState<string>()
  const [previewUrl, setPreviewUrl] = useState<Phototype>();
  const [item, setItem] = useState<PersonalOffersType>();
  const [loading, setLoading] = useState(false);
  const [currentId, setCurrentId] = useState<number>(id ? +id : 0);


  const [sendFile, result] = useUploadFileMutation()
  /**
   * function for disable date after end date
   * @param date - Moment from datePicker
   * @returns boolean
   */
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
   * function for disable date before start date
   * @param date - Moment from datePicker
   * @returns boolean
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
   * get one offer by id
   */
  const getOneOfferById = useCallback(async () => {
    try {
      const response = await PersonalOffersService.getPersonalOffersById(
        Number(id)
      );

      setName(response.title);
      setDateEnd(response.end);
      setDateStart(response.begin);
      setDescr(response.description);
      setNameAtribut(response.attribute)
      setPhotoUrl(response?.photo)
      setPreviewUrl(response?.preview)
      setLoading(false);
    } catch (error) {
      message.error(t("errors.get_data"));
      setLoading(false);
    }
  }, [id, t]);

  /**
   * create new offer with draft: true
   */
  const createPersonalOffer = async () => {
    try {
      const sendData: CreatePersonalOffersType = { draft: true };
      const response = await PersonalOffersService.createPersonalOffers(
        sendData
      );
      setCurrentId(response.id);
    } catch (error) {
      message.error(t("errors.save_data"));
    }
  };

  useEffect(() => {
    if (!!id) {
      getOneOfferById().then();
    } else {
      createPersonalOffer().then();
    }
  }, [id]);

  /**
   * 
   * save offer
   */
  const onHandleSave = async () => {
    const data: CreatePersonalOffersType = {
      attribute: nameAtribut,
      title: name,
      draft: false,
      description: descr,
      begin: dateStart,
      end: dateEnd
    };
    if (currentId) {
      try {
        const redact = await PersonalOffersService.updatePersonalOffers(
          currentId,
          data
        );
      } catch (error) {
        message.error(t("errors.save_data"));
        return
      }
    } else {
      try {
        const save = await PersonalOffersService.createPersonalOffers(data);
      } catch (error) {
        message.error(t("errors.save_data"));
        return
      }
    }
    if (file) {
      await uploadFile(file, ImageType.PHOTO)
    }
    if (preview) {
      await uploadFile(preview, ImageType.PREVIEW)
    }
    navigate(Routes.PERSONAL_OFFERS)
    setName("");
    setDescr("");
    setNameAtribut("");
  };


  /**
* upload image
*/
  const uploadFile = useCallback(async (image: File, type: ImageType) => {

    const sendData: UploadFileDocType = {
      source: FileSource.REQUEST,
      'source-parameters': JSON.stringify({}),
      entities: [{ field: type === ImageType.PHOTO ?
         EntitiesFieldName.PERSONAL_OFFER_PHOTO :
         EntitiesFieldName.PERSONAL_OFFER_PREVIEW, documentId: currentId }],
      file: image
    }
   await sendFile(sendData)
  }, [currentId, sendFile])


  return (
    <MainLayout title={t("screen_title.personal_offers")}>
      <EditWrapper
        title={t(!id ? "common.add" : "common.edit_full")}
        onSave={onHandleSave}
      >
        <Row gutter={[16, 16]}>
          <Col>
            <InputHolder>
              <TextField
                label={t("forms.name_atribut")}
                value={nameAtribut}
                onChange={setNameAtribut}
              />
            </InputHolder>
            <InputHolder>
              <TextField
                label={t("forms.name")}
                value={name}
                onChange={setName}
              />
            </InputHolder>

            {/* <Label>{t("common.date_start_end")}</Label> */}

            {/* <InputHolder>
              <div className={styles.blockDate}>
                <div className={styles.blockDateOne}>
                  <DatePickerComponent
                    onDisableDate={onHandleDisebleStartDate}
                    value={dateStart}
                    onChange={setDateStart}
                    placeholder="c"
                  />
                </div>

                <div className={styles.blockDateTwo}>
                  <DatePickerComponent
                    onDisableDate={onHandleDisebleEndDate}
                    value={dateEnd}
                    onChange={setDateEnd}
                    placeholder="до"
                  />
                </div>
              </div>
            </InputHolder> */}
          </Col>
          <Col>
            <InputHolder>
              <TextAria
                height={95}
                maxRows={3}
                classes={{
                  input: styles.textArea
                }}
                label={t("forms.desc")}
                value={descr}
                onChange={setDescr}
              />
            </InputHolder>
            <InputHolder>
              <div className={styles.blockLoad}>
                <PhotoLoader
                  size={120}
                  label={t("forms.upload_image")}
                  image={photoUrl}
                  onChange={setFile}
                  onDelete={() => setFile(undefined)}
                />
                <PhotoLoader
                  size={120}
                  label={t("forms.upload_preview")}
                  image={previewUrl}
                  onChange={setPreview}
                  onDelete={() => setFile(undefined)}
                />
              </div>
            </InputHolder>
          </Col>
        </Row>
      </EditWrapper>
    </MainLayout>
  );
};
export default PersonalOffersRedact;
