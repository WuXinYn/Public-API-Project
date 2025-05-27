import React, { useState } from 'react';
import { PageContainer } from '@ant-design/pro-components';
import { PlusOutlined } from '@ant-design/icons';
import { Button, message, Popconfirm, Space, Table, Modal, Form, Input, InputNumber } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  addFuelPackageUsingPost,
  deleteFuelPackageUsingDelete,
  listFuelPackageUsingGet,
  updateFuelPackageUsingPost,
} from '@/services/api-backend/fuelPackageController';
import { useRequest } from '@umijs/max';

const ResourceManage: React.FC = () => {
  const [form] = Form.useForm();
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<string>('新增加油包');
  const [editingRecord, setEditingRecord] = useState<API.FuelPackage | null>(null);
  const [total, setTotal] = useState<number>(0); // 总数据量

  // 获取加油包列表
  const { data, loading, refresh } = useRequest(
    async (params = {}) => {
      const res = await listFuelPackageUsingGet({
        ...params,
        pageSize: 10,
        current: 1,
      });
      setTotal(res.data?.total || 0)
      return {
        data: res.data?.records || [],
        total: res.data?.total || 0,
      };
    },
    {
      refreshDeps: [],
    }
  );

  // 处理编辑
  const handleEdit = (record: API.FuelPackage) => {
    setEditingRecord(record);
    setModalTitle('编辑加油包');
    form.setFieldsValue({
      name: record.name,
      description: record.description,
      price: record.price,
      amount: record.amount,
    });
    setModalVisible(true);
  };

  // 处理删除
  const handleDelete = async (id?: number) => {
    if (!id) return;
    try {
      const res = await deleteFuelPackageUsingDelete({ id });
      if (res.code === 0) {
        message.success('删除成功');
        refresh();
      } else {
        message.error(res.message || '删除失败');
      }
    } catch (error) {
      message.error('删除失败');
    }
  };

  // 处理新增
  const handleAdd = () => {
    setEditingRecord(null);
    setModalTitle('新增加油包');
    form.resetFields();
    setModalVisible(true);
  };

  // 处理新增/编辑表单提交
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingRecord) {
        // 更新加油包
        const res = await updateFuelPackageUsingPost({
          ...values,
          id: editingRecord.id,
        });
        if (res.code === 0) {
          message.success('更新成功');
          setModalVisible(false);
          refresh();
        } else {
          message.error(res.message || '更新失败');
        }
      } else {
        // 新增加油包
        const res = await addFuelPackageUsingPost(values);
        if (res.code === 0) {
          message.success('添加成功');
          setModalVisible(false);
          refresh();
        } else {
          message.error(res.message || '添加失败');
        }
      }
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  // 表格列定义
  const columns: ColumnsType<API.FuelPackage> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: '价格（元）',
      dataIndex: 'price',
      key: 'price',
    },
    {
      title: '加油包含量',
      dataIndex: 'amount',
      key: 'amount',
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      key: 'createdTime',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <a onClick={() => handleEdit(record)}>编辑</a>
          <Popconfirm
            title="确定要删除这个加油包吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <a>删除</a>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增加油包
        </Button>
      </div>

      <Table
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={data}
        pagination={{
          total: total,
          showSizeChanger: true,
          showQuickJumper: true,
        }}
      />

      <Modal
        title={modalTitle}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="name"
            label="名称"
            rules={[{ required: true, message: '请输入加油包名称' }]}
          >
            <Input placeholder="请输入加油包名称" />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea placeholder="请输入加油包描述" />
          </Form.Item>
          <Form.Item
            name="price"
            label="价格（元）"
            rules={[{ required: true, message: '请输入价格' }]}
          >
            <InputNumber min={0} style={{ width: '100%' }} placeholder="请输入价格" />
          </Form.Item>
          <Form.Item
            name="amount"
            label="加油包含量"
            rules={[{ required: true, message: '请输入加油包含量' }]}
          >
            <InputNumber min={0} style={{ width: '100%' }} placeholder="请输入加油包含量" />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
};

export default ResourceManage;
