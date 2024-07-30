import { message } from "antd";
import React, { FC, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import { EntitiesFieldName, FileSource, Routes } from "../../../../config";
import { UploadFileDocType, useUploadFileMutation } from "../../../../services/FileService";
import { PersonalProductsService } from "../../../../services/PersonalProductsService";
import {
  CreatePersonalProductsType,
  PersonalProductsType,
  Phototype
} from "../../../../types";
import { MainLayout } from "../../../complexes/MainLayout";
import { EditWrapper } from "../../../simples/EditWrapper";
import { InputHolder } from "../../../simples/InputHolder";
import { PhotoLoader } from "../../../simples/PhotoLoader";
import { TextAria } from "../../../simples/TextAria";
import { TextField } from "../../../simples/TextField";
import styles from "./PersonalProductsRedact.module.scss";

type Props = {};

enum ImageType {
  PREVIEW = 'preview',
  PHOTO = 'photo'
}

const PersonalProductsRedact: FC<Props> = () => {
  const { t } = useTranslation();
  const navigation = useNavigate();
  const id = useLocation().search.split("=")[1];
  const [identificator, setIdentificator] = useState<string>("");
  const [name, setName] = useState<string>("");
  const [descr, setDescr] = useState<string>("");
  const [file, setFile] = useState<File>();
  const [preview, setPreview] = useState<File>();
  const [photoUrl, setPhotoUrl] = useState<Phototype>();
  const [previewUrl, setPreviewUrl] = useState<Phototype>();
  const [item, setItem] = useState<PersonalProductsType>();
  const [loading, setLoading] = useState(false);
  const currentId = useRef<number>(id ? +id : 0);

  
  const [sendFile, result] = useUploadFileMutation()
  
  const getOneScreenById = useCallback(async () => {
    try {
      const response = await PersonalProductsService.getPersonalProductsById(
        Number(id)
      );
      setName(response.name || name);
      setDescr(response.description);
      setIdentificator(response.goodsId);
      setPhotoUrl(response?.photo)
      setPreviewUrl(response?.preview)

      setLoading(false);
    } catch (error) {
      message.error(t("errors.get_data"));
      setLoading(false);
    }
  }, [id, t]);

  const createInfoScreen = async () => {
    try {
      // cities временно
      const sendData: CreatePersonalProductsType = { draft: true, name: 'draft', goodsId: '0' };
      const response = await PersonalProductsService.createPersonalProducts(
        sendData
      );
      currentId.current = response.id;
    } catch (error) {
      message.error(t("errors.save_data"));
    }
  };

  const onHandleSave = async () => {
    const data: CreatePersonalProductsType = {
      name: name,
      goodsId: identificator,
      draft: false,
      description: descr
    };
    if (currentId.current) {
      try {
        const redact = await PersonalProductsService.updatePersonalProducts(
          currentId.current,
          data
        );
      } catch (error) {
        message.error(t("errors.save_data"));
      }
    } else {
      try {
        const save = await PersonalProductsService.createPersonalProducts(data);
      } catch (error) {
        message.error(t("errors.save_data"));
      }
    }

    if (file) {
      await uploadFile(file, ImageType.PHOTO)
    }
    if (preview) {
      await uploadFile(preview, ImageType.PREVIEW)
    }

    setName("");
    setDescr("");
    setIdentificator("");

    navigation(Routes.PERSONAL_PRODUCTS)
  };

  useEffect(() => {
    if (!!id) {
      getOneScreenById().then();
    } else {
      createInfoScreen().then();
    }
  }, [id]);


   /**
* upload image
*/
const uploadFile = useCallback(async (image: File, type: ImageType) => {

  const sendData: UploadFileDocType = {
    source: FileSource.REQUEST,
    'source-parameters': JSON.stringify({}),
    entities: [{ field: type === ImageType.PHOTO ?
       EntitiesFieldName.GOODS_PHOTO :
       EntitiesFieldName.GOODS_PREVIEW, documentId: currentId.current }],
    file: image
  }
  await sendFile(sendData)
}, [currentId, sendFile])


  return (
    <MainLayout title={t("screen_title.personal_products")}>
      <EditWrapper
        title={t(!id ? "common.add" : "common.edit_full")}
        onSave={onHandleSave}
      >
        <div className={styles.block}>
          <div className={styles.blockText}>
            <TextField
              value={identificator}
              onChange={setIdentificator}
              label={t("forms.identification_products")}
            />
            <InputHolder />
            <TextField
              value={name}
              onChange={setName}
              label={t("forms.name")}
            />
            <InputHolder />
            <TextAria
              value={descr}
              onChange={setDescr}
              label={t("forms.desc")}
              height={140}
            />
          </div>
          <div className={styles.blockLoad}>
            <PhotoLoader
              label={t("forms.upload_image")}
              image={photoUrl}
              onChange={setFile}
              onDelete={() => setFile(undefined)}
            />
            <PhotoLoader
              label={t("forms.upload_preview")}
              image={previewUrl}
              onChange={setPreview}
              onDelete={() => setFile(undefined)}
            />
          </div>
        </div>
      </EditWrapper>
    </MainLayout>
  );
};
export default PersonalProductsRedact;
