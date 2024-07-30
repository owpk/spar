import { FC, useCallback, useContext, useEffect, useRef, useState } from "react";

import styles from "./FilterShop.module.scss";

import { useTranslation } from "react-i18next";
import { Selector } from "../../../../simples/Selector";
import { SelectOption } from "../../../../simples/Selector/OptionItem";
import { ShopsService } from "../../../../../services/ShopsService";
import { ShopsType } from "../../../../../types";
import { FilterContext } from "../contexts";

type Props = {
  onFilter?: () => number;
};

export type FilterStarType = {};

const FilterShop: FC<Props> = () => {
  const { t } = useTranslation();
  const [filter, setFilter] = useContext(FilterContext)
  const [loading, setLoading] = useState(false)
  const [list, setList] = useState<Array<SelectOption>>([])
  const offset = useRef(0)
  const has = useRef(true)
  const shopsRef = useRef<Array<SelectOption>>([])
  shopsRef.current = list

  const load = async () => {

    if (!has.current || loading) {
      return
    }
    setLoading(true)
    try {
      const result = await ShopsService.getShops({
        offset: offset.current,
        limit: 100
      })
      if (!result.length) {
        has.current = false
        setLoading(false)
        return
      }

      const rows = result.map((item: ShopsType): SelectOption => {
        return ({
          value: `${item.id}`,
          label:  `${item.title}, ${item?.address}` || ""
        })
      })

      offset.current = offset.current + result.length

      setList([...shopsRef.current, ...rows])
    } catch (error) {
      // message.error(t('errors.get_data'))
    }
    setLoading(false)
  }
  useEffect(() => {
    load().then()
  }, [])

  const checkShop = useCallback((value: SelectOption) => {
      if(filter && filter.merchantId){
        const find = filter.merchantId.find(i => i === Number(value.value))
        if(find){
          setFilter({...filter, merchantId: filter.merchantId?.filter(i => i !== Number(value.value))})
        } else {
          setFilter({...filter, merchantId: filter.merchantId ? [...filter.merchantId, +value.value] : [+value.value]})
        }
      } else {
        setFilter({...filter, merchantId: [+value.value]})
      }
  },[filter, setFilter])



  return (
    <div className={styles.root}>
      <Selector
        multiple
        placeholder={t("filter.shop")}
        label={""}
        options={list}
        onChange={checkShop}
        value={filter?.merchantId?.map(i => ({value:`${i}`, label: ''})) || []}
        onRechedEnd={load}
      />
    </div>
  );
};
export default FilterShop;
