import {Footer} from '@/components';
import {LockOutlined, UserOutlined, MobileOutlined} from '@ant-design/icons';
import {LoginForm,} from '@ant-design/pro-components';
import {Helmet, history} from '@umijs/max';
import {Alert, message, Tabs, Button} from 'antd';
import Settings from '../../../../config/defaultSettings';
import React, {useState} from 'react';
import {createStyles} from 'antd-style';
import {userRegisterUsingPost, smsUserRegisterUsingPost} from '@/services/api-backend/userController';
import {phoneUsingGet} from '@/services/api-backend/smsController';
import {ProFormText, ProFormCaptcha} from "@ant-design/pro-form/lib";

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
      background: 'linear-gradient(135deg, rgba(66, 133, 244, 0.6), rgba(219, 68, 55, 0.4)), url(/my_source/huahuo.jpeg)',
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundRepeat: 'no-repeat',
      boxShadow: 'inset 0 0 100px rgba(0, 0, 0, 0.15)',
      backdropFilter: 'blur(10px)',
    },
    registerForm: {
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
    actionButtons: {
      display: 'flex',
      justifyContent: 'space-between',
      marginTop: '24px',
      gap: '12px',
    },
    registerButton: {
      flex: 1,
      height: '44px',
      fontSize: '16px',
      borderRadius: '8px',
      fontWeight: 500,
      boxShadow: '0 2px 8px rgba(22, 119, 255, 0.3)',
    },
    loginButton: {
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

const RegisterMessage: React.FC<{
  content: string;
}> = ({ content }) => {
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
const Register: React.FC = () => {
  const [userLoginState] = useState<API.LoginResult>({});
  const [type, setType] = useState<string>('account');
  const { styles } = useStyles();

  // 账号密码注册提交
  const handleAccountSubmit = async (values: API.UserRegisterRequest) => {
    // 校验
    const {userPassword,checkPassword} = values;

    if (userPassword !== checkPassword) { // !==判断值与类型
      message.error("两次输入的密码不一致！");
      return ;
    }
    try {
      // 注册
      const res = await userRegisterUsingPost({
        ...values,
      });
      if (res.data && res.data > 0) {
        const defaultLoginFailureMessage = '注册成功！';
        message.success(defaultLoginFailureMessage);

        // 跳转到登录页面
        history.push('/user/login');
        return;
      }
    } catch (error: any) {
      console.log(error);
      message.error(error.toString());
    }
  };

  // 短信验证码注册提交
  const handleSmsSubmit = async (values: API.SmsUserLoginRequest) => {
    try {
      // 短信注册
      const res = await smsUserRegisterUsingPost({
        ...values,
      });
      if (res.data && res.data > 0) {
        message.success('注册成功！');
        // 跳转到登录页面
        history.push('/user/login');
        return;
      }
    } catch (error: any) {
      console.log(error);
      message.error(error.toString());
    }
  };

  // 统一处理表单提交
  const handleSubmit = async (values: any) => {
    if (type === 'account') {
      await handleAccountSubmit(values as API.UserRegisterRequest);
    } else {
      await handleSmsSubmit(values as API.SmsUserLoginRequest);
    }
  };

  const { status, type: loginType } = userLoginState;

  return (
    <div className={styles.container}>
      <Helmet>
        <title>
          {'注册'}- {Settings.title}
        </title>
      </Helmet>

      <div style={{flex: '1', padding: '48px 0', display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
        <LoginForm
          className={styles.registerForm}
          submitter={{
            render: (props) => {
              return (
                <div className={styles.actionButtons}>
                  <Button
                    type="primary"
                    size="large"
                    className={styles.registerButton}
                    onClick={() => props.form?.submit()}
                  >
                    注册
                  </Button>
                  <Button
                    size="large"
                    className={styles.loginButton}
                    onClick={() => {
                      window.location.href="/user/login"
                    }}
                  >
                    返回登录
                  </Button>
                </div>
              );
            }
          }}
          contentStyle={{
            minWidth: 350,
            maxWidth: '85vw',
          }}
          logo={false}
          title={false}
          subTitle={false}
          initialValues={{
            autoLogin: true,
          }}
          // 注册提交
          onFinish={async (values) => {
            await handleSubmit(values);
          }}
        >
          <div className={styles.logoContainer}>
            <img alt="logo" src="/my_source/huahuologo.png" className={styles.logoImage}/>
            <h1 className={styles.formTitle}>API开放平台</h1>
          </div>
          <div className={styles.formSubtitle}>API开放平台是我的项目实践之一，欢迎注册！</div>

          <Tabs
            activeKey={type}
            onChange={setType}
            centered
            items={[
              {
                key: 'account',
                label: '账号密码注册',
              },
              {
                key: 'mobile',
                label: '手机号注册',
              },
            ]}
            style={{marginBottom: '20px'}}
          />

          {status === 'error' && loginType === 'account' && (
            <RegisterMessage content={'错误的账号和密码'} />
          )}

          {/* 账号密码注册 */}
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
                  {
                    min: 4,
                    message: '账号长度不低于4位',
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
                  {
                    min: 8,
                    message: '密码长度不低于8位',
                  },
                ]}
              />
              <ProFormText.Password
                name="checkPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                placeholder="确认密码"
                rules={[
                  {
                    required: true,
                    message: '请再次输入密码！',
                  },
                  {
                    min: 8,
                    message: '密码长度不低于8位',
                  },
                ]}
              />
            </>
          )}

          {/* 手机号注册 */}
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
                phoneName="userAccount"
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
                onGetCaptcha={async (userAccount) => {
                  try {
                    console.log("userAccount:"+userAccount);
                    // 获取验证码
                    const result = await phoneUsingGet({
                      userAccount,
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
            </>
          )}
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};

export default Register;
