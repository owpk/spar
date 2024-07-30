import React, { FC, useState } from 'react'
import { Upload, message } from 'antd'
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons'
import styles from './LoadingFile.module.scss'

type Props = {}
const LoadingFile: FC<Props> = () => {
    const [loader, setLoader] = useState(false)
    const [imageUrl, setImageUrl] = useState()

    function getBase64(
        img: Blob,
        callback: {
            (imageUrl: any): void
            (arg0: string | ArrayBuffer | null): any
        }
    ) {
        const reader = new FileReader()
        reader.addEventListener('load', () => callback(reader.result))
        reader.readAsDataURL(img)
    }
    function beforeUpload(file: { type: string; size: number }) {
        const isJpgOrPng =
            file.type === 'image/jpeg' || file.type === 'image/png'
        if (!isJpgOrPng) {
            message.error('You can only upload JPG/PNG file!')
        }
        const isLt2M = file.size / 1024 / 1024 < 2
        if (!isLt2M) {
            message.error('Image must smaller than 2MB!')
        }
        return isJpgOrPng && isLt2M
    }

    const handleChange = (info: {
        file: { status: string; originFileObj: Blob }
    }) => {
        if (info.file.status === 'uploading') {
            setLoader(true)
            return
        }
        if (info.file.status === 'done') {
            // Get this url from response in real world.
            getBase64(info.file.originFileObj, (imageUrl) =>
                setImageUrl(imageUrl)
            )
        }
        setLoader(false)
    }
    const uploadButton = (
        <div>
            {loader ? <LoadingOutlined /> : <PlusOutlined />}
            <div style={{ marginTop: 8 }}>Загрузить Jpg, gif, png ..</div>
        </div>
    )

    return (
        <Upload
            name="avatar"
            listType="picture"
            showUploadList={false}
            className={styles.wrapper}
            // action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
            beforeUpload={beforeUpload}
            // onChange={handleChange}
        >
            {imageUrl ? (
                <img src={imageUrl} alt="avatar" style={{ width: '100%' }} />
            ) : (
                <div>
                    {/* {loader ? <LoadingOutlined /> : <PlusOutlined />} */}
                    <div
                        className={styles.avatar_uploader}
                        style={{ marginTop: 8 }}
                    >
                        Загрузить Jpg, gif, png ..
                    </div>
                </div>
            )}
        </Upload>
    )
}
export default LoadingFile
