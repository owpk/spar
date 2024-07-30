import { message } from "antd";
import React, { FC, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { SettingsService } from "../../../../../services/SettingsPagesService";
import { SettingsPaymentType } from "../../../../../types";
import { BlockWrapper } from "../../../../simples/BlockWrapper";
import { Button } from "../../../../simples/Button";
import { ButtonType } from "../../../../simples/Button/Button";
import { TextField } from "../../../../simples/TextField";
import styles from "./TabPayment.module.scss";

type Props = {
  data: SettingsPaymentType;
};

const TabPayment: FC<Props> = ({ data }) => {
  const { t } = useTranslation();
  const [value, setValue] = useState<string>(data.tinkoffMerchantId);

  useEffect(() => {
    setValue(data.tinkoffMerchantId)
  },[data.tinkoffMerchantId])

  const onClickSave = async () => {
    const data: SettingsPaymentType = {
      tinkoffMerchantId: value
    };
    try {
      await SettingsService.upddateSettingsPayment(data);
      message.success(t("suсcess_messages.save_data"));
    } catch {
      message.error(t("errors.update_data"));
    }
  };
  return (
    <div>
      <BlockWrapper>
        <div className={styles.block}>
          <TextField
            label="Tinkoff merchant ID"
            value={value}
            onChange={setValue}
          />
        </div>
      </BlockWrapper>
      <div className={styles.btn}>
        <Button
          onClick={onClickSave}
          label={"Сохранить"}
          textUp={"capitalize"}
          typeStyle={ButtonType.SECOND}
        />
      </div>
    </div>
  );
};
export default TabPayment;
