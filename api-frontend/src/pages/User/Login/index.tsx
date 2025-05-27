import { Footer } from '@/components';
import {
  LockOutlined,
  UserOutlined,
  MobileOutlined,
} from '@ant-design/icons';
import {
  LoginForm,
  ProFormCheckbox,
  ProFormText,
  ProFormCaptcha,
} from '@ant-design/pro-components';
import { history, useModel, Helmet } from '@umijs/max';
import { Alert, message, Tabs, Button } from 'antd';
import Settings from '../../../../config/defaultSettings';
import React, { useState } from 'react';
import { createStyles } from 'antd-style';
import { userLoginUsingPost, smsUserLoginUsingPost } from '@/services/api-backend/userController';
import { phoneUsingGet } from '@/services/api-backend/smsController';

// import axios from 'axios';
// const { TabPane } = Tabs;
// const MultiUserTabs = ({ users }) => (
  //   <Tabs>
  //     {users.map((user) => (
  //       <TabPane tab={user.name} key={user.id}>
  //         <div>{`User ${user.name}'s workspace`}</div>
  //       </TabPane>
  //     ))}
  //   </Tabs>
  // );

  // const initialState = {
  //   users: {},
  // };

  // const userReducer = (state = initialState, action) => {
  //   switch (action.type) {
  //     case 'ADD_USER':
  //       return {
  //         ...state,
  //         users: {
  //           ...state.users,
  //           [action.payload.userId]: action.payload.userData,
  //         },
  //       };
  //     default:
  //       return state;
  //   }
  // };

  // axios.interceptors.request.use((config) => {
  //   const currentUserId = getCurrentUserId(); // 根据当前活动用户获取 ID
  //   const token = getUserToken(currentUserId);
  //   if (token) {
  //     config.headers.Authorization = `Bearer ${token}`;
  //   }
  //   return config;
  // });

const useStyles = createStyles(({ token }) => {
  return {
    action: {
      marginLeft: '8px',
      color: 'rgba(0, 0, 0, 0.2)',
      fontSize: '24px',
      verticalAlign: 'middle',
      cursor: 'pointer',
      transition: 'color 0.3s',
      '&:hover': {
        color: token.colorPrimaryActive,
      },
    },
    lang: {
      width: 42,
      height: 42,
      lineHeight: '42px',
      position: 'fixed',
      right: 16,
      borderRadius: token.borderRadius,
      ':hover': {
        backgroundColor: token.colorBgTextHover,
      },
    },
    container: {
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      overflow: 'auto',
      background: 'linear-gradient(135deg, rgba(66, 133, 244, 0.6), rgba(219, 68, 55, 0.4)), url(/my_source/ailixiya04.png)',
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundRepeat: 'no-repeat',
      boxShadow: 'inset 0 0 100px rgba(0, 0, 0, 0.15)',
      backdropFilter: 'blur(10px)',
    },
    loginForm: {
      backgroundColor: 'rgba(255, 255, 255, 0.7)',
      borderRadius: '16px',
      boxShadow: '0 10px 30px rgba(0, 0, 0, 0.15)',
      padding: '24px',
      backdropFilter: 'blur(20px)',
      transition: 'all 0.3s ease',
      border: '1px solid rgba(255, 255, 255, 0.8)',
    },
    logoContainer: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'flex-start',
      gap: '15px',
      marginBottom: '20px',
    },
    logoImage: {
      height: '60px',
      width: 'auto',
      filter: 'drop-shadow(0 2px 5px rgba(0, 0, 0, 0.2))'
    },
    formTitle: {
      fontSize: '28px',
      fontWeight: 800,
      color: '#1677ff',
      margin: 0,
      textShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
      display: 'inline-block',
    },
    formSubtitle: {
      textAlign: 'center' as const,
      fontSize: '16px',
      fontWeight: 600,
      fontStyle: 'italic',
      marginTop: '10px',
      marginBottom: '32px',
      color: 'rgba(0, 0, 0, 0.8)',
      textShadow: '1px 1px 3px rgba(0, 0, 0, 0.15)',
    },
    registerLink: {
      fontWeight: 500,
      color: '#1677ff',
      transition: 'all 0.3s',
      '&:hover': {
        color: '#4096ff',
      }
    },
    forgotLink: {
      float: 'right',
      fontWeight: 500,
      color: '#ff4d4f',
      transition: 'all 0.3s',
      '&:hover': {
        color: '#ff7875',
      }
    },
    actionButtons: {
      display: 'flex',
      justifyContent: 'space-between',
      marginTop: '24px',
      gap: '12px',
    },
    loginButton: {
      flex: 1,
      height: '44px',
      fontSize: '16px',
      borderRadius: '8px',
      fontWeight: 500,
      boxShadow: '0 2px 8px rgba(22, 119, 255, 0.3)',
    },
    registerButton: {
      flex: 1,
      height: '44px',
      fontSize: '16px',
      borderRadius: '8px',
      fontWeight: 500,
      backgroundColor: 'rgba(0, 0, 0, 0.03)',
      color: '#666',
      border: '1px solid #d9d9d9',
      boxShadow: '0 2px 0 rgba(0, 0, 0, 0.02)',
      '&:hover': {
        color: '#1677ff',
        borderColor: '#1677ff',
        backgroundColor: 'rgba(22, 119, 255, 0.1)',
      }
    }
  };
});

const LoginMessage: React.FC<{ content: string;}> = ({ content }) => {
  return (
    <Alert
      style={{
        marginBottom: 24,
      }}
      message={content}
      type="error"
      showIcon
    />
  );
};

