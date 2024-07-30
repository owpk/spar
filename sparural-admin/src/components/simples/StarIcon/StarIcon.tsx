import React, { FC, useState } from "react";
import { ReactComponent as Star } from "../../../assets/icons/star_icon.svg";
import { ReactComponent as StarFill } from "../../../assets/icons/star_icon_fill.svg";

type Props = {
  starCount: number;
};

const StarIcon: FC<Props> = ({ starCount }) => {
  const [count, setCount] = useState<number>();
  const arr: (string | number)[] = [
    "simple star",
    "simple star",
    "simple star",
    "simple star",
    "simple star"
  ];
  for (let i = 0; i < starCount; i++) {
    arr.push(i);
  }
  arr.reverse();
  arr.length = 5;
  return (
    <div>
      {arr.map((item, index) => {
        if (typeof item === "string") {
          return <Star key={index} />;
        } else {
        }
        return <StarFill key={index} />;
      })}
    </div>
  );
};

export default StarIcon;
