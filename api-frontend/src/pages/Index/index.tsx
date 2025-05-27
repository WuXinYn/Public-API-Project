import { PageContainer } from '@ant-design/pro-components';
import React, {useEffect, useState} from 'react';
import {Button, List, message} from "antd";
import {
  listInterfaceInfoByPageUsingGet
} from "@/services/api-backend/interfaceInfoController";
/**
 * 主页
 * @constructor
 */
const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [list, setList] = useState<API.InterfaceInfo[]>([]);
  const [total, setTotal] = useState<number>(0);

  const loadData = async (current = 1, pageSize = 5) => {
    setLoading(true); // 正在加载
    try {
      const res = await listInterfaceInfoByPageUsingGet({
        current, pageSize
      });
      setList(res?.data?.records ?? []);
      setTotal(res?.data?.total ?? 0);
    } catch(error: any) {
      message.error('请求失败！' + (error as Error).message);
    }
    setLoading(false); // 加载完毕
  }

  // 加载数据逻辑
  useEffect(() => {
    loadData();
  },[])

  return (
    // 页面的最外层
    <PageContainer title="在线接口开放平台" style={{
      background: 'linear-gradient(rgba(246, 244, 244, 0.7), rgba(254, 250, 250, 0.7)), url("/yuanshen/paimeng.png")',
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundRepeat: 'no-repeat',
      height: '75vh',
      fontWeight: 'bold',
      boxShadow:'inset 0 0 100px rgb(249, 247, 247)',
    }}>
      <List
        className="my-list"
        loading={loading}
        itemLayout="horizontal"
        dataSource={list}
        renderItem={(item) => {
          const apiLink = `/interface_info/${item.id}`;
          return <List.Item
            actions={
              [<Button key={item.id} href={apiLink} style={{ borderColor: 'black' , color: 'black', backgroundColor:'#1fe0' }}>查看</Button>]
            }
          >
              <List.Item.Meta
                title={<a href={apiLink}>{item.name}</a>}
                description={item.description}
              />
          </List.Item>
        }}
        pagination = {{
            showTotal(total: number) {
              return '总数: ' + total;
            },
            pageSize: 5,
            total,
            onChange(page, pageSize) { // 切换分页时自动加载数据
              loadData(page, pageSize);
            },
          }
        }
      />
    </PageContainer>
  );
};

export default Index;
