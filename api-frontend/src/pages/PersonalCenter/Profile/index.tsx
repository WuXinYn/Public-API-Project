import React, { useEffect, useState } from 'react';
import { PageContainer } from '@ant-design/pro-components';
import {
  Card, Form, Input, Select, Button, message, Avatar,
  Row, Col, Spin, Tabs, Divider, Typography, Space, Alert
} from 'antd';
import {
  UserOutlined, EditOutlined, SaveOutlined,
  InfoCircleOutlined, EyeOutlined, EyeInvisibleOutlined,
  CopyOutlined, SafetyCertificateOutlined
} from '@ant-design/icons';
import { useModel } from '@umijs/max';
import { getLoginUserUsingGet, updateUserUsingPost } from '@/services/api-backend/userController';
import './index.less';

const { Text } = Typography;

// 性别选项
const GENDER_OPTIONS = [
  { label: '男', value: 0 },
  { label: '女', value: 1 },
  { label: '未知', value: 2 },
];

const Profile: React.FC = () => {
  const [form] = Form.useForm();
  const { initialState, setInitialState } = useModel('@@initialState');
  const [loading, setLoading] = useState<boolean>(true);
  const [editing, setEditing] = useState<boolean>(false);
  const [userInfo, setUserInfo] = useState<API.UserVO | undefined>(undefined);
  const [securityInfo, setSecurityInfo] = useState<{
    accessKey: string | null;
    secretKey: string | null;
  }>({
    accessKey: null,
    secretKey: null,
  });
  const [showKeys, setShowKeys] = useState<boolean>(false);

  // 获取用户信息
  const fetchUserInfo = async () => {
    setLoading(true);
    try {
      const res = await getLoginUserUsingGet();
      if (res.data) {
        setUserInfo(res.data);
        form.setFieldsValue({
          userName: res.data.userName,
          userAccount: res.data.userAccount,
          gender: res.data.gender,
          userAvatar: res.data.userAvatar,
          userRole: res.data.userRole,
        });

        // 模拟从另一个API获取安全信息 todo
        // 注意：实际实现中应该调用真实API获取accessKey和secretKey
        setSecurityInfo({
          accessKey: 'ak_' + res.data.userAccount + '_' + (Math.floor(Math.random() * 1000000)).toString().padStart(6, '0'),
          secretKey: 'sk_' + (Math.floor(Math.random() * 1000000000)).toString(16),
        });
      }
    } catch (error) {
      message.error('获取用户信息失败');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  // 更新用户信息
  const handleUpdateUser = async (values: any) => {
    if (!userInfo?.id) {
      message.error('用户ID不存在');
      return;
    }

    setLoading(true);
    try {
      const res = await updateUserUsingPost({
        id: userInfo.id,
        ...values,
      });

      if (res.data) {
        message.success('更新成功');
        fetchUserInfo();
        setEditing(false);

        // 更新全局用户状态
        if (initialState?.loginUser) {
          setInitialState({
            ...initialState,
            loginUser: {
              ...initialState.loginUser,
              ...values,
            },
          });
        }
      } else {
        message.error('更新失败');
      }
    } catch (error) {
      message.error('更新出错');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  // 初始化加载用户信息
  useEffect(() => {
    fetchUserInfo();
  }, []);

  // 渲染用户角色名称
  const renderUserRole = (role?: string) => {
    switch (role) {
      case 'admin':
        return '管理员';
      case 'user':
        return '普通用户';
      default:
        return '未知角色';
    }
  };

  // 格式化时间显示
  const formatDateTime = (dateTimeStr?: string) => {
    if (!dateTimeStr) return '-';
    const date = new Date(dateTimeStr);
    return date.toLocaleString();
  };

  // 复制文本到剪贴板
  const copyToClipboard = (text: string, itemName: string) => {
    navigator.clipboard.writeText(text).then(
      () => {
        message.success(`${itemName}已复制到剪贴板`);
      },
      () => {
        message.error('复制失败，请手动复制');
      }
    );
  };

  // 渲染账户信息卡片
  const renderAccountInfo = () => (
    <Card
      title="账户信息"
      bordered={false}
      className="profile-card"
      extra={
        !editing ? (
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => setEditing(true)}
          >
            编辑
          </Button>
        ) : (
          <Button
            type="link"
            icon={<SaveOutlined />}
            onClick={() => form.submit()}
          >
            保存
          </Button>
        )
      }
    >
      <Spin spinning={loading}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleUpdateUser}
          disabled={!editing}
        >
          <Row gutter={16}>
            <Col xs={24} sm={24} md={8} lg={8}>
              <div className="avatar-container">
                <Avatar
                  className="avatar"
                  size={100}
                  src={userInfo?.userAvatar}
                  icon={<UserOutlined />}
                />
                {editing && (
                  <Form.Item name="userAvatar" style={{ marginBottom: 0 }}>
                    <Input placeholder="输入头像链接" />
                  </Form.Item>
                )}
              </div>
            </Col>

            <Col xs={24} sm={24} md={16} lg={16}>
              <Form.Item
                label="用户名"
                name="userName"
                rules={[{ required: true, message: '请输入用户名' }]}
              >
                <Input prefix={<UserOutlined />} placeholder="用户名" />
              </Form.Item>

              <Form.Item
                label="账号"
                name="userAccount"
                tooltip={{ title: '账号不可修改', icon: <InfoCircleOutlined /> }}
              >
                <Input disabled prefix={<UserOutlined />} placeholder="账号" />
              </Form.Item>

              <Form.Item
                label="性别"
                name="gender"
              >
                <Select options={GENDER_OPTIONS} />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Spin>
    </Card>
  );

  // 渲染详细信息卡片
  const renderDetailInfo = () => (
    <Card title="详细信息" bordered={false} className="profile-card">
      <Spin spinning={loading}>
        <Row gutter={[16, 16]}>
          <Col span={12}>
            <div className="detail-item">
              <span className="detail-label">用户角色:</span>
              <span className="detail-value">{renderUserRole(userInfo?.userRole)}</span>
            </div>
          </Col>
          <Col span={12}>
            <div className="detail-item">
              <span className="detail-label">账号ID:</span>
              <span className="detail-value">{userInfo?.id || '-'}</span>
            </div>
          </Col>
          <Col span={12}>
            <div className="detail-item">
              <span className="detail-label">创建时间:</span>
              <span className="detail-value">{formatDateTime(userInfo?.createTime)}</span>
            </div>
          </Col>
          <Col span={12}>
            <div className="detail-item">
              <span className="detail-label">上次更新:</span>
              <span className="detail-value">{formatDateTime(userInfo?.updateTime)}</span>
            </div>
          </Col>
        </Row>
      </Spin>
    </Card>
  );

  // 渲染安全信息卡片
  const renderSecurityInfo = () => (
    <Card
      title={
        <span>
          <SafetyCertificateOutlined style={{ marginRight: 8 }} />
          API 密钥
        </span>
      }
      bordered={false}
      className="profile-card security-card"
    >
      <Spin spinning={loading}>
        <Alert
          message="安全提示"
          description="您的 API 密钥代表您的身份和权限，请妥善保管，不要泄露给他人。如果您怀疑密钥已泄露，请联系管理员重置密钥。"
          type="warning"
          showIcon
          style={{ marginBottom: 24 }}
        />

        <div className="key-section">
          <div className="key-item">
            <div className="key-header">
              <Text strong>Access Key</Text>
              <Space>
                <Button
                  type="text"
                  icon={showKeys ? <EyeInvisibleOutlined /> : <EyeOutlined />}
                  onClick={() => setShowKeys(!showKeys)}
                >
                  {showKeys ? '隐藏' : '显示'}
                </Button>
                {securityInfo.accessKey && (
                  <Button
                    type="text"
                    icon={<CopyOutlined />}
                    onClick={() => copyToClipboard(securityInfo.accessKey || '', 'Access Key')}
                  >
                    复制
                  </Button>
                )}
              </Space>
            </div>
            <div className="key-content">
              {securityInfo.accessKey ? (
                <Input.Password
                  value={securityInfo.accessKey}
                  visibilityToggle={false}
                  readOnly
                  bordered={false}
                  className="key-input"
                  type={showKeys ? "text" : "password"}
                />
              ) : (
                <Text type="secondary">无法获取 Access Key 信息</Text>
              )}
            </div>
          </div>

          <Divider style={{ margin: '16px 0' }} />

          <div className="key-item">
            <div className="key-header">
              <Text strong>Secret Key</Text>
              <Space>
                <Button
                  type="text"
                  icon={showKeys ? <EyeInvisibleOutlined /> : <EyeOutlined />}
                  onClick={() => setShowKeys(!showKeys)}
                >
                  {showKeys ? '隐藏' : '显示'}
                </Button>
                {securityInfo.secretKey && (
                  <Button
                    type="text"
                    icon={<CopyOutlined />}
                    onClick={() => copyToClipboard(securityInfo.secretKey || '', 'Secret Key')}
                  >
                    复制
                  </Button>
                )}
              </Space>
            </div>
            <div className="key-content">
              {securityInfo.secretKey ? (
                <Input.Password
                  value={securityInfo.secretKey}
                  visibilityToggle={false}
                  readOnly
                  bordered={false}
                  className="key-input"
                  type={showKeys ? "text" : "password"}
                />
              ) : (
                <Text type="secondary">无法获取 Secret Key 信息</Text>
              )}
            </div>
          </div>
        </div>
      </Spin>
    </Card>
  );

  return (
    <PageContainer style={{
      background: 'url("/bengtie/xiadie02.png")',
      // backgroundSize: '10%',
      // backgroundPosition: 'center -50px' ,
      backgroundRepeat: 'no-repeat',
      height: '90vh',
    }}>

      <div className="profile-container">
        <Tabs
          defaultActiveKey="basic"
          items={[
            {
              key: 'basic',
              label: '基本资料',
              children: (
                <>
                  {renderAccountInfo()}
                  <Divider style={{ margin: '24px 0' }} />
                  {renderDetailInfo()}
                </>
              )
            },
            {
              key: 'security',
              label: '安全设置',
              children: (
                <>
                  {renderSecurityInfo()}
                </>
              )
            }
          ]}
        />
      </div>
    </PageContainer>
  );
};

export default Profile;
