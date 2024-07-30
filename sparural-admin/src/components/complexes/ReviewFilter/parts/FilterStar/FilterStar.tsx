import classNames from "classnames";
import React, { FC, useCallback, useRef, useState } from "react";

import styles from "./FilterStar.module.scss";

// import { Button } from "../../../../simples/Button";
import { useTranslation } from "react-i18next";
import { ReactComponent as Chevron } from "../../../../../assets/icons/chevron.svg";

import { Col, Row } from "antd";

import { InputRadio } from "../../../../simples/InputRadio";
import StarIcon from "../../../../simples/StarIcon/StarIcon";

type Props = {
  onFilter?: () => number;
};

export type FilterStarType = {};

const FilterStar: FC<Props> = () => {
  const { t } = useTranslation();

  const [isOpen, setisOpen] = useState<boolean>(false);
  const [value, setValue] = useState<number>(0);

  const onToggleOpen = () => {
    setisOpen(!isOpen);
  };

  return (

    <div className={styles.root}>
      <div
        className={classNames(styles.wrapper, {
          [styles.opened]: isOpen
        })}
      >
        <div onClick={onToggleOpen} className={styles.top}>
          <div className={classNames(styles.topLabel, {})}>
            <span className={styles.nameFilter}>{t("table.rate")}</span>
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
            <div className={styles.wrapperStar}>
              <div className={styles.rowFilter}>
                <div className={styles.checkbox}>
                  <InputRadio
                    size={30}
                    isChecked={1 === value}
                    onChange={() => setValue(1)}
                    labelPosition={"left"}
                  ></InputRadio>
                </div>
                <div>
                  <StarIcon starCount={1} />{" "}
                </div>
              </div>
              <div className={styles.rowFilter}>
                <div className={styles.checkbox}>
                  <InputRadio
                    size={30}
                    isChecked={2 === value}
                    onChange={() => setValue(2)}
                    labelPosition={"left"}
                  ></InputRadio>
                </div>
                <div>
                  <StarIcon starCount={2} />
                </div>
              </div>
              <div className={styles.rowFilter}>
                <div className={styles.checkbox}>
                  <InputRadio
                    size={30}
                    isChecked={3 === value}
                    onChange={() => setValue(3)}
                    labelPosition={"left"}
                  ></InputRadio>
                </div>
                <div>
                  <StarIcon starCount={3} />{" "}
                </div>
              </div>
              <div className={styles.rowFilter}>
                <div className={styles.checkbox}>
                  <InputRadio
                    size={30}
                    isChecked={4 === value}
                    onChange={() => setValue(4)}
                    labelPosition={"left"}
                  ></InputRadio>
                </div>
                <div>
                  <StarIcon starCount={4} />{" "}
                </div>
              </div>
              <div className={styles.rowFilter}>
                <div className={styles.checkbox}>
                  <InputRadio
                    size={30}
                    isChecked={5 === value}
                    onChange={() => setValue(5)}
                    labelPosition={"left"}
                  ></InputRadio>
                </div>
                <div>
                  <StarIcon starCount={5} />{" "}
                </div>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};
export default FilterStar;
