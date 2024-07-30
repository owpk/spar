import { Col, Row } from 'antd'
import { FC, useCallback, useContext } from 'react'
import { useTranslation } from 'react-i18next'
import { IntervalTimeType } from '../../../../../config'
import { Checkbox } from '../../../../simples/Checkbox'
import { InputHolder } from '../../../../simples/InputHolder'
import { Label } from '../../../../simples/Label'
import { Selector } from '../../../../simples/Selector'
import { SelectOption } from '../../../../simples/Selector/OptionItem'
import { TextAria } from '../../../../simples/TextAria'
import { TextField } from '../../../../simples/TextField'
import memoize from 'memoize-one'
import { DatePickerComponent } from '../../../../simples/DatePickerComponent'
import { TimePickerComponent } from '../../../../simples/TimePickerComponent'
import moment, { Moment } from 'moment'
import { UsersGroupDropdown } from '../../../../complexes/UsersGroupDropdown'
import { UsersDropdown } from '../../../../complexes/UsersDropdown'
import {
    fromTimeToTimestamp,
    getNumbers,
    getTimeFromTimestamp,
} from '../../../../../utils/helpers'
import {
    NotificationContext,
    TriggersContext,
    ValidateErrorContext,
    CurrenciesContext,
} from '../contexts'

const SmsTab: FC = () => {
    const { t } = useTranslation()
    const [notification, setNotification] = useContext(NotificationContext)
    const [validateError, setValidateError] = useContext(ValidateErrorContext)

    const [triggers] = useContext(TriggersContext)
    const [currencies] = useContext(CurrenciesContext)

    const IntervalOptions = memoize(() => {
        return Object.keys(IntervalTimeType).map((key: string) => {
            let option: any = IntervalTimeType
            return {
                value: option[key],
                label: t(`options.interval.${option[key]}`),
            }
        })
    })

    /**
     * function for disable date after end date
     * @param date - Moment from datePicker
     * @returns boolean
     */
    const onHandleDisebleStartDate = (date?: Moment) => {
        let disable = false
        if (notification.startDate?.end && date) {
            if (moment(date).valueOf() > notification.startDate.end) {
                disable = true
            } else {
                disable = false
            }
        }
        return disable
    }
    /**
     * function for disable date before start date
     * @param date - Moment from datePicker
     * @returns boolean
     */
    const onHandleDisebleEndDate = (date?: Moment) => {
        let disable = false
        if (notification.startDate?.start && date) {
            if (moment(date).valueOf() < notification.startDate.start) {
                disable = true
            } else {
                disable = false
            }
        }
        return disable
    }

    /**
     * add users
     */
    const onAddDelUser = useCallback(
        async (item: SelectOption) => {
            if (
                notification.users.length > 0 &&
                notification.users.find((i) => i.value === item.value)
            ) {
                setNotification((prev) => ({
                    ...prev,
                    users: prev.users.filter((i) => i.value !== item.value),
                }))
            } else {
                setNotification((prev) => ({
                    ...prev,
                    users: [...prev.users, item],
                }))
            }
        },
        [notification.users, setNotification]
    )

    return (
        <div className="">
            {/* name, time interval, text */}
            <Row gutter={[16, 16]}>
                <Col>
                    <InputHolder>
                        <TextField
                            error={validateError.name || ''}
                            label={t('forms.name')}
                            onChange={(name) => {
                                setValidateError((prev) => ({
                                    ...prev,
                                    name: undefined,
                                }))
                                setNotification((prev) => ({ ...prev, name }))
                            }}
                            value={notification.name}
                        />
                    </InputHolder>
                    <InputHolder>
                        <Selector
                            error={validateError.trigger || ''}
                            label={t('forms.trigger')}
                            options={triggers}
                            onChange={(trigger) => {
                                setValidateError((prev) => ({
                                    ...prev,
                                    trigger: undefined,
                                }))
                                setNotification((prev) => ({
                                    ...prev,
                                    trigger,
                                }))
                            }}
                            value={
                                notification.trigger
                                    ? [notification.trigger]
                                    : []
                            }
                        />
                    </InputHolder>
                </Col>
                <Col span={9}>
                    <TextAria
                        maxCount={100}
                        height={148}
                        maxRows={6}
                        label={t('forms.text')}
                        onChange={(text) =>
                            setNotification((prev) => ({ ...prev, text }))
                        }
                        value={notification.text}
                    />
                </Col>
            </Row>
            {/* trigger, choose users */}
            <Row gutter={[16, 16]}>
                <Col>
                    <InputHolder>
                        <>
                            <Label>{t('forms.start_date')}</Label>
                            <Row gutter={[8, 8]}>
                                <Col span={12}>
                                    <DatePickerComponent
                                        error={validateError.startDateStart || ''}
                                        onDisableDate={onHandleDisebleStartDate}
                                        value={notification.startDate?.start}
                                        onChange={(e) => {
                                            setValidateError((prev) => ({
                                                ...prev,
                                                startDateStart: undefined,
                                            }))
                                            setNotification({
                                                ...notification,
                                                startDate: {
                                                    ...notification.startDate,
                                                    start: e,
                                                },
                                            })
                                        }}
                                        placeholder="c"
                                    />
                                </Col>
                                <Col span={12}>
                                    <DatePickerComponent
                                        error={validateError.startDateEnd || ''}
                                        onDisableDate={onHandleDisebleEndDate}
                                        value={notification.startDate?.end}
                                        onChange={(e) => {
                                            setValidateError((prev) => ({
                                                ...prev,
                                                startDateEnd: undefined,
                                            }))
                                            setNotification({
                                                ...notification,
                                                startDate: {
                                                    ...notification.startDate,
                                                    end: e,
                                                },
                                            })
                                        }}
                                        placeholder="до"
                                    />
                                </Col>
                            </Row>
                        </>
                    </InputHolder>
                </Col>
                <Col span={7}>
                    <InputHolder>
                        <UsersDropdown
                            multiple
                            label={t('forms.choose_receivers')}
                            onChange={onAddDelUser}
                            value={notification.users}
                            placeholder="Получатель"
                        />
                    </InputHolder>
                </Col>
            </Row>
            {/* start date, choose group */}
            <Row gutter={[16, 16]}>
                <Col>
                    <InputHolder>
                        <>
                            {' '}
                            <Label>{t('forms.repeat_period')}</Label>
                            <Row align={'top'} gutter={[8, 8]}>
                                <Col span={12}>
                                    <TextField
                                        error={validateError.periodCount || ''}
                                        label={''}
                                        onChange={(e) => {
                                            setValidateError((prev) => ({
                                                ...prev,
                                                periodCount: undefined,
                                            }))
                                            setNotification((prev) => ({
                                                ...prev,
                                                periodCount: getNumbers(e),
                                            }))
                                        }}
                                        value={
                                            notification.periodCount
                                                ? String(
                                                      notification.periodCount
                                                  )
                                                : ''
                                        }
                                    />
                                </Col>
                                <Col span={12}>
                                    <Selector
                                        error={validateError.period || ''}
                                        label={''}
                                        options={IntervalOptions()}
                                        onChange={(period) => {
                                            setValidateError((prev) => ({
                                                ...prev,
                                                period: undefined,
                                            }))
                                            setNotification((prev) => ({
                                                ...prev,
                                                period,
                                            }))
                                        }}
                                        value={
                                            notification.period
                                                ? [notification.period]
                                                : []
                                        }
                                    />
                                </Col>
                            </Row>
                        </>
                    </InputHolder>
                </Col>
                <Col span={7}>
                    <InputHolder>
                        <UsersGroupDropdown
                            values={
                                notification.group ? [notification.group] : []
                            }
                            onChange={(group) =>
                                setNotification((prev) => ({ ...prev, group }))
                            }
                        />
                    </InputHolder>
                </Col>
            </Row>
            {/* repeat period, push leads, photo, checkboxes */}
            <Row gutter={[16, 16]}>
                <Col>
                    <Row>
                        <Col span={24}>
                            <InputHolder>
                                <>
                                    <Label>
                                        {t('forms.time_interval_during_day')}
                                    </Label>
                                    <Row gutter={[8, 8]}>
                                        <Col span={12}>
                                            <TimePickerComponent
                                                error={validateError.intervalStart || ''}
                                                // transform string as "08:00" into timestamp
                                                value={fromTimeToTimestamp(
                                                    notification.interval.start
                                                )}
                                                onChange={(e) => {
                                                    setValidateError((prev) => ({
                                                        ...prev,
                                                        intervalStart: undefined,
                                                    }))
                                                    setNotification({
                                                        ...notification,
                                                        interval: {
                                                            ...notification.interval,
                                                            start: getTimeFromTimestamp(
                                                                e
                                                            ),
                                                        },
                                                        // transform timestamp into string as "00:00"
                                                    })
                                                }}
                                                placeholder="c"
                                            />
                                        </Col>
                                        <Col span={12}>
                                            <TimePickerComponent
                                                error={validateError.intervalEnd || ''}
                                                // transform string as "08:00" into timestamp
                                                value={fromTimeToTimestamp(
                                                    notification.interval.end
                                                )}
                                                onChange={(e) => {
                                                    setValidateError((prev) => ({
                                                        ...prev,
                                                        intervalEnd: undefined,
                                                    }))
                                                    setNotification({
                                                        ...notification,
                                                        interval: {
                                                            ...notification.interval,
                                                            end: getTimeFromTimestamp(
                                                                e
                                                            ),
                                                        },
                                                        // transform timestamp into string as "00:00"
                                                    })
                                                }}
                                                placeholder="до"
                                            />
                                        </Col>
                                    </Row>
                                </>
                            </InputHolder>
                            {notification.trigger?.value === 'lifespan-of-currency' && (
                                <>
                                    <InputHolder>
                                        <Row>
                                            <Col span={24}>
                                                <Selector
                                                    label={t('forms.currency_id')}
                                                    placeholder={t(
                                                        'forms.notificationType'
                                                    )} ///
                                                    options={currencies} ///
                                                    onChange={(currencyId) =>
                                                        setNotification((prev) => ({
                                                            ...prev,
                                                            currencyId,
                                                        }))
                                                    }
                                                    value={
                                                        notification.currencyId
                                                            ? [
                                                                notification.currencyId,
                                                            ]
                                                            : [
                                                                {
                                                                    label: t('forms.currency_empty_value'),
                                                                    value: '0',
                                                                },
                                                            ]
                                                    }
                                                />
                                            </Col>
                                        </Row>
                                    </InputHolder>
                                    <InputHolder>
                                        <TextField
                                            label={t(
                                                'forms.currency_days_before_during'
                                            )}
                                            onChange={(
                                                currencyDaysBeforeBurning
                                            ) => {
                                                setNotification((prev) => ({
                                                    ...prev,
                                                    currencyDaysBeforeBurning:
                                                        +currencyDaysBeforeBurning,
                                                }))
                                            }}
                                            value={
                                                notification.currencyDaysBeforeBurning
                                                    ? String(
                                                        notification.currencyDaysBeforeBurning
                                                    )
                                                    : ''
                                            } ///
                                        />
                                    </InputHolder>
                                </>
                            )}
                            {notification.trigger?.value === 'no-purchase-for-n-days' && (
                                <InputHolder>
                                    <TextField
                                        label={t('forms.daysWithoutPurchasingLabel')}
                                        placeholder={t(
                                            'forms.daysWithoutPurchasingPlaceholder'
                                        )}
                                        onChange={(
                                            daysWithoutPurchasing
                                        ) => {
                                            setNotification((prev) => ({
                                                ...prev,
                                                daysWithoutPurchasing:
                                                    +daysWithoutPurchasing,
                                            }))
                                        }}
                                        value={
                                            notification.daysWithoutPurchasing
                                                ? String(
                                                    notification.daysWithoutPurchasing
                                                )
                                                : ''
                                        }
                                    />
                                </InputHolder>
                            )}
                        </Col>
                    </Row>
                </Col>
                {/* load photo and checkbox */}
                <Col>
                    <InputHolder>
                        <Checkbox
                            label={t('forms.send_to_all_users')}
                            labelPosition={'right'}
                            value={''}
                            onClick={() =>
                                setNotification((prev) => ({
                                    ...prev,
                                    sendAll: !prev.sendAll,
                                }))
                            }
                            isChecked={notification.sendAll}
                        />
                    </InputHolder>
                    <InputHolder>
                        <Checkbox
                            label={t('forms.required')}
                            labelPosition={'right'}
                            value={''}
                            onClick={() =>
                                setNotification((prev) => ({
                                    ...prev,
                                    required: !prev.required,
                                }))
                            }
                            isChecked={notification.required}
                        />
                    </InputHolder>
                    <InputHolder>
                        <Checkbox
                            label={t('forms.is_system')}
                            labelPosition={'right'}
                            value={''}
                            onClick={() =>
                                setNotification((prev) => ({
                                    ...prev,
                                    isSystem: !prev.isSystem,
                                }))
                            }
                            isChecked={notification.isSystem || false}
                        />
                    </InputHolder>
                </Col>
            </Row>
        </div>
    )
}

export default SmsTab
