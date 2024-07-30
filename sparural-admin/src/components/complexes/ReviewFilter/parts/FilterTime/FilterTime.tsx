import classNames from "classnames";
import React, { FC, useCallback, useContext, useMemo, useRef, useState } from "react";

import styles from "./FilterTime.module.scss";

import { Button } from "../../../../simples/Button";
import { useTranslation } from "react-i18next";
import moment from "moment";
import { Moment } from "moment";
import { ReactComponent as Chevron } from "../../../../../assets/icons/chevron.svg";
import { ReactComponent as Star } from "../../../../../assets/icons/star_icon.svg";
import { ReactComponent as StarFill } from "../../../../../assets/icons/star_icon_fill.svg";
import { Col, Row } from "antd";
import { InputRadio } from "../../../../simples/InputRadio";
import { DatePickerComponent } from "../../../../simples/DatePickerComponent";
import { InputHolder } from "../../../../simples/InputHolder";
import { ButtonType } from "../../../../simples/Button/Button";
import { wrapper } from "../../../../simples/FilterTopRight/FilterTopRight";
import { printDate } from "../../../../../utils/helpers";
import { FilterContext } from "../contexts";


type Props = {
};

export type FilterTimeType = {};

const FilterTime: FC<Props> = () => {
  const { t } = useTranslation();

  const [filter, setFilter] = useContext(FilterContext)

  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [dateTimeStart, setDateTimeStart] = useState<number>();
  const [dateTimeEnd, setDateTimeEnd] = useState<number>();

  const dataBlock = useRef<HTMLDivElement>(null);


  const labelInner = useMemo((): string => {
    let label: string = t("filter.time_interval")
    if(dateTimeStart || dateTimeEnd){
      label = `${dateTimeStart ? printDate(dateTimeStart) : ''} - ${dateTimeEnd ? printDate(dateTimeEnd) : ''}`
    }
    return label
  },[dateTimeEnd, dateTimeStart, t])

  const onToggleOpen = () => {
    setIsOpen(!isOpen);
  };


  const onHandleDisebleStartDate = (date?: Moment) => {
    let disable = false;
    if (dateTimeEnd && date) {
      if (moment(date).valueOf() > dateTimeEnd) {
        disable = true;
      } else {
        disable = false;
      }
    }
    return disable;
  };

  const onHandleDisebleEndDate = (date?: Moment) => {
    let disable = false;
    if (dateTimeStart && date) {
      if (moment(date).valueOf() < dateTimeStart) {
        disable = true;
      } else {
        disable = false;
      }
    }
    return disable;
  };

  const context = useContext(wrapper);

  context.time.from = 2;

const getStartDay = (date: number) => {
  return Math.round(Number(moment(date).startOf('day'))/1000)
}
const getEndDay = (date: number) => {
  return Math.round(Number(moment(date).endOf('day'))/1000)
}
  const changeFilter = useCallback(() => {
    setFilter(prev => ({...prev, dateTimeEnd: dateTimeEnd ? getEndDay(dateTimeEnd) : undefined, dateTimeStart: dateTimeStart ?getStartDay( dateTimeStart) : undefined }))
    setIsOpen(false)
  },[dateTimeEnd, dateTimeStart, setFilter])


  return (
    <div className={styles.root}>
      <div
        className={classNames(styles.wrapper, {
          [styles.opened]: isOpen
        })}
      >
        <div onClick={onToggleOpen} className={styles.top}>
          <div className={classNames(styles.topLabel, {})}>
            <span className={styles.nameFilter}>
              {labelInner}
            </span>
          </div>
          <div
            className={classNames({
              [styles.openIcon]: isOpen
            })}
          >
            <Chevron className={styles.chevron} />
          </div>
        </div>

        {isOpen && (
          <>
            <div className={styles.openBlock}>

                <div className={styles.blockDate}>
                  <div className={styles.blockDateOne}>
                    <DatePickerComponent
                      onDisableDate={onHandleDisebleStartDate}
                      value={dateTimeStart}
                      onChange={setDateTimeStart}
                      placeholder="ДД.ММ.ГГГГ"
                    />
                  </div>

                  <div className={styles.blockDateTwo}>
                    <DatePickerComponent
                      onDisableDate={onHandleDisebleEndDate}
                      value={dateTimeEnd}
                      onChange={setDateTimeEnd}
                      placeholder="ДД.ММ.ГГГГ"
                    />
                  </div>
                </div>
    
              <div>
                <Button
                  typeStyle={ButtonType.SECOND}
                  onClick={changeFilter}
                  backgroundColor={"#ffffff"}
                  colorText={"#007C45"}
                  label={t("common.apply")}
                  textUp={"uppercase"}
                />
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};
export default FilterTime;
