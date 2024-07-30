import { FC, useCallback, useEffect, useMemo, useRef, useState } from 'react'
import produce from 'immer'
import { useTranslation } from 'react-i18next'
import { useLocation, useNavigate } from 'react-router-dom'
import { MainLayout } from '../../../complexes/MainLayout'
import { EditWrapper } from '../../../simples/EditWrapper'
import { Selector } from '../../../simples/Selector'
import { TextField } from '../../../simples/TextField'
import styles from './ShopsScreenRedact.module.scss'
import { SelectOption } from '../../../simples/Selector/OptionItem'
import { TimePickerComponent } from '../../../simples/TimePickerComponent'
import { Col, message, Row } from 'antd'
import { InputHolder } from '../../../simples/InputHolder'
import { Label } from '../../../simples/Label'
import { ShopsService } from '../../../../services/ShopsService'
import { CreateShopsType, ShopStatus } from '../../../../types'
import { Routes } from '../../../../config'
import { useAppSelector } from '../../../../hooks/store'
import { selectShopFormats } from '../../../../store/slices/storage'
import {
    fromTimeToTimestamp,
    getTimeFromTimestamp,
} from '../../../../utils/helpers'
import { AttributesService } from '../../../../services/AttributesService'

type Props = {}
type CurrentShopType = {
    title?: string
    address?: string
    longitude: number
    latitude: number
    formatId?: number
    workingHoursFrom?: string
    workingHoursTo?: string
    workingStatus?: ShopStatus
    attributes?: Array<number>
    loymaxLocationId?: string
}

