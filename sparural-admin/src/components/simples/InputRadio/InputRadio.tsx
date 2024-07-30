import React, { FC } from "react";
import { RouteProps } from "react-router-dom";
import styles from "./InputRadio.module.scss";
type Props = RouteProps & {
  styleCircle?: "50%";
  isChecked: boolean;
  onChange: () => void;
  size?: number;
  labelPosition?: "left" | "right";
};

const InputRadio: FC<Props> = ({
  styleCircle,
  isChecked,
  onChange,
  size,
  children,
  labelPosition
}) => {
  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        flexDirection: labelPosition === "left" ? "row" : "row-reverse"
      }}
    >
      {children && (
        <span
          style={{
            marginLeft: labelPosition === "left" ? 0 : 10,
            marginRight: labelPosition === "left" ? 10 : 0
          }}
          className={styles.label}
        >
          {children}
        </span>
      )}
      <input
        onChange={onChange}
        checked={isChecked}
        type="radio"
        className={styles.InputRario}
        style={{
          borderRadius: `${styleCircle ? styleCircle : "4px"}`,
          minWidth: size,
          minHeight: size
        }}
      />
    </div>
  );
};
export default InputRadio;
