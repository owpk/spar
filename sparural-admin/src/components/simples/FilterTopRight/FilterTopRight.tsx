import { Col, Row } from "antd";
import React, { createContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { FilterShop } from "../../complexes/ReviewFilter/parts/FilterShop";
// import { FilterStar } from "../../complexes/FilterStar";
// import { FilterTime } from "../../complexes/FilterTime";
import { InputHolder } from "../InputHolder";
import { Label } from "../Label";
import { TextField } from "../TextField";
import styles from "./FilterTopRight.module.scss";

type Props = {};

type TFilterTime = {
  from: number;
  after: number;
};

type TFilterTopRight = {
  rate: number;
  time: TFilterTime;
  shop: string;
};

const initialState: TFilterTopRight = {
  rate: 0,
  time: {
    from: 0,
    after: 0
  },
  shop: ""
};
export const wrapper = createContext(initialState);

const FilterTopRight: React.FC = (props: Props): JSX.Element => {
  return (
    <>
      <wrapper.Provider value={initialState}>
        <AllFilter />
      </wrapper.Provider>
    </>
  );
};

function AllFilter() {
  const [searchAllFilter, setSearchAllFilter] = useState<string>("");
  const { t } = useTranslation();

  return (
    <>
      <Row>
        <Row
          justify={"space-between"}
          style={{ width: "100%" }}
          gutter={[16, 16]}
        >
          <div className={styles.search}>
            <TextField
              isSearch
              label={""}
              placeholder={t("reviews.find_reviews")}
              onChange={setSearchAllFilter}
              value={searchAllFilter}
            />
          </div>
          <Col className={styles.AllFilter}>
            {/* <InputHolder>
              <FilterStar />
            </InputHolder>
            <InputHolder>
              <FilterTime />
            </InputHolder>
            <InputHolder>
              <FilterShop />
            </InputHolder> */}
          </Col>
        </Row>
      </Row>
    </>
  );
}

export default FilterTopRight;
