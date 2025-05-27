import { ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Modal } from 'antd';
import React from 'react';

export type Props = {
  onCancel: () => void;
  onSubmit: (values: API.NotificationInfo) => Promise<void>;
  visible: boolean;
};

const createNOtificationModel = [
  { 
    title: '类型', dataIndex: 'type', key: 'type' , valueType: 'select',
    valueEnum: {
      system: { text: '系统通知', status: 'system' },
      user: { text: '用户通知', status: 'user' },
      warning: { text: '警告', status: 'warning' },
      error: { text: '重大错误', status: 'error' },
    },
    formItemProps: { rules: [ {required: true, message: '请选择类型', },],},
  },
  { title: '标题', dataIndex: 'title', key: 'title' ,valueType: 'text',formItemProps: { rules: [ {required: true, message: '请输入标题', },],}},
  { title: '被通知者ID', 
    dataIndex: 'user_id',
    key: 'user_id' ,
    formItemProps: { 
      rules: [ {
        required: true, message: '请输入有效ID', type: 'number', min: 0, transform: (value: any) => Number(value), 
      },
    ],
  }, },
  { 
    title: '通知内容', 
    dataIndex: 'content', 
    key: 'content', 
    valueType: 'textarea' ,
    formItemProps: { 
      rules: [ {
          required: true, message: '请输入通知内容', // 必填校验提示
        },
      ],
    },
  },
]
const CreateModel: React.FC<Props> = (props) => {
  const { visible,onCancel,onSubmit } = props;

  return (
    <Modal open={visible} footer={null} onCancel={() => onCancel?.()} >
      <ProTable type="form" columns={ createNOtificationModel } onSubmit={
        async (value) => {
            onSubmit?.(value)
        }
      }/>
    </Modal>
  );
};

export default CreateModel;
