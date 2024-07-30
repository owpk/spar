import { message } from 'antd'
import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { AttributesService } from '../../../../services/AttributesService'
import { AttributesType } from '../../../../types'
import { MainLayout } from '../../../complexes/MainLayout'
import { ShopAttributCard } from '../../../complexes/ShopAttributCard'
import { AttributesContext } from './contexts'
import styles from './ShopAttributes.module.scss'

const ShopAttributes: FC = () => {
    const { t } = useTranslation()

    const [list, setList] = useState<Array<AttributesType>>([])
    const [loading, setLoading] = useState(false)
    const offset = useRef(0)
    const has = useRef(true)

    const load = async () => {
        if (!has.current || loading) {
            return
        }
        setLoading(true)
        try {
            const result = await AttributesService.getAttributes({
                offset: offset.current,
            })
            if (!result.length) {
                has.current = false
                setLoading(false)
                return
            }

            offset.current = offset.current + result.length
            setList([...list, ...result])
        } catch (error: any) {

            message.error(t("errors.get_data"))
        }
        setLoading(false)


    }
    useEffect(() => {
        load().then()
    }, [])


    const onAddAtribute = useCallback(async () => {
        const response = await AttributesService.createAttributes({ draft: true })
        setList(prev => [response,...prev])
    }, [])


    return (
        <MainLayout
            onEndReached={load}
            isLoading={loading}
            onAdd={onAddAtribute}
            title={t('screen_title.shop_atributes')}
        >
            <AttributesContext.Provider value={[list, setList]}>
                <div className={styles.blockWrapper}>

                    {list.map((atr) =>
                        <ShopAttributCard key={atr.id} attribute={atr} />
                    )}

                </div>
            </AttributesContext.Provider>
        </MainLayout>
    )
}

export default ShopAttributes;