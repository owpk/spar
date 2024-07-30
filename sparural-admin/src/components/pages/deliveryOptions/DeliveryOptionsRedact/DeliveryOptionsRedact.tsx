import { FC, useCallback, useEffect, useState } from "react";
import { MainLayout } from "../../../complexes/MainLayout";
import { message } from "antd";
import { PhotoLoader } from "../../../simples/PhotoLoader";
import { TextField } from "../../../simples/TextField";
import { EditWrapper } from "../../../simples/EditWrapper";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import style from "./DeliveryOptionsRedact.module.scss";
import { Button } from "../../../simples/Button";

import { InputHolder } from "../../../simples/InputHolder";
import { InputRadio } from "../../../simples/InputRadio";


import { EntitiesFieldName, FileSource, Routes } from "../../../../config";
import { DeliveryCreateType, Phototype } from "../../../../types";
import { DeliveryOptionsServices } from "../../../../services/DeliveryOptionsServices";
import { isValidUrl } from "../../../../utils/helpers";
import { UploadFileDocType, useUploadFileMutation } from "../../../../services/FileService";

enum ErrorFields {
  TITLE = 'title',
  DESCRIPSHON = 'shortDescription',
  URL = 'url'
}

type ErrorType = {
  [key in ErrorFields]?: string
}

const DeliveryOptionsRedact: FC = () => {
  const { t } = useTranslation();
  const id = useLocation().search.split("=")[1];

  const navigate = useNavigate();


  const [header, setheader] = useState<string>("");
  const [shortDescr, setshortDescr] = useState<string>("");
  const [link, setlink] = useState<string>("");
  const [file, setFile] = useState<File>();
  const [photoUrl, setPhotoUrl] = useState<Phototype>();
  const [isCheck, setisCheck] = useState<boolean>(false);

  const [errors, setErrors] = useState<ErrorType>({})

  const [sendFile, { error }] = useUploadFileMutation()

  const getCardId = useCallback(async (id: number) => {
    const response = await DeliveryOptionsServices.getDeliveryById(id);
    setlink(response.url || '')
    setshortDescr(response.shortDescription)
    setheader(response.title)
    setPhotoUrl(response?.photo || undefined)
    setisCheck(!!response.url)
  }, []);

  useEffect(() => {
    if (id) {
      getCardId(+id).then()
    }
  }, [id]);


  const saveBtn = useCallback(async () => {
    const data: DeliveryCreateType = {
      title: header,
      shortDescription: shortDescr,
      url: link,
      isPublic: isCheck,
      draft: false
    };



    if (!isValidUrl(link) && isCheck) {
      message.error(t("errors.wrong_link"));
      setErrors({ ...errors, [ErrorFields.URL]: (t("errors.wrong_link")) })
      return;
    }

    if (!id) {
      const response = await DeliveryOptionsServices.createBannerPlace(data);
      if (file) {
        await uploadFile(response.id, file)
      }

      // dispatch(updateDelivery(data));
    } else {
      const response = await DeliveryOptionsServices.updateDeliveryPlace(+id, data);
      if (file) {
         await uploadFile(response.id, file)
      }
    }
    navigate(Routes.DELIVERY_OPTIONS);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [errors, file, header, id, isCheck, link, navigate, shortDescr, t]);

  const headerWrite = (text: string) => {
    setErrors(prev => ({ ...prev, [ErrorFields.TITLE]: '' }))
    if (header.length > 100) {
      message.error(t("errors.max_header"));
      setErrors({ ...errors, [ErrorFields.TITLE]: (t("errors.max_header")) })
      setheader(text.slice(0, 100));
    } else {
      setheader(text);
    }

  };
  const shortDescrWrite = (text: string) => {
    setErrors(prev => ({ ...prev, [ErrorFields.DESCRIPSHON]: '' }))
    if (shortDescr.length > 255) {
      message.error(t("errors.max_descr"));
      setErrors({ ...errors, [ErrorFields.DESCRIPSHON]: (t("errors.max_descr")) })
      setshortDescr(text.slice(0, 255));
    } else {
      setshortDescr(text);
    }

  };

  const linkWrite = (text: string) => {
    setErrors(prev => ({ ...prev, [ErrorFields.URL]: '' }))
    if (link.length > 255) {
      message.error(t("errors.max_link"));
      setErrors({ ...errors, [ErrorFields.URL]: (t("errors.max_link")) })
      setlink(text.slice(0, 255));
    } else {
      setlink(text);
    }

  };

  /**
  * upload image
  */
  const uploadFile = useCallback(async (id: number, image: File) => {
    // const sendData = createFormDataFile(image)
    // const response = await DeliveryOptionsServices.uploadPhoto(+id, sendData)

    const sendData: UploadFileDocType = {
      source: FileSource.REQUEST,
      'source-parameters': JSON.stringify({}),
      entities: [{ field: EntitiesFieldName.DELIVERY_PHOTO, documentId: id }],
      file: image
    }
    await sendFile(sendData)

  }, [sendFile])

  useEffect(() => {
    if (error) {
      message.warning(t("errors.upload_photo"))
    }
  }, [error, t])


  return (
    <div>
      <MainLayout title={t("screen_title.deliveryOptions")}>
        <EditWrapper title={t(!id ? "common.add" : "common.edit_full")}>
          <>
            <div className={style.wrapperEdit}>
              <div className={style.wrapper_input}>
                <div className={style.header_item}>
                  <TextField
                    error={errors.title}
                    label={t("forms.header")}
                    onChange={headerWrite}
                    value={header}
                  />
                </div>

                <div className={style.shortDescr_item}>
                  <TextField
                    error={errors.shortDescription}
                    label={t("forms.short_descrption")}
                    onChange={shortDescrWrite}
                    value={shortDescr}
                  />
                </div>

                <div className={style.radio_item}>
                  <>
                    <div className={style.redio_span}>
                      <InputHolder>
                        <>{t("forms.show_button_partner")}</>
                      </InputHolder>
                    </div>
                    <div className={style.radio_items}>
                      <InputRadio
                        size={26}
                        isChecked={true === isCheck}
                        onChange={() => setisCheck(true)}
                        labelPosition={"left"}
                      >
                        Да
                      </InputRadio>
                      <InputRadio
                        size={26}
                        isChecked={false === isCheck}
                        onChange={() => {setisCheck(false); linkWrite('')}}
                        labelPosition={"left"}
                      >
                        Нет
                      </InputRadio>
                    </div>
                  </>
                </div>

                <div className={style.link_item}>
                  <TextField
                    disabled={!isCheck}
                    error={errors.url}
                    label={t("forms.link")}
                    onChange={linkWrite}
                    value={link}
                  />
                </div>
              </div>

              <div>
                {/* <InputHolder> */}
                <PhotoLoader
                  size={180}
                  label={t("forms.upload_image")}
                  image={photoUrl}
                  onChange={setFile}
                  onDelete={() => setFile(undefined)}
                />
                {/* </InputHolder> */}
              </div>
            </div>

            <div className={style.btn_save}>
              <Button
                onClick={saveBtn}
                label={"Сохранить"}
                textUp={"capitalize"}
                colorText={"#ffffff"}
                backgroundColor={"#007C45"}
              />
            </div>
          </>
        </EditWrapper>
      </MainLayout>
    </div>
  );
};

export default DeliveryOptionsRedact;
