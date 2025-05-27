import React, { useState, useEffect } from 'react';
import {Table, Space, message, Input, Button, Tag, Select, Form, Row, Col} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { useModel } from 'umi';
import {
  addUserInterfaceInfoLeftNumUsingPut,
  listUserInterfaceInfoByPageUsingGet
} from '@/services/api-backend/userInterfaceInfoController';
import { useNavigate } from 'react-router-dom';
import moment from 'moment';

const { Option } = Select;

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
  const [searchForm] = Form.useForm();
  const navigate = useNavigate(); // React Router v6
  const [selectedCount, setSelectedCount] = useState(10); // 默认选择10次

  // 获取数据
  const loadData = async (current : number, pageSize : number, searchParams?: any) => {
    if (!loginUser) {
      message.error('用户未登录，无法获取通知!');
      return ;
    }
    setLoading(true); // 正在加载
    try {
      const params: API.ListUserInterfaceInfoByPageUsingGETParams = {
        current,
        pageSize,
        ...searchParams
      };

      // 移除空值
      Object.keys(params).forEach(key => {
        if (params[key] === undefined || params[key] === null || params[key] === '') {
          delete params[key];
        }
      });

      const res = await listUserInterfaceInfoByPageUsingGet(params);
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
    searchForm.resetFields();
    loadData(pagination.current, pagination.pageSize);
  };

  // 分页参数变化时，重新加载数据
  const handleTableChange = (current : any, pageSize : any) => {
    setPagination({ current, pageSize }); // 更新分页状态
    const values = searchForm.getFieldsValue();
    loadData(current, pageSize, values); // 重新获取数据
  };

  // 搜索处理
  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    // 重置分页到第一页
    setPagination({ ...pagination, current: 1 });
    loadData(1, pagination.pageSize, values);
  };

  const addLeftNumUsingPut = (record : any, selectedCount: number) => {
    // 准备参数
    const fields: API.AddUserInterfaceInfoLeftNumRequest = {
      id: record.id,
      num: selectedCount, // 选择的次数
    };
    addUserInterfaceInfoLeftNumUsingPut(fields).then((res) => {
      if (res.code === 0) {
        message.success('添加成功！');
        handleRefresh();
      } else {
        message.error('添加失败！');
      }
    }).catch((err) => {
      message.error('添加失败！'+err);
    });
  }

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', },
    { title: '用户ID', dataIndex: 'userId', key: 'userId', },
    { title: '接口ID', dataIndex: 'interfaceInfoId', key: 'interfaceInfoId', },
    { title: '接口名称', dataIndex: 'name', key: 'name', },
    { title: '剩余调用次数', dataIndex: 'leftNum', key: 'leftNum', },
    { title: '已调用次数', dataIndex: 'totalNum', key: 'totalNum', },
    {
      title: '接口调用状态',
      dataIndex: 'status',
      key: 'status',
      render: (text: number) => {
        if (text === 0) {
          return <Tag color="green">可调用</Tag>;
        }
        if (text === 1) {
          return <Tag color='red'>禁用</Tag>;
        }
        // 如果是其他值，直接返回原始值
        return text;
      },
    },
    { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', render: (text:any) => moment(text).format('YYYY-MM-DD HH:mm:ss'), },
    {
      title: '操作',
      align: 'center' as 'center',
      key: 'action',
      render: (text: any, record: any) => (
        <Space size="middle">
          <Select
            defaultValue={10} // 默认值
            style={{ width: 80 }}
            onChange={(value: number) => setSelectedCount(value)} // 更新状态
          >
            {[10, 20, 30, 40, 50].map((count) => (
              <Option key={count} value={count}>
                {count}
              </Option>
            ))}
          </Select>
          <Button
            style={{ borderColor: 'blue' , color: 'blue'}}
            type="text"
            onClick={
              () => addLeftNumUsingPut(record, selectedCount)
            }>
            增加次数
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
    <PageContainer>
      {/*<Form form={searchForm} layout="horizontal" style={{ marginBottom: 16 }}>*/}
      {/*  <Row gutter={16}>*/}
      {/*    <Col span={5}>*/}
      {/*      <Form.Item name="userId" label="用户ID">*/}
      {/*        <Input placeholder="请输入用户ID" />*/}
      {/*      </Form.Item>*/}
      {/*    </Col>*/}
      {/*    <Col span={5}>*/}
      {/*      <Form.Item name="interfaceInfoId" label="接口ID">*/}
      {/*        <Input placeholder="请输入接口ID" />*/}
      {/*      </Form.Item>*/}
      {/*    </Col>*/}
      {/*    <Col span={5}>*/}
      {/*      <Form.Item name="status" label="接口状态">*/}
      {/*        <Select placeholder="请选择调用状态">*/}
      {/*          <Option value="">全部</Option>*/}
      {/*          <Option value={0}>可调用</Option>*/}
      {/*          <Option value={1}>禁用</Option>*/}
      {/*        </Select>*/}
      {/*      </Form.Item>*/}
      {/*    </Col>*/}
      {/*    <Col span={4}>*/}
      {/*      <Form.Item>*/}
      {/*        <Space>*/}
      {/*          <Button type="primary" onClick={handleSearch}>*/}
      {/*            搜索*/}
      {/*          </Button>*/}
      {/*          <Button onClick={handleRefresh}>重置</Button>*/}
      {/*        </Space>*/}
      {/*      </Form.Item>*/}
      {/*    </Col>*/}
      {/*  </Row>*/}
      {/*</Form>*/}
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
    </PageContainer>
  );
};

export default UserInterfaceInfo;
