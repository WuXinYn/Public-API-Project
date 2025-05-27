import React, { useEffect, useRef, useState } from 'react';
import { Modal, Spin, message, Button, Alert, Tabs } from 'antd';
import { SafetyOutlined, FormOutlined } from '@ant-design/icons';
import type { TabsProps } from 'antd';

interface AlipayFormModalProps {
  visible: boolean;
  formHtml: string;
  onCancel: () => void;
}

interface FormField {
  name: string;
  value: string;
}

interface PaymentFormData {
  action: string;
  inputs: FormField[];
}

const AlipayFormModal: React.FC<AlipayFormModalProps> = ({ visible, formHtml, onCancel }) => {
  const iframeRef = useRef<HTMLIFrameElement>(null);
  const formContainerRef = useRef<HTMLDivElement>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [paymentData, setPaymentData] = useState<PaymentFormData>({
    action: '',
    inputs: []
  });
  const [activeTab, setActiveTab] = useState('direct');

  // 添加调试函数，输出处理前后的HTML
  const debugHtml = (html: string, label: string) => {
    console.log(`[${label}] HTML长度: ${html.length}`);
    console.log(`[${label}] HTML前200字符: ${html.substring(0, 200)}`);
  };

  // 从HTML中提取表单数据
  const extractFormData = (html: string): PaymentFormData | null => {
    try {
      // 创建临时DOM解析HTML
      const parser = new DOMParser();
      const doc = parser.parseFromString(html, 'text/html');
      const form = doc.querySelector('form');
      
      if (!form) {
        throw new Error('未找到表单元素');
      }
      
      const action = form.getAttribute('action') || '';
      const inputs: FormField[] = [];
      
      // 获取所有表单输入字段
      const inputElements = form.querySelectorAll('input[type="hidden"]');
      inputElements.forEach((element) => {
        const input = element as HTMLInputElement;
        if (input.name) {
          inputs.push({
            name: input.name,
            value: input.value
          });
        }
      });
      
      return { action, inputs };
    } catch (err) {
      console.error('解析表单数据出错:', err);
      return null;
    }
  };

  useEffect(() => {
    if (visible && formHtml) {
      try {
        setLoading(true);
        setError(false);
        setErrorMsg('');
        
        // 调试原始HTML
        debugHtml(formHtml, '原始表单');
        
        // 提取支付表单数据
        const formData = extractFormData(formHtml);
        if (formData) {
          setPaymentData(formData);
          console.log('提取的表单数据:', formData);
          setLoading(false);
        } else {
          throw new Error('无法解析支付表单数据');
        }
        
        // 尝试在iframe中加载（备选方案）
        if (activeTab === 'iframe' && formHtml) {
          const timer = setTimeout(() => {
            if (iframeRef.current) {
              try {
                // 尝试设置iframe的src而不是写入内容
                const blob = new Blob([formHtml], { type: 'text/html' });
                const blobURL = URL.createObjectURL(blob);
                iframeRef.current.src = blobURL;
                
                // 清理URL对象
                return () => URL.revokeObjectURL(blobURL);
              } catch (err) {
                console.error('设置iframe src出错:', err);
                // 不设置错误状态，因为我们有直接支付的备选方案
              }
            }
          }, 300);
          
          return () => clearTimeout(timer);
        }
      } catch (err) {
        console.error('设置支付表单出错:', err);
        setError(true);
        setLoading(false);
        message.error('初始化支付表单失败，请重试');
      }
    }
  }, [visible, formHtml, activeTab]);

  // 直接提交表单到支付宝
  const handleDirectSubmit = () => {
    try {
      if (paymentData.action && paymentData.inputs.length > 0) {
        // 创建临时表单并提交
        const tempForm = document.createElement('form');
        tempForm.method = 'POST';
        tempForm.action = paymentData.action;
        tempForm.target = '_blank'; // 在新窗口中打开
        
        // 添加所有表单字段
        paymentData.inputs.forEach(input => {
          const inputElement = document.createElement('input');
          inputElement.type = 'hidden';
          inputElement.name = input.name;
          inputElement.value = input.value;
          tempForm.appendChild(inputElement);
        });
        
        // 添加到文档并提交
        document.body.appendChild(tempForm);
        tempForm.submit();
        
        // 移除临时表单
        setTimeout(() => {
          document.body.removeChild(tempForm);
        }, 100);
        
        message.success('支付表单已提交，请在新窗口完成支付');
      } else {
        throw new Error('表单数据不完整');
      }
    } catch (err) {
      console.error('直接提交表单出错:', err);
      message.error('提交支付表单失败，请重试');
    }
  };

  // 处理选项卡切换
  const handleTabChange = (key: string) => {
    setActiveTab(key);
  };

  const items: TabsProps['items'] = [
    {
      key: 'direct',
      label: (
        <span>
          <FormOutlined />
          直接支付
        </span>
      ),
      children: (
        <div style={{ padding: '20px', textAlign: 'center' }}>
          <Alert
            message="跳转支付"
            description="点击下方按钮将在新窗口中打开支付宝支付页面。完成支付后请返回此页面。"
            type="info"
            showIcon
            style={{ marginBottom: 20 }}
          />
          
          <div style={{ marginBottom: 20 }}>
            <Button 
              type="primary" 
              size="large" 
              onClick={handleDirectSubmit}
              disabled={!paymentData.action || paymentData.inputs.length === 0}
            >
              跳转到支付宝付款
            </Button>
          </div>
          
          <div ref={formContainerRef} style={{ display: 'none' }}></div>
        </div>
      )
    },
    {
      key: 'iframe',
      label: (
        <span>
          <SafetyOutlined />
          内嵌页面
        </span>
      ),
      children: (
        <div>
          <Alert
            message="支付提示"
            description={"如果内嵌页面无法正常显示，请尝试使用\"直接支付\"选项卡。"}
            type="info"
            showIcon
            style={{ marginBottom: 16 }}
          />
          
          <iframe
            ref={iframeRef}
            style={{ 
              width: '100%', 
              height: '400px', 
              border: 'none',
              display: 'block'
            }}
            title="alipay-form"
            sandbox="allow-forms allow-scripts allow-popups allow-top-navigation allow-same-origin"
          />
        </div>
      )
    }
  ];

  return (
    <Modal
      title="支付宝支付"
      open={visible}
      onCancel={onCancel}
      footer={
        <Button onClick={onCancel}>关闭</Button>
      }
      width={800}
      destroyOnClose
    >
      {loading && (
        <div style={{ textAlign: 'center', padding: '24px' }}>
          <Spin size="large" tip="加载中..." />
        </div>
      )}
      
      {error && (
        <div style={{ textAlign: 'center', padding: '24px', color: '#ff4d4f' }}>
          加载支付表单失败，请关闭后重试
        </div>
      )}
      
      {errorMsg && (
        <Alert
          message="支付宝返回错误"
          description={
            <div>
              <p>{errorMsg}</p>
              <p>可能原因：支付参数无效，请联系技术支持解决。</p>
              <p>错误码INVALID_PARAMETER通常表示订单参数格式不正确或缺失。</p>
            </div>
          }
          type="error"
          showIcon
          style={{ marginBottom: 16 }}
        />
      )}
      
      {!loading && !error && (
        <Tabs 
          activeKey={activeTab} 
          onChange={handleTabChange}
          items={items}
        />
      )}
    </Modal>
  );
};

export default AlipayFormModal;
