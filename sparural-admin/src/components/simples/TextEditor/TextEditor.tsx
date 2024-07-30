import React, { FC } from 'react'
import JoditEditor, { Jodit } from 'jodit-react'
import styles from './TextEditor.module.scss'
import "./Jodit.css"
import classNames from 'classnames'

const buttons = [
    'undo',
    'redo',
    '|',
    'bold',
    'strikethrough',
    'underline',
    'italic',
    '|',
    'superscript',
    'subscript',
    '|',
    'align',
    '|',
    'ul',
    'ol',
    'outdent',
    'indent',
    '|',
    'font',
    'fontsize',
    'brush',
    'paragraph',
    '|',
    'image',
    'link',
    'table',
    '|',
    'hr',
    'eraser',
    'copyformat',
    '|',
    'fullsize',
    'selectall',
    'print',
    '|',
    'source',
    '|',
]

type Props = {
    value: string
    onChange: (data: string) => void
    error?: string
    width?: number
}

/**
 *
 * @param value
 * @param onChange
 * @returns
 */
const TextEditor: FC<Props> = ({ value, onChange, error, width = 1000 }) => {
    const config = {
        readonly: false,
        addNewLineOnDBLClick: false,
        toolbar: true,
        spellcheck: true,
        language: 'ru',
        // toolbarButtonSize: "middle",
        toolbarAdaptive: false,
        showCharsCounter: false,
        showWordsCounter: false,
        showXPathInStatusbar: false,
        askBeforePasteHTML: true,
        askBeforePasteFromWord: true,
        //defaultActionOnPaste: "insert_clear_html",
        buttons: buttons,
        uploader: {
            ...Jodit.defaultOptions.uploader,
            insertImageAsBase64URI: true,
        },
        width: width,
        height: '100%',
        tabIndex: 1,
    }
    return (
        <div
            className={classNames({
                [styles.root]: !!error,
            })}
        >
            <JoditEditor
                value={value}
                config={config}
                onBlur={onChange}
                // onChange={onChange}
            />
            {!!error && <span className={styles.error}>{error}</span>}
        </div>
    )
}

export default TextEditor
