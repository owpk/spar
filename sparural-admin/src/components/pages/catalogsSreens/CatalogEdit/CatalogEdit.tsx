import { message } from 'antd'
import produce from 'immer'
import React, { FC, useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation, useNavigate } from 'react-router-dom'
import { EntitiesFieldName, FileSource, Routes } from '../../../../config'
import { useAppSelector } from '../../../../hooks/store'
import { CatalogsPagesService } from '../../../../services/CatalogsService'
import { UploadFileDocType, useUploadFileMutation } from '../../../../services/FileService'
import { appCitiesOptions } from '../../../../store/slices/storage'
import {
    CitySelectType,
    CreateCatalogType,
    Phototype,
} from '../../../../types'
import { isValidUrl } from '../../../../utils/helpers'
import { MainLayout } from '../../../complexes/MainLayout'
import { EditWrapper } from '../../../simples/EditWrapper'
import { InputHolder } from '../../../simples/InputHolder'
import { PhotoLoader } from '../../../simples/PhotoLoader'
import { Selector } from '../../../simples/Selector'
import { SelectOption } from '../../../simples/Selector/OptionItem'
import { TextField } from '../../../simples/TextField'
import styles from './CatalogEdit.module.scss'

type Errors = {
    name?: string
    link?: string
}

const CatalogEdit: FC = () => {
    const { t } = useTranslation()
    const id = useLocation().search.split('=')[1]
    const citiesOptions = useAppSelector(appCitiesOptions)
    const navigation = useNavigate()
    const [name, setName] = useState<string>('')
    const [link, setLink] = useState<string>('')
    const [file, setFile] = useState<File>()
    const [photoUrl, setPhotoUrl] = useState<Phototype>()
    const [cities, setCities] = useState<Array<SelectOption>>([])
    const [extraOptions, setExtraOptions] = useState<Array<SelectOption>>([])
    const [loading, setLoading] = useState(false)
    const [currentId, setCurrentId] = useState<number>(id ? +id : 0)

    const [errors, setErrors] = useState<Errors>({})

    const [sendFile, result] = useUploadFileMutation()

    /**
     * change city via selector
     */
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

    /**
     * if we click on extra options in Selector CIty
     */
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
        },
        [citiesOptions, extraOptions]
    )

    /**
     * get one catalog by id
     */
    const getOneCatalogById = useCallback(async () => {
        try {
            const response = await CatalogsPagesService.getCatalogsPagesById(
                Number(id)
            )

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
                response.cities[0].id === null
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
            setName(response.name || name)
            setLink(response.url || link)
            setLoading(false)
            setPhotoUrl(response?.photo)
        } catch (error) {
            message.error(t('errors.get_data'))
            setLoading(false)
        }
    }, [citiesOptions, id, link, name, t])

    /**
     * create new Catalog
     */
    const createCatalog = async () => {
        try {
            // cities временно
            const sendData: CreateCatalogType = { draft: true, cities: [] }
            const response = await CatalogsPagesService.createCatalogsPages(
                sendData
            )
            setCurrentId(response.id)
        } catch (error) {
            message.error(t('errors.save_data'))
        }
    }

    const onHandleSave = useCallback(async () => {
        if (!name) { setErrors(prev => ({ ...prev, name: t("errors.required_field") })) }
        if (!link) { setErrors(prev => ({ ...prev, link: t("errors.required_field") }))}
        if (link && !isValidUrl(link)) { setErrors(prev => ({ ...prev, link: t("errors.wrong_url") }))}
        const sendData: CreateCatalogType = {
            name: name,
            url: link,
            citySelect:
                extraOptions.length > 0
                    ? (extraOptions[0].value as CitySelectType)
                    : CitySelectType.SELECTION,
            cities: cities.map((item) => {
                return { id: item.value, name: item.label }
            }),
            draft: false,
        }

        try {
            await CatalogsPagesService.updateCatalogsPages(
                Number(currentId),
                sendData
            )
            message.success(t('success.update_data'))
            if (file) {
                try {
                    await uploadFile(file)
                } catch (error) {
                    message.error(t('errors.upload_photo'))
                }
            }
            navigation(Routes.CATALOG)
        } catch (error) {
            message.error(t('errors.update_data'))
        }
      
    }, [name, link, extraOptions, file, cities, currentId, t, navigation])

    useEffect(() => {
        if (!!id) {
            getOneCatalogById().then()
        } else {
            createCatalog().then()
        }
    }, [id])

    /**
* upload image
*/
    const uploadFile = useCallback(async (image: File) => {
        const sendData: UploadFileDocType = {
            source: FileSource.REQUEST,
            'source-parameters': JSON.stringify({}),
            entities: [{ field: EntitiesFieldName.CATALOG, documentId: currentId }],
            file: image
        }
        await sendFile(sendData)

    }, [currentId, sendFile])

    return (
        <MainLayout isLoading={false} title={t('screen_title.catalogs')}>
            <EditWrapper
                title={t(!id ? 'common.add' : 'common.edit_full')}
                onSave={onHandleSave}
            >
                <div className={styles.CatalogEdit}>
                    <div className={styles.CatalogEditBlock}>
                        <InputHolder>
                            <TextField
                                error={errors.name || ''}
                                label={t('common.name')}
                                value={name}
                                onChange={(e) => {
                                    setErrors({ ...errors, name: undefined })
                                    setName(e)
                                }
                                }
                            />
                        </InputHolder>
                        <InputHolder>
                            <TextField
                                error={errors.link || ''}
                                label={t('common.link')}
                                value={link}
                                onChange={(e) => {
                                    setErrors({ ...errors, link: undefined })
                                    setLink(e)
                                }
                                }
                            />
                        </InputHolder>
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
                            onChange={onHandleChangeCities}
                            value={cities}
                            multiple
                        />
                    </div>
                    <div className={styles.CatalogEditBlockLoading}>
                        <PhotoLoader
                            image={photoUrl}
                            onChange={setFile}
                            onDelete={() => { setFile(undefined) }}
                        />
                    </div>
                </div>
            </EditWrapper>
        </MainLayout>
    )
}

export default CatalogEdit
