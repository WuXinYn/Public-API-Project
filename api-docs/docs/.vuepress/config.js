module.exports = {
  title: 'API开放平台文档',
  description: '用户指南与开发者文档',
  port: 8001,
  head: [
    ['link', { rel: 'icon', href: '/images/logo.png' }]
  ],
  base: '/',
  themeConfig: {
    logo: '/images/logo.png',
    nav: [
      { text: '首页', link: '/' },
      { text: '指南', link: '/guide/' },
      { text: 'API参考', link: '/api/' },
      { 
        text: '开发者工具', 
        items: [
          { text: 'SDK下载', link: '/tools/sdk.html' },
          { text: '心跳包工具', link: '/tools/heartbeat.html' }
        ]
      },
      { text: '登录平台', link: 'http://localhost:8000/user/login' },
    ],
    sidebar: {
      '/guide/': [
        {
          title: '使用指南',
          collapsable: false,
          children: [
            '',
            'quickstart',
            'authentication',
          ]
        }
      ],
      '/api/': [
        {
          title: 'API参考',
          collapsable: false,
          children: [
            '',
            'interface',
            'examples',
          ]
        }
      ],
      '/tools/': [
        {
          title: '开发者工具',
          collapsable: false,
          children: [
            'sdk',
            'heartbeat',
          ]
        }
      ]
    },
    lastUpdated: '上次更新',
    repo: '',
    editLinks: false,
    docsDir: 'docs',
    editLinkText: '在 GitHub 上编辑此页',
    smoothScroll: true
  }
} 