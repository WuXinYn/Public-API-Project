import React, { useEffect, useState } from 'react';
import { Modal, Form, Select, message, Radio, Descriptions } from 'antd';
import { useModel } from '@umijs/max';
import { listAllFuelPackageUsingGet } from '@/services/api-backend/fuelPackageController';
import { generateFuelPackageOrderUsingPost } from '@/services/api-backend/fuelPackageController';
import { history } from '@umijs/max';

interface OrderModalProps {
  visible: boolean;
  onCancel: () => void;
  interfaceInfo: API.UserInterfaceInfoAndName;
}

const OrderModal: React.FC<OrderModalProps> = ({ visible, onCancel, interfaceInfo }) => {
  const [form] = Form.useForm();
  const { initialState } = useModel('@@initialState');
  const loginUser = initialState?.loginUser;
  const [fuelPackages, setFuelPackages] = useState<API.FuelPackage[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [selectedPackage, setSelectedPackage] = useState<API.FuelPackage | null>(null);

  // 获取加油包列表
  const fetchFuelPackages = async () => {
    try {
      const res = await listAllFuelPackageUsingGet();
      if (res.data) {
        setFuelPackages(res.data);
      }
    } catch (error) {
      message.error('获取加油包列表失败');
    }
  };

  useEffect(() => {
    if (visible) {
      fetchFuelPackages();
    }
  }, [visible]);

  // 处理加油包选择变化
  const handlePackageChange = (value: number) => {
    const selected = fuelPackages.find(pkg => pkg.id === value);
    setSelectedPackage(selected || null);
  };

  // 处理表单提交
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (!loginUser || !loginUser.id) {
        message.error('用户未登录');
        return;
      }

      setLoading(true);

      const params: API.FuelPackageOrderGenerateRequest = {
        fuelPackageId: values.packageId,
        payMethod: values.payMethod,
        interfaceId: interfaceInfo.interfaceInfoId,
        userId: loginUser.id,
      };

      const result = await generateFuelPackageOrderUsingPost(params);

      if (result.code === 0) {
        message.success('已下单，请前往订单中心完成支付');
        onCancel();

        // 导航到订单详情页
        setTimeout(() => {
          history.push('/personal_center/order_detail');
        }, 1500);
      } else {
        message.error(result.message || '下单失败');
      }
    } catch (error: any) {
      message.error('下单失败！' + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title="购买接口调用次数"
      open={visible}
      onCancel={onCancel}
      onOk={handleSubmit}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form form={form} layout="vertical" preserve={false}>
        <Descriptions title="接口信息" bordered column={1} style={{ marginBottom: 20 }}>
          <Descriptions.Item label="接口ID">{interfaceInfo?.interfaceInfoId}</Descriptions.Item>
          <Descriptions.Item label="接口名称">{interfaceInfo?.name}</Descriptions.Item>
          <Descriptions.Item label="剩余调用次数">{interfaceInfo?.leftNum}</Descriptions.Item>
        </Descriptions>

        <Form.Item
          name="packageId"
          label="选择充值套餐"
          rules={[{ required: true, message: '请选择充值套餐' }]}
        >
          <Select
            placeholder="请选择充值套餐"
            onChange={handlePackageChange}
            options={fuelPackages.map(pkg => ({
              label: `加油包-${pkg.id} - ¥${pkg.price} (充值${pkg.amount}次)`,
              value: pkg.id,
            }))}
          />
        </Form.Item>

        {selectedPackage && (
          <Descriptions bordered column={1} style={{ marginBottom: 20 }}>
            <Descriptions.Item label="加油包ID">{selectedPackage.id || '加油包'}</Descriptions.Item>
            <Descriptions.Item label="套餐价格">¥{selectedPackage.price}</Descriptions.Item>
            <Descriptions.Item label="充值次数">{selectedPackage.amount}次</Descriptions.Item>
          </Descriptions>
        )}

        <Form.Item
          name="payMethod"
          label="支付方式"
          initialValue={1}
          rules={[{ required: true, message: '请选择支付方式' }]}
        >
          <Radio.Group>
            <Radio value={0}>微信</Radio>
            <Radio value={1}>支付宝</Radio>
          </Radio.Group>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default OrderModal;
