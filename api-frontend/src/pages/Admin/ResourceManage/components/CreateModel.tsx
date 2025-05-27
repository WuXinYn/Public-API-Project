import { ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Modal } from 'antd';
import React from 'react';

export type Props = {
  onCancel: () => void;
  onSubmit: (values: API.FuelPackage) => Promise<void>;
  visible: boolean;
};

const createModel = [
  { title: '加油包名称', dataIndex: 'name' , key: 'name' ,
    formItemProps: {
      rules: [{
        required: true,
      }]
    }
  },
  { title: '描述', dataIndex: 'description', key: 'description' },
  { title: '价格', dataIndex: 'price', key: 'price',
    formItemProps: {
      rules: [{
        required: true,
      }]
    }},
  { title: '加油包含量', dataIndex: 'amount', key: 'amount',
    formItemProps: {
      rules: [{
        required: true,
      }]
    }},
]
const CreateModel: React.FC<Props> = (props) => {
  const { visible,onCancel,onSubmit } = props;

  return (
    <Modal open={visible} footer={null} onCancel={() => onCancel?.()} >
      <ProTable type="form" columns={ createModel } onSubmit={
        async (value) => {
          onSubmit?.(value)
        }
      }/>
    </Modal>
  );
};

export default CreateModel;
