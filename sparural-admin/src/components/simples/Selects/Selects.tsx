import React, { FC } from 'react'

import styles from './Selects.module.scss'

type Props = {
    label: string
    item: Array<string>
}
/**
 *
 * @param label text label component, type string
 * @param city text option component, type array(string)
 * @returns
 */

const Selects: FC<Props> = ({ label, item }) => {
    return (
        <>
            <label className={styles.label}>{label}</label>
            <select className={styles.selects}>
                {item.map((item, idx) => (
                    <option key={idx}>{item}</option>
                ))}
            </select>
        </>
    )
}
export default Selects
