import { PageContainer } from '@ant-design/pro-components';
import React, {useEffect, useState} from 'react';
import {Button, Card, Descriptions, Form, message, Input, Divider} from "antd";
import {
  getInterfaceInfoByIdUsingGet, invokeInterfaceInfoUsingPost,
} from "@/services/api-backend/interfaceInfoController";
import { useParams} from "react-router";
import { useNavigate } from 'react-router-dom';  // React Router v6 使用 `useNavigate`

/**
 * 主页
 * @constructor
 */
const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<API.InterfaceInfo>();
  const params = useParams();
  const [invokeRes, setInvokeRes] = useState<any>();
  const [invokeLoading, setInvokeLoading] = useState(false);
  const navigate = useNavigate();  // 获取 navigate 函数
  const [form] = Form.useForm();
  const [fields, setFields] = useState<{ [key: string]: any }>({});  // 表单字段

  // 返回上一个页面
  const goBack = () => {
    navigate(-1);  // 返回上一页
  };

  const loadData = async () => {
    if (!params.id) {
      message.error('参数不存在');
      return ;
    }
    setLoading(true); // 正在加载
    try {
      const res = await getInterfaceInfoByIdUsingGet({
        id: Number(params.id)
      });
      setData(res.data);
      const parsedFields = JSON.parse(res.data?.requestParams || '{}');
      setFields(parsedFields);
    } catch(error: unknown) {
      if (error instanceof Error) {
        message.error('请求失败！' + (error as any).message);
      } else {
        message.error('请求失败！');
      }
    }
    setLoading(false); // 加载完毕
  }

  // 加载数据逻辑
  useEffect(() => {
    loadData();
  },[])

  const onFinish = async (values:any) => {
    if (!params.id) {
      message.error("接口不存在");
      return ;
    }
    setInvokeLoading(true);
    // 打包成 JSON 字符串
    const jsonString = JSON.stringify(values??"{ }");
    const interfaceInfoInvokeRequest = { id: Number(params.id), userRequestParams: jsonString, isTest: false}
    try {
      // const res = await invokeInterfaceInfoUsingPost({
      //   id: params.id,
      //   ...values
      // });
      const res = await invokeInterfaceInfoUsingPost(interfaceInfoInvokeRequest);
      setInvokeRes(res.data);
      message.success('请求成功！');
      return true;
    } catch (error) {
      message.error('请求失败！' + (error as any).message);
      return false;
    } finally {
      setInvokeLoading(false);
    }
  };

  return (
    // 页面的最外层
    <PageContainer title="查看接口文档" >
      <Card style={{
        background: 'linear-gradient(rgba(246, 244, 244, 0.7), rgba(254, 250, 250, 0.7)), url("/yuanshen/ying01.jpg")',
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
        fontWeight: 'bold',
        boxShadow:'inset 0 0 100px rgb(241, 246, 247)',
    }}>
        { data ?
          <Descriptions title={data.name} column={1}>
            <Descriptions.Item label="接口状态">{ data.status ? '正常' : '关闭' }</Descriptions.Item>
            <Descriptions.Item label="描述">{ data.description }</Descriptions.Item>
            <Descriptions.Item label="请求地址">{ data.url }</Descriptions.Item>
            <Descriptions.Item label="请求参数">
              <pre>{ data.requestParams ? JSON.stringify(JSON.parse(data.requestParams), null, 2) : '无请求参数' }</pre>
            </Descriptions.Item>
            <Descriptions.Item label="请求方法">{ data.method }</Descriptions.Item>
            <Descriptions.Item label="请求头">
              <pre>{ data.requestHeader ? JSON.stringify(JSON.parse(data.requestHeader), null, 2) : '无请求头' }</pre>
            </Descriptions.Item>
            <Descriptions.Item label="响应头">
              <pre>{ data.responseHeader ? JSON.stringify(JSON.parse(data.responseHeader), null, 2) : '无响应头' }</pre>
            </Descriptions.Item>
            <Descriptions.Item label="创建时间">{ data.createTime?.substring(0,10) }</Descriptions.Item>
            <Descriptions.Item label="更新时间">{ data.updateTime?.substring(0,10) }</Descriptions.Item>
          </Descriptions> : <>接口不存在</>
        }
        <Button type="text" style={{borderColor: 'white' , color: 'grey', backgroundColor:'#1fe0', float:'right'}} onClick={goBack}>返回上一页</Button>
      </Card>
      <Divider/>
      <Card title="在线测试"  style={{
        background: 'linear-gradient(rgba(246, 244, 244, 0.7), rgba(254, 250, 250, 0.7)), url("/yuanshen/image.png")',
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
        fontWeight: 'bold',
        boxShadow:'inset 0 0 100px rgb(241, 246, 247)',
      }}>
        <Form name="userRequestParams" layout="horizontal" onFinish={onFinish} form={form} >
          { Object.keys(fields).map((fieldKey) => ( //根据接口的请求参数自动生成对应的表单
            <Form.Item
              key={fieldKey}
              name={fieldKey}
              label={fieldKey.charAt(0).toUpperCase() + fieldKey.slice(1)+" : "} // Capitalize first letter for label
              initialValue={fields[fieldKey]}>
              <Input.TextArea style={{ backgroundColor:'#1fe0', borderColor: 'white', width: '400px'}}
              />
            </Form.Item>))}
          {/* <Form.Item label="请求参数"  name="userRequestParams"> invoke
            <Input.TextArea />
          </Form.Item> */}
          <Form.Item wrapperCol={{span: 16}} >
            <Button type="text" htmlType="reset" style={{borderColor: 'grey' , color: 'grey', backgroundColor:'#1fe0', marginRight: '-375px', float:'right'}}>
              重置
            </Button>
            <Button type="text" htmlType="submit" style={{borderColor: 'blue' , color: 'blue', backgroundColor:'#1fe0', marginRight: '-305px', float:'right'}}>
              调用
            </Button>
          </Form.Item>
        </Form>
      </Card>
      <Divider/>
      <Card title="测试结果" loading={invokeLoading} style={{
        background: 'linear-gradient(rgba(246, 244, 244, 0.7), rgba(254, 250, 250, 0.7)), url("/yuanshen/aleiqinuo01.png")',
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
        fontWeight: 'bold',
        boxShadow:'inset 0 0 100px rgb(241, 246, 247)',
      }}>
        {invokeRes}
      </Card>
    </PageContainer>
  );
};

export default Index;
