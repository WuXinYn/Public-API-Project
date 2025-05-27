import { ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Modal } from 'antd';
import React, {useEffect, useRef} from 'react';
import {ProColumns} from "@ant-design/pro-table/es/typing";


export type Props = {
  values: API.InterfaceInfo;
  columns: ProColumns<API.InterfaceInfo>[];
  onCancel: () => void;
  onSubmit: (values: API.InterfaceInfo) => Promise<void>;
  visible: boolean;
};

const UpdateModel: React.FC<Props> = (props) => {
  const { values,columns,visible,onCancel,onSubmit } = props;
  const formRef = useRef<any>();
  // 监听某个变量的变化
  useEffect(() => {
    if (formRef){
      formRef.current?.setFieldsValue(values)
    }
  },[values]);

  return (
    <Modal open={visible} footer={null} onCancel={() => onCancel?.()} >
      <ProTable
        type="form"
        columns={ columns }
        formRef={ formRef }
        onSubmit={
        async (value) => {
            onSubmit?.(value as API.InterfaceInfo)
        }
      }/>
    </Modal>
  );
};

export default UpdateModel;
