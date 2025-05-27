import { Footer, Question, AvatarDropdown, AvatarName } from '@/components';
import { LinkOutlined } from '@ant-design/icons';
import { SettingDrawer } from '@ant-design/pro-components';
import type { RunTimeLayoutConfig } from '@umijs/max';
import { history, Link } from '@umijs/max';
import { requestConfig } from './requestConfig';
import React from 'react';
import { getLoginUserUsingGet } from '@/services/api-backend/userController';
const isDev = process.env.NODE_ENV === 'development';
const loginPath = '/user/login';
/**
 * 白名单
 */
const WHITE_LIST = ['/user/register', loginPath];

/**
 * 全局文件
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 * */
// 首次打开页面加载信息
export async function getInitialState(): Promise<InitialState> {
  const state: InitialState = {
    loginUser: undefined,
    settings: {
      layout: 'top', // 确保初始状态设置为顶部布局
    },
  };
  const { location } = history;
  // 设置白名单(跳转到注册页面的时候)
  if (WHITE_LIST.includes(location.pathname)) {
    return state;
  }
  // 当页面首次加载时，获取到全局保存的信息，比如用户信息
  try {
    const res = await getLoginUserUsingGet();
    if (res.data) {
      state.loginUser = res.data;
    }

  } catch (error) {
    history.push(loginPath);
  }
  return state;
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({ initialState, setInitialState }) => {
  return {
    layout: 'top',
    actionsRender: () => [<Question key="doc" />],
    avatarProps: {
      src: initialState?.loginUser?.userAvatar,
      title: <AvatarName />,
      render: (_, avatarChildren) => {
        return <AvatarDropdown>{avatarChildren}</AvatarDropdown>;
      },
    },
    // 登录成功后，打上水印
    waterMarkProps: {
      content: initialState?.loginUser?.userName,
    },
    footerRender: () => <Footer />,

    // 每次跳转页面时触发
    onPageChange: () => {
      const { location } = history;
      // 设置白名单(跳转到注册页面的时候)
      if (WHITE_LIST.includes(location.pathname)) {
        return ;
      }
      // 如果没有登录，重定向到 login
      if (!initialState?.loginUser && location.pathname !== loginPath && location.pathname !== '/') {
        history.push(loginPath);
      }
    },

    bgLayoutImgList: [
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/D2LWSqNny4sAAAAAAAAAAAAAFl94AQBr',
        left: 85,
        bottom: 100,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/C2TWRpJpiC0AAAAAAAAAAAAAFl94AQBr',
        bottom: -68,
        right: -45,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/F6vSTbj8KpYAAAAAAAAAAAAAFl94AQBr',
        bottom: 0,
        left: 0,
        width: '331px',
      },
    ],
    // 配置菜单
    links: isDev
      ? [
          <Link key="openapi" to="/umi/plugin/openapi" target="_blank">
            <LinkOutlined />
            <span>OpenAPI 文档</span>
          </Link>,
        ]
      : [],
    menuHeaderRender: undefined,
    // 自定义 403 页面
    // unAccessible: <div>unAccessible</div>,
    // 增加一个 loading 的状态
    childrenRender: (children) => {
      // if (initialState?.loading) return <PageLoading />;
      return (
        <>
          <div className="fade-enter-active">
            {children}
          </div>
          {isDev && (
            <SettingDrawer
              disableUrlParams
              enableDarkTheme
              settings={initialState?.settings}
              onSettingChange={(settings) => {
                setInitialState((preInitialState) => ({
                  ...preInitialState,
                  settings,
                }));
              }}
            />
          )}
        </>
      );
    },
    ...initialState?.settings,
  };
};

/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request = requestConfig;
