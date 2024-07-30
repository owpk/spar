import { Col, Layout, Row, Tabs } from "antd";
import Title from "antd/lib/typography/Title";
import classNames from "classnames";
import React, { FC, useRef, useState } from "react";
import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useAppSelector } from "../../../hooks/store";
import { selectAppLoading } from "../../../store/slices/appSlice";
import { Button } from "../../simples/Button";
import { ButtonType } from "../../simples/Button/Button";
import { Filter } from "../Filter";
import { Loader } from "../../simples/Loader";
import { TextField } from "../../simples/TextField";
import styles from "./MainLayout.module.scss";
import { InputHolder } from "../../simples/InputHolder";
import { FilterType } from "../Filter/Filter";

const { TabPane } = Tabs;

export type TabType = {
  name: string;
  key: string;
};

type Props = {
  children: JSX.Element;
  title: string;
  isLoading?: boolean;
  onAdd?: () => void;
  onEndReached?: () => void;
  onSearch?: (search: string) => void;
  onFilter?: (filter: FilterType) => void;
  searchPlaceholder?: string;
  tabs?: Array<TabType>;
  tabActive?: string;
  onChangeTab?: (key: string) => void;
  defaultTab?: string;
  customFilter?: JSX.Element
};
const { Content, Sider } = Layout;

/**
 *
 * @param children - component in content field
 * @param title - screen title
 * @param isLoading - boolean true if we doing async request
 * @param onAdd -
 * @param onEndReached -
 * @param onSearch -
 * @param searchPlaceholder -
 * @param tabs -
 * @param tabActive -
 * @param onChangeTab -
 * @param defaultTab -
 * @returns wrapper for all pages
 */
const MainLayout: FC<Props> = ({
  children,
  title,
  isLoading,
  onAdd,
  onEndReached,
  onSearch,
  searchPlaceholder,
  onFilter,
  tabs,
  tabActive,
  onChangeTab,
  defaultTab,
  customFilter
}) => {
  const { t } = useTranslation();

  const loading = useAppSelector(selectAppLoading);

  const contentRef = useRef<HTMLDivElement>(null);
  const reached = useRef(false);

  const [search, setSearch] = useState<string>("");

  useEffect(() => {
    if (onSearch) onSearch(search);
  }, [onSearch, search]);

  /**
   *
   * dynamic pagination
   */
  const handleScroll = () => {
    if (!contentRef.current) {
      return;
    }

    const contentHeight = contentRef.current.offsetHeight;
    const scrollHeight = contentRef.current.scrollHeight;

    const scrollTop = contentRef.current.scrollTop;

    if (scrollHeight <= contentHeight) {
      return;
    }

    const afterEndReach =
      scrollHeight - (scrollTop + contentHeight) < contentHeight / 2;

    if (afterEndReach && !reached.current) {
      reached.current = true;
      onEndReached && onEndReached();
    } else if (!afterEndReach && reached.current) {
      reached.current = false;
    }
  };

  return (
      <div ref={contentRef} onScroll={handleScroll} className={styles.layoutContent}>
        <Content
          className={classNames("site-layout-background", styles.mainContent)}
        >
          <Title className={styles.title} level={2}>
            {title}
          </Title>
          <Content className={styles.content}>
            <div className="">
              {tabs && (
                <Tabs
                  activeKey={tabActive}
                  defaultActiveKey={defaultTab}
                  onChange={onChangeTab}
                  className="block"
                >
                  {tabs.map((tab) => (
                    <TabPane tab={tab.name} key={tab.key} />
                  ))}
                </Tabs>
              )}
              {!!customFilter && customFilter}
              <Row justify={"space-between"}>
                {/* <div className={styles.addSearchBlock}> */}
                {onAdd && (
                  <Col span={5} style={{ marginLeft: "15px" }}>
                    <Button
                      typeStyle={ButtonType.SECOND}
                      onClick={onAdd}
                      backgroundColor={"#ffffff"}
                      colorText={"#007C45"}
                      label={t("common.add")}
                      textUp={"uppercase"}
                    />
                  </Col>
                )}
                <Col span={!!onFilter ? 24 : 12}>
                  {onSearch && (
                    <Row
                      justify={"space-between"}
                      style={{ width: "100%" }}
                      gutter={[16, 16]}
                    >
                      <Col span={!!!onFilter ? 24 : 12}>
                        <TextField
                          isSearch
                          label={""}
                          placeholder={searchPlaceholder || ""}
                          onChange={setSearch}
                          value={search}
                        />
                      </Col>

                      {!!onFilter && (
                        <Col>
                          <InputHolder>
                            <Filter onFilter={onFilter} />
                          </InputHolder>
                        </Col>
                      )}
                    </Row>
                  )}
                </Col>
                {/* </div> */}
              </Row>

              {children}
              {(isLoading || loading) && <Loader />}
            </div>
          </Content>
        </Content>
      </div>
  );
};

export default React.memo(MainLayout);
