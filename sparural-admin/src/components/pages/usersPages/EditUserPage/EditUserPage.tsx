import { Col, message, Row } from 'antd'
import produce from 'immer'
import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation, useNavigate } from 'react-router-dom'
import { EntitiesFieldName, FileSource, Routes } from '../../../../config'
import { regMail, regOnlyNumbers } from '../../../../constants'
import { useAppDispatch, useAppSelector } from '../../../../hooks/store'
import { UploadFileDocType, useUploadFileMutation } from '../../../../services/FileService'
import { UsersService } from '../../../../services/UsersService'
import { setLoading } from '../../../../store/slices/appSlice'
import { appRolesOptions } from '../../../../store/slices/storageRoles'

import { CreateUserType, Phototype, RoleCode } from '../../../../types'
import { MainLayout } from '../../../complexes/MainLayout'
import { EditWrapper } from '../../../simples/EditWrapper'
import { InputHolder } from '../../../simples/InputHolder'
import { PhotoLoader } from '../../../simples/PhotoLoader'
import { Selector } from '../../../simples/Selector'
import { SelectOption } from '../../../simples/Selector/OptionItem'
import { TextField } from '../../../simples/TextField'

type ErrorType = {
    email?: string
    phone?: string
}

const EditUserPage: FC = () => {
    const { t } = useTranslation()
    const [sendFile, {error}] = useUploadFileMutation()

    const rolesOptions = useAppSelector(appRolesOptions)
    const dispatch = useAppDispatch()
    const userId = useLocation().search.split('=')[1]
    const navigation = useNavigate()
    const [lastName, setLastName] = useState<string>('')
    const [firstName, setFirstName] = useState<string>('')
    const [phoneNumber, setPhoneNumber] = useState<string>('')
    const [patronymicName, setPatronymicName] = useState<string>('')
    const [email, setEmail] = useState<string>('')
    const [role, setRole] = useState<SelectOption[]>([])
    const [file, setFile] = useState<File>()
    const [photo, setPhoto] = useState<Phototype>()
    const currentId = useRef<number>(0)
    const [errors, setErrors] = useState<ErrorType>({})
    const rolesrr = rolesOptions.filter((item) => item.value !== 1)

    const onHandleChangeCities = useCallback(
        (data: SelectOption) => {
            const find = role.find((i) => i.value === data.value)

            if (!!find) {
                setRole((prev) => prev.filter((j) => j.value !== data.value))
            } else {
                setRole(
                    produce((draft) => {
                        draft.push(data)
                    })
                )
            }
        },
        [role]
    )

    /**
     * fetch one screen
     */
    const getOneScreenById = useCallback(async () => {
        try {
            dispatch(setLoading(true))
            const response = await UsersService.getUserById(Number(userId))
            setLastName(response.lastName || '')
            setFirstName(response.firstName || '')
            setPatronymicName(response.patronymicName || '')
            setPhoneNumber(response.phoneNumber || '')
            setEmail(response.email || '')
            setRole(
                response.roles.map((role) => {
                    return {
                        value: role.id,
                        label: role.name,
                    }
                })
            )
            setPhoto(response.photo || undefined)

            currentId.current = Number(userId)
            dispatch(setLoading(false))
        } catch (error) {
            message.error(t('errors.get_data'))
            dispatch(setLoading(false))
        }
    }, [dispatch, t, userId])

    /**
     * creating draft
     */
    const createDraft = async () => {
        try {
            const response = await UsersService.createUser({ draft: true })
            currentId.current = response.id
        } catch (error) {}
    }

    /**
     * save function
     */

    const onHandleSave = useCallback(async () => {
        if (!email.match(regMail)) {
            setErrors({ ...errors, email: t('errors.wrong_mail_format') })
            return
        }
        if(!phoneNumber){
            setErrors({ ...errors, phone:  t("errors.required_field") })
            return
        }
        const sendData: CreateUserType = {
            firstName,
            lastName,
            patronymicName,
            phoneNumber,
            email,
            draft: false,
            roles: role.map((role) => {
                return { id: role.value }
            }),
        }

        try {
            const response = await UsersService.updateUser(
                Number(currentId.current),
                sendData
            )
            message.success(t('suсcess_messages.update_data'))
            navigation(Routes.USERS_SCREEN)
        } catch (error) {
            message.error(t('errors.update_data'))
        }
        if(file){
            await uploadFile(file)
        }
    }, [email, errors, file, firstName, lastName, navigation, patronymicName, phoneNumber, role, t])

     /**
     * upload image
     */
      const uploadFile = useCallback( async(image: File) => {
        const sendData: UploadFileDocType = {
            source: FileSource.REQUEST,
            'source-parameters':JSON.stringify({}),
            entities:[{field: EntitiesFieldName.USER_PHOTO, documentId:currentId.current }],
            file: image
        }

        
        
       await sendFile(sendData)

     },[currentId, sendFile])

     useEffect(() => {
        if(error){
            message.warning(t("errors.upload_photo"))
        }
    },[error, t])

    /**
     * fetching data if we edit Info screen
     */
    useEffect(() => {
        if (!!userId) {
            getOneScreenById().then()
        } else {
            createDraft().then()
        }
    }, [userId])

    /**
     * change phone number
     */

    const onChangePhoneNumber = useCallback((e: string) => {
        if (e.match(regOnlyNumbers)) {
            setPhoneNumber(e)
            setErrors(prev => ({ ...prev, phone: '' }))
        }
    }, [])

    /**
     * change email
     */
    const onChangeEmail = useCallback(
        (e: string) => {
            setErrors(prev => ({ ...prev, email: '' }))
            setEmail(e)
        },
        []
    )

    return (
        <MainLayout title={t('screen_title.users_page')}>
            <EditWrapper
                onSave={onHandleSave}
                title={t(!userId ? 'common.add' : 'common.edit_full')}
            >
                <>
                    <Row gutter={[16, 16]}>
                        <Col>
                            <InputHolder>
                                <TextField
                                    label={t('forms.last_name')}
                                    onChange={setLastName}
                                    value={lastName}
                                />
                            </InputHolder>
                            <InputHolder>
                                <TextField
                                    label={t('forms.first_name')}
                                    onChange={setFirstName}
                                    value={firstName}
                                />
                            </InputHolder>
                            <InputHolder>
                                <TextField
                                    label={t('forms.patronymic')}
                                    onChange={setPatronymicName}
                                    value={patronymicName}
                                />
                            </InputHolder>
                        </Col>
                        <Col>
                            <InputHolder>
                                <TextField
                                error={errors.phone}
                                    label={t('forms.phone_number')}
                                    onChange={onChangePhoneNumber}
                                    value={phoneNumber}
                                />
                            </InputHolder>
                            <InputHolder>
                                <TextField
                                    error={errors?.email || ''}
                                    label={t('forms.email_address')}
                                    onChange={onChangeEmail}
                                    value={email}
                                />
                            </InputHolder>
                            <InputHolder>
                                <Selector
                                    label={t('forms.role')}
                                    options={rolesrr}
                                    onChange={onHandleChangeCities}
                                    value={role}
                                    multiple
                                />
                            </InputHolder>
                        </Col>
                        <Col>
                            <PhotoLoader
                                size={260}
                                image={photo}
                                onChange={setFile}
                                onDelete={() => setFile(undefined)}
                            />
                        </Col>
                    </Row>
                </>
            </EditWrapper>
        </MainLayout>
    )
}

export default EditUserPage
