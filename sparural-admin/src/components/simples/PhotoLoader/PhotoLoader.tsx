import { DeleteOutlined } from '@ant-design/icons'
import React, { FC, useCallback, useEffect, useRef, useState } from 'react'
import style from './PhotoLoader.module.scss'
import { ReactComponent as PlusIcon } from '../../../assets/icons/plus_photo.svg'
import { useTranslation } from 'react-i18next'
import { useDropzone } from 'react-dropzone';
import classNames from 'classnames'
import { message } from 'antd'
import { useDeleteFileMutation } from '../../../services/FileService'
import { Phototype } from '../../../types'


type Props = {
    image?: Phototype
    onChange: (file: File) => void
    onDelete: () => void
    size?: number
    label?: string
    uuid?: string
}

/**
 * 
 * @param image 
 * @param onChange 
 * @param onDelete 
 * @param size 
 * @returns 
 */
const PhotoLoader: FC<Props> = ({ image, onChange, onDelete, size = 170, label }) => {
    const inputRef = useRef<any>()
    const inputUrlRef = useRef<string>('')
    const [imageUrl, setImageUrl] = useState<string>('')
    const [currentImage, setCurrentImage] = useState<Phototype>()
    const [file, setFile] = useState<File>()
    const { t } = useTranslation()


    const [deleteFile, status] = useDeleteFileMutation()

    const imageRef = useRef<any>(null)

    const getImage = useCallback(async () => {
        let url = `/files/${image?.uuid}`;
        if (!url) return
        const response = await fetch(url, {
            headers: { "x-client-type": "web", "Sec-Fetch-Mode": "no-cors" },
        });
        if (!response.ok) {
            // throw new Error('Ответ сети был не ok.');
        }
        const myBlob = await response.blob();
        const objectURL = URL.createObjectURL(myBlob);
        setImageUrl(objectURL)
        inputUrlRef.current = objectURL


    }, [image?.uuid])

    useEffect(() => {
        setCurrentImage(image)
    }, [image])
    useEffect(() => {

        if (currentImage?.uuid) {
            getImage()
        }

    }, [currentImage, getImage])

    const onDrop = useCallback(acceptedFiles => {
        if (acceptedFiles.length > 0) {
            onChange(acceptedFiles);
            setImageUrl(URL.createObjectURL(acceptedFiles[0]))
            // imageRef.current.src = URL.createObjectURL(acceptedFiles[0])
            setFile(acceptedFiles[0])
        }

    }, [onChange]);
    const {

        getRootProps,
        getInputProps,
        isDragActive,
        isDragAccept,
        isDragReject
    } = useDropzone({
        onDrop,
        accept: 'image/jpeg, image/png',
    });


    const onAddPhoto = useCallback(async (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files[0]) {
            if (event.target.files[0].size > 10 * 1024 * 1024) {
                message.error(`${t("errors.file_size")}`)
                return
            }
            // imageRef.current.src = URL.createObjectURL(event.target.files[0])
            setImageUrl(URL.createObjectURL(event.target.files[0]))
            onChange(event.target.files[0])
            setFile(event.target.files[0])
        }
    }, [onChange, t])

    const onHandleDelete = async () => {
        if (file) {
            setImageUrl(inputUrlRef.current)
            setFile(undefined)
            return
        } else if (currentImage?.uuid) {

            await deleteFile(currentImage?.uuid)
            setCurrentImage(undefined)
            inputUrlRef.current = ''
            setImageUrl('')
            onDelete()
        }
    }

    useEffect(() => {
        if (status.isSuccess) {
            message.success(t("suсcess_messages.delete_file"))
        }
        if (status.isError) {
            message.error(`${t("errors.delete_file")} ${status.error || ''}`)
        }
    }, [status.error, status.isError, status.isSuccess, t])
    return (
        <div className={style.root}>

            <label
                {...getRootProps()}
                style={{
                    height: size,
                    width: size,

                }}
                className={classNames(style.addButton)}>
                <input
                    {...getInputProps()}
                    style={{
                        width: 0,
                        height: 0
                    }}
                    accept=".png, .jpg, .jpeg"
                    ref={inputRef}
                    type={'file'}
                    onChange={onAddPhoto}
                />
                <div className={classNames(style.avatar_uploader, {
                    [style.activeDrop]: isDragActive,
                    [style.error]: isDragReject,
                    [style.accept]: isDragAccept
                })}
                    style={{
                        height: size,
                        width: size,
                        marginTop: 8
                    }}

                >
                    {!imageUrl ? <>
                        <div ><PlusIcon style={{
                            maxWidth: size / 3,
                            maxHeight: size / 3
                        }} /></div>
                        <div className={style.label}>{label ? label : t("common.upload_photo")}</div>
                    </>
                        :
                        <div className={style.imgHolder}>
                            <div
                                {...getRootProps()}
                                style={{
                                    height: size,
                                    width: size,
                                    // marginTop: 8
                                }}
                                className={style.imageWrapper}>

                                <img
                                    ref={imageRef}
                                    // @ts-ignore
                                    // authsrc={`/files/${image?.uuid}`}
                                    src={imageUrl}
                                    alt="avatar" style={{ width: '100%' }} />

                            </div>

                        </div>}
                </div>
            </label>
            <div style={{ marginTop: 8 }} onClick={onHandleDelete} className={style.deleteIcon}>
                {imageUrl && <DeleteOutlined style={{
                    color: '#E42B24',
                    fontSize: 25,
                }} />}
            </div>


        </div>
    )
}

export default React.memo(PhotoLoader)