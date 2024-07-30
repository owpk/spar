import { t } from 'i18next'
import React, { FC, useMemo, useRef, useState } from 'react'
import { UserType } from '../../../../types'
import { Checkbox } from '../../../simples/Checkbox'
import { InputHolder } from '../../../simples/InputHolder'
import styles from './RecipesRedact.module.scss'
import { TextField } from '../../../simples/TextField'

type Props = {
    goods: any[]
    values: Array<number>
    onCheck: (userId: number) => void
    onCheckAll: () => void
    isAll: boolean
    onEndReached?: () => void
    upperOne?: boolean
    title?: string
    children: JSX.Element
}

/**
 *
 * @param goods
 * @param values
 * @param onCheck
 * @param onCheckAll
 * @returns
 */

const GoodsBlock: FC<Props> = ({
    goods,
    upperOne,
    values,
    onCheck,
    onCheckAll,
    isAll = false,
    onEndReached,
    title = 'Товары',
    children,
}) => {
    const contentRef = useRef<HTMLDivElement>(null)
    const reached = useRef(false)
    /**
     *
     * dynamic pagination
     */
    const handleScroll = () => {
        if (!contentRef.current) {
            return
        }

        const contentHeight = contentRef.current.offsetHeight
        const scrollHeight = contentRef.current.scrollHeight

        const scrollTop = contentRef.current.scrollTop

        if (scrollHeight <= contentHeight) {
            return
        }

        const afterEndReach =
            scrollHeight - (scrollTop + contentHeight) < contentHeight / 2

        if (afterEndReach && !reached.current) {
            reached.current = true
            onEndReached && onEndReached()
        } else if (!afterEndReach && reached.current) {
            reached.current = false
        }
    }

    return (
        <div style={{ width: 445 }}>
            <h3 className={styles.goodsTitle}>{title}</h3>
            {children}
            <div
                ref={contentRef}
                onScroll={handleScroll}
                className={styles.goodsBlockWrapper}
            >
                <InputHolder classes={styles.inputHolderRoot}>
                    <Checkbox
                        value={0}
                        onClick={onCheckAll}
                        isChecked={isAll}
                        label={t('forms.check_all_goods')}
                        labelPosition={'right'}
                    />
                </InputHolder>
                {goods &&
                    goods.map((good) => (
                        <div key={`${good.id}${Math.random()}`}>
                            <InputHolder classes={styles.inputHolderRoot}>
                                <Checkbox
                                    upperOne
                                    labelPosition={'right'}
                                    value={good.id}
                                    onClick={(e) => onCheck(e as number)}
                                    isChecked={values.includes(good.id)}
                                    label={good.name}
                                />
                            </InputHolder>
                        </div>
                    ))}
            </div>
        </div>
    )
}

export default GoodsBlock