const Login: React.FC = () => {
  const [userLoginState] = useState<API.LoginResult>({});
  const [type, setType] = useState<string>('account');
  const { setInitialState } = useModel('@@initialState');
  const { styles } = useStyles();

  // 账号密码登录
  const handleAccountSubmit = async (values: API.UserLoginRequest) => {
    try {
      // 登录
      const res = await userLoginUsingPost({
        ...values,
      });
      if (res.data) {
        const urlParams = new URL(window.location.href).searchParams;
        history.push(urlParams.get('redirect') || '/');
        setInitialState({
          loginUser: res.data,
        });
        return;
      }
      else{
        message.error(res.message);
      }
    } catch (error: any) {
      console.log(error);
      message.error(error.message);
    }
  };

  // 短信登录
  const handleSmsSubmit = async (values: API.SmsUserLoginRequest) => {
    try {
      // 短信登录
      const res = await smsUserLoginUsingPost({
        ...values,
      });
      if (res.data) {
        const urlParams = new URL(window.location.href).searchParams;
        history.push(urlParams.get('redirect') || '/');
        setInitialState({
          loginUser: res.data,
        });
        return;
      }
    } catch (error: any) {
      const defaultLoginFailureMessage = '短信登录失败，请重试！';
      console.log(error);
      message.error(defaultLoginFailureMessage);
    }
  };

  // 统一处理表单提交
  const handleSubmit = async (values: API.UserLoginRequest | API.SmsUserLoginRequest) => {
    if (type === 'account') {
      await handleAccountSubmit(values as API.UserLoginRequest);
    } else {
      await handleSmsSubmit(values as API.SmsUserLoginRequest);
    }
  };

  const { status, type: loginType } = userLoginState;
  return (
    <div className={styles.container}>
      <Helmet>
        <title>
          {'登录'}- {Settings.title}
        </title>
      </Helmet>

      <div style={{flex: '1', padding: '48px 0', display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
        <LoginForm
          contentStyle={{
            minWidth: 350,
            maxWidth: '85vw',
          }}
          className={styles.loginForm}
          logo={false}
          title={false}
          subTitle={false}
          initialValues={{
            autoLogin: true,
          }}
          submitter={{
            render: (props) => {
              return (
                <div className={styles.actionButtons}>
                  <Button
                    type="primary"
                    size="large"
                    className={styles.loginButton}
                    onClick={() => props.form?.submit()}
                  >
                    登录
                  </Button>
                  <Button
                    size="large"
                    className={styles.registerButton}
                    onClick={() => {
                      window.location.href="/user/register"
                    }}
                  >
                    注册
                  </Button>
                </div>
              );
            }
          }}
          // 登录表单提交
          onFinish={async (values) => {
            await handleSubmit(values as API.UserLoginRequest | API.SmsUserLoginRequest);
          }}
        >
          <div className={styles.logoContainer}>
            <img alt="logo" src="/my_source/ailixiya.gif" className={styles.logoImage}/>
            <h1 className={styles.formTitle}>API开放平台</h1>
          </div>
          <div className={styles.formSubtitle}>API开放平台是我的项目实践之一，欢迎使用！</div>

          <Tabs
            activeKey={type}
            onChange={setType}
            centered
            items={[
              {
                key: 'account',
                label: '账号密码登录',
              },
              {
                key: 'mobile',
                label: '手机号登录',
              },
            ]}
            style={{marginBottom: '20px'}}
          />

          {status === 'error' && loginType === 'account' && (
            <LoginMessage content={'错误的账号和密码'} />
          )}

          {/* 账号密码登录 */}
          {type === 'account' && (
            <>
              <ProFormText
                name="userAccount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined />,
                }}
                placeholder="账号"
                rules={[
                  {
                    required: true,
                    message: '请输入账号!',
                  },
                ]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                placeholder="密码"
                rules={[
                  {
                    required: true,
                    message: '请输入密码！',
                  },
                ]}
              />
              <div style={{ marginBottom: 24 }}>
                <ProFormCheckbox noStyle name="autoLogin">
                  自动登录
                </ProFormCheckbox>
                <a
                  style={{
                    float: 'right',
                  }}
                  className={styles.forgotLink}
                  onClick={()=>{
                    alert("管理员VX: wxid_3ks9ypesnvvl22")
                  }}
                >
                  忘记密码
                </a>
              </div>
            </>
          )}

          {/* 手机号验证码登录 */}
          {type === 'mobile' && (
            <>
              <ProFormText
                fieldProps={{
                  size: 'large',
                  prefix: <MobileOutlined />,
                }}
                name="userAccount"
                placeholder="手机号"
                rules={[
                  {
                    required: true,
                    message: '请输入手机号！',
                  },
                  {
                    pattern: /^1\d{10}$/,
                    message: '手机号格式错误！',
                  },
                ]}
              />
              <ProFormCaptcha
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                captchaProps={{
                  size: 'large',
                }}
                placeholder="验证码"
                captchaTextRender={(timing, count) => {
                  if (timing) {
                    return `${count} 秒后重新获取`;
                  }
                  return '获取验证码';
                }}
                name="code"
                rules={[
                  {
                    required: true,
                    message: '请输入验证码！',
                  },
                ]}
                onGetCaptcha={async (phone) => {
                  try {
                    // 获取验证码
                    const result = await phoneUsingGet({
                      userAccount: phone,
                    });

                    if (result.code === 0) {
                      message.success('验证码发送成功！');
                      return;
                    } else {
                      message.error(result.message || '获取验证码失败');
                      return Promise.reject(new Error(result.message));
                    }
                  } catch (error) {
                    message.error('获取验证码失败');
                    return Promise.reject(new Error('获取验证码失败'));
                  }
                }}
              />
              <div style={{ marginBottom: 24 }}>
                <ProFormCheckbox noStyle name="autoLogin">
                  自动登录
                </ProFormCheckbox>
              </div>
            </>
          )}
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};

export default Login;
