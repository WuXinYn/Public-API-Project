import React, { useState, useEffect } from 'react';
import { Table, Tag, Space, Input, Button, message, Popconfirm , Badge, Modal, Image, Card, Typography } from 'antd';
const { Title, Paragraph } = Typography;
import { PageContainer } from '@ant-design/pro-layout';
import { NotificationOutlined } from '@ant-design/icons';
import { useModel } from 'umi';
import moment from 'moment';
import {
  deleteNotificationUsingPost, listNotificationInfoByPageUsingGet, updateNotificationStatusUsingPost
} from '@/services/api-backend/notificationsInfoController';
import { useNavigate } from 'react-router-dom';

interface DeleteRequest {
  id: number;
  [key: string]: any;
}

/**
 * 通知
 * @returns
 */
const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState<string>('');
  const { initialState } = useModel('@@initialState'); // 获取全局状态
  const loginUser = initialState?.loginUser;
  const [list, setList] = useState<API.NotificationInfo[]>([]); // 数据源
  const [total, setTotal] = useState<number>(0); // 总数据量
  const [pagination, setPagination] = useState({ current: 1, pageSize: 5});
  const [unreadCount, setUnreadCount] = useState(0); // 初始未读通知数
  const [isModalVisible, setIsModalVisible] = useState(false); // 控制 Modal 显示
  const [notificationContent, setNotificationContent] = useState<API.NotificationInfo>(); // 存储通知的具体内容
  const navigate = useNavigate(); // React Router v6

  // 获取通知数据
  const loadData = async (current : number, pageSize : number) => {
    if (!loginUser) {
        message.error('用户未登录，无法获取通知!');
        return ;
    }
    setLoading(true); // 正在加载
    try {
      const res = await listNotificationInfoByPageUsingGet({
        current,
        pageSize,
        user_id: Number(loginUser.id)
      });
      setList(res?.data?.records ?? []);
      setTotal(res?.data?.total ?? 0);
      setUnreadCount(res.data?.records?.filter(item => item.status === 'unread').length ?? 0);  //获取未读通知数量
    } catch (error: unknown) {
      if (error instanceof Error) {
        message.error('请求失败！' + (error as any).message);
      } else {
        message.error('请求失败！');
      }
    }
    setLoading(false); // 加载完毕
  };

  // 分页参数变化时，重新加载数据
  const handleTableChange = (current : any, pageSize : any) => {
    setPagination({ current, pageSize }); // 更新分页状态
    loadData(current, pageSize); // 重新获取数据
  };

  // 点击查看详情
  const showNotificationDetails = (fields: API.NotificationInfo) => {
    setNotificationContent(fields); // 设置通知的详细内容
    setIsModalVisible(true); // 显示 Modal
  };

  // 刷新
  const handleRefresh = () => {
    loadData(pagination.current, pagination.pageSize);
    navigate(0);
  };

  // 关闭 Modal
  const handleCancel = async (notificationContent : any) => {
    setIsModalVisible(false); // 隐藏 Modal
    if (notificationContent.status === 'unread') {
      try {
        await updateNotificationStatusUsingPost({
          id: notificationContent.id,
        }); // 更新通知状态为已读
        // 更新本地数据
        const updatedList = list.map(item => {
          if (item.id === notificationContent.id) {
            return { ...item, status: 'read' };
          }
          return item;
        });
        setList(updatedList);
        // 更新未读消息数量
        setUnreadCount(prev => Math.max(0, prev - 1));
      } catch (error) {
        message.error('更新通知状态失败');
      }
    }
  };

  // 搜索处理
  const handleSearch = () => {
    if (!search) {
      loadData(pagination.current, pagination.pageSize);
    } else {
      const filteredData = list?.filter((item: any) =>
        item.title.toLowerCase().includes(search.toLowerCase())
      );
      setList(filteredData);
    }
  };

  // 删除通知
  const handleDelete = async (item: DeleteRequest) => {
    const hide = message.loading('正在删除');
    if (!item) return true;
    try {
      const res = await deleteNotificationUsingPost({
        id: item.id,
      });
      hide();
      if(res.data === false) {
        message.success('删除失败！');
        return false;
      }
      message.success('删除成功！');
      loadData(pagination.current, pagination.pageSize);
      return true;
    } catch (error: any) {
      hide();
      message.error('删除失败！' + error.message);
      return false;
    }
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (text: string) => {
        if (text === 'system') {
          return <Tag color="green">系统通知</Tag>;
        }
        if (text === 'user') {
          return <Tag color='#FFB6C1'>用户通知</Tag>;
        }
        if (text === 'warning') {
          return <Tag color='yellow'>警告</Tag>;
        }
        if (text === 'error') {
          return <Tag color='red-inverse'>重大错误</Tag>;
        }
        // 如果是其他值，直接返回原始值
        return text;
      },
      filters: [
        { text: '系统通知', value: 'system' },
        { text: '用户通知', value: 'user' },
        { text: '警告', value: 'warning' },
      ],
      filterMultiple: true, // 开启多选
      onFilter: (value : any, record : any) => record.type.includes(value),
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      // 使用 render 进行字符串值的映射
      render: (text: string) => {
        // 根据 'status' 值转换为对应的文本
        if (text === 'unread') {
          return <Tag color="red">未读</Tag>;
        }
        if (text === 'read') {
          return <Tag color="green">已读</Tag>;
        }
        if (text === 'treatment') {
          return <Tag color="blue">处理中</Tag>;
        }
        if (text === 'timeout') {
          return <Tag color="black">已失效</Tag>;
        }
        if (text === 'resolved') {
          return <Tag color="green">已解决</Tag>;
        }
        if (text === 'error') {
          return <Tag color="red-inverse">重大错误</Tag>;
        }
        if (text === 'warning') {
          return <Tag color="yellow">警告，处理时长已经超过24h</Tag>;
        }
        return text;  // 如果是其他值，直接返回原始值
      },
      filters: [
        { text: '未读', value: 'unread' },
        { text: '已读', value: 'read' },
        { text: '处理中', value: 'treatment' },
        { text: '已失效', value: 'timeout' },
      ],
      filterMultiple: true, // 开启多选
      onFilter: (value : any, record : any) => record.status.includes(value),
    },
    {
      title: '通知时间',
      dataIndex: 'created_at',
      key: 'created_at',
      render: (text:any) => moment(text).format('YYYY-MM-DD HH:mm:ss'), // 格式化时间
      sorter: (a : any, b : any) => a.created_at.localeCompare(b.created_at), // 对时间进行排序
    },
    {
      title: '操作',
      align: 'center' as 'center',
      key: 'action',
      render: (text: any, record: any) => (
        <Space size="middle">
          <Button
            style={{ borderColor: 'blue' , color: 'blue'}}
            type="text"
            onClick={
              () => showNotificationDetails(record)
            }>
            查看详情
          </Button>

          {/* Modal 显示通知的具体内容和图片 */}
          <Modal
            title={notificationContent ? notificationContent.title : '通知详情'}
            open={isModalVisible}
            onCancel={() => handleCancel(notificationContent)}
            footer={null} // 关闭 Modal 时不显示 footer 按钮
            width={600}
          >
            <Card>
              <Title level={4}>正文:</Title>
              <Paragraph style={{whiteSpace: 'pre-wrap', marginBottom: 16 ,fontStyle: 'italic', fontWeight: 'bold'}}>
                {notificationContent?.content}
              </Paragraph>
              <Image width="100%" src="/my_source/ailixiya.gif" alt="通知图片" />
            </Card>
          </Modal>
          <Popconfirm
            title="确认删除这条通知吗？"
            onConfirm={() => handleDelete(record)}
          >
            <Button
              type="text"
              style={{ borderColor: 'red' , color: 'red'}}
              >
              删除
            </Button>
          </Popconfirm>
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
    // 假设在组件加载时，拉取未读通知数
    loadData(pagination.current, pagination.pageSize);
  }, []);

  // 如果 initialState 还未加载完成，显示加载状态
  if (!initialState) {
    return <div>加载中...</div>;
  }

  return (
    <PageContainer style={{
      background: 'linear-gradient(rgba(246, 244, 244, 0.73), rgba(254, 250, 250, 0.69)), url("/gift/xiaohuli.gif")',
      // backgroundSize: 'cover',
      backgroundPosition: 'center -50px' ,
      backgroundRepeat: 'no-repeat',
      height: '90vh',
     }}>
      <Space style={{ marginBottom: 16 }}>
        <Input.Search
          placeholder="搜索标题"
          onSearch={handleSearch}
          onChange={(e) => setSearch(e.target.value)}
          style={{ width: 300 }}
        />
        <Button type="text" style={{ borderColor: 'blue' , color: 'blue'}} onClick={handleRefresh}>
          刷新
        </Button>
      </Space>
      <div style={{ marginBottom: 16 ,fontStyle: 'italic', fontWeight: 'bold' }}>
        <Badge count={unreadCount} overflowCount={99} style={{ backgroundColor: '#FFB6C1' }}>
          {unreadCount === 0 ? '没有新通知哦~♪' : `哇~♪，快看，你有新的通知了！(≧▽≦)，在页数${pagination.current}哦，别找错啦~~~♪`}
        <NotificationOutlined style={{ fontSize: '24px', color: '#fff' }} />
        </Badge>
      </div>
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

export default Index;
