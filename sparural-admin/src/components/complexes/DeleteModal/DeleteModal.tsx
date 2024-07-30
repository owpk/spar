import { FC,  } from 'react'
import { Button } from '../../simples/Button'
import styles from './DeleteModal.module.scss'
//@ts-ignore
import Modal from 'react-modal';
import { ButtonType } from '../../simples/Button/Button';
import { t } from 'i18next';

type Props = {
    onSubmit: () => void
    onCancel: () => void
    visible: boolean
}

/**
 * 
 * @param onSubmit 
 * @param onCancel 
 * @param visible 
 * @returns 
 */

Modal.setAppElement('#root');

const DeleteModal: FC<Props> = ({ onSubmit, onCancel, visible }) => {

    return (
        <>
            <Modal
                isOpen={visible}
                onRequestClose={onclose}
                overlayClassName={styles.modalOverlay}
                className={styles.modal}
            >
                <div className={styles.wrapper}>
                    <div className={styles.portal}>
                        <span className={styles.portal__text}>
                            Вы действительно хотите удалить ?
                        </span>
                        <div className={styles.portal__button}>
                            <Button
                                label={t("common.cancel")}
                                onClick={onCancel}
                                typeStyle={ButtonType.SECOND}
                                textUp="uppercase"
                            />
                            <Button
                                label={t("common.delete")}
                                onClick={onSubmit}
                                backgroundColor="#007C45"
                                textUp="uppercase"
                            />
                        </div>
                    </div>
                </div>
            </Modal>
        </>
    )
}

export default DeleteModal;