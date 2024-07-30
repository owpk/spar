import classNames from "classnames";
import React, { FC, useCallback, useRef, useState } from "react";

import { ReactComponent as Chevron } from "../../../assets/icons/chevron.svg";
import styles from "./Filter.module.scss";

import { Button } from "../../simples/Button";
import { ButtonType } from "../../simples/Button/Button";
import { useTranslation } from "react-i18next";
import OptionsFilter, { FilterOption } from "./OptionsFilter";
import memoize from "memoize-one";

type Props = {
  onFilter?: (filter: FilterType) => void;
};

export type FilterType = {
  full_name?: FilterOption.FULL_NAME_ASC | FilterOption.FULL_NAME_DESC;
  rate?: FilterOption.RATE_ASC | FilterOption.RATE_DESC;
  isDate?: FilterOption.DATE;
  isShop?: FilterOption.SHOP;
  date?: {
    start?: number;
    end?: number;
  };
  shop?: string;
};

const Filter: FC<Props> = ({ onFilter }) => {
  const { t } = useTranslation();
  const mainBlock = useRef<HTMLDivElement>(null);
  const [openStatus, setOpenStatus] = useState(false);

  const [currentFilter, setCurrentFilter] = useState<FilterType>(
    {} as FilterType
  );

  const filterOptions = memoize(() => {
    return Object.keys(FilterOption).map((key: string) => {
      let option: any = FilterOption;
      return {
        value: option[key],
        label: t(`filter.${option[key]}`),
        type: option[key]
      };
    });
  });

  const onToggleOpen = useCallback(() => {
    setOpenStatus(!openStatus);
  }, [openStatus]);

  const onSetCheckbox = useCallback(
    (checkbox: FilterOption) => {
      switch (checkbox) {
        case FilterOption.FULL_NAME_ASC:
          if (currentFilter.full_name === FilterOption.FULL_NAME_ASC) {
            setCurrentFilter((prev) => ({ ...prev, full_name: undefined }));
          } else {
            setCurrentFilter((prev) => ({
              ...prev,
              full_name: FilterOption.FULL_NAME_ASC
            }));
          }
          break;
        case FilterOption.FULL_NAME_DESC:
          if (currentFilter.full_name === FilterOption.FULL_NAME_DESC) {
            setCurrentFilter((prev) => ({ ...prev, full_name: undefined }));
          } else {
            setCurrentFilter((prev) => ({
              ...prev,
              full_name: FilterOption.FULL_NAME_DESC
            }));
          }
          break;
        case FilterOption.RATE_ASC:
          if (currentFilter.rate === FilterOption.RATE_ASC) {
            setCurrentFilter((prev) => ({ ...prev, rate: undefined }));
          } else {
            setCurrentFilter((prev) => ({
              ...prev,
              rate: FilterOption.RATE_ASC
            }));
          }
          break;
        case FilterOption.RATE_DESC:
          if (currentFilter.rate === FilterOption.RATE_DESC) {
            setCurrentFilter((prev) => ({ ...prev, rate: undefined }));
          } else {
            setCurrentFilter((prev) => ({
              ...prev,
              rate: FilterOption.RATE_DESC
            }));
          }
          break;
        case FilterOption.DATE:
          if (currentFilter.isDate === FilterOption.DATE) {
            setCurrentFilter((prev) => ({ ...prev, isDate: undefined }));
          } else {
            setCurrentFilter((prev) => ({
              ...prev,
              isDate: FilterOption.DATE
            }));
          }
          break;
        case FilterOption.SHOP:
          if (currentFilter.isShop === FilterOption.SHOP) {
            setCurrentFilter((prev) => ({ ...prev, isShop: undefined }));
          } else {
            setCurrentFilter((prev) => ({
              ...prev,
              isShop: FilterOption.SHOP
            }));
          }
          break;

        default:
          break;
      }
      // setActiveCheckbox(checkbox)
    },
    [
      currentFilter.full_name,
      currentFilter.isDate,
      currentFilter.isShop,
      currentFilter.rate
    ]
  );

  const onApplyFilter = useCallback(() => {
    if (onFilter) {
      onFilter(currentFilter);
    }
  }, [currentFilter, onFilter]);

  const filt = Object.values(currentFilter);

  return (
    <div ref={mainBlock} className={styles.root}>
      <div
        className={classNames(styles.wrapper, {
          [styles.opened]: openStatus
        })}
        tabIndex={0}
      >
        <div onClick={onToggleOpen} className={styles.top}>
          <div className={classNames(styles.topLabel, {})}>
            {t("forms.filter")}
          </div>
          <div
            className={classNames({
              [styles.openIcon]: openStatus
            })}
          >
            <Chevron className={styles.chevron} />
          </div>
        </div>
        {openStatus &&
          filterOptions().map((item) => {
            return (
              <OptionsFilter
                key={item.value}
                option={item}
                isChecked={filt.includes(item.value)}
                setActiveCheckbox={onSetCheckbox}
                onChangeDate={(date) =>
                  setCurrentFilter({ ...currentFilter, date })
                }
                onChangeShop={(shop) =>
                  setCurrentFilter({ ...currentFilter, shop })
                }
              />
            );
          })}
        {/* {openStatus &&
                    data.map((options, idx) => (
                        <OptionsFilter
                            key={idx}
                            title={options.title}
                            type={options.type}
                            activeCheckbox={activeCheckbox}
                            setActiveCheckbox={onSetCheckbox}
                        />
                    ))}
                    */}
        {openStatus && (
          <div className={styles.button}>
            <Button
              typeStyle={ButtonType.SECOND}
              onClick={() => {
                onApplyFilter();
                setOpenStatus(false);
              }}
              backgroundColor={"#ffffff"}
              colorText={"#007C45"}
              label={t("common.apply")}
              textUp={"uppercase"}
            />
          </div>
        )}
      </div>
    </div>
  );
};
export default Filter;
