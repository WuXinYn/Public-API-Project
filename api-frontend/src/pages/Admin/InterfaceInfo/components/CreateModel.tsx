import { ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Modal } from 'antd';
import React from 'react';
import {ProColumns} from "@ant-design/pro-table/es/typing";

export type Props = {
  columns: ProColumns<API.InterfaceInfo>[];
  onCancel: () => void;
  onSubmit: (values: API.InterfaceInfo) => Promise<void>;
  visible: boolean;
};

const CreateModel: React.FC<Props> = (props) => {
  const { columns,visible,onCancel,onSubmit } = props;

  return (
    <Modal open={visible} footer={null} onCancel={() => onCancel?.()} >
      <ProTable type="form" columns={ columns } onSubmit={
        async (value) => {
            onSubmit?.(value)
        }
      }/>
    </Modal>
  );
};

export default CreateModel;
