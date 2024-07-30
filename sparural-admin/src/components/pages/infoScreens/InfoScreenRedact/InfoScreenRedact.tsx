import { Col, message, Row } from 'antd'
import produce from 'immer'
import { FC, useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation, useNavigate } from 'react-router-dom'
import { EntitiesFieldName, FileSource, Routes } from '../../../../config'
import { useAppDispatch, useAppSelector } from '../../../../hooks/store'
import {
    UploadFileDocType,
    useUploadFileMutation,
} from '../../../../services/FileService'
import { InfoScreenService } from '../../../../services/InfoScreenService'
import { setLoading } from '../../../../store/slices/appSlice'
import { appCitiesOptions } from '../../../../store/slices/storage'
import { CitySelectType, CreateInfoScreen, Phototype } from '../../../../types'
import { MainLayout } from '../../../complexes/MainLayout'
import { EditWrapper } from '../../../simples/EditWrapper'
import { PhotoLoader } from '../../../simples/PhotoLoader'
import { Selector } from '../../../simples/Selector'
import { SelectOption } from '../../../simples/Selector/OptionItem'
import styles from './InfoScreenRedact.module.scss'
import { DatePickerComponent } from '../../../simples/DatePickerComponent'

type Props = {}

type Errors = {
    cities?: string
}

