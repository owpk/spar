import React, { Suspense } from 'react'
import * as ReactDOM from 'react-dom'
import { Provider } from 'react-redux'
import { App } from './components'
import './styles/app.global.scss'
import { store } from './store'
import './i18n';
import { Loader } from './components/simples/Loader'
import ru from "antd/lib/locale/ru_RU";
import { ConfigProvider } from "antd";
import 'moment/locale/ru'

  


ReactDOM.render(
    <React.StrictMode>
        <Suspense fallback={<Loader />}>
            <Provider store={store}>
                <ConfigProvider  locale={ru}>
                    <App />
                </ConfigProvider>
            </Provider>
        </Suspense>
    </React.StrictMode>,
    document.getElementById('root')
)
