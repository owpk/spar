import { Col, message, Row } from 'antd'
import React, {
    FC,
    useCallback,
    useContext,
    useEffect,
    useMemo,
    useRef,
    useState,
} from 'react'
import { useTranslation } from 'react-i18next'
import { EntitiesFieldName, FileSource } from '../../../config'
import { AttributesService } from '../../../services/AttributesService'
import {
    UploadFileDocType,
    useUploadFileMutation,
} from '../../../services/FileService'
import { AttributesType } from '../../../types'
import { AttributesContext } from '../../pages/attributesScreens/ShopAttributes/contexts'
import { BlockWrapper } from '../../simples/BlockWrapper'
import { Button } from '../../simples/Button'
import { ButtonType } from '../../simples/Button/Button'
import { CloseBtn } from '../../simples/CloseBtn'
import { TextField } from '../../simples/TextField'
import { DeleteModal } from '../DeleteModal'
import styles from './ShopAttributCard.module.scss'

type Props = {
    attribute: AttributesType
}

/**
 *
 * @param attribute
 * @returns
 */

const ShopAttributCard: FC<Props> = ({ attribute }) => {
    const imageRef = useRef<any>(null)
    const { t } = useTranslation()
    const [del, setDel] = useState<number>()
    const [name, setName] = useState<string>(attribute.name)

    const [file, setFile] = useState<File>()
    const inputRef = useRef<HTMLInputElement>(null)

    const [list, setList] = useContext(AttributesContext)

    const [sendFile, { error }] = useUploadFileMutation()

    const [imageUrl, setImageUrl] = useState<string>('')

    /**
     * delete attribute
     */
    const onHandleDelete = useCallback(async () => {
        const response = await AttributesService.deleteAttribute(attribute.id)
        if (response) {
            setList((prev) => prev.filter((i) => i.id !== attribute.id))
            message.success(t('suсcess_messages.delete_data'))
        } else {
            message.error(t('errors.delete_data'))
        }
        setDel(undefined)
    }, [attribute.id, setList, t])

    /**
     * click on upload button
     * open window explorer
     */
    const onUploadPhotoStart = () => {
        if (inputRef.current) {
            inputRef.current.click()
        }
    }

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
                        field: EntitiesFieldName.MERCHANT_ATTRIBUTE_ICON,
                        documentId: attribute.id,
                    },
                ],
                file: image,
            }
            await sendFile(sendData)
        },
        [attribute.id, sendFile]
    )

    /**
     * public icon
     */
    const onHandlePublick = useCallback(async () => {
        try {
            const response = await AttributesService.updateAttributes(
                attribute.id,
                { name: name }
            )
            message.success(t('suсcess_messages.save_data'))
        } catch (error) {
            message.error(t('errors.save_data'))
        }
        if (file) {
            await uploadFile(file)
        }
    }, [attribute.id, file, name, t, uploadFile])

    const onAddPhoto = useCallback(
        async (event: React.ChangeEvent<HTMLInputElement>) => {
            if (event.target.files && event.target.files[0]) {
                setImageUrl(URL.createObjectURL(event.target.files[0]))
                setFile(event.target.files[0])
            }
        },
        []
    )

    const iconUrl = useMemo(() => {
        let url: string = ''
        if (imageUrl) {
            url = imageUrl
        } else if (attribute.icon && attribute.icon.url) {
            url = attribute.icon.url
        }

        return url
    }, [attribute.icon, imageUrl])

    const getImage = useCallback(async () => {
        let url = `/files/${attribute.icon?.uuid}`
        if (!url) return
        const response = await fetch(url, {
            headers: { 'x-client-type': 'web', 'Sec-Fetch-Mode': 'no-cors' },
        })
        if (!response.ok) {
            throw new Error('Ответ сети был не ok.')
        }
        const myBlob = await response.blob()
        const objectURL = URL.createObjectURL(myBlob)
        imageRef.current.src = objectURL
    }, [attribute.icon?.uuid])

    useEffect(() => {
        if (attribute.icon && attribute.icon.url && !imageUrl) {
            getImage()
        } else if (imageUrl) {
            imageRef.current.src = imageUrl
        }
    }, [attribute.icon, getImage, imageUrl])

    useEffect(() => {
        if (error) {
            message.warning(t('errors.upload_icon'))
        }
    }, [error, t])

    return (
        <div className={styles.root}>
            <BlockWrapper>
                <div className={styles.wrapper}>
                    <div className={styles.holder}>
                        <TextField
                            value={name}
                            onChange={setName}
                            label={t('common.name')}
                            placeholder={t('options.enter_name_attribute')}
                        />
                    </div>
                    <div className={styles.holder}>
                        <div className={styles.holder2}>
                            <Row align={'middle'}>
                                <Col span={6}>
                                    <div className={styles.iconBlock}>
                                        {iconUrl && (
                                            <img
                                                ref={imageRef}
                                                alt={'icon'}
                                                // src={iconUrl}
                                            />
                                        )}
                                    </div>
                                </Col>
                                <Col>
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
                                </Col>
                            </Row>
                        </div>
                        <Row align={'middle'} justify={'space-between'}>
                            <Button
                                onClick={onHandlePublick}
                                label={t('common.save')}
                                colorText={'#ffffff'}
                                textUp={'capitalize'}
                            />
                            <CloseBtn onClick={() => setDel(attribute.id)} />
                        </Row>
                    </div>
                </div>
            </BlockWrapper>
            <DeleteModal
                onSubmit={onHandleDelete}
                onCancel={() => setDel(undefined)}
                visible={!!del}
            />
        </div>
    )
}

export default ShopAttributCard
