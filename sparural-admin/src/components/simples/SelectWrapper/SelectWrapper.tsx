import classNames from 'classnames';
import React, { FC, useCallback, useEffect, useRef, useState } from 'react'
import { Label } from '../Label';
import styles from './SelectWrapper.module.scss'
import { ReactComponent as Chevron } from "../../../assets/icons/chevron.svg";
import { RoutesProps } from 'react-router-dom';

type Props = RoutesProps & {
    label?: string;
    disabled?: boolean;
    error?: string
    palceholder?: string
  };
const SelectWrapper:FC<Props> = ({
    label,
    disabled,
    error,
    palceholder,
    children,
    
}) => {
    const [openStatus, setOpenStatus] = useState(false);
    const optionsBlock = useRef<HTMLDivElement>(null);
    const mainBlock = useRef<HTMLDivElement>(null);

    // Метод сброса фокуса с выбранного элемента
    const loseFocus = useCallback(() => {
      if (optionsBlock && optionsBlock.current) {
        optionsBlock.current.blur();
      }
    }, []);
  
    // Метод закрытия селектора при клике вне самого селектора
    const closeSelectOutOfBlock = useCallback(
      (event: any) => {
        if (mainBlock && mainBlock.current) {
          // Проверка добавлена для устранения бага в Firefox
          if (!mainBlock.current.contains(event.target)) {
            setOpenStatus(false);
            loseFocus();
          }
        }
      },
      [loseFocus]
    );
  
    // Раскрытие пунктов меню с опциями при фокусе на селекторе
    const onHandleFocus = useCallback(() => {
      if (!disabled) setOpenStatus(true);
    }, [disabled]);
  
    //закрытие по второму тапу
    const onToggleOpen = useCallback(() => {
      if (disabled) {
        return;
      }
      setOpenStatus(!openStatus);
    }, [openStatus, disabled]);
  
    // Установка/удаление обработчика события на документе.
    useEffect(() => {
      document.addEventListener("click", closeSelectOutOfBlock, false);
      return () => {
        document.removeEventListener("click", closeSelectOutOfBlock, false);
      };
    }, [closeSelectOutOfBlock]);
  

  
    return (
      <div ref={mainBlock} className={styles.root}>
        {label && <Label>{label}</Label>}
        <div className={styles.static}>
          <div
            className={classNames(styles.wrapper, {
              [styles.error]: error,
              [styles.opened]: openStatus,
              [styles.disabled]: disabled
            })}
            tabIndex={0}
          >
            <div
              onFocus={onHandleFocus}
              onClick={onToggleOpen}
              className={styles.top}
            >
              <div
                className={classNames(styles.topLabel, {
                  [styles.placeholder]: disabled
                })}
              >
                {palceholder || label}
              </div>
              <div
                className={classNames({
                  [styles.openIcon]: openStatus
                })}
              >
                <Chevron
                  className={classNames(styles.chevron, {
                    [styles.chevronDisable]: disabled
                  })}
                />
              </div>
            </div>
  
            {openStatus && (
              <div ref={optionsBlock} className={styles.optionBlock}>
                {openStatus &&
                    <>{children}</>
                 }
              </div>
            )}
          </div>
  
        </div>
        {error && <span className={styles.errorText}>{error}</span>}
      </div>
    );
}

export default React.memo(SelectWrapper)