type Errors = {
    [field in keyof CurrentShopType]?: string
}
const ShopsScreenRedact: FC<Props> = () => {
    const { t } = useTranslation()
    const id = useLocation().search.split('=')[1]
    const navigation = useNavigate()

    const shopFormats = useAppSelector(selectShopFormats)

    const shopFoprmatsOptions: Array<SelectOption> = useMemo(() => {
        return shopFormats.map((item) => ({
            value: item.id,
            label: item.name,
        }))
    }, [shopFormats])

    const [location, setLocation] = useState<string>('')
    const [loading, setLoading] = useState<boolean>(false)
    const currentId = useRef<number>(0)

    const [errors, setErrors] = useState<Errors>({})

    /**
     * attributes
     */
    const [shopAttributes, setShopAttributes] = useState<Array<SelectOption>>(
        []
    )

    const fetchAttributes = useCallback(async () => {
        let offset = 0
        let load = true
        while (load) {
            const response = await AttributesService.getAttributes({
                limit: 100,
                offset,
            })

            const options = response.map((i) => ({
                value: i.id,
                label: i.name,
            }))
            offset = response.length
            setShopAttributes((prev) => [...prev, ...options])
            if (response.length === 0) {
                load = false
            }
        }
    }, [])

    useEffect(() => {
        fetchAttributes().then()
    }, [])

    const [currentShop, setCurrentShop] = useState<CurrentShopType>({
        title: '',
        address: '',
        longitude: 0,
        latitude: 0,
        formatId: undefined,
        workingHoursFrom: '',
        workingHoursTo: '',
        workingStatus: undefined,
        attributes: undefined,
        loymaxLocationId: '',
    })

    /**
     * wrokstatus options
     */
    const workStatusOptions: Array<SelectOption> = useMemo(() => {
        return Object.values(ShopStatus).map((status) => ({
            value: status,
            label: t(`shopStatuses.${status}`),
        }))
    }, [t])

    /**
     * shop format labels
     */
    const shopFormatLabel = useMemo(() => {
        return shopFoprmatsOptions.filter(
            (i) => i.value === currentShop.formatId
        )
    }, [currentShop.formatId, shopFoprmatsOptions])

    /**
     * Attribute values
     */
    const shopAttributeValues = useMemo((): Array<SelectOption> => {
        if (currentShop.attributes) {
            return currentShop.attributes.map((i) => ({
                value: i,
                label: shopAttributes.find((j) => +j.value === +i)?.label || '',
            }))
        } else {
            return []
        }
    }, [currentShop.attributes, shopAttributes])

    const changeShop = useCallback(
        (value: string | number, key: keyof CurrentShopType) => {
            if (key === 'attributes') {
                if (currentShop.attributes) {
                    const find = currentShop.attributes.find((i) => i === value)
                    if (find) {
                        setCurrentShop((prev) => ({
                            ...prev,
                            [key]: prev[key]?.filter((i) => i !== value),
                        }))
                    } else {
                        setCurrentShop(
                            produce((draft) => {
                                draft[key]?.push(value as number)
                            })
                        )
                    }
                } else {
                    setCurrentShop((prev) => ({
                        ...prev,
                        [key]: [value as number],
                    }))
                }
            } else {
                setCurrentShop((prev) => ({ ...prev, [key]: value }))
            }

            setErrors((prev) => ({ ...prev, [key]: undefined }))
        },
        [currentShop.attributes]
    )

    const getOneScreenById = useCallback(async () => {
        try {
            setLoading(true)
            const response = await ShopsService.getShopsById(Number(id))

            setCurrentShop({
                title: response.title,
                address: response.address,
                longitude: response.longitude,
                latitude: response.latitude,
                formatId: response.format.id,
                workingHoursFrom: response.workingHoursFrom,
                workingHoursTo: response.workingHoursTo,
                workingStatus: response.workingStatus,
                attributes: response.attributes.map((i) => i.id),
                loymaxLocationId: response.loymaxLocationId,
            })
            currentId.current = Number(id)
            setLoading(false)
        } catch (error) {
            message.error(t('errors.get_data'))
            setLoading(false)
        }
    }, [id, t])

    useEffect(() => {
        if (!!id) {
            getOneScreenById().then()
        }
    }, [id])

    const onHandleSave = useCallback(async () => {
        let error: boolean = false
        Object.entries(currentShop).forEach(([key, value]) => {
            if (!value) {
                error = true
                setErrors((prev) => ({
                    ...prev,
                    [key]: t('errors.required_field'),
                }))
            }
        })

        if (error) return
        const sendData: CreateShopsType = {
            ...currentShop,
            latitude: +currentShop?.latitude || 0,
            longitude: +currentShop?.longitude || 0,
        }
        if (currentId.current) {
            try {
                const response = await ShopsService.updateShops(
                    Number(currentId.current),
                    sendData
                )
                message.success(t('suсcess_messages.update_data'))
                navigation(Routes.SHOPS)
            } catch (error) {
                message.error(t('errors.update_data'))
            }
        } else {
            try {
                const response = await ShopsService.createShops(sendData)
                message.success(t('suсcess_messages.update_data'))
                navigation(Routes.SHOPS)
            } catch (error) {
                message.error(t('errors.update_data'))
            }
        }
    }, [currentShop, navigation, t, errors])

    return (
        <MainLayout title={t('screen_title.shops')}>
            <EditWrapper
                title={t(!id ? 'common.add' : 'common.edit_full')}
                onSave={onHandleSave}
            >
                <Row gutter={[16, 16]}>
                    <Col>
                        <InputHolder>
                            <TextField
                                error={errors.address}
                                label={t('forms.adress')}
                                value={currentShop.address || ''}
                                onChange={(e) => changeShop(e, 'address')}
                            />
                        </InputHolder>
                        <InputHolder>
                            <Row gutter={8}>
                                <Col span={12}>
                                    <TextField
                                        error={errors.longitude}
                                        label={t('forms.lng')}
                                        value={currentShop.longitude}
                                        onChange={(e) =>
                                            changeShop(e, 'longitude')
                                        }
                                    />
                                </Col>
                                <Col span={12}>
                                    <TextField
                                        error={errors.latitude}
                                        label={t('forms.lat')}
                                        value={currentShop.latitude}
                                        onChange={(e) =>
                                            changeShop(e, 'latitude')
                                        }
                                    />
                                </Col>
                            </Row>
                        </InputHolder>
                        <InputHolder>
                            <TextField
                                error={errors.loymaxLocationId}
                                label={t('forms.location_identifier')}
                                value={currentShop.loymaxLocationId || ''}
                                onChange={(e) =>
                                    changeShop(e, 'loymaxLocationId')
                                }
                            />
                        </InputHolder>
                        <InputHolder>
                            <Selector
                                error={errors.workingStatus}
                                label={t('forms.status')}
                                options={workStatusOptions}
                                extraOptions={[]}
                                value={
                                    currentShop.workingStatus
                                        ? [
                                              {
                                                  value: currentShop.workingStatus,
                                                  label: t(
                                                      `shopStatuses.${currentShop.workingStatus}`
                                                  ),
                                              },
                                          ]
                                        : []
                                }
                                onChange={({ value }) =>
                                    changeShop(value as string, 'workingStatus')
                                }
                            />
                        </InputHolder>
                    </Col>
                    <Col>
                        <InputHolder>
                            <Selector
                                error={errors.formatId}
                                label={t('forms.format_shop')}
                                options={shopFoprmatsOptions}
                                extraOptions={[]}
                                value={shopFormatLabel}
                                onChange={({ value }) =>
                                    changeShop(value as string, 'formatId')
                                }
                            />
                        </InputHolder>

                        <Label>{t('forms.working_hours')}</Label>

                        <InputHolder>
                            <Row gutter={8}>
                                <Col span={12}>
                                    <TimePickerComponent
                                        error={errors.workingHoursFrom}
                                        // transform string as "08:00" into timestamp
                                        value={fromTimeToTimestamp(
                                            currentShop.workingHoursFrom
                                        )}
                                        // transform timestamp into string as "00:00"
                                        onChange={(e) =>
                                            changeShop(
                                                getTimeFromTimestamp(e),
                                                'workingHoursFrom'
                                            )
                                        }
                                        placeholder="c"
                                    />
                                </Col>
                                <Col span={12}>
                                    <TimePickerComponent
                                        error={errors.workingHoursTo}
                                        // transform string as "08:00" into timestamp
                                        value={fromTimeToTimestamp(
                                            currentShop.workingHoursTo
                                        )}
                                        // transform timestamp into string as "00:00"
                                        onChange={(e) =>
                                            changeShop(
                                                getTimeFromTimestamp(e),
                                                'workingHoursTo'
                                            )
                                        }
                                        placeholder="до"
                                    />
                                </Col>
                            </Row>
                        </InputHolder>
                        <InputHolder>
                            <TextField
                                error={errors.title}
                                label={t('forms.name_shop')}
                                value={currentShop.title || ''}
                                onChange={(e) => changeShop(e, 'title')}
                            />
                        </InputHolder>
                        <div className={styles.select}>
                            <Selector
                                error={errors.attributes}
                                multiple
                                label={t('forms.attribute')}
                                options={shopAttributes}
                                extraOptions={[]}
                                value={shopAttributeValues}
                                onChange={({ value }) =>
                                    changeShop(value as string, 'attributes')
                                }
                            />
                        </div>
                    </Col>
                </Row>
            </EditWrapper>
        </MainLayout>
    )
}
export default ShopsScreenRedact
