import React, { useState } from 'react';
import { PageContainer } from '@ant-design/pro-components';
import { Table, Tag, Card, Descriptions, message, Space, Button, Modal, Popconfirm } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { useModel, useRequest } from '@umijs/max';
import {
  getUserMenuInfoByUserIdUsingGet,
  getUserOrderInfoByIdUsingGet,
  cancelOrderUsingGet,
  deleteFuelPackageUsingDelete,
} from '@/services/api-backend/userOrderController';
import { payByPostUsingPost, toRefundUsingPost } from '@/services/api-backend/aliPayController';
import moment from 'moment';
import dayjs from 'dayjs';
import AlipayFormModal from "@/pages/PersonalCenter/OrderDetail/components/AlipayFormModal";

const OrderDetail: React.FC = () => {
  const { initialState } = useModel('@@initialState');
  const loginUser = initialState?.loginUser;
  const [total, setTotal] = useState<number>(0); // 总数据量
  const [detailModalVisible, setDetailModalVisible] = useState<boolean>(false);
  const [currentOrder, setCurrentOrder] = useState<API.UserOrderInfo | null>(null);
  const [orderList, setOrderList] = useState<API.UserOrderInfo[]>([]);
  const [visible, setVisible] = useState(false);
  const [formHtml, setFormHtml] = useState('');

  // 获取用户订单列表
  const { loading, refresh } = useRequest(
    async () => {
      if (!loginUser?.id) {
        message.error('用户未登录');
        setTotal(0);
        setOrderList([]);
        return;
      }

      try {
        const res = await getUserMenuInfoByUserIdUsingGet({
          userId: loginUser.id,
          pageSize: 10,
          current: 1,
        });

        if (res.code === 0 && res.data) {
          setTotal(res.data.total || 0);
          setOrderList(res.data.records || []);
        } else {
          message.error(res.message || '获取订单列表失败');
          setTotal(0);
          setOrderList([]);
        }
      } catch (error) {
        message.error('获取订单列表失败');
        setTotal(0);
        setOrderList([]);
      }
    },
    {
      refreshDeps: [loginUser?.id],
    }
  );

  // 订单状态映射
  const orderStatusMap: Record<number, { text: string; color: string }> = {
    0: { text: '待支付', color: 'orange' },
    1: { text: '已支付', color: 'green' },
    2: { text: '已取消', color: 'red' },
    3: { text: '支付失败', color: 'red' },
    4: { text: '已退款', color: 'red' },
  };

  // 支付方式映射
  const paymentMethodMap: Record<number, string> = {
    0: '微信',
    1: '支付宝',
    2: '银行卡',
  };

  // 检查是否可以退款（沙箱测试的订单支付时间距离当前时间小于30分钟）
  const canRefund = (payTime?: string): boolean => {
    if (!payTime) return false;
    const payMoment = moment(payTime);
    const now = moment();
    const minutesDiff  = now.diff(payMoment, 'minutes');
    return minutesDiff  < 30;
  };

  // 查看订单详情
  const handleViewDetail = async (orderId?: number) => {
    if (!orderId) {
      message.error('订单ID不存在');
      return;
    }

    try {
      const res = await getUserOrderInfoByIdUsingGet({
        id: orderId,
      });

      if (res.code === 0 && res.data) {
        setCurrentOrder(res.data);
        setDetailModalVisible(true);
      } else {
        message.error('获取订单详情失败');
      }
    } catch (error) {
      message.error('获取订单详情失败');
    }
  };

  // 去支付
  const handlePayOrder = async (record: API.UserOrderInfo) => {
    if (!record.orderNumber) {
      message.error('订单号不存在');
      return;
    }

    try {
      const payParams: API.AliPayRequest = {
        orderNumber: record.orderNumber,
        userId: loginUser?.id,
        totalAmount: record.paymentNumber,
        fuelPackageId: record.setMenuId,
        amount: record.setMenuNumber,
        number: record.number,
        serviceName: record.setMenuName
      };

      const res = await payByPostUsingPost(payParams);

      if (res.code === 0 && res.data) {
        // 支付成功，显示支付页面或跳转到支付链接
        setFormHtml(res.data); // 假设接口返回的 data 是表单 HTML
        setVisible(true); // 显示弹窗
        message.success('正在跳转到支付页面');
      } else {
        message.error(res.message || '支付失败');
      }
    } catch (error) {
      console.error('获取支付表单失败', error);
    }
  };

  // 取消订单
  const handleCancelOrder = async (orderId?: number) => {
    if (!orderId) {
      message.error('订单ID不存在');
      return;
    }

    try {
      const res = await cancelOrderUsingGet({
        id: orderId,
      });

      if (res.code === 0 && res.data) {
        message.success('订单已取消');
        refresh(); // 刷新订单列表
      } else {
        message.error(res.message || '取消订单失败');
      }
    } catch (error) {
      message.error('取消订单失败');
    }
  };

  // 删除订单
  const handleDeleteOrder = async (orderId?: number) => {
    if (!orderId) {
      message.error('订单ID不存在');
      return;
    }

    try {
      const res = await deleteFuelPackageUsingDelete({
        id: orderId,
      });

      if (res.code === 0 && res.data) {
        message.success('订单已删除');
        refresh(); // 刷新订单列表
      } else {
        message.error(res.message || '删除订单失败');
      }
    } catch (error) {
      message.error('删除订单失败');
    }
  };

  // 退款
  const handleRefund = async (record: API.UserOrderInfo) => {
    if (!record.orderNumber) {
      message.error('订单号不存在');
      return;
    }

    try {
      const refundParams: API.AliPayRequest = {
        orderNumber: record.orderNumber,
        userId: loginUser?.id,
        fuelPackageId: record.setMenuId,
        totalAmount: record.paymentNumber,
        amount: record.setMenuNumber,
        number: record.number,
        serviceName: record.setMenuName
      };

      const res = await toRefundUsingPost(refundParams);

      if (res.code === 0) {
        message.success('退款申请已提交');
        refresh(); // 刷新订单列表
      } else {
        message.error(res.message || '退款失败');
      }
    } catch (error) {
      message.error('退款失败');
    }
  };

  // 表格列定义
  const columns: ColumnsType<API.UserOrderInfo> = [
    {
      title: '订单号',
      dataIndex: 'orderNumber',
      key: 'orderNumber',
    },
    {
      title: '套餐名称',
      dataIndex: 'setMenuName',
      key: 'setMenuName',
    },

    {
      title: '支付金额(元)',
      dataIndex: 'paymentNumber',
      key: 'paymentNumber',
    },
    {
      title: '加油包含量(次)',
      dataIndex: 'setMenuNumber',
      key: 'setMenuNumber',
    },
    {
      title: '订单状态',
      dataIndex: 'orderStatus',
      key: 'orderStatus',
      render: (status: number) => (
        <Tag color={orderStatusMap[status]?.color || 'default'}>
          {orderStatusMap[status]?.text || '未知状态'}
        </Tag>
      ),
    },
    {
      title: '支付方式',
      dataIndex: 'paymentMethod',
      key: 'paymentMethod',
      render: (method: number) => paymentMethodMap[method] || '未知方式',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (text) => dayjs(text).format('YYYY年M月D日 H点mm分ss秒'),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => {
        // 根据订单状态显示不同的按钮
        const orderStatus = record.orderStatus;
        const refundAvailable = orderStatus === 1 && canRefund(record.payTime);
        const payAvailable = orderStatus === 0 && canRefund(record.createTime);
        const refundOutTime = orderStatus === 1 && !canRefund(record.payTime);
        const payOutTime = orderStatus === 0 && !canRefund(record.payTime);

        return (
          <Space>
            {/* 查看详情按钮 - 所有状态下都显示 */}
            <Button
              type="link"
              onClick={() => handleViewDetail(record.id)}
            >
              查看详情
            </Button>

            {/* 订单生成30分钟内待支付状态显示：去支付、取消订单 */}
            {payAvailable && (
              <>
                <Button
                  type="link"
                  onClick={() => handlePayOrder(record)}
                >
                  去支付
                </Button>

                <Popconfirm
                  title="确定要取消此订单吗？"
                  onConfirm={() => handleCancelOrder(record.id)}
                  okText="确定"
                  cancelText="取消"
                >
                  <Button type="link" danger>取消订单</Button>
                </Popconfirm>
              </>
            )}

            {/* 订单生成超30分钟待支付状态显示：删除按钮 */}
            {payOutTime && (
              <Popconfirm
                title="确定要删除此订单吗？"
                onConfirm={() => handleDeleteOrder(record.id)}
                okText="确定"
                cancelText="取消"
              >
                <Button type="link" danger>删除订单</Button>
              </Popconfirm>
            )}

            {/* 已取消状态显示：删除订单 */}
            {orderStatus === 2 && (
              <Popconfirm
                title="确定要删除此订单吗？"
                onConfirm={() => handleDeleteOrder(record.id)}
                okText="确定"
                cancelText="取消"
              >
                <Button type="link" danger>删除订单</Button>
              </Popconfirm>
            )}

            {/* 已支付状态下，如果支付时间距离当前时间小于30分钟，显示退款按钮 */}
            {refundAvailable && (
              <Popconfirm
                title="确定要申请退款吗？"
                onConfirm={() => handleRefund(record)}
                okText="确定"
                cancelText="取消"
              >
                <Button type="link">申请退款</Button>
              </Popconfirm>
            )}

            {/* 已支付状态下，如果支付时间距离当前时间大于30分钟，显示删除按钮 */}
            {refundOutTime && (
              <Popconfirm
                title="确定要删除此订单吗？"
                onConfirm={() => handleDeleteOrder(record.id)}
                okText="确定"
                cancelText="取消"
              >
                <Button type="link" danger>删除订单</Button>
              </Popconfirm>
            )}

            {/* 支付失败或已退款，显示删除按钮 */}
            {(orderStatus === 3 || orderStatus === 4) && (
              <Popconfirm
                title="确定要删除此订单吗？"
                onConfirm={() => handleDeleteOrder(record.id)}
                okText="确定"
                cancelText="取消"
              >
                <Button type="link" danger>删除订单</Button>
              </Popconfirm>
            )}
          </Space>
        );
      },
    },
  ];

  return (
    <PageContainer style={{
      background: 'linear-gradient(rgba(246, 244, 244, 0.73), rgba(254, 250, 250, 0.69)), url("/gift/hutao02.gif")',
      backgroundSize: '9%',
      backgroundPosition: 'center -50px' ,
      backgroundRepeat: 'no-repeat',
      height: '90vh',
    }}>
      <Card title="我的订单" bordered={false}>
        <Descriptions style={{ marginBottom: 20 }} bordered>
          <Descriptions.Item label="用户ID">{loginUser?.id}</Descriptions.Item>
          <Descriptions.Item label="用户名">{loginUser?.userName}</Descriptions.Item>
          <Descriptions.Item label="订单总数">{total || 0}</Descriptions.Item>
        </Descriptions>

        <Table
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={orderList}
          pagination={{
            total: total,
            showSizeChanger: true,
            showQuickJumper: true,
          }}
        />

        {/* 订单支付弹窗 */}
        <AlipayFormModal
          visible={visible}
          formHtml={formHtml}
          onCancel={() => setVisible(false)}
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
              <Descriptions.Item label="接口id">{currentOrder.interfaceId}</Descriptions.Item>
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

export default OrderDetail;
