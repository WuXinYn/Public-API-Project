import { PageContainer } from '@ant-design/pro-components';
import '@umijs/max';
import React, {useEffect, useState} from 'react';
import ReactECharts from 'echarts-for-react';
import {listTopInvokeInterfaceInfoUsingGet} from "@/services/api-backend/analysisController";

/**
 * 接口分析
 * @constructor
 */
const InterfaceAnalysis: React.FC = () => {
  const [data, setData] = useState<API.InterfaceInfo[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 从远程获取数据
    try {
      listTopInvokeInterfaceInfoUsingGet().then(res => {
        if (res.data) {
          setData(res.data);
        }
      });
    } catch (error:any) {

    }
  },[])

  // 映射成{ value: 1048, name: 'Search Engine' }
  const chartData = data.map(item => {
    return {
      value: item.totalNum,
      name: item.name
    }
  })

  const option = {
    title: {
      text: '调用次数最多的接口Top5',
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: 'Access From',
        type: 'pie',
        radius: '50%',
        data: chartData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  };

  return (
    <PageContainer style={{
        background: 'linear-gradient(rgba(246, 244, 244, 0.73), rgba(254, 250, 250, 0.69)), url("/gift/yinlang.gif")',
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
        height: '75vh',
      }}>
      <ReactECharts
        loadingOption={{
          showLoading: loading
        }}
        option={ option } />
    </PageContainer>
  );
};
export default InterfaceAnalysis;
