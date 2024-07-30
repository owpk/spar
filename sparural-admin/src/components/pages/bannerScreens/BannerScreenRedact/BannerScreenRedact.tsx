import { message } from 'antd'
import produce from 'immer'
import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation, useNavigate } from 'react-router-dom'
import { EntitiesFieldName, FileSource, Routes } from '../../../../config'
import { regOnlyNumbers } from '../../../../constants'
import { useAppSelector } from '../../../../hooks/store'
import { BannerPlaceService } from '../../../../services/BannerPlaceService'
import { appCitiesOptions } from '../../../../store/slices/storage'
import {
    CitySelectType,
    collectionScrensType,
    CreateBannerPlaceType,
    Phototype,
} from '../../../../types'
import { MainLayout } from '../../../complexes/MainLayout'
import { EditWrapper } from '../../../simples/EditWrapper'
import { InputRadio } from '../../../simples/InputRadio'
import { PhotoLoader } from '../../../simples/PhotoLoader'
import { Selector } from '../../../simples/Selector'
import { SelectOption } from '../../../simples/Selector/OptionItem'
import { TextField } from '../../../simples/TextField'
import styles from './BannerScreenRedact.module.scss'
import {
    UploadFileDocType,
    useUploadFileMutation,
} from '../../../../services/FileService'
import { DatePickerComponent } from '../../../simples/DatePickerComponent'
enum LinkType {
    INNER = 'inner',
    OUTSIDE = 'outside',
}

type Errors = {
    name?: string
    place?: string
    cities?: string
    innerLink?: string
    link?: string
    text?: string
}

