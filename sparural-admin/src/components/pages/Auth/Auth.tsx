import { FC, useCallback } from 'react'
import styles from './Auth.module.scss'
import * as yup from 'yup'
import { Form, Formik } from 'formik'

import { FormField } from '../../simples/FormField'
import { Button } from '../../simples/Button';
import AuthLayout from '../../complexes/AuthLayout/AuthLayout';
import { Link } from 'react-router-dom';
import { Routes } from '../../../config';
import { useTranslation } from "react-i18next";
import { useAppDispatch } from '../../../hooks/store'
import { ValidStatus } from '../../simples/FormField/FormField'
import { login } from '../../../store/slices/authSlice'

type MyFormValues = {
    phoneNumber: string
    password: string
}

/**
 * 
 * @returns auth screen
 */

const Auth: FC = () => {
    const initialValues: MyFormValues = { phoneNumber: '', password: '' }
    const dispatch = useAppDispatch()

    const { t, } = useTranslation();

    /**
     * validation login and password
     * !TODO - enable validation
     */
    const validationSchema = yup.object({
        phoneNumber: yup
            .string()
            .matches(/^\d+$/ , t("errors.only_numbers"))
            .required(t("errors.required_field")),
        password: yup
            .string()
            // .min(6, t("errors.min_pass"))
            // .max(50, t("errors.max_pass"))
            .matches(/.*(?=.*\d).*/ , t("errors.pass_one_number"))
            // !TODO enable when correct data will be available
            // .matches(/.*(?=.*[A-Z]).*/ ,  t("errors.pass_one_capitaloze"))
            // .matches(/.*(?=.*[a-z]).*/ , t("errors.pass_one_lowercase"))
            .required(t("errors.required_field")),
    })

    const onSubmit = useCallback(async (values: {
        phoneNumber: string,
        password: string
    }, {setStatus, resetForm, setErrors, errors}) => {
        dispatch(login(values)).then(data => {
            if(data.meta.requestStatus === 'rejected'){
                setStatus('Error')
                setErrors({...errors, 
                    phoneNumber: t('authorisation.wrong_login_or_password'),
                    password: t('authorisation.wrong_login_or_password'),
                })
            }
        })
    }, [dispatch, t])

    // const onRecoveryPass = () => {
    //     route.push()
    // }

    return (
        <AuthLayout title={t("authorisation.auth")}>

            <Formik
                initialValues={initialValues}
                onSubmit={onSubmit}
                validationSchema={validationSchema}
            >
                 {({ errors, touched, status }) => (
                <Form>
                    <div className={styles.inputHolder}>
                        <FormField
                            classes={{
                                label: styles.label,
                            }}
                            label={`${t("authorisation.email")} / ${t("authorisation.phone_number")}`}
                            id={'phoneNumber'}
                            name={'phoneNumber'}
                            placeholder={''}
                            error={errors.phoneNumber && touched.phoneNumber ? errors.phoneNumber : ''}
                            isValide={touched.phoneNumber ? ValidStatus.VALID : ValidStatus.EMPTY}
                        />
                    </div>
                    <div className={styles.inputHolder}>
                        <FormField
                            isSecure
                            classes={{
                                label: styles.label,
                            }}
                            label={t("authorisation.password")}
                            id={'password'}
                            name={'password'}
                            placeholder={''}
                            error={errors.password && touched.password ? errors.password : ''}
                            isValide={touched.password ? ValidStatus.VALID : ValidStatus.EMPTY}
                        />
                    </div>
                    <div className={styles.linkBtn}>
                        {t("authorisation.forget")} <Link to={Routes.REPAIR_PASS} className={styles.link} > {t("authorisation.pass")}</Link>
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

export default Auth
