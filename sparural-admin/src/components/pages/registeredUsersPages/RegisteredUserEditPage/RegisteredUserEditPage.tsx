import { Col, message, Row } from "antd";
import React, { FC, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import { EntitiesFieldName, FileSource, Routes } from "../../../../config";
import { useAppDispatch } from "../../../../hooks/store";
import { UploadFileDocType, useUploadFileMutation } from "../../../../services/FileService";
import { UsersService } from "../../../../services/UsersService";
import { setLoading } from "../../../../store/slices/appSlice";
import {
  CreateUserType,
  DeleteInfoType,
  GenderType,
  Phototype,
  RoleCode
} from "../../../../types";
import { Image } from "../../../complexes/Image";
import { MainLayout } from "../../../complexes/MainLayout";
import { DatePickerComponent } from "../../../simples/DatePickerComponent";
import { EditWrapper } from "../../../simples/EditWrapper";
import { InputHolder } from "../../../simples/InputHolder";
import { InputRadio } from "../../../simples/InputRadio";
import { PhotoLoader } from "../../../simples/PhotoLoader";
import { TextField } from "../../../simples/TextField";
import styles from "./registeredUsersEditPage.module.scss";

const RegisteredUserEditPage: FC = () => {
  const { t } = useTranslation();

  const dispatch = useAppDispatch();
  const userId = useLocation().search.split("=")[1];
  const navigation = useNavigate();

  const [lastName, setLastName] = useState<string>("");
  const [firstName, setFirstName] = useState<string>("");
  const [phoneNumber, setPhoneNumber] = useState<string>("");
  const [patronymicName, setPatronymicName] = useState<string>("");
  const [email, setEmail] = useState<string>("");

  const [deleteInfo, setDeleteInfo] = useState<DeleteInfoType | null>(null);

  const [file, setFile] = useState<File>();
  const [photo, setPhoto] = useState<Phototype>();
  const [gender, setGender] = useState<GenderType>();
  const [birthday, setBirthday] = useState<number>();
  const [errors, setErrors] = useState("");

  const [sendFile, { error, isLoading }] = useUploadFileMutation()

  const currentId = useRef<number>(0);

  /**
   * fetch one screen
   */
  const getOneScreenById = useCallback(async () => {
    try {
      dispatch(setLoading(true));
      const response = await UsersService.getUserById(Number(userId));

      setLastName(response.lastName || "");
      setFirstName(response.firstName || "");
      setPatronymicName(response.patronymicName || "");
      setPhoneNumber(response.phoneNumber || "");
      setEmail(response.email || "");
      setPhoto(response.photo || undefined);
      setDeleteInfo(response.deleteInfo);
      setGender(response.gender as GenderType);
      // its need because we getting time in seconds, but we have to use milliseconds
      setBirthday(
        response.birthday < 1000000000
          ? response.birthday * 1000
          : response.birthday
      );
      currentId.current = Number(userId);
      dispatch(setLoading(false));
    } catch (error) {
      message.error(t("errors.get_data"));
      dispatch(setLoading(false));
    }
  }, [dispatch, t, userId]);

  /**
   * creating draft
   */
  const createDraft = async () => {
    try {
      const response = await UsersService.createUser({ draft: true });
      currentId.current = response.id;
    } catch (error) { }
  };

  /**
* upload image
*/
  const uploadFile = useCallback(async (image: File) => {
    const sendData: UploadFileDocType = {
      source: FileSource.REQUEST,
      'source-parameters': JSON.stringify({}),
      entities: [{ field: EntitiesFieldName.USER_PHOTO, documentId: currentId.current }],
      file: image
    }
    await sendFile(sendData)

  }, [currentId, sendFile])

  /**
   * save function
   */
  const onHandleSave = useCallback(async () => {
    const sendData: CreateUserType = {
      firstName,
      lastName,
      patronymicName,
      phoneNumber,
      email,
      draft: false,
      birthday,
      gender
    };
    if (sendData.gender === undefined) {
      return setErrors("Выберите пол");
    }

    try {
      const response = await UsersService.updateUser(
        Number(currentId.current),
        sendData
      );
      message.success(t("success.update_data"));
      if (file) {
        uploadFile(file)
      }
      navigation(Routes.REGISTRED_USERS_SCREEN);
    } catch (error) {
      message.error(t("errors.update_data"));
    }
  }, [firstName, lastName, patronymicName, phoneNumber, email, birthday, gender, t, file, navigation, uploadFile]);



  /**
   * fetching data if we edit Info screen
   */
  useEffect(() => {
    if (!!userId) {
      getOneScreenById().then();
    } else {
      createDraft().then();
    }
  }, [userId]);

  // changin gender
  const onChangeGender = (sex: GenderType) => {
    if (gender === sex) {
      setGender(undefined);
    } else {
      setGender(sex);
    }
  };
  return (
    <MainLayout title={t("screen_title.registered_users_page")}>
      <EditWrapper
        onSave={onHandleSave}
        title={t(!userId ? "common.add" : "common.edit_full")}
      >
        <>
          <Row gutter={[16, 16]}>
            <Col>
              <InputHolder>
                <TextField
                  label={t("forms.last_name")}
                  onChange={setLastName}
                  value={lastName}
                  readOnly={!!userId ? true : false}
                />
              </InputHolder>
              <InputHolder>
                <TextField
                  label={t("forms.first_name")}
                  onChange={setFirstName}
                  value={firstName}
                  readOnly={!!userId ? true : false}
                />
              </InputHolder>
              <InputHolder>
                <TextField
                  label={t("forms.patronymic")}
                  onChange={setPatronymicName}
                  value={patronymicName}
                  readOnly={!!userId ? true : false}
                />
              </InputHolder>
              <InputHolder alert={!!errors && true}>
                <>
                  <InputHolder>
                    <div className={styles.label}>{t("forms.gender")}</div>
                  </InputHolder>
                  <div className={styles.checkedGender}>
                    <Col
                      style={{
                        display: "flex",
                        alignItems: "center"
                      }}
                      span={8}
                    >
                      <span className={styles.sexLabel}>
                        {t("common.male")}
                      </span>
                      <InputRadio
                        size={26}
                        isChecked={gender === GenderType.MAIL}
                        onChange={() => {
                          !userId && onChangeGender(GenderType.MAIL);
                        }}
                      />
                      {!!errors && <div className={styles.error}>{errors}</div>}
                    </Col>
                    <Col
                      style={{
                        display: "flex",
                        alignItems: "center"
                      }}
                      span={8}
                    >
                      <span className={styles.sexLabel}>
                        {t("common.female")}
                      </span>
                      <InputRadio
                        size={26}
                        isChecked={gender === GenderType.FEMALE}
                        onChange={() => {
                          !userId && onChangeGender(GenderType.FEMALE);
                        }}
                      />
                    </Col>
                  </div>
                </>
              </InputHolder>
              <InputHolder>
                <Row>
                  <Col span={16}>
                    <DatePickerComponent
                      label={t("common.birthday_date")}
                      placeholder={t("common.choose_date")}
                      onChange={setBirthday}
                      value={birthday}
                      disabled={!!userId ? true : false}
                    />
                  </Col>
                </Row>
              </InputHolder>
            </Col>
            <Col span={8}>
              <InputHolder>
                <TextField
                  label={t("forms.phone_number")}
                  onChange={setPhoneNumber}
                  value={phoneNumber}
                  readOnly={!!userId ? true : false}
                />
              </InputHolder>
              <InputHolder>
                <TextField
                  label={t("forms.email_address")}
                  onChange={setEmail}
                  value={email}
                  readOnly={!!userId ? true : false}
                />
              </InputHolder>
              {/* <InputHolder>
                                <Selector
                                    label={t("forms.role")}
                                    options={_userRoles}
                                    onChange={(role) => setRoles([role])}
                                    value={roles}
                                />
                            </InputHolder> */}
              <InputHolder>

                {photo?.uuid ?
                  <Image size={170} photo={photo.uuid} /> :
                  <div className={styles.photoWrapper}>
                    <span className={styles.notAvatar}>{t("forms.not_avatar")}</span>
                  </div>}

                {/* <PhotoLoader
                  image={photo}
                  onChange={setFile}
                  onDelete={() => setFile(undefined)}
                /> */}
              </InputHolder>
            </Col>
            <Col span={8}></Col>
          </Row>
        </>
      </EditWrapper>
    </MainLayout>
  );
};

export default RegisteredUserEditPage;
