import React, { useState } from 'react';
import { PageContainer } from '@ant-design/pro-components';
import { Table, Tag, Card, Descriptions, message, Space, Button, Modal, Popconfirm } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { getAllOrderInfosUsingGet, deleteFuelPackageUsingDelete } from '@/services/api-backend/userOrderController';
import { useRequest } from '@umijs/max';
import dayjs from 'dayjs';

// 定义订单数据接口
interface OrderData {
  records: API.UserOrderInfo[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

const OrderManagement: React.FC = () => {
  const [searchText, setSearchText] = useState('');
  const [detailModalVisible, setDetailModalVisible] = useState<boolean>(false);
  const [currentOrder, setCurrentOrder] = useState<API.UserOrderInfo | null>(null);
  const [orderData, setOrderData] = useState<OrderData>({
    records: [],
    total: 0,
    size: 0,
    current: 1,
    pages: 0
  });
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0
  });

  // 获取订单列表
  const { loading, run: fetchOrders } = useRequest(
    async (params: { current: number; pageSize: number; orderNumber?: string }) => {
      try {
        const response = await getAllOrderInfosUsingGet({
          current: params.current,
          pageSize: params.pageSize,
          orderNumber: params.orderNumber || undefined
        });
        
        if (response.code === 0 && response.data) {
          const data = response.data as OrderData;
          setOrderData(data);
          setPagination(prev => ({
            ...prev,
            total: data.total || 0
          }));
        } else {
          message.error(response.message || '获取订单数据失败');
        }
      } catch (error) {
        message.error('获取订单列表失败');
        console.error('获取订单列表出错:', error);
      }
    },
    {
      manual: true
    }
  );

  // 初始加载和条件变化时获取数据
  React.useEffect(() => {
    fetchOrders({
      current: pagination.current,
      pageSize: pagination.pageSize,
      orderNumber: searchText
    });
  }, [pagination.current, pagination.pageSize, searchText]);

  // 刷新订单列表
  const refreshOrders = () => {
    fetchOrders({
      current: pagination.current,
      pageSize: pagination.pageSize,
      orderNumber: searchText
    });
  };

  // 订单状态映射
  const orderStatusMap: Record<number, { text: string; color: string }> = {
    0: { text: '待支付', color: 'orange' },
    1: { text: '已支付', color: 'green' },
    2: { text: '已取消', color: 'red' },
    3: { text: '已完成', color: 'blue' }
  };

  // 支付方式映射
  const paymentMethodMap: Record<number, string> = {
    0: '微信',
    1: '支付宝',
    2: '银行卡'
  };

  // 查看订单详情
  const handleViewDetail = (order: API.UserOrderInfo) => {
    setCurrentOrder(order);
    setDetailModalVisible(true);
  };

  // 处理删除订单
  const handleDeleteOrder = async (orderId?: number) => {
    if (!orderId) {
      message.error('订单ID不存在');
      return;
    }

    try {
      const res = await deleteFuelPackageUsingDelete({
        id: orderId
      });

      if (res.code === 0 && res.data) {
        message.success('订单已删除');
        refreshOrders(); // 刷新订单列表
      } else {
        message.error(res.message || '删除订单失败');
      }
    } catch (error) {
      message.error('删除订单失败');
    }
  };

