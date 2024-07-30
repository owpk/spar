import { message } from "antd";
import { FC, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import { EntitiesFieldName, FileSource, Routes } from "../../../../config";
import { FavoriteCategoriesService } from "../../../../services/FavoriteCategoriesService";
import { UploadFileDocType, useUploadFileMutation } from "../../../../services/FileService";
import { Phototype } from "../../../../types";
import { MainLayout } from "../../../complexes/MainLayout";

import { EditWrapper } from "../../../simples/EditWrapper";
import { PhotoLoader } from "../../../simples/PhotoLoader";
import { TextField } from "../../../simples/TextField";
import styles from "./FavoriteCategoriesRedact.module.scss";

type Props = {};

const FavoriteCategoriesRedact: FC<Props> = () => {
  const { t } = useTranslation();
  const navigate = useNavigate()
  const id = useLocation().search.split("=")[1];
  const [name, setName] = useState<string>("");
  const [photoUrl, setPhotoUrl] = useState<Phototype>();
  const [file, setFile] = useState<File>();
  const [loading, setLoading] = useState(false);
  const [sendFile, { error }] = useUploadFileMutation()


  const load = async (id: number) => {
    setLoading(true);
    try {
      const result = await FavoriteCategoriesService.getFavoriteCategoriesById(
        id
      );
      setName(result.name);
      setPhotoUrl(result.photo || undefined);
    } catch (error) {
      message.error(t("errors.get_data"));
    }
    setLoading(false);
  };
  useEffect(() => {
    if (id !== undefined) {
      load(+id).then();
    }
  }, [id]);
  /**
   * 
   * @param id save category
   */
  const onHandleSave = async (id: number) => {
    if (file) {
      await uploadFile(file)
    }

    navigate(Routes.FAVORITE_CATEGORIES)
  };

  /**
* upload image
*/
  const uploadFile = useCallback(async (image: File) => {
    const sendData: UploadFileDocType = {
      source: FileSource.REQUEST,
      'source-parameters': JSON.stringify({}),
      entities: [{ field: EntitiesFieldName.FAVORITE_CATEGORY_PHOTO, documentId: +id }],
      file: image
    }
    await sendFile(sendData)

  }, [id, sendFile])

  useEffect(() => {
    if (error) {
      message.warning(t("errors.upload_photo"))
    }
  }, [error, t])
  return (
    <MainLayout title={t("screen_title.favorite_categories")}>
      <EditWrapper
        title={t(!id ? "common.add" : "common.edit_full")}
        onSave={() => onHandleSave(+id)}
      >
        <div className={styles.block}>
          <div className={styles.blockOne}>
            <div className={styles.blockInput}>
              <TextField
                disabled
                value={name}
                onChange={setName}
                label={t("forms.name_categories")}
              />
            </div>

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
export default FavoriteCategoriesRedact;
