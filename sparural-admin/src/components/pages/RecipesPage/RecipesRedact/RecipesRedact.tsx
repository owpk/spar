import { Col, message, Row } from 'antd'
import { FC, useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation, useNavigate } from 'react-router-dom'
import { EntitiesFieldName, FileSource, Routes } from '../../../../config'
import produce from 'immer'
import {
    UploadFileDocType,
    useUploadFileMutation,
} from '../../../../services/FileService'
import {
    AttributesType,
    GoodsType,
    PersonalOffersType,
    Phototype,
} from '../../../../types'
import { MainLayout } from '../../../complexes/MainLayout'
import { EditWrapper } from '../../../simples/EditWrapper'
import { InputHolder } from '../../../simples/InputHolder'
import { PhotoLoader } from '../../../simples/PhotoLoader'
import { TextField } from '../../../simples/TextField'
import styles from './RecipesRedact.module.scss'
import {
    CreateRecipe,
    RecipesService,
    UpdateRecipe,
} from '../../../../services/RecipesService'
import { SelectOption } from '../../../simples/Selector/OptionItem'
import { TextEditor } from '../../../simples/TextEditor'
import { Selector } from '../../../simples/Selector'
import { AttributesService } from '../../../../services/AttributesService'
import GoodsBlock from './GoodsBlock'
import { ReactComponent as DeleteUsers } from '../../../../assets/icons/delete_users.svg'
import { ReactComponent as AddUsers } from '../../../../assets/icons/add_users.svg'
import { PersonalProductsService } from '../../../../services/PersonalProductsService'

type Props = {}

enum ImageType {
    PREVIEW = 'preview',
    PHOTO = 'photo',
}

