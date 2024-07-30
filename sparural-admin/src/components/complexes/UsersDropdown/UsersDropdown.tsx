import { ReactComponent as Search } from '../../../assets/icons/small_search.svg'
import classNames from 'classnames'
import React, {
    FC,
    useCallback,
    useEffect,
    useMemo,
    useRef,
    useState,
} from 'react'
import { useTranslation } from 'react-i18next'
import { Label } from '../../simples/Label'
import OptionItem, { SelectOption } from '../../simples/Selector/OptionItem'
import styles from './UsersDropdown.module.scss'
import { ReactComponent as Chevron } from '../../../assets/icons/chevron.svg'
import memoize from 'memoize-one'
import { UsersService } from '../../../services/UsersService'
import { UserType } from '../../../types'

enum SortOption {
    FULL_NAME_ASC = 'full_name_asc',
    FULL_NAME_DESC = 'full_name_desc',
}

enum SortType {
    ASC = 'ASC',
    DESC = 'DESC',
}

type Props = {
    label: string
    // options: Array<SelectOption>
    // onSearch: (val: string) => void
    // onChangeSort: (data: any) => void
    // onEndReached: () => void
    onChange: (data: SelectOption) => void
    disabled?: boolean
    multiple?: boolean
    value: Array<SelectOption>
    placeholder?: string
}

/**
 *
 * @param label
 * @param options
 * @param onSearch
 * @param onChangeSort
 * @param onEndReached
 * @param onChange
 * @param disabled
 * @param multiple
 * @param value
 * @param placeholder
 * @returns
 */
