import { GithubOutlined, ApiOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {
  const currentYear = new Date().getFullYear();
  
  return (
    <DefaultFooter
      style={{
        background: 'none',
        padding: '24px 0',
        borderTop: '1px solid #f0f0f0',
      }}
      copyright={`©${currentYear} API开放平台`}
      links={[
        {
          key: 'Ant Design Pro',
          title: 'Ant Design Pro',
          href: 'https://pro.ant.design',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <><GithubOutlined /> WuXinYn GitHub</>,
          href: 'https://github.com/WuXinYn',
          blankTarget: true,
        },
        {
          key: 'api',
          title: <><ApiOutlined /> API文档</>,
          href: '/umi/plugin/openapi',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
