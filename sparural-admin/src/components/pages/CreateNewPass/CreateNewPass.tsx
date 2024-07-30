import { Form, Formik } from 'formik'
import React, { FC, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { AuthLayout } from '../../complexes/AuthLayout'
import { Button } from '../../simples/Button'
import { FormField } from '../../simples/FormField'
import * as yup from 'yup'
import styles from './CreateNewPass.module.scss'
import { ValidStatus } from '../../simples/FormField/FormField'

type MyFormValues = {
    code: string
    password: string
    pass_repeat: string
}

/**
 *
 * @returns компонент восстановление пароля . Создание нового пароля
 */
const CreateNewPass: FC = () => {
    const { t } = useTranslation()
    const initialValues: MyFormValues = { code: '', password: '', pass_repeat: '' }
    const validationSchema = yup.object({
        phoneNumber: yup
            .string()
            .matches(/^\d+$/, t("errors.only_numbers"))
            .required(t("errors.required_field")),
        password: yup
            .string()
            .min(6, t("errors.min_pass"))
            .max(50, t("errors.max_pass"))
            .matches(/.*(?=.*\d).*/, t("errors.pass_one_number"))
            // !TODO enable when correct data will be available
            .matches(/.*(?=.*[A-Z]).*/, t("errors.pass_one_capitaloze"))
            .matches(/.*(?=.*[a-z]).*/, t("errors.pass_one_lowercase"))
            .required(t("errors.required_field")),
        pass_repeat: yup
            .string()
            .oneOf([yup.ref('password')], t("authorisation.different_passwords"))
    })

    const onSubmit = async (values: any) => {

    }
    return (
        <AuthLayout title={'Восстановление пароля'}>
            <Formik
                validationSchema={validationSchema}
                initialValues={initialValues}
                onSubmit={onSubmit}
            >
                {({ errors, touched, isValidating }) => (
                    <Form>
                        <div className={styles.inputHolder}>
                            <FormField
                                classes={{
                                    label: styles.label,
                                }}
                                id={'code'}
                                name={'code'}
                                label={t("authorisation.enter_code")}
                                placeholder={t("authorisation.enter_code")}
                            />
                        </div>
                        <div className={styles.inputHolder}>
                            <FormField
                                isSecure
                                classes={{
                                    label: styles.label,
                                }}
                                label={'Новый пароль'}
                                id={'password'}
                                name={'password'}
                                placeholder={''}
                                error={errors.password && touched.password ? errors.password : ''}
                                isValide={touched.password ? ValidStatus.VALID : ValidStatus.EMPTY}
                            />
                        </div>
                        <div className={styles.inputHolder}>
                            <FormField
                                // validate={comparePass}
                                isSecure
                                classes={{
                                    label: styles.label,
                                }}
                                label={t("authorisation.repeate_pass")}
                                id={'pass_repeat'}
                                name={'pass_repeat'}
                                placeholder={''}
                                error={errors.pass_repeat && errors.pass_repeat  ? errors.pass_repeat : ''}
                                isValide={touched.pass_repeat ? ValidStatus.VALID : ValidStatus.EMPTY}
                            />
                        </div>
                        <Button
                            onClick={() => { }}
                            label={t("authorisation.enter")}
                            type={'submit'}
                            textUp="capitalize"
                        />
                    </Form>
                )}
            </Formik>
        </AuthLayout>
    )
}

export default CreateNewPass
