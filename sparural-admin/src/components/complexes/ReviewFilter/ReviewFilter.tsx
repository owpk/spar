import { Row, Col } from 'antd'
import { t } from 'i18next'
import produce from 'immer'
import React, { FC, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react'
import { InputHolder } from '../../simples/InputHolder'
import { Selector } from '../../simples/Selector'
import { SelectOption } from '../../simples/Selector/OptionItem'
import { TextField } from '../../simples/TextField'
import { FilterContext } from './parts/contexts'
import { FilterShop } from './parts/FilterShop'
import { FilterTime } from './parts/FilterTime'
import styles from './ReviewFilter.module.scss'


type Props = {
    // onChange: () => void
}
const ReviewFilter: FC<Props> = ({ }) => {
    const [filter, setFilter] = useContext(FilterContext)
    const [search, setSearch] = useState<string>(filter.search || '')

    const isTyping = useRef<any>()

    const countdownTimer = useCallback(async () => {
        if (isTyping.current

        ) {
            setFilter(produce(
                draft => {
                    draft.search = search
                }
            ))
        }
        clearInterval(isTyping.current)
    }, [search, setFilter])


    const setSeachFilter = useCallback((search: string) => {

        setSearch(search)
        clearInterval(isTyping.current)
      
    }, [])

    useEffect(() => {
        isTyping.current = setInterval(countdownTimer, 500)
    }, [countdownTimer, search])



    const starOptions = useMemo((): SelectOption[] => {
        let options: SelectOption[] = []
        for (let i = 0; i < 5; i++) {
            options.push({
                value: String(i + 1),
                label: String(i + 1),
                isStar: true
            })
        }
        return options
    }, [])

    /**
     * change rage filter
     */
    const onChangeRate = useCallback((value: SelectOption) => {
        if (filter.grade && filter.grade.includes(Number(value.value))) {
            setFilter(prev => ({ ...prev, grade: prev.grade?.filter(i => i !== Number(value.value)) }))
        } else {
            setFilter(prev => ({ ...prev, grade: prev.grade ? [...prev.grade, Number(value.value)] : [Number(value.value)] }))
        }
    }, [filter.grade])

    return (
        <>
            <Row>


                <Row>
                    <div className={styles.search}>
                        <TextField
                            isSearch
                            label={""}
                            placeholder={t("reviews.find_reviews")}
                            onChange={setSeachFilter}
                            value={search || ''}
                        />
                    </div>
                </Row>
                <Row
                    justify={"space-between"}
                    style={{ width: "100%" }}
                    gutter={[16, 16]}
                >
                    <Col className={styles.AllFilter}>
                        <InputHolder classes={styles.wrapperInput}>
                            <Selector
                                isStar
                                placeholder={t("table.rate")}
                                multiple
                                label={''}
                                options={starOptions}
                                onChange={onChangeRate}
                                value={filter.grade ? filter.grade.map(i => ({ value: `${i}`, label: `${i}`, isStar: true })) : []}
                            />
                        </InputHolder>
                        <InputHolder classes={styles.wrapperInput}>
                            <FilterTime />
                        </InputHolder>
                        <InputHolder>
                            <FilterShop />
                        </InputHolder>
                    </Col>
                </Row>
            </Row>
        </>
    )
}

export default React.memo(ReviewFilter)