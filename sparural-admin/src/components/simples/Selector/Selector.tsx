import { FC, useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import OptionItem, { SelectOption } from "./OptionItem";
import styles from "./Selector.module.scss";
import { ReactComponent as Chevron } from "../../../assets/icons/chevron.svg";
import classNames from "classnames";
import { Label } from "../Label";
import { Button } from "../Button";
import { message } from "antd";
import { ButtonType } from "../Button/Button";
import { spawn } from "child_process";
import StarIcon from "../StarIcon/StarIcon";

type Props = {
  label: string;
  options: Array<SelectOption>;
  onChange: (data: SelectOption) => void;
  value: Array<SelectOption>;
  disabled?: boolean;
  extraOptions?: Array<SelectOption>;
  onExtraOptionClick?: (data: SelectOption) => void;
  extraValue?: Array<SelectOption>;
  multiple?: boolean;
  classes?: {
    label?: string;
    root?: string;
  };
  placeholder?: string;
  onAdd?: (value: string) => Promise<boolean>;
  inputPlaceholder?: string;
  error?: string
  isStar?: boolean
  onRechedEnd?: () => void
};

/**
 *
 * @param label
 * @param options
 * @param onChange
 * @param value
 * @param disabled
 * @returns
 */

const Selector: FC<Props> = ({
  label,
  options,
  onChange,
  value,
  disabled,
  extraOptions,
  onExtraOptionClick,
  extraValue,
  multiple,
  classes,
  placeholder,
  onAdd,
  inputPlaceholder,
  error,
  isStar,
  onRechedEnd
}) => {
  const [openStatus, setOpenStatus] = useState(false);
  const { t } = useTranslation();
  const optionsBlock = useRef<HTMLDivElement>(null);
  const mainBlock = useRef<HTMLDivElement>(null);

  const [text, setText] = useState<string>("");

  const labelInner: string = useMemo(() => {
    let lab = placeholder ? placeholder : label;
    if (extraValue && extraValue.length > 0) {
      lab = extraValue[0].label;
      return lab;
    }
    if (value.length > 1) {
      lab = t("forms.checked_some");
    }
    if (value.length === 1) {
      lab = options.find(i => i.value === value[0].value)?.label || value[0].label;
    }
    return lab;
  }, [placeholder, label, extraValue, value, t, options]);

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

  const onHandleClick = (data: SelectOption) => {
    if (multiple) {
      onChange(data);
    } else {
      onChange(data);
      onToggleOpen();
    }
  };

  /**
   * callback when we click on BUTTON ADD
   */
  const onHandleAdd = useCallback(async () => {
    if (onAdd) {
      const response = await onAdd(text);
      if (response) {
        setText("");
        message.success(t("suсcess.save_data"));
      } else {
        message.error(t("errors.save_data"));
      }
    }
  }, [onAdd, text]);

  const reached = useRef(false)
    /**
    *
    * dynamic pagination
    */
     const handleScroll = () => {
      if (!optionsBlock.current) {
          return
      }

      const contentHeight = optionsBlock.current.offsetHeight
      const scrollHeight = optionsBlock.current.scrollHeight

      const scrollTop = optionsBlock.current.scrollTop

      if (scrollHeight <= contentHeight) {
          return
      }

      const afterEndReach =
          scrollHeight - (scrollTop + contentHeight) < contentHeight / 2

      if (afterEndReach && !reached.current) {
          reached.current = true
          onRechedEnd && onRechedEnd()
      } else if (!afterEndReach && reached.current) {
          reached.current = false
      }
  }

  return (
    <div ref={mainBlock} className={styles.root}>
      {label && <Label>{label}</Label>}
      <div className={styles.static}>
        <div
          className={classNames(styles.wrapper, classes?.root, {
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
                [styles.placeholder]: labelInner === placeholder || disabled
              })}
            >
              {!isStar ? labelInner : Number(labelInner) ? <StarIcon starCount={Number(labelInner)} /> : labelInner}
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
            <div onScroll={handleScroll} ref={optionsBlock} className={styles.optionBlock}>
              {extraOptions &&
                onExtraOptionClick &&
                extraOptions.map((i) => {
                  return (
                    <OptionItem
                      key={i.value}
                      data={i}
                      onClick={onExtraOptionClick}
                      values={extraValue || []}
                      multiple={multiple}
                    />
                  );
                })}
              {openStatus &&
                options.map((option) => {
                  return (
                    <OptionItem
                      key={option.value}
                      data={option}
                      onClick={onHandleClick}
                      values={value}
                      multiple={multiple}
                    />
                  );
                })}
              {!!onAdd && (
                <div className="">
                  <input
                    placeholder={inputPlaceholder || ""}
                    className={styles.inputField}
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                  />
                  <div className="">
                    <Button
                      typeStyle={ButtonType.SECOND}
                      disabled={!!!text}
                      onClick={onHandleAdd}
                      label={t("common.add")}
                      textUp={"capitalize"}
                    />
                  </div>
                </div>
              )}
            </div>
          )}
        </div>

      </div>
      {error && <span className={styles.errorText}>{error}</span>}
    </div>
  );
};

export default Selector;
