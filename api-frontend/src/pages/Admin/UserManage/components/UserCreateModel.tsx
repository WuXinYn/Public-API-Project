import { ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Modal } from 'antd';
import React from 'react';

export type Props = {
  onCancel: () => void;
  onSubmit: (values: API.UserVO) => Promise<void>;
  visible: boolean;
};

const userModel = [
  {
    title: '头像',
    dataIndex: 'userAvatar',
    valueType: 'text',
  },
  {
    title: '用户账号',
    dataIndex: 'userAccount',
    valueType: 'text',
    formItemProps: { rules: [
      {
        required: true,
        message: '用户账号不能为空',
      },
      {
        min: 4,
        message: '账号长度不能小于 4 个字符',
      }
    ],}
  },
  {
    title: '用户密码',
    dataIndex: 'userPassword',
    valueType: 'text',
    formItemProps: { rules: [
      {
        required: true,
        message: '用户密码不能为空',
      },
      {
        min: 8,
        type: 'string',
        message: '密码长度不能小于8！'
      },
    ],}
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    valueType: 'text',
    formItemProps: { rules: [ {required: true,},],}
  },
  {
    title: '性别',
    dataIndex: 'gender',
    valueType: 'select',
    valueEnum: {
      '0': {text: '男'},
      '1': {text: '女'},
    }
  },
  {
    title: '权限',
    dataIndex: 'userRole',
    valueType: 'select',
    valueEnum: {
      'user': {text: '普通用户', status: 'Default'},
      'admin': {
        text: '管理员',
        status: 'Success',
      },
    },
    formItemProps: { rules: [ {required: true,},],}
  },
]

const UserCreateModel: React.FC<Props> = (props) => {
  const { visible,onCancel,onSubmit } = props;

  return (
    <Modal open={visible} footer={null} onCancel={() => onCancel?.()} >
      <ProTable type="form" columns={ userModel } onSubmit={
        async (value) => {
            onSubmit?.(value)
        }
      }/>
    </Modal>
  );
};

export default UserCreateModel;
