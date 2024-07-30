import { Col, Row } from 'antd'
import { FC, useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import {
    Counters,
    GenderType,
    StatusBuyersType,
    UserAtributes,
    Currency as CurrencyType,
} from '../../../../types'
import { InputHolder } from '../../../simples/InputHolder'
import { Selector } from '../../../simples/Selector'
import { SelectOption } from '../../../simples/Selector/OptionItem'
import { TextField } from '../../../simples/TextField'
import memoize from 'memoize-one'
import { DatePickerComponent } from '../../../simples/DatePickerComponent'
import { Label } from '../../../simples/Label'
import { Checkbox } from '../../../simples/Checkbox'
import moment, { Moment } from 'moment'
import styles from './EditGroupUsers.module.scss'

export type UsersFilterType = {
    search?: string
    minAge?: number
    maxAge?: number
    minRegistrationDate?: number
    maxRegistrationDate?: number
    gender?: GenderType
    atributeId?: number
    statusId?: number
    currencyId?: number
    currencyMin?: number
    currencyMax?: number
    hasEmail?: number
    counterId?: number
    counterMin?: number
    counterMax?: number
}

const ageOptions = memoize(() => {
    let options: SelectOption[] = []
    for (let i = 1; i < 100; i++) {
        options.push({
            value: i,
            label: `${i}`,
        })
    }
    return options
})

const isEmailOptions = [
    { value: 0, label: 'Нет' },
    { value: 1, label: 'Да' },
]

type Props = {
    onFilter: (filter: UsersFilterType) => void
    isShowAll?: boolean
    allUserAtributes: UserAtributes[]
    allUserStatuses: StatusBuyersType[]
    allUserCounters: Counters[]
    allCurrencies: Array<CurrencyType>
}

const UserSearchBlock: FC<Props> = ({
    onFilter,
    isShowAll,
    allUserAtributes,
    allUserStatuses,
    allUserCounters,
    allCurrencies,
}) => {
    const { t } = useTranslation()

    const [search, setSearch] = useState<string>('')
    const [minAge, setMinAge] = useState<SelectOption>()
    const [maxAge, setMaxAge] = useState<SelectOption>()
    const [minRegistrationDate, setMinRegistrationDate] = useState<number>()
    const [maxRegistrationDate, setMaxRegistrationDate] = useState<number>()
    const [gender, setGender] = useState<GenderType>()
    const [selectedAtribute, setSelectedAtribute] = useState<SelectOption>()
    const [selectedStatus, setSelectedStatus] = useState<SelectOption>()
    const [selectedCurrencyId, setSelectedCurrencyId] = useState<SelectOption>()
    const [isBalanceActive, setIsBalanceActive] = useState<boolean>(false)
    const [selectedCurrencyMin, setSelectedCurrencyMin] = useState<string>()
    const [selectedCurrencyMax, setSelectedCurrencyMax] = useState<string>()
    const [selectedIsEmail, setSelectedIsEmail] = useState<SelectOption>()
    const [selectedCounter, setSelectedCounter] = useState<SelectOption>()
    const [selectedCounterFrom, setSelectedCounterFrom] = useState<string>()
    const [selectedCounterTo, setSelectedCounterTo] = useState<string>()

    const selectUserAtributesData: { value: number; label: string }[] =
        useMemo(() => {
            const result: { value: number; label: string }[] = []
            if (allUserAtributes && allUserAtributes?.length > 0) {
                allUserAtributes.forEach((atribute) => {
                    const obj = {
                        value: atribute.id,
                        label: atribute.name,
                    }
                    result.push(obj)
                })
            }
            return result
        }, [allUserAtributes])

    const selectUserStatusesData: { value: number; label: string }[] =
        useMemo(() => {
            const result: { value: number; label: string }[] = []
            if (allUserStatuses && allUserStatuses?.length > 0) {
                allUserStatuses.forEach((status) => {
                    const obj = {
                        value: status.id,
                        label: status.name,
                    }
                    result.push(obj)
                })
            }
            return result
        }, [allUserStatuses])

    const selectCountersData: { value: number; label: string }[] =
        useMemo(() => {
            const result: { value: number; label: string }[] = []
            if (allUserCounters && allUserCounters?.length > 0) {
                allUserCounters.forEach((counter) => {
                    const obj = {
                        value: counter.id,
                        label: counter.name,
                    }
                    result.push(obj)
                })
            }
            return result
        }, [allUserCounters])

    const currenciesOptions: { value: number; label: string }[] =
        useMemo(() => {
            return allCurrencies.map(currency => ({
                value: currency.id,
                label: currency.name,
            }))
        }, [allCurrencies])

    /**
     * function for disable date after end date
     * @param date - Moment from datePicker
     * @returns boolean
     */
    const onHandleDisebleStartDate = (date?: Moment) => {
        let disable = false
        if (maxRegistrationDate && date) {
            if (moment(date).valueOf() > maxRegistrationDate) {
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
        if (minRegistrationDate && date) {
            if (moment(date).valueOf() < minRegistrationDate) {
                disable = true
            } else {
                disable = false
            }
        }
        return disable
    }

    const onChangeIsBalanceActive = () => {
        setIsBalanceActive(!isBalanceActive)
    }

    // changin gender
    const onChangeGender = (sex: GenderType) => {
        if (gender === sex) {
            setGender(undefined)
        } else {
            setGender(sex)
        }
    }

    useEffect(() => {
        const filterData: UsersFilterType = {
            minAge: minAge?.value as number,
            maxAge: maxAge?.value as number,
            minRegistrationDate,
            maxRegistrationDate,
            gender,
        }
        if (search && search !== '') {
            filterData.search = search
        }
        if (selectedAtribute) {
            filterData.atributeId = +selectedAtribute.value
        }
        if (selectedStatus) {
            filterData.statusId = +selectedStatus.value
        }
        if (selectedCurrencyId) {
            filterData.currencyId = +selectedCurrencyId.value
        }
        if (selectedCurrencyMin) {
            filterData.currencyMin = +selectedCurrencyMin
        }
        if (selectedCurrencyMax) {
            filterData.currencyMax = +selectedCurrencyMax
        }
        if (selectedIsEmail) {
            filterData.hasEmail = +selectedIsEmail.value
        }
        if (selectedCounter) {
            filterData.counterId = +selectedCounter.value
        }
        if (selectedCounterFrom) {
            filterData.counterMin = +selectedCounterFrom
        }
        if (selectedCounterTo) {
            filterData.counterMax = +selectedCounterTo
        }
        onFilter(filterData)
    }, [
        search,
        minAge,
        maxAge,
        minRegistrationDate,
        maxRegistrationDate,
        gender,
        selectedAtribute,
        selectedStatus,
        selectedCurrencyId,
        selectedCurrencyMin,
        selectedCurrencyMax,
        selectedIsEmail,
        selectedCounter,
        selectedCounterFrom,
        selectedCounterTo,
    ])

    /**
     * set min date
     */
    const setMinDate = (date?: number) => {
        const d = new Date(date ? date : '')
        const newTime = d.setHours(0, 0, 0)

        if (!newTime) {
            setMinRegistrationDate(undefined)
        } else {
            setMinRegistrationDate(Math.round(newTime))
        }
    }

    /**
     * set max date
     */
    const setMaxDate = (date?: number) => {
        const d = new Date(date ? date : '')
        const newTime = d.setHours(23, 59, 59)

        if (!newTime) {
            setMaxRegistrationDate(undefined)
        } else {
            setMaxRegistrationDate(newTime)
        }
    }
    return (
        <div className="">
            <InputHolder classes={styles.inputHolderRoot}>
                <TextField
                    label={t('forms.search')}
                    isSearch
                    onChange={setSearch}
                    value={search || ''}
                />
            </InputHolder>
            <InputHolder classes={styles.inputHolderRoot}>
                <>
                    <Label>{t('forms.age')}</Label>
                    <Row gutter={8}>
                        <Col span={12}>
                            <Selector
                                label={''}
                                options={ageOptions()}
                                onChange={setMinAge}
                                value={minAge ? [minAge] : []}
                                placeholder={t('forms.from')}
                            />
                        </Col>
                        <Col span={12}>
                            <Selector
                                label={''}
                                options={ageOptions()}
                                onChange={setMaxAge}
                                value={maxAge ? [maxAge] : []}
                                placeholder={t('forms.to')}
                            />
                        </Col>
                    </Row>
                </>
            </InputHolder>
            <InputHolder classes={styles.inputHolderRoot}>
                <>
                    <Label>{t('forms.registration_date')}</Label>
                    <Row gutter={8}>
                        <Col span={12}>
                            <DatePickerComponent
                                onChange={setMinDate}
                                onDisableDate={onHandleDisebleStartDate}
                                placeholder={t('forms.from')}
                            />
                        </Col>
                        <Col span={12}>
                            <DatePickerComponent
                                onDisableDate={onHandleDisebleEndDate}
                                onChange={setMaxDate}
                                placeholder={t('forms.to')}
                            />
                        </Col>
                    </Row>
                </>
            </InputHolder>
            <InputHolder classes={styles.inputHolderRoot}>
                <Selector
                    label={'Наличие атрибута'}
                    options={selectUserAtributesData}
                    onChange={setSelectedAtribute}
                    value={selectedAtribute ? [selectedAtribute] : []}
                    placeholder={t('forms.not_selected')}
                />
            </InputHolder>
            {isShowAll && (
                <>
                    <InputHolder classes={styles.inputHolderRoot}>
                        <Selector
                            label={'Статус'}
                            options={selectUserStatusesData}
                            onChange={setSelectedStatus}
                            value={selectedStatus ? [selectedStatus] : []}
                            placeholder={t('forms.not_selected')}
                        />
                    </InputHolder>
                    <InputHolder classes={styles.inputHolderRoot}>
                        <Selector
                            label={'Валюта'}
                            options={currenciesOptions}
                            onChange={setSelectedCurrencyId}
                            value={selectedCurrencyId ? [selectedCurrencyId] : []}
                            placeholder={t('forms.not_selected')}
                        />
                    </InputHolder>
                    <InputHolder classes={styles.inputHolderRoot}>
                        <Row gutter={8}>
                            <Col
                                style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                }}
                                span={4}
                            >
                                <Checkbox
                                    value={0}
                                    onClick={onChangeIsBalanceActive}
                                    isChecked={isBalanceActive}
                                />
                            </Col>
                            <Col span={10}>
                                <TextField
                                    label={''}
                                    onChange={setSelectedCurrencyMin}
                                    value={selectedCurrencyMin || ''}
                                    placeholder={t('forms.from')}
                                    isNumber
                                    disabled={!isBalanceActive}
                                />
                            </Col>
                            <Col span={10}>
                                <TextField
                                    label={''}
                                    onChange={setSelectedCurrencyMax}
                                    value={selectedCurrencyMax || ''}
                                    placeholder={t('forms.to')}
                                    isNumber
                                    disabled={!isBalanceActive}
                                />
                            </Col>
                        </Row>
                    </InputHolder>
                    <InputHolder classes={styles.inputHolderRoot}>
                        <Selector
                            label={'Наличие Email'}
                            options={isEmailOptions}
                            onChange={setSelectedIsEmail}
                            value={selectedIsEmail ? [selectedIsEmail] : []}
                            placeholder={t('forms.not_selected')}
                        />
                    </InputHolder>
                    <InputHolder classes={styles.inputHolderRoot}>
                        <Selector
                            label={'Счётчик'}
                            options={selectCountersData}
                            onChange={setSelectedCounter}
                            value={selectedCounter ? [selectedCounter] : []}
                            placeholder={t('forms.not_selected')}
                        />
                    </InputHolder>
                    <InputHolder classes={styles.inputHolderRoot}>
                        <Row gutter={8}>
                            <Col span={12}>
                                <TextField
                                    label={''}
                                    onChange={setSelectedCounterFrom}
                                    value={selectedCounterFrom || ''}
                                    placeholder={t('forms.from')}
                                    isNumber
                                />
                            </Col>
                            <Col span={12}>
                                <TextField
                                    label={''}
                                    onChange={setSelectedCounterTo}
                                    value={selectedCounterTo || ''}
                                    placeholder={t('forms.to')}
                                    isNumber
                                />
                            </Col>
                        </Row>
                    </InputHolder>
                    <InputHolder classes={styles.inputHolderRoot}>
                        <>
                            <Label>{t('forms.gender')}</Label>
                            <Row gutter={8}>
                                <Col
                                    style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                    }}
                                    span={6}
                                >
                                    <Checkbox
                                        value={0}
                                        onClick={() =>
                                            onChangeGender(GenderType.MAIL)
                                        }
                                        labelPosition={'left'}
                                        isChecked={gender === GenderType.MAIL}
                                        label={t('common.male')}
                                    />
                                </Col>
                                <Col
                                    style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                    }}
                                    span={6}
                                >
                                    <Checkbox
                                        value={0}
                                        onClick={() =>
                                            onChangeGender(GenderType.FEMALE)
                                        }
                                        labelPosition={'left'}
                                        isChecked={gender === GenderType.FEMALE}
                                        label={t('common.female')}
                                    />
                                </Col>

                                <Col span={12}></Col>
                            </Row>
                        </>
                    </InputHolder>
                </>
            )}
        </div>
    )
}
export default UserSearchBlock
