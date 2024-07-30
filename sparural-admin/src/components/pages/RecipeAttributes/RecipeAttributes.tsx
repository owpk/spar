import { message } from 'antd'
import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { AttributesService } from '../../../services/AttributesService'
import { AttributesType } from '../../../types'
import { MainLayout } from '../../complexes/MainLayout'
import styles from './RecipeAttributes.module.scss'
import RecipeAttributCard, { RecipeAttributesType } from './RecipeAttributCard'

const RecipeAttributes: FC = () => {
    const { t } = useTranslation()

    const [list, setList] = useState<Array<RecipeAttributesType>>([])
    const [loading, setLoading] = useState(false)
    const offset = useRef(0)
    const has = useRef(true)

    const load = async () => {
        if (!has.current || loading) {
            return
        }
        setLoading(true)
        try {
            const result = await AttributesService.getRecipeAttributes({
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
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }
    useEffect(() => {
        load().then()
    }, [])

    const removeAttr = (id: number) => {
        setList((prev) => prev.filter((i) => i.id !== id))
    }

    const onAddAtribute = useCallback(async () => {
        const response = await AttributesService.createRecipeAttribute({
            draft: true,
        })
        setList((prev) => [response, ...prev])
    }, [])

    return (
        <MainLayout
            onEndReached={load}
            isLoading={loading}
            onAdd={onAddAtribute}
            title={t('screen_title.recipe-attributes')}
        >
            <div className={styles.blockWrapper}>
                {list.map((atr) => (
                    <RecipeAttributCard
                        key={atr.id}
                        attribute={atr}
                        deleteAttr={removeAttr}
                    />
                ))}
            </div>
        </MainLayout>
    )
}

export default RecipeAttributes