const RecipesRedact: FC<Props> = () => {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const id = useLocation().search.split('=')[1]

    const [recipeAttributesData, setRecipeAttributedData] = useState<
        AttributesType[]
    >([])

    const [recipeAttributes, setRecipeAttributes] = useState<
        Array<SelectOption>
    >([])

    const inGroupGoodsList = useRef<any[]>([])
    const notInGroupGoodsList = useRef<any[]>([])

    const [searchAllGoods, setSearchAllGoods] = useState('')

    const [title, setTitle] = useState<string>('')
    const [description, setDescr] = useState<string>('')
    const [file, setFile] = useState<File>()
    const [preview, setPreview] = useState<File>()
    const [photoUrl, setPhotoUrl] = useState<Phototype>()
    const [previewUrl, setPreviewUrl] = useState<Phototype>()
    const [calories, setCalories] = useState<string>('')
    const [proteins, setProteins] = useState<string>('')
    const [carbohydrates, setCarbohydrates] = useState<string>('')
    const [fats, setFats] = useState<string>('')
    const [selectedAttributes, setSelectedAttributes] = useState<
        SelectOption[]
    >([]) //// ne prihodit c backa
    console.log('RecipesRedact', {description})

    const [goods, setGoods] = useState<number[]>([])
    const [allGoods, setAllGoods] = useState<boolean>(false)
    const [goodsNotInTheGroup, setGoodsNotInTheGroup] = useState<GoodsType[]>(
        []
    )
    notInGroupGoodsList.current = goodsNotInTheGroup

    const [groupGoods, setGroupGoods] = useState<number[]>([])
    const [allGroupGoods, setAllGroupGoods] = useState<boolean>(false)
    const [goodsInTheGroup, setGoodsInTheGroup] = useState<GoodsType[]>([])
    console.log(goodsInTheGroup)
    inGroupGoodsList.current = goodsInTheGroup

    const [item, setItem] = useState<PersonalOffersType>()
    const [loading, setLoading] = useState(false)
    const [currentId, setCurrentId] = useState<number>(id ? +id : 0)
    const offset2 = useRef(0)
    const has = useRef(true)

    const [sendFile, result] = useUploadFileMutation()

    const selectedAttributesId = useMemo(() => {
        return selectedAttributes.map((attribute) => +attribute.value)
    }, [selectedAttributes])

    const addGoodInGroup = (goods: number[]) => {
        const goodsArr = goodsNotInTheGroup.filter((item) =>
            goods.includes(item.id ?? 0)
        )
        setGoodsNotInTheGroup((prev) => [
            ...prev.filter((item) => !goods.includes(item.id ?? 0)),
        ])
        setGoodsInTheGroup((prev) => [...prev, ...goodsArr])
        setGoods([])
    }

    const removeGoodFromGroup = (groupGoods: number[]) => {
        const ids = new Set(groupGoods.map((id) => id))
        setGoodsInTheGroup((prev) => [
            ...prev.filter((obj) => !ids.has(obj.id ?? 0)),
        ])
    }

    const loadGoodsNotIntoGroup = useCallback(
        async (isFiltered?: boolean) => {
            if (!has.current || loading) {
                return
            }
            setLoading(true)

            const result = await PersonalProductsService.getPersonalProducts({
                offset: offset2.current,
                limit: 50,
                search: searchAllGoods,
            })
            if (result.length === 0) {
                has.current = false
                setLoading(false)
            }
            offset2.current = offset2.current + result.length

            const idsInGroup = new Set(goodsInTheGroup.map((good) => good.id))

            setGoodsNotInTheGroup((prev) =>
                isFiltered
                    ? result.filter((obj) => !idsInGroup.has(obj.id))
                    : [
                          ...prev.filter((obj) => !idsInGroup.has(obj.id)),
                          ...result.filter((obj) => !idsInGroup.has(obj.id)),
                      ]
            )

            setLoading(false)
        },
        [loading, offset2.current, searchAllGoods, goodsInTheGroup]
    )

    /**
     * get one offer by id
     */
    const getOneRecipeById = useCallback(async () => {
        try {
            const response = await RecipesService.getRecipe(Number(id))
            setTitle(response?.title)
            setDescr(response?.description)
            setPhotoUrl(response?.photo)
            setPreviewUrl(response?.preview)
            setCalories(response?.calories.toString())
            setProteins(response?.proteins.toString())
            setCarbohydrates(response?.carbohydrates.toString())
            setFats(response?.fats.toString())
            setSelectedAttributes(
                response.attributes.map((attribute) => ({
                    value: attribute.id,
                    label: attribute.name,
                }))
            )
            setGoodsInTheGroup(response?.goods)
            setLoading(false)
        } catch (error) {
            message.error(t('errors.get_data'))
            setLoading(false)
        }
    }, [id, t])

    /**
     * create new recipe with draft: true
     */
    const createRecipe = async () => {
        try {
            const sendData: CreateRecipe = {
                draft: true,
                title,
                description,
                calories: +calories,
                proteins: +proteins,
                fats: +fats,
                carbohydrates: +carbohydrates,
                attributes: selectedAttributesId,
            }
            const response = await RecipesService.createRecipe(sendData)
            setCurrentId(response.id)
        } catch (error) {
            message.error(t('errors.save_data'))
        }
    }

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const getAllAttributes = useMemo(async () => {
        const response = await AttributesService.getRecipeAttributes({})
        const arr = response.map((attribute) => ({
            value: attribute.id,
            label: attribute.name,
        }))
        setRecipeAttributes(arr)
        setRecipeAttributedData(response)
    }, [])

    const clearAllGoodsGroup = async () => {
        has.current = true
        setLoading(false)
        setGoodsNotInTheGroup([])
        notInGroupGoodsList.current = []
        offset2.current = 0
        await loadGoodsNotIntoGroup(true)
    }

    useEffect(() => {
        loadGoodsNotIntoGroup().then()
        if (!!id) {
            getOneRecipeById().then()
        }
        //  else {
        //     createRecipe().then()
        // }
    }, [getOneRecipeById, id])

    useEffect(() => {
        if (searchAllGoods !== '') {
            clearAllGoodsGroup().then()
        }
    }, [searchAllGoods])

    /**
     *
     * save offer
     */
    const onHandleSave = async () => {

        if (currentId) {
            try {
                const data: UpdateRecipe = {
                    draft: false,
                    title,
                    description,
                    calories: +calories,
                    proteins: +proteins,
                    fats: +fats,
                    carbohydrates: +carbohydrates,
                    attributes: selectedAttributesId,
                    goods: goodsInTheGroup.map((good) => good.id || 0).filter(id => !!id),
                }

                const redact = await RecipesService.updateRecipe(
                    currentId,
                    data
                )
            } catch (error) {
                message.error(t('errors.save_data'))
                return
            }
        } else {
            try {
                const sendData: CreateRecipe = {
                    draft: false,
                    title,
                    description,
                    calories: +calories,
                    proteins: +proteins,
                    fats: +fats,
                    carbohydrates: +carbohydrates,
                    attributes: selectedAttributesId,
                    goods: goodsInTheGroup.map((good) => good.id || 0).filter(id => !!id),
                }
                const response = await RecipesService.createRecipe(sendData)
                setCurrentId(response.id)
            } catch (error) {
                message.error(t('errors.save_data'))
                return
            }
        }
        if (file) {
            await uploadFile(file, ImageType.PHOTO)
        }
        if (preview) {
            await uploadFile(preview, ImageType.PREVIEW)
        }
        navigate(Routes.RECIPES)
        setTitle('')
        setDescr('')
        setCalories('')
        setCarbohydrates('')
        setFats('')
        setProteins('')
        setPhotoUrl(undefined)
        setPreviewUrl(undefined)
    }

    /**
     * upload image
     */
    const uploadFile = useCallback(
        async (image: File, type: ImageType) => {
            const sendData: UploadFileDocType = {
                source: FileSource.REQUEST,
                'source-parameters': JSON.stringify({}),
                entities: [
                    {
                        field:
                            type === ImageType.PHOTO
                                ? EntitiesFieldName.RECIPE_PHOTO
                                : EntitiesFieldName.RECIPE_PREVIEW,
                        documentId: currentId,
                    },
                ],
                file: image,
            }
            await sendFile(sendData)
        },
        [currentId, sendFile]
    )

    const checkAllGoods = () => {
        setAllGoods((prev) => {
            if (prev) {
                setGoods([])
            } else {
                setGoods(Array.from(goodsNotInTheGroup, (i) => +i.goodsId))
            }
            return !prev
        })
    }
    const checkAllGroupGoods = () => {
        setAllGroupGoods((prev) => {
            if (prev) {
                setGroupGoods([])
            } else {
                setGroupGoods(Array.from(goodsInTheGroup, (i) => +i.goodsId))
            }
            return !prev
        })
    }

    const onClickGroupGood = (goodId: number) => {
        if (groupGoods.includes(goodId)) {
            setGroupGoods((prev) => prev.filter((i) => i !== goodId))
        } else {
            setGroupGoods(
                produce((draft) => {
                    draft.push(goodId)
                })
            )
        }
    }

    const onClickGood = (goodId: number) => {
        if (goods.includes(goodId)) {
            setGoods((prev) => prev.filter((i) => i !== goodId))
        } else {
            setGoods(
                produce((draft) => {
                    draft.push(goodId)
                })
            )
        }
    }

    return (
        <MainLayout title={t('screen_title.recipes')}>
            <EditWrapper
                title={t(!id ? 'common.add' : 'common.edit_full')}
                onSave={onHandleSave}
            >
                <>
                    <Row gutter={[16, 16]}>
                        <Col>
                            <InputHolder>
                                <TextField
                                    label={t('forms.header')}
                                    value={title}
                                    onChange={setTitle}
                                />
                            </InputHolder>
                        </Col>
                    </Row>
                    <Row gutter={[16, 16]} style={{ marginBottom: 25 }}>
                        <Col span={24}>
                            <TextEditor
                                value={description}
                                onChange={event => setDescr(event)}
                                width={1070}
                            />
                            {!!currentId && (
                                <InputHolder>
                                    <div className={styles.blockLoad}>
                                        <PhotoLoader
                                            size={120}
                                            label={t('forms.upload_image')}
                                            image={photoUrl}
                                            onChange={setFile}
                                            onDelete={() => setFile(undefined)}
                                        />
                                        <PhotoLoader
                                            size={120}
                                            label={t('forms.upload_preview')}
                                            image={previewUrl}
                                            onChange={setPreview}
                                            onDelete={() => setFile(undefined)}
                                        />
                                    </div>
                                </InputHolder>
                            )}
                        </Col>
                    </Row>
                    <Row gutter={[16, 16]}>
                        <Col>
                            <InputHolder>
                                <TextField
                                    label={t('forms.calories')}
                                    value={calories?.toString()}
                                    onChange={setCalories}
                                    isNumber
                                />
                            </InputHolder>
                        </Col>
                        <Col>
                            <InputHolder>
                                <TextField
                                    label={t('forms.proteins')}
                                    value={proteins?.toString()}
                                    onChange={setProteins}
                                    isNumber
                                />
                            </InputHolder>
                        </Col>
                    </Row>
                    <Row gutter={[16, 16]}>
                        <Col>
                            <InputHolder>
                                <TextField
                                    label={t('forms.carbohydrates')}
                                    value={carbohydrates?.toString()}
                                    onChange={setCarbohydrates}
                                    isNumber
                                />
                            </InputHolder>
                        </Col>
                        <Col>
                            <InputHolder>
                                <TextField
                                    label={t('forms.fats')}
                                    value={fats?.toString()}
                                    onChange={setFats}
                                    isNumber
                                />
                            </InputHolder>
                        </Col>
                    </Row>
                    <Row gutter={[16, 16]} style={{ marginBottom: 25 }}>
                        <div style={{ width: 445 }}>
                            <Selector
                                multiple
                                label={t('forms.attribute')}
                                options={recipeAttributes}
                                extraOptions={[]}
                                value={selectedAttributes}
                                onChange={(data) =>
                                    setSelectedAttributes((prevState) =>
                                        prevState.includes(data)
                                            ? [...prevState].filter(
                                                  (attr) =>
                                                      attr.value !== data.value
                                              )
                                            : [...prevState, data]
                                    )
                                }
                            />
                        </div>
                    </Row>
                    <Row>
                        <Col span={8}>
                            <GoodsBlock
                                onEndReached={() => null}
                                isAll={allGroupGoods}
                                goods={goodsInTheGroup}
                                values={groupGoods}
                                onCheck={onClickGroupGood}
                                onCheckAll={checkAllGroupGoods}
                                title="Добавленные товары"
                                children={
                                    <InputHolder
                                        classes={`${styles.searchHolder}`}
                                    >
                                        <TextField
                                            label={t('forms.search')}
                                            value={''}
                                            onChange={() => null}
                                        />
                                    </InputHolder>
                                }
                            />
                        </Col>
                        <Col span={3}>
                            <div className={styles.addDeleteBlock}>
                                <div
                                    onClick={() =>
                                        removeGoodFromGroup(groupGoods)
                                    }
                                    className={styles.delUsersIcon}
                                >
                                    <DeleteUsers />
                                </div>
                                <div
                                    onClick={() => addGoodInGroup(goods)}
                                    className={styles.addUsersIcon}
                                >
                                    <AddUsers />
                                </div>
                            </div>
                        </Col>
                        <Col span={8}>
                            <GoodsBlock
                                upperOne
                                onEndReached={() => loadGoodsNotIntoGroup()}
                                isAll={allGoods}
                                goods={goodsNotInTheGroup}
                                values={goods}
                                onCheck={onClickGood}
                                onCheckAll={checkAllGoods}
                                title="Все товары"
                                children={
                                    <InputHolder
                                        classes={`${styles.searchHolder}`}
                                    >
                                        <TextField
                                            label={t('forms.search')}
                                            value={searchAllGoods}
                                            onChange={setSearchAllGoods}
                                        />
                                    </InputHolder>
                                }
                            />
                        </Col>
                    </Row>
                </>
            </EditWrapper>
        </MainLayout>
    )
}
export default RecipesRedact
