import React, { useState } from 'react';
import { Card, Button, message, Upload, Form } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import { ProForm, ProFormText, ProFormTextArea, ProFormSelect } from '@ant-design/pro-components';
import { publishInterface } from '@/services/ant-design-pro/api';
import styles from './index.less';
import {addInterfaceInfoUsingPost} from "@/services/api-backend/interfaceInfoController";

const PublishInterface: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (values: API.InterfaceInfoAddRequest) => {
    try {
      setLoading(true);
      const response = await addInterfaceInfoUsingPost(values);
      if (response.code === 0) {
        message.success('接口发布成功！');
        form.resetFields();
      }
      else {
        message.error('接口发布失败！' + response.message);
      }
    } catch (error) {
      message.error('发布失败，请重试！');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container} style={{
      background: 'url("/zzz/xingjianya01.png")',
      // backgroundSize: '10%',
      // backgroundPosition: 'center -50px' ,
      backgroundRepeat: 'no-repeat',
      backgroundPositionX: 'right',
      height: "auto",
    }}>
      <Card title="发布新接口" className={styles.card}>
        <ProForm
          form={form}
          onFinish={handleSubmit}
          submitter={{
            render: (props) => {
              return (
                <Button
                  type="primary"
                  loading={loading}
                  onClick={() => props.form?.submit()}
                >
                  发布接口
                </Button>
              );
            },
          }}
        >
          <ProFormText
            name="name"
            label="接口名称"
            placeholder="请输入接口名称"
            rules={[{ required: true, message: '请输入接口名称' }]}
          />

          <ProFormText
            name="url"
            label="接口地址"
            placeholder="请输入接口地址"
            rules={[{ required: true, message: '请输入接口地址' }]}
          />

          <ProFormSelect
            name="method"
            label="请求方法"
            options={[
              { label: 'GET', value: 'GET' },
              { label: 'POST', value: 'POST' },
              { label: 'PUT', value: 'PUT' },
              { label: 'DELETE', value: 'DELETE' },
            ]}
            rules={[{ required: true, message: '请选择请求方法' }]}
          />

          <ProFormTextArea
            name="description"
            label="接口描述"
            placeholder="请详细描述接口的功能和使用方法"
            rules={[{ required: true, message: '请输入接口描述' }]}
          />

          <ProFormTextArea
            name="requestParams"
            label="请求参数示例"
            placeholder="请输入请求参数的JSON格式说明"
            fieldProps={{
              autoSize: { minRows: 4 },
            }}
            initialValue={`{
    // 请按照(参数名 : MOCK值)格式填写请求参数,多行用英文逗号隔开
    // 管理员将用你提交的参数示例进行接口审核测试，请认真填写
    // 提交前请删除注释
    "username": "旅行者"
}`}
            rules={[{ required: true }]}
          />

          <ProFormTextArea
            name="requestHeader"
            label="请求头"
            placeholder="请输入请求头"
            fieldProps={{
                autoSize: { minRows: 3 },
            }}
            initialValue={`{
    "Content-Type": "application/json"
}`}
            rules={[{ required: true }]}
          />

          <ProFormTextArea
            name="responseHeader"
            label="响应头"
            placeholder="请输入响应头"
            fieldProps={{
                autoSize: { minRows: 3 },
            }}
            initialValue={`{
    "Content-Type": "application/json"
}`}
            rules={[{ required: true }]}
          />

          <Form.Item
            name="documentation"
            label="接口文档"
          >
            <Upload>
              <Button icon={<UploadOutlined />}>上传接口文档</Button>
            </Upload>
          </Form.Item>
        </ProForm>
      </Card>
    </div>
  );
};

export default PublishInterface;