const UsersDropdown: FC<Props> = ({
    label,
    // options,
    // onSearch,
    // onChangeSort,
    // onEndReached,
    onChange,
    disabled,
    multiple,
    value,
    placeholder,
}) => {
    const [openStatus, setOpenStatus] = useState(false)
    const { t } = useTranslation()
    const optionsBlock = useRef<HTMLDivElement>(null)
    const mainBlock = useRef<HTMLDivElement>(null)

    const [text, setText] = useState<string>('')

    const IntervalOptions = memoize(() => {
        return Object.keys(SortOption).map((key: string) => {
            let option: any = SortOption
            return {
                value: option[key],
                label: t(`filter.${option[key]}`),
            }
        })
    })

    const labelInner: string = useMemo(() => {
        let lab = placeholder ? placeholder : label
        if (value.length > 1) {
            lab = t('forms.checked_some')
        }
        if (value.length === 1) {
            lab = value[0].label
        }
        return lab
    }, [placeholder, label, value, t])

    // Метод сброса фокуса с выбранного элемента
    const loseFocus = useCallback(() => {
        if (optionsBlock && optionsBlock.current) {
            optionsBlock.current.blur()
        }
    }, [])

    // Метод закрытия селектора при клике вне самого селектора
    const closeSelectOutOfBlock = useCallback(
        (event: any) => {
            if (mainBlock && mainBlock.current) {
                // Проверка добавлена для устранения бага в Firefox
                if (!mainBlock.current.contains(event.target)) {
                    setOpenStatus(false)
                    loseFocus()
                }
            }
        },
        [loseFocus]
    )

    // Раскрытие пунктов меню с опциями при фокусе на селекторе
    const onHandleFocus = useCallback(() => {
        if (!disabled) setOpenStatus(true)
    }, [disabled])

    //закрытие по второму тапу
    const onToggleOpen = useCallback(() => {
        setOpenStatus(!openStatus)
    }, [openStatus])

    // Установка/удаление обработчика события на документе.
    useEffect(() => {
        document.addEventListener('click', closeSelectOutOfBlock, false)
        return () => {
            document.removeEventListener('click', closeSelectOutOfBlock, false)
        }
    }, [closeSelectOutOfBlock])

    const onHandleClick = (data: SelectOption) => {
        if (multiple) {
            onChange(data)
        } else {
            onChange(data)
            onToggleOpen()
        }
    }

    const reached = useRef(false)
    /**
     *
     * dynamic pagination
     */
    const handleScroll = () => {
        if (!optionsBlock.current) {
            return
        }

        const contentHeight = optionsBlock.current.offsetHeight
        const scrollHeight = optionsBlock.current.scrollHeight

        const scrollTop = optionsBlock.current.scrollTop

        if (scrollHeight <= contentHeight) {
            return
        }

        const afterEndReach =
            scrollHeight - (scrollTop + contentHeight) < contentHeight / 2

        if (afterEndReach && !reached.current) {
            reached.current = true
            load && load()
        } else if (!afterEndReach && reached.current) {
            reached.current = false
        }
    }

    const has = useRef<boolean>(true)
    const offset = useRef<number>(0)
    const [loading, setLoading] = useState<boolean>(false)
    const [users, setUsers] = useState<SelectOption[]>([])
    const [search, setSearch] = useState<string>('')
    const usersList = useRef<SelectOption[]>(users)
    usersList.current = users

    const [sort, setSort] = useState<SortType>(SortType.ASC)

    const load = async () => {
        if (!has.current || loading) {
            return
        }
        setLoading(true)

        const result = await UsersService.getUsersList({
            offset: offset.current,
            search,
            limit: 50,
            role: [2, 3, 4],
            alphabetSort: sort,
        })
        if (result.data.length === 0) {
            has.current = false
            setLoading(false)
            return
        }
        offset.current = offset.current + result.data.length
        const options: SelectOption[] = result.data.map((i) => ({
            value: i.id,
            label: `${i.lastName || ''} ${i.firstName || ''}`,
        }))
        setUsers([...usersList.current, ...options])

        setLoading(false)
    }

    const clear = async () => {
        offset.current = 0
        has.current = true
        setLoading(false)
        setUsers([])
        usersList.current = []
        await load()
    }

    useEffect(() => {
        clear().then()
    }, [search, sort])

    /**
     * change sort users
     */
    const onChangeSort = useCallback(
        (e: string) => {
            if (e === t(`filter.${SortOption.FULL_NAME_ASC}`)) {
                setSort(SortType.ASC)
            }
            if (e === t(`filter.${SortOption.FULL_NAME_DESC}`)) {
                setSort(SortType.DESC)
            }
        },
        [t]
    )

    return (
        <div ref={mainBlock} className={styles.root}>
            {label && <Label>{label}</Label>}

            <div
                className={classNames(styles.wrapper, {
                    [styles.opened]: openStatus,
                    [styles.disabled]: disabled,
                })}
                tabIndex={0}
            >
                <div
                    onFocus={onHandleFocus}
                    onClick={onToggleOpen}
                    className={styles.top}
                >
                    <div
                        className={classNames(styles.topLabel, {
                            [styles.placeholder]: labelInner === placeholder,
                        })}
                    >
                        {labelInner}
                    </div>
                    <div
                        className={classNames({
                            [styles.openIcon]: openStatus,
                        })}
                    >
                        <Chevron className={styles.chevron} />
                    </div>
                </div>

                {openStatus && (
                    <div className={styles.optionBlock}>
                        <div className={styles.headerFilter}>
                            <div className={styles.searchBlock}>
                                <input
                                    value={search}
                                    onChange={(e) => setSearch(e.target.value)}
                                />
                                <Search />
                            </div>
                            <select
                                onChange={(e) => onChangeSort(e.target.value)}
                                className={styles.sortFilter}
                            >
                                {IntervalOptions().map((option) => (
                                    <option className={styles.option}>
                                        {option.label}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div
                            ref={optionsBlock}
                            onScroll={handleScroll}
                            className={styles.itemsBlock}
                        >
                            {openStatus &&
                                users.map((option) => {
                                    return (
                                        <OptionItem
                                            key={option.value}
                                            data={option}
                                            onClick={onHandleClick}
                                            values={value}
                                            multiple={multiple}
                                        />
                                    )
                                })}
                        </div>
                    </div>
                )}
            </div>
        </div>
    )
}

export default UsersDropdown
