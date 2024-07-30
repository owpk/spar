import { Col, message, Row } from "antd";
import React, { FC, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import { Routes } from "../../../../config";
import { OutsideDocsService } from "../../../../services/OutsideDocsService";
import { OutsideDocType } from "../../../../types";
import { MainLayout } from "../../../complexes/MainLayout";
import { EditWrapper } from "../../../simples/EditWrapper";
import { TextField } from "../../../simples/TextField";

const OutsideDocsEditScreen: FC = () => {
  const { t } = useTranslation();
  const idAlias = useLocation().search.split("=")[1];

  const navigate = useNavigate();

  const style = {};

  const [item, setItem] = useState<OutsideDocType>();
  const [alias, setAlias] = useState<string>("");
  const [docName, setDocName] = useState<string>("");
  const [link, setLink] = useState<string>("");

  const [loading, setLoading] = useState(false);

  const load = async (id: string) => {
    setLoading(true);
    try {
      const result = await OutsideDocsService.getOutsideDocsById(id);

      setItem(result);

      setAlias(result.alias);
      setDocName(result.title);
      setLink(result.url);
    } catch (error) {
      message.error(t("errors.get_data"));
    }
    setLoading(false);
  };
  useEffect(() => {
    if (idAlias !== undefined) {
      load(idAlias).then();
    }
  }, [idAlias]);

  const onHandleSave = useCallback(async () => {
    const data = {
      alias: alias,
      title: docName,
      url: link
    };
    if (idAlias) {
      try {
        const redact = await OutsideDocsService.updateOutsideDocs(
          idAlias,
          data
        );
        message.success(t("suсcess_messages.save_data"));
        navigate(Routes.OUTSIDE_DOCS_SCREEN);
      } catch (error) {
        message.error(t("errors.save_data"));
      }
    } else {
      try {
        const save = await OutsideDocsService.createOutsideDocs(data);
        message.success(t("suсcess_messages.save_data"));
        navigate(Routes.OUTSIDE_DOCS_SCREEN);
      } catch (error) {
        message.error(t("errors.save_data"));
      }
    }
  }, [alias, docName, idAlias, link, navigate, t]);

  return (
    <MainLayout isLoading={loading} title={t("screen_title.outside_docs")}>
      <EditWrapper
        onSave={onHandleSave}
        title={t(!idAlias ? "common.add" : "common.edit_full")}
      >
        <Row
          gutter={{ xs: 8, sm: 16, md: 24, lg: 32 }}
          style={{ alignItems: "end" }}
        >
          <Col className="gutter-row" span={6}>
            <div style={style}>
              <TextField
                label={t("forms.alias_only_lat")}
                onChange={setAlias}
                value={alias}
              />
            </div>
          </Col>
          <Col className="gutter-row" span={6}>
            <div style={style}>
              <div style={style}>
                <TextField
                  label={t("forms.doc_name")}
                  onChange={setDocName}
                  value={docName}
                />
              </div>
            </div>
          </Col>
          <Col className="gutter-row" span={6}>
            <div style={style}>
              <div style={style}>
                <TextField
                  label={t("forms.link")}
                  onChange={setLink}
                  value={link}
                />
              </div>
            </div>
          </Col>
        </Row>
      </EditWrapper>
    </MainLayout>
  );
};

export default OutsideDocsEditScreen;