const BannerScreenRedact: FC = () => {
    const { t } = useTranslation()
    const navigation = useNavigate()
    const id = useLocation().search.split('=')[1]

    const citiesOptions = useAppSelector(appCitiesOptions)

    const [name, setName] = useState<string>('')
    const [text, setText] = useState<string>('')
    const [place, setPlace] = useState<string>('')
    const [link, setLink] = useState<string>('')
    const [innerLink, setInnerLink] = useState<SelectOption>()
    const [linkType, setLinkType] = useState<LinkType>(LinkType.INNER)
    const [cities, setCities] = useState<Array<SelectOption>>([])
    const [dateStart, setDateStart] = useState<number>()
    const [dateEnd, setDateEnd] = useState<number>()
    const [extraOptions, setExtraOptions] = useState<Array<SelectOption>>([])
    const [loading, setLoading] = useState<boolean>(false)
    const [file, setFile] = useState<File>()
    const [photoUrl, setPhotoUrl] = useState<Phototype>()
    // const [errors, setErrors] = useState<string>("");
    const offset = useRef(0)
    const [listSelector, setListSelector] = useState<Array<SelectOption>>([])

    const [currentId, setCurrentId] = useState<number>(id ? +id : 0)

    const [sendFile] = useUploadFileMutation()

    const [errors, setErrors] = useState<Errors>({})

    // chang city
    const onHandleChangeCities = useCallback(
        (data: SelectOption) => {
            const find = cities.find((i) => i.value === data.value)

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
            setErrors((prev) => ({ ...prev, cities: undefined }))
        },
        [citiesOptions, extraOptions]
    )
    /**
     * fetch one screen
     */
    const getOneScreenById = useCallback(async () => {
        try {
            setLoading(true)
            const response = await BannerPlaceService.getBannerPlaceById(
                Number(id)
            )

            const gotCities: SelectOption[] = response.cities.map(
                (city: { id: number; name: string }) => {
                    return {
                        value: city.id,
                        label: city.name,
                    }
                }
            )

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
            setDateStart(response.dateStart)
            setDateEnd(response.dateEnd)
            setPlace(`${response.order}` || '')
            setName(response.title)
            setText(response.description)
            setPhotoUrl(response?.photo)
            setLink(response?.url || '')
            setLinkType(response.url ? LinkType.OUTSIDE : LinkType.INNER)
            setInnerLink({
                value: String(response.mobileNavigateTarget?.id),
                label: response.mobileNavigateTarget?.name || '',
            })
            setLoading(false)
        } catch (error) {
            message.error(t('errors.get_data'))
            setLoading(false)
        }
    }, [citiesOptions, id, t])

    // fetch data for dropdown link
    const getSelectDropDown = useCallback(async () => {
        try {
            setLoading(true)
            const response = await BannerPlaceService.getSelectDropDown({
                offset: offset.current,
            })

            const demoarr = response.map((item: collectionScrensType) => {
                return {
                    value: String(item.id),
                    label: item.name,
                }
            })
            setListSelector(demoarr)
            setLoading(false)
        } catch (error) {
            message.error(t('errors.get_data'))
            setLoading(false)
        }
    }, [t])

    /**
     * save function
     */
    const onHandleSave = useCallback(async () => {
        let hasError = false
        if (!place) {
            setErrors((prev) => ({
                ...prev,
                place: t('errors.required_field'),
            }))
            if (!place.match(regOnlyNumbers)) {
                setErrors((prev) => ({
                    ...prev,
                    place: t('errors.banner_place'),
                }))
            }
            hasError = true
        }
        if (!name) {
            setErrors((prev) => ({ ...prev, name: t('errors.required_field') }))
            hasError = true
        }
        if (linkType === LinkType.OUTSIDE && !link) {
            setErrors((prev) => ({ ...prev, link: t('errors.required_field') }))
            hasError = true
        }
        if (linkType === LinkType.INNER && !innerLink) {
            setErrors((prev) => ({
                ...prev,
                innerLink: t('errors.required_field'),
            }))
            hasError = true
        }
        if (!text) {
            setErrors((prev) => ({ ...prev, text: t('errors.required_field') }))
            hasError = true
        }
        if (cities.length === 0 && extraOptions.length === 0) {
            setErrors((prev) => ({
                ...prev,
                cities: t('errors.required_field'),
            }))
            hasError = true
        }

        if (hasError) return

        const sendData: CreateBannerPlaceType = {
            isPublic: false,
            title: name,
            description: text,
            url: linkType === LinkType.OUTSIDE ? link : null,

            mobileNavigateTargetId:
                linkType === LinkType.INNER ? Number(innerLink?.value) : null,
            citySelect:
                extraOptions.length > 0
                    ? (extraOptions[0].value as CitySelectType)
                    : CitySelectType.SELECTION,
            cities:
                extraOptions.length === 0
                    ? cities.map((item) => {
                          return { id: item.value, name: item.label }
                      })
                    : [],
            dateStart: dateStart ? dateStart : 0,
            dateEnd: dateEnd ? dateEnd : 0,
            order: place,
            draft: false,
        }

        try {
            await BannerPlaceService.updateBannerPlace(
                Number(currentId),
                sendData
            )
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
        navigation(Routes.BANNER_SCREEN)
    }, [
        place,
        name,
        linkType,
        link,
        innerLink,
        text,
        cities,
        dateStart,
        dateEnd,
        extraOptions,
        file,
        navigation,
        t,
        currentId,
    ])

    /**
     * create draft
     */
    const onCreateBanner = useCallback(async () => {
        try {
            const sendData: CreateBannerPlaceType = {
                citySelect: CitySelectType.NOWHERE,
                draft: true,
                // url: ""
            }
            const response = await BannerPlaceService.createBannerPlace(
                sendData
            )
            if (response) {
                setCurrentId(response.id)
            }
        } catch (error) {}
    }, [])

    /**
     * fetching data if we edit Info screen
     */
    useEffect(() => {
        getSelectDropDown()
        if (!!id) {
            getOneScreenById().then()
        } else {
            onCreateBanner().then()
        }
    }, [id])

    const onChangePlace = useCallback((e: string) => {
        setErrors((prev) => ({ ...prev, place: undefined }))
        setPlace(e.replace(/[^0-9]/g, ''))
    }, [])

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
                        field: EntitiesFieldName.ONBOX_BANNER_PHOTO,
                        documentId: currentId,
                    },
                ],
                file: image,
            }
            await sendFile(sendData)
        },
        [currentId, sendFile]
    )

    return (
        <MainLayout isLoading={loading} title={t('screen_title.banner_place')}>
            <EditWrapper
                onSave={onHandleSave}
                title={t(!id ? 'common.add' : 'common.edit_full')}
            >
                <div className={styles.BannerScreenRedact__contener}>
                    <div className={styles.BannerScreenRedact__item}>
                        <div className={styles.BannerScreenRedact__item__one}>
                            <div style={{ marginBottom: '16px' }}>
                                <TextField
                                    maxLength={50}
                                    label={t('forms.name')}
                                    value={name}
                                    error={errors.name}
                                    onChange={(e) => {
                                        setErrors((prev) => ({
                                            ...prev,
                                            name: undefined,
                                        }))
                                        setName(e)
                                    }}
                                />
                            </div>
                            <div style={{ marginBottom: '16px' }}>
                                <TextField
                                    label={t('forms.place')}
                                    value={place}
                                    onChange={onChangePlace}
                                    error={errors.place}
                                />
                            </div>
                            <div style={{ marginBottom: '40px' }}>
                                <Selector
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
                                    onChange={(e) => {
                                        setErrors((prev) => ({
                                            ...prev,
                                            link: undefined,
                                        }))
                                        onHandleChangeCities(e)
                                    }}
                                    value={cities}
                                    error={errors.cities}
                                    multiple
                                />
                            </div>
                            <div
                                className={
                                    styles.BannerScreenRedact__datepicker_container
                                }
                            >
                                <DatePickerComponent
                                    label={t('forms.date_start')}
                                    value={dateStart}
                                    onChange={setDateStart}
                                    placeholder="___ - ___ - ___"
                                    withoutIcon={true}
                                    withCalencarIcon={true}
                                    withClearIcon={false}
                                    classes={{
                                        input: styles.BannerScreenRedact__datepicker,
                                        root: styles.BannerScreenRedact__datepicker_root,
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
                                        input: styles.BannerScreenRedact__datepicker,
                                        root: styles.BannerScreenRedact__datepicker_root,
                                    }}
                                />
                            </div>
                        </div>
                        <div className={styles.BannerScreenRedact__item__two}>
                            <TextField
                                maxLength={255}
                                label={t('forms.text')}
                                value={text}
                                onChange={(e) => {
                                    setErrors((prev) => ({
                                        ...prev,
                                        text: undefined,
                                    }))
                                    setText(e)
                                }}
                            />
                            <div
                                className={
                                    styles.BannerScreenRedact__item__two__link
                                }
                            >
                                <div
                                    className={
                                        styles.BannerScreenRedact__item__two__link__radio
                                    }
                                >
                                    <InputRadio
                                        styleCircle="50%"
                                        isChecked={linkType === LinkType.INNER}
                                        onChange={() =>
                                            setLinkType(LinkType.INNER)
                                        }
                                    />
                                </div>

                                <div style={{ width: '100%' }}>
                                    <Selector
                                        disabled={linkType !== LinkType.INNER}
                                        label={t('forms.inner_link')}
                                        options={listSelector}
                                        // value={cities}
                                        multiple
                                        onChange={(e) => {
                                            setErrors((prev) => ({
                                                ...prev,
                                                innerLink: undefined,
                                            }))
                                            setInnerLink(e)
                                        }}
                                        value={innerLink ? [innerLink] : []}
                                        error={errors.innerLink}
                                    />
                                </div>
                            </div>
                            <div
                                className={
                                    styles.BannerScreenRedact__item__two__link
                                }
                            >
                                <div
                                    className={
                                        styles.BannerScreenRedact__item__two__link__radio
                                    }
                                >
                                    <InputRadio
                                        styleCircle="50%"
                                        isChecked={
                                            linkType === LinkType.OUTSIDE
                                        }
                                        onChange={() =>
                                            setLinkType(LinkType.OUTSIDE)
                                        }
                                    />
                                </div>

                                <div style={{ width: '100%' }}>
                                    <TextField
                                        disabled={linkType !== LinkType.OUTSIDE}
                                        label={t('forms.link')}
                                        value={link}
                                        onChange={(e) => {
                                            setErrors((prev) => ({
                                                ...prev,
                                                link: undefined,
                                            }))
                                            setLink(e)
                                        }}
                                        error={errors.link}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className={styles.BannerScreenRedact__loading}>
                        <PhotoLoader
                            image={photoUrl}
                            onChange={setFile}
                            onDelete={() => setFile(undefined)}
                        />
                        {/* <LoadingFile /> */}
                    </div>
                </div>
            </EditWrapper>
        </MainLayout>
    )
}
export default BannerScreenRedact