  // 表格列定义
  const columns: ColumnsType<API.UserOrderInfo> = [
    {
      title: '订单号',
      dataIndex: 'orderNumber',
      key: 'orderNumber'
    },
    {
      title: '用户ID',
      dataIndex: 'userId',
      key: 'userId'
    },
    {
      title: '套餐名称',
      dataIndex: 'setMenuName',
      key: 'setMenuName'
    },
    {
      title: '支付金额(元)',
      dataIndex: 'paymentNumber',
      key: 'paymentNumber'
    },
    {
      title: '加油包含量(次)',
      dataIndex: 'setMenuNumber',
      key: 'setMenuNumber'
    },
    {
      title: '订单状态',
      dataIndex: 'orderStatus',
      key: 'orderStatus',
      render: (status: number) => (
        <Tag color={orderStatusMap[status]?.color || 'default'}>
          {orderStatusMap[status]?.text || '未知状态'}
        </Tag>
      )
    },
    {
      title: '支付方式',
      dataIndex: 'paymentMethod',
      key: 'paymentMethod',
      render: (method: number) => paymentMethodMap[method] || '未知方式'
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (text) => text ? dayjs(text).format('YYYY年M月D日 H点mm分ss秒') : '-'
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            onClick={() => handleViewDetail(record)}
          >
            查看详情
          </Button>

          <Popconfirm
            title="确定要删除此订单吗？"
            onConfirm={() => handleDeleteOrder(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" danger>删除订单</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <PageContainer>
      <Card title="订单管理" bordered={false}>
        <Descriptions style={{ marginBottom: 20 }} bordered>
          <Descriptions.Item label="总订单数">{orderData.total}</Descriptions.Item>
        </Descriptions>

        <Space style={{ marginBottom: 16 }}>
          <input
            placeholder="搜索订单号"
            value={searchText}
            onChange={e => setSearchText(e.target.value)}
            style={{
              padding: '6px 11px',
              border: '1px solid #d9d9d9',
              borderRadius: '2px',
              width: '200px'
            }}
          />
        </Space>

        <Table
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={orderData.records}
          pagination={{
            ...pagination,
            total: orderData.total,
            showSizeChanger: true,
            showQuickJumper: true,
            onChange: (page, pageSize) => {
              setPagination(prev => ({
                ...prev,
                current: page,
                pageSize: pageSize || 10
              }));
            }
          }}
          scroll={{ x: 1300 }}
        />

        {/* 订单详情弹窗 */}
        <Modal
          title="订单详情"
          open={detailModalVisible}
          onCancel={() => {
            setDetailModalVisible(false);
            setCurrentOrder(null);
          }}
          footer={[
            <Button
              key="close"
              onClick={() => {
                setDetailModalVisible(false);
                setCurrentOrder(null);
              }}
            >
              关闭
            </Button>
          ]}
        >
          {currentOrder && (
            <Descriptions bordered column={1}>
              <Descriptions.Item label="订单ID">{currentOrder.id}</Descriptions.Item>
              <Descriptions.Item label="订单号">{currentOrder.orderNumber}</Descriptions.Item>
              <Descriptions.Item label="用户id">{currentOrder.userId}</Descriptions.Item>
              <Descriptions.Item label="加油包id">{currentOrder.setMenuId}</Descriptions.Item>
              <Descriptions.Item label="套餐名称">{currentOrder.setMenuName}</Descriptions.Item>
              <Descriptions.Item label="加油包含量">{currentOrder.setMenuNumber}</Descriptions.Item>
              <Descriptions.Item label="数量">{currentOrder.number}</Descriptions.Item>
              <Descriptions.Item label="支付金额(元)">{currentOrder.paymentNumber}</Descriptions.Item>
              <Descriptions.Item label="订单状态">
                <Tag color={orderStatusMap[currentOrder.orderStatus || 0]?.color || 'default'}>
                  {orderStatusMap[currentOrder.orderStatus || 0]?.text || '未知状态'}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="支付方式">
                {paymentMethodMap[currentOrder.paymentMethod || 0] || '未知方式'}
              </Descriptions.Item>
              <Descriptions.Item label="创建时间">
                {currentOrder.createTime ? dayjs(currentOrder.createTime).format('YYYY年M月D日 H点mm分ss秒') : ''}
              </Descriptions.Item>
              <Descriptions.Item label="支付时间">
                {currentOrder.payTime ? dayjs(currentOrder.payTime).format('YYYY年M月D日 H点mm分ss秒') : ''}
              </Descriptions.Item>
              <Descriptions.Item label="更新时间">
                {currentOrder.updateTime ? dayjs(currentOrder.updateTime).format('YYYY年M月D日 H点mm分ss秒') : ''}
              </Descriptions.Item>
            </Descriptions>
          )}
        </Modal>
      </Card>
    </PageContainer>
  );
};

export default OrderManagement;
