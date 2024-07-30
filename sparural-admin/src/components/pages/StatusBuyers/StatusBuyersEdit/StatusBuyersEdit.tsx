import { message } from 'antd'
import image from 'antd/lib/image'
import React, { FC, useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation, useNavigate } from 'react-router-dom'
import { EntitiesFieldName, FileSource, Routes } from '../../../../config'
import { UploadFileDocType, useUploadFileMutation } from '../../../../services/FileService'
import { StatusBuyersService } from '../../../../services/StatusBuyersService'
import {
    CreateStatusBuyersType,
    Phototype,
} from '../../../../types'
import { createFormDataFile } from '../../../../utils/helpers'
import { IconTable } from '../../../complexes/IconTable'
import { MainLayout } from '../../../complexes/MainLayout'
import { Button } from '../../../simples/Button'
import { ButtonType } from '../../../simples/Button/Button'
import { EditWrapper } from '../../../simples/EditWrapper'
import { TextField } from '../../../simples/TextField'
import styles from './StatusBuyersEdit.module.scss'

const StatusBuyersEdit: FC = () => {
    const { t } = useTranslation()
    const id = useLocation().search.split('=')[1]

    const navigation = useNavigate()
    const [name, setName] = useState<string>('')
    const [porog, setPorog] = useState<string>('')
    const [file, setFile] = useState<File>()
    const [icons, setIcons] = useState<Phototype>()
    const [photoUrl, setPhotoUrl] = useState<string>('')
    const [loading, setLoading] = useState(false)
    const [currentId, setCurrentId] = useState<number>(id ? +id : 0)
    const inputRef = useRef<HTMLInputElement>(null)

    const [sendFile, {error}] = useUploadFileMutation()

    const [imageUrl, setImageUrl] = useState<string>('')

    const getOneScreenById = useCallback(async () => {
        setLoading(true)
        try {
            const response = await StatusBuyersService.getStatusBuyersById(
                Number(id)
            )
            setName(response.name)
            setPorog(`${response.threshold}`)
            setIcons(response.icon)
            setPhotoUrl(response?.icon?.uuid || '')

            setLoading(false)
        } catch (error) {
            message.error(t('errors.get_data'))
            setLoading(false)
        }
    }, [id, t])

    const createInfoScreen = async () => {
        try {
            const sendData: CreateStatusBuyersType = { draft: true }
            const response = await StatusBuyersService.createStatusBuyers(
                sendData
            )
            setCurrentId(response.id)
        } catch (error) {
            message.error(t('errors.save_data'))
        }
    }

    const onHandleSave = useCallback(async () => {
        if(file){
            await uploadFile(file)
        }
        
        navigation(Routes.STATUS_BUYERS)
        // const sendData: CreateStatusBuyersType = {
        //     name: name,
        //     threshold: +porog,
        //     draft: false,
        // }

        try {
            // const response = await StatusBuyersService.updateStatusBuyers(
            //     Number(currentId),
            //     sendData
            // )
            // message.success(t('success.update_data'))
            // navigation(Routes.STATUS_BUYERS)
        } catch (error) {
            message.error(t('errors.update_data'))
        }
    }, [file, porog, currentId, t, navigation])

    useEffect(() => {
        if (!!id) {
            getOneScreenById().then()
        } else {
            createInfoScreen().then()
        }
    }, [id])

    const onUploadPhotoStart = () => {
        if (inputRef.current) {
            inputRef.current.click()
        }
    }

    const onAddPhoto = useCallback(async (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files[0]) {
            setImageUrl(URL.createObjectURL(event.target.files[0]))
            setFile(event.target.files[0])
        }
    }, [])

    /**
* upload image
*/
    const uploadFile = useCallback(async (file: File) => {

            // const sendData = createFormDataFile(file)
            // const response = await StatusBuyersService.uploadPhoto(+id, sendData)
            const sendData: UploadFileDocType = {
                source: FileSource.REQUEST,
                'source-parameters': JSON.stringify({}),
                entities:[{field: EntitiesFieldName.CLIENT_STATUS_ICON, documentId: currentId }],
                file
            }
            await sendFile(sendData)
        
    }, [currentId, sendFile])

    const iconUrl = useMemo(() => {
        let url: string = ''
        if (imageUrl) {
            url = imageUrl
        } else if (photoUrl) {
            url = photoUrl
        }

        return url
    }, [photoUrl, imageUrl])

    return (
        <MainLayout isLoading={loading} title={t('screen_title.status_buyers')}>
            <EditWrapper
                title={t(!id ? 'common.add' : 'common.edit_full')}
                onSave={onHandleSave}
            >
                <div className={styles.StatusBuyersEdit}>
                    <div className={styles.StatusBuyersEditBlock}>
                        <TextField
                            disabled
                            label={t('common.name')}
                            value={name}
                            onChange={setName}
                        />
                        <TextField
                            disabled
                            label={t('common.threshold')}
                            value={porog}
                            onChange={setPorog}
                        />
                    </div>
                    <div className={styles.StatusBuyersLoad}>
                        <div className={styles.iconBlock}>
                            {iconUrl && <IconTable size={48} photo={iconUrl} />}
                        </div>
                        <Button
                            onClick={onUploadPhotoStart}
                            label={t('common.upload')}
                            typeStyle={ButtonType.SECOND}
                            textUp={'capitalize'}
                        />
                        <input
                            onChange={onAddPhoto}
                            className={styles.inputPhoto}
                            ref={inputRef}
                            type={'file'}
                        />
                    </div>
                </div>
            </EditWrapper>
        </MainLayout>
    )
}

export default StatusBuyersEdit
