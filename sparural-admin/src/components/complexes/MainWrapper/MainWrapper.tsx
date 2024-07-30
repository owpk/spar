import React, { FC } from 'react';
import styles from './MainWrapper.module.scss'

import { Header as MainHeader } from "../../complexes/Header";
import Sider from 'antd/lib/layout/Sider';
import { Sidebar } from '../Sidebar';

type Props = {
    children: JSX.Element
}
const MainWrapper: FC<Props> = ({children}) => {
    return (
        <div
        style={{
          height: "100vh",
          maxHeight: "100vh",
        //   overflowY: "auto",
          backgroundColor: "#fff"
        }}
      >
        <div
          style={{
            position: "fixed",
            zIndex: 16,
            width: "100%",
            padding: 0,
            backgroundColor: "#fff",
            lineHeight: 0
          }}
        >
          <MainHeader />
        </div>
        <Sider
          className={styles.sidebar}
          width={250}
          style={{
            background: "#fff",
            zIndex: 15,
          }}
        >
          <Sidebar />
        </Sider>
          {children}
      </div>
    )
}


export default React.memo(MainWrapper)