const InfoScreenRedact: FC<Props> = () => {
    const { t } = useTranslation()
    const dispatch = useAppDispatch()
    const id = useLocation().search.split('=')[1]
    const navigation = useNavigate()

    const citiesOptions = useAppSelector(appCitiesOptions)

    const [cities, setCities] = useState<Array<SelectOption>>([])
    const [dateStart, setDateStart] = useState<number>()
    const [dateEnd, setDateEnd] = useState<number>()

    const [extraOptions, setExtraOptions] = useState<Array<SelectOption>>([])

    const [file, setFile] = useState<File>()
    const [photoUrl, setPhotoUrl] = useState<Phototype>()
    const [photoId, setPhotoId] = useState<string>()

    const [currentId, setCurrentId] = useState<number>(id ? +id : 0)

    const [sendFile, { error }] = useUploadFileMutation()

    const [errors, setErrors] = useState<Errors>({})

    const onHandleChangeCities = useCallback(
        (data: SelectOption) => {
            const find = cities.find((i) => i.value === data.value)
            setErrors({ cities: undefined })
            if (!!find) {
                setCities((prev) => prev.filter((j) => j.value !== data.value))
            } else {
                setCities(
                    produce((draft) => {
                        draft.push(data)
                    })
                )
            }
            setExtraOptions([])
        },
        [cities]
    )

    const onExtraOptionClick = useCallback(
        (data: SelectOption) => {
            const find = extraOptions.find((i) => i.value === data.value)
            setErrors({ cities: undefined })
            if (find) {
                setExtraOptions([])
                return
            }
            if (data.value === CitySelectType.ALL) {
                setCities(citiesOptions)
            }
            if (data.value === CitySelectType.NOWHERE) {
                setCities([])
            }
            setExtraOptions([data])
        },
        [citiesOptions, extraOptions]
    )

    /**
     * fetch one screen
     */
    const getOneScreenById = useCallback(async () => {
        try {
            dispatch(setLoading(true))
            const response = await InfoScreenService.getScreenById(Number(id))
            console.log(response)
            const gotCities: SelectOption[] = response.cities.map((city) => {
                return {
                    value: city.id,
                    label: city.name,
                }
            })
            setCities(
                response.citySelect === CitySelectType.NOWHERE
                    ? []
                    : response.citySelect === CitySelectType.ALL
                    ? citiesOptions
                    : gotCities
            )
            setExtraOptions(
                response.cities.length === 0
                    ? response.citySelect === CitySelectType.NOWHERE
                        ? [
                              {
                                  value: CitySelectType.NOWHERE,
                                  label: t('forms.nowhere'),
                              },
                          ]
                        : [
                              {
                                  value: CitySelectType.ALL,
                                  label: t('forms.all_cities'),
                              },
                          ]
                    : []
            )
            setPhotoUrl(response.photo || undefined)
            setPhotoId(response.photo?.uuid)
            setDateStart(response.dateStart)
            setDateEnd(response.dateEnd)
            dispatch(setLoading(false))
        } catch (error) {
            message.error(t('errors.get_data'))
            dispatch(setLoading(false))
        }
    }, [citiesOptions, dispatch, id, t])

    /**
     * create draft
     */
    const createInfoScreen = async () => {
        try {
            // cities временно
            const sendData: CreateInfoScreen = { draft: true, cities: [] }
            const response = await InfoScreenService.createScreen(sendData)
            setCurrentId(response.id)
        } catch (error) {
            message.error(t('errors.save_data'))
        }
    }

    /**
     * save function
     */

    const onHandleSave = useCallback(async () => {
        if (cities.length === 0 && extraOptions.length === 0) {
            setErrors({ cities: t('errors.required_field') })
            return
        }
        const sendData: CreateInfoScreen = {
            isPublic: false,
            citySelect:
                extraOptions.length > 0
                    ? (extraOptions[0].value as CitySelectType)
                    : CitySelectType.SELECTION,
            cities: cities.map((item) => {
                return { id: item.value, name: item.label }
            }),
            draft: false,
            dateStart: dateStart ? dateStart : 0,
            dateEnd: dateEnd ? dateEnd : 0,
        }
        try {
            await InfoScreenService.updateScreen(Number(currentId), sendData)
            message.success(t('suсcess_messages.update_data'))
        } catch (error: any) {
            message.error(
                t('errors.update_data') + ` (${error.response.data.message})`
            )
        }
        if (file) {
            try {
                await uploadFile(file)
            } catch (error) {
                message.error(t('errors.upload_photo'))
            }
        }
        navigation(Routes.INFO_SCREEN)
    }, [
        cities,
        extraOptions,
        dateStart,
        dateEnd,
        file,
        navigation,
        t,
        currentId,
    ])

    /**
     * fetching data if we edit Info screen
     */
    useEffect(() => {
        if (!!id) {
            getOneScreenById().then()
        } else {
            createInfoScreen().then()
        }
    }, [id])

    /**
     * upload image
     */
    const uploadFile = useCallback(
        async (image: File) => {
            const sendData: UploadFileDocType = {
                source: FileSource.REQUEST,
                'source-parameters': JSON.stringify({}),
                entities: [
                    {
                        field: EntitiesFieldName.INFO_SCREEN_PHOTO,
                        documentId: currentId,
                    },
                ],
                file: image,
            }

            await sendFile(sendData)
        },
        [currentId, sendFile]
    )

    useEffect(() => {
        if (error) {
            message.warning(t('errors.upload_photo'))
        }
    }, [error, t])

    const onDeleteFile = async () => {
        if (photoId) {
            // await deleteFile(photoId)
            setFile(undefined)
        }
    }

    return (
        <MainLayout isLoading={false} title={t('screen_title.info_screen')}>
            <EditWrapper
                onSave={onHandleSave}
                title={t(!id ? 'common.add' : 'common.edit_full')}
            >
                <>
                    <div className={styles.InfoScreenRedact__top}>
                        <div className={styles.InfoScreenRedact__select}>
                            <Selector
                                error={errors.cities || ''}
                                label={t('forms.cities')}
                                options={citiesOptions}
                                extraOptions={[
                                    {
                                        value: CitySelectType.ALL,
                                        label: t('forms.all_cities'),
                                    },
                                    {
                                        value: CitySelectType.NOWHERE,
                                        label: t('forms.nowhere'),
                                    },
                                ]}
                                onExtraOptionClick={onExtraOptionClick}
                                extraValue={extraOptions}
                                onChange={onHandleChangeCities}
                                value={cities}
                                multiple
                            />
                        </div>
                        <DatePickerComponent
                            label={t('forms.date_start')}
                            value={dateStart}
                            onChange={setDateStart}
                            placeholder="___ - ___ - ___"
                            withoutIcon={true}
                            withCalencarIcon={true}
                            withClearIcon={false}
                            classes={{
                                input: styles.InfoScreenRedact__datepicker,
                                root: styles.InfoScreenRedact__datepicker_root,
                                label: styles.InfoScreenRedact__datepicker_label,
                            }}
                        />
                        <DatePickerComponent
                            label={t('forms.date_end')}
                            value={dateEnd}
                            onChange={setDateEnd}
                            placeholder="___ - ___ - ___"
                            withoutIcon={true}
                            withCalencarIcon={true}
                            withClearIcon={false}
                            classes={{
                                input: styles.InfoScreenRedact__datepicker,
                                root: styles.InfoScreenRedact__datepicker_root,
                                label: styles.InfoScreenRedact__datepicker_label,
                            }}
                        />
                    </div>

                    <div className={styles.InfoScreenRedact__loading}>
                        <PhotoLoader
                            image={photoUrl}
                            onChange={setFile}
                            onDelete={onDeleteFile}
                        />
                        {/* <LoadingFile /> */}
                    </div>
                </>
            </EditWrapper>
        </MainLayout>
    )
}
export default InfoScreenRedact
