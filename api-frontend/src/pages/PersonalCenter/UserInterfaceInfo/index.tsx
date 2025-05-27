import React, { useState, useEffect } from 'react';
import {Table, Space, message, Input, Button, Tag} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PageContainer } from '@ant-design/pro-layout';
import { useModel } from 'umi';
import { getUserInterfaceInfoByUserByPageUsingGet} from '@/services/api-backend/userInterfaceInfoController';
import { useNavigate } from 'react-router-dom';
import moment from 'moment';
import OrderModal from './components/OrderModal';

/**
 * 用户调用接口信息
 * @returns
 */
const UserInterfaceInfo: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const { initialState } = useModel('@@initialState'); // 获取全局状态
  const loginUser = initialState?.loginUser;
  const [list, setList] = useState<API.UserInterfaceInfoAndName[]>([]); // 数据源
  const [total, setTotal] = useState<number>(0); // 总数据量
  const [pagination, setPagination] = useState({ current: 1, pageSize: 5});
  const [search, setSearch] = useState<string>('');
  const navigate = useNavigate(); // React Router v6

  // 订单表单状态
  const [orderModalVisible, setOrderModalVisible] = useState<boolean>(false);
  const [selectedInterface, setSelectedInterface] = useState<API.UserInterfaceInfoAndName | null>(null);

  // 获取通知数据
  const loadData = async (current : number, pageSize : number) => {
    if (!loginUser) {
        message.error('用户未登录，无法获取通知!');
        return ;
    }
    setLoading(true); // 正在加载
    try {
      const res = await getUserInterfaceInfoByUserByPageUsingGet({
        current,
        pageSize,
        userId: Number(loginUser.id)
      });
      setList(res?.data?.records ?? []);
      setTotal(res?.data?.total ?? 0);
    } catch (error: unknown) {
      if (error instanceof Error) {
        message.error('请求失败！' + (error as any).message);
      } else {
        message.error('请求失败！');
      }
    }
    setLoading(false); // 加载完毕
  };

  // 刷新
  const handleRefresh = () => {
    loadData(pagination.current, pagination.pageSize);
    navigate(0);
  };

  // 分页参数变化时，重新加载数据
  const handleTableChange = (current : any, pageSize : any) => {
    setPagination({ current, pageSize }); // 更新分页状态
    loadData(current, pageSize); // 重新获取数据
  };

  // 搜索处理
  const handleSearch = () => {
    if (!search) {
      loadData(pagination.current, pagination.pageSize);
    } else {
      const filteredData = list?.filter((item: any) =>
        item.name.toLowerCase().includes(search.toLowerCase())
      );
      setList(filteredData);
    }
  };

  // 处理购买次数按钮点击
  const handleBuyClick = (record: API.UserInterfaceInfoAndName) => {
    setSelectedInterface(record);
    setOrderModalVisible(true);
  };

  const columns: ColumnsType<API.UserInterfaceInfoAndName> = [
    { title: 'ID', dataIndex: 'id', key: 'id', },
    { title: '接口名称', dataIndex: 'name', key: 'name', },
    { title: '剩余调用次数', dataIndex: 'leftNum', key: 'leftNum', },
    { title: '已调用次数', dataIndex: 'totalNum', key: 'totalNum', },
    {
      title: '接口调用状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: number) => {
        if (status === 0) {
          return <Tag color="green">可调用</Tag>;
        }
        if (status === 1) {
          return <Tag color='red'>禁用</Tag>;
        }
        // 如果是其他值，直接返回原始值
        return status;
      },
      filters: [
        { text: '正常', value: 0 },
        { text: '禁用', value: 1 },
      ],
      filterMultiple: true, // 开启多选
      onFilter: (value: any, record: API.UserInterfaceInfoAndName) =>
        record.status === value,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      render: (text:any) => moment(text).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '操作',
      align: 'center' as 'center',
      key: 'action',
      render: (_, record: API.UserInterfaceInfoAndName) => (
        <Space size="middle">
          <Button
            style={{ borderColor: 'blue' , color: 'blue'}}
            type="text"
            onClick={() => handleBuyClick(record)}>
            购买次数
          </Button>
        </Space>
      ),
    },
  ];

  // 初始加载数据
  useEffect(() => {
    if (!initialState) {
      message.error('用户信息加载失败');
      return;
    }
    loadData(pagination.current, pagination.pageSize);
  }, []);

  // 如果 initialState 还未加载完成，显示加载状态
  if (!initialState) {
    return <div>加载中...</div>;
  }

  return (
    <PageContainer style={{
      background: 'linear-gradient(rgba(246, 244, 244, 0.73), rgba(254, 250, 250, 0.69)), url("/gift/bonisi.gif")',
      backgroundSize: '10%',
      backgroundPosition: 'center 0px' ,
      backgroundRepeat: 'no-repeat',
      height: '90vh',
    }}>
      <Space style={{ marginBottom: 16 }}>
        <Input.Search
          placeholder="搜索接口名称"
          onSearch={handleSearch}
          onChange={(e) => setSearch(e.target.value)}
          style={{ width: 300 }}
        />
        <Button type="text" style={{ borderColor: 'blue' , color: 'blue'}} onClick={handleRefresh}>
          刷新
        </Button>
      </Space>
      <Table
        columns={columns}
        dataSource={list}
        tableLayout="auto"
        loading={loading}
        rowKey="id"
        pagination={{
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: total,
          showTotal: (total: number) => {
            return '总数:' + total;
          },
          onChange: handleTableChange, // 分页变化时更新数据
        }}
      />

      {/* 订单表单弹窗 */}
      {selectedInterface && (
        <OrderModal
          visible={orderModalVisible}
          onCancel={() => {
            setOrderModalVisible(false);
            setSelectedInterface(null);
          }}
          interfaceInfo={selectedInterface}
        />
      )}
    </PageContainer>
  );
};

export default UserInterfaceInfo;
