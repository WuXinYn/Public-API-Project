import {
  addInterfaceInfoUsingPost,
  deleteInterfaceInfoUsingPost,
  listInterfaceInfoByPageUsingGet, offlineInterfaceInfoUsingPost, onlineInterfaceInfoUsingPost,
  updateInterfaceInfoUsingPost,
} from '@/services/api-backend/interfaceInfoController';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns, ProDescriptionsItemProps } from '@ant-design/pro-components';
import {
  PageContainer,
  ProDescriptions,
  ProTable,
} from '@ant-design/pro-components';
import '@umijs/max';
import { Button, Drawer, message, Popconfirm} from 'antd';
import React, { useRef, useState } from 'react';
import UpdateModel from '@/pages/Admin/InterfaceInfo/components/UpdateModel';
import { SortOrder } from 'antd/lib/table/interface';
import CreateModel from '@/pages/Admin/InterfaceInfo/components/CreateModel';
// import { Model } from 'echarts';

const InterfaceInfo: React.FC = () => {
  const [createModalOpen, handleModalOpen] = useState<boolean>(false); // 新建窗口的弹窗
  const [updateModalOpen, handleUpdateModalOpen] = useState<boolean>(false); // 分布更新窗口的弹窗
  const [showDetail, setShowDetail] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [currentRow, setCurrentRow] = useState<API.InterfaceInfo>();
  // const [selectedRowsState, setSelectedRows] = useState<API.InterfaceInfo[]>([]);

  /**
   * @en-US Add node
   * @zh-CN 添加节点
   * @param fields
   */
  const handleAdd = async (fields: API.InterfaceInfo) => {
    const hide = message.loading('正在添加');
    try {
      await addInterfaceInfoUsingPost({
        ...fields,
      });
      hide();
      message.success('创建成功');
      handleModalOpen(false);
      return true;
    } catch (error : any) {
      hide();
      message.error('创建失败' + error.message);
      return false;
    }
  };

  /**
   * @en-US Update node
   * @zh-CN 更新节点
   *
   * @param fields
   */
  const handleUpdate = async (fields: API.InterfaceInfo) => {
    if (!currentRow) {
      return ;
    }
    const hide = message.loading('修改中');
    try {
      await updateInterfaceInfoUsingPost({
        id: currentRow.id,
        ...fields,
      });
      hide();
      message.success('修改成功！');
      return true;
    } catch (error : any) {
      hide();
      message.error('修改失败！' + error.message);
      return false;
    }
  };

  /**
   *
   * @zh-CN 发布接口
   *
   * @param record
   */
  const handleOnlie = async (record: API.IDRequest) => {
    const hide = message.loading('发布中');
    if (!record) return true;
    try {
      await onlineInterfaceInfoUsingPost({
        id: record.id,
      });
      hide();
      message.success('发布成功！');
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('发布失败！' + error.message);
      return false;
    }
  };

  /**
   *
   * @zh-CN 下线接口
   *
   * @param record
   */
  const handleOffline = async (record: API.IDRequest) => {
    const hide = message.loading('下线中');
    if (!record) return true;
    try {
      await offlineInterfaceInfoUsingPost({
        id: record.id,
      });
      hide();
      message.success('下线成功！');
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('下线失败！' + error.message);
      return false;
    }
  };

  /**
   *  Delete node
   * @zh-CN 删除节点
   *
   * @param selectedRows
   */
  const handleRemove = async (record: API.InterfaceInfo) => {
    const hide = message.loading('正在删除');
    if (!record) return true;
    try {
      await deleteInterfaceInfoUsingPost({
        id: record.id,
      });
      hide();
      message.success('删除成功！');
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('删除失败！' + error.message);
      return false;
    }
  };

  const columns: ProColumns<API.InterfaceInfo>[] = [
    {
      title: 'id',
      dataIndex: 'id',
      hideInForm: true,  // 在表单中隐藏
    },
    {
      title: '接口名称',
      dataIndex: 'name',
      valueType: 'text',
      ellipsis: true, // 是否允许缩略
      formItemProps: {
        rules: [
          {
            required: true,
          },
        ],
      },
    },
    {
      title: '描述',
      dataIndex: 'description',
      valueType: 'textarea',
      ellipsis: true,
      formItemProps: {
        rules: [
          {
            required: true,
          },
        ],
      },
    },
    {
      title: '请求方法',
      dataIndex: 'method',
      // valueType: 'text',
      ellipsis: true, // 是否允许缩略
      valueType: 'select',
      valueEnum: {
        GET: { text: 'GET', status: 'GET' },
        POST: { text: 'POST', status: 'POST' },
        PUT: { text: 'PUT', status: 'PUT' },
        DELETE: { text: 'DELETE', status: 'DELETE' },
        PATCH: { text: 'PATCH', status: 'PATCH' },
        HEAD: { text: 'HEAD', status: 'HEAD' },
        OPTIONS: { text: 'OPTIONS', status: 'OPTIONS' },
        TRACE: { text: 'TRACE', status: 'TRACE' },
        CONNECT: { text: 'CONNECT', status: 'CONNECT' },
      },
      formItemProps: {
        rules: [
          {
            required: true,
          },
        ],
      },
    },
    {
      title: '接口地址',
      dataIndex: 'url',
      valueType: 'text',
      formItemProps: {
        rules: [
          {
            required: true,
          },
        ],
      },
    },
    {
      title: '请求参数',
      dataIndex: 'requestParams',
      valueType: 'jsonCode',
    },
    {
      title: '请求头',
      dataIndex: 'requestHeader',
      valueType: 'jsonCode',
      formItemProps: {
        rules: [
          {
            required: true,
          },
        ],
      },
    },
    {
      title: '响应头',
      dataIndex: 'responseHeader',
      valueType: 'jsonCode',
      formItemProps: {
        rules: [
          {
            required: true,
          },
        ],
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      hideInForm: true,
      ellipsis: true, // 是否允许缩略
      valueEnum: {
        0: {
          text: '关闭',
          status: 'Default',
        },
        1: {
          text: '开启',
          status: 'Processing',
        },
        2: {
          text: '异常',
          status: 'Error',
        },
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInForm: true,
      ellipsis: true, // 是否允许缩略
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      valueType: 'dateTime',
      hideInForm: true,
      ellipsis: true, // 是否允许缩略
    },
    {
      title: '操作',
      align: 'center',
      dataIndex: 'option',
      ellipsis: true, // 是否允许缩略
      valueType: 'option',
      render: (_, record) => [  // record指代当前选择的某条记录
        <Button
          type="text"
          style={{ borderColor: 'blue' , color: 'blue'}}
          key="update"
          onClick={() => {
            handleUpdateModalOpen(true);
            setCurrentRow(record);
          }}
        >
          修改
        </Button>,

        record.status === 0 ? <Button   // 条件渲染，已经上线的接口只显示下架按钮
          type="text"
          key="online"
          style={{ borderColor: 'green' , color: 'green'}}
          onClick={() => {
            handleOnlie(record);
          }}
        >
          发布
        </Button> : null ,

        record.status === 1 ? <Button
          type="text"
          style={{ borderColor: 'red' , color: 'red'}}
          key="offline"
          onClick={() => {
            handleOffline(record);
          }}
        >
          下线
        </Button> : null,

        <Popconfirm
          title="确定要删除吗？"
          onConfirm={() => handleRemove(record)}
          okText="确定"
          cancelText="取消"
        >
          <Button
            type="text"
            style={{ borderColor: 'red', color: 'red' }}
            key="delete"
          >
            删除
          </Button>
        </Popconfirm>

      ],
    },
  ];

  return (
    <PageContainer style={{
      background: 'linear-gradient(rgba(246, 244, 244, 0.56), rgba(254, 250, 250, 0.52)), url("/gift/liuying.gif")',
      backgroundSize: '10%',
      backgroundPosition: 'center -20px -10px',
      backgroundRepeat: 'no-repeat',
      height: '90vh',
     }}>
      <ProTable<API.RuleListItem, API.PageParams>
        headerTitle={'接口信息'}
        actionRef={actionRef}
        rowKey="key"
        tableLayout="auto"
        search={{ labelWidth: 120 }}
        toolBarRender={() => [
          <Button
            style={{ borderColor: 'blue' , color: 'blue'}}
            type="text"
            key="primary"
            onClick={() => {
              handleModalOpen(true);
            }}
          >
            <PlusOutlined /> 新建
          </Button>,
        ]}
        request={async (
          params,
          sort: Record<string, SortOrder>,
          filter: Record<string, (string | number)[] | null>,
        ) => {
          const res = await listInterfaceInfoByPageUsingGet({
            ...params,
          });
          if (res?.data) {
            return {
              data: res?.data.records || [],
              success: true,
              total: res.data.total || 0,
            };
          } else {
            return {
              data: [],
              success: false,
              total: 0,
            };
          }
        }}
        columns={columns}
        scroll={{ x: true}}
        pagination={false}
        // rowSelection={{
        //   onChange: (_, selectedRows) => {
        //     setSelectedRows(selectedRows);
        //   },
        // }}
      />
      {/* {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              已选择{' '}
              <a
                style={{
                  fontWeight: 600,
                }}
              >
                {selectedRowsState.length}
              </a>{' '}
              项 &nbsp;&nbsp;
              <span>
                服务调用次数总计 {selectedRowsState.reduce((pre, item) => pre + item.callNo!, 0)} 次
              </span>
            </div>
          }
        >
          <Button
            onClick={async () => {
              for (const row of selectedRowsState) {
                await handleRemove(row);
              }
              setSelectedRows([]);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            批量删除
          </Button>
          <Button type="primary">批量审批</Button>
        </FooterToolbar>
      )} */}

      <UpdateModel
        columns={columns}
        onSubmit={async (value) => {
          const success = await handleUpdate(value);
          if (success) {
            handleUpdateModalOpen(false);
            setCurrentRow(undefined);
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
        onCancel={() => {
          handleUpdateModalOpen(false);
          if (!showDetail) {
            setCurrentRow(undefined);
          }
        }}
        visible={updateModalOpen}
        values={currentRow || {}}
      />

      <Drawer
        width={600}
        open={showDetail}
        onClose={() => {
          setCurrentRow(undefined);
          setShowDetail(false);
        }}
        closable={false}
      >
        {currentRow?.name && (
          <ProDescriptions<API.RuleListItem>
            column={2}
            title={currentRow?.name}
            request={async () => ({
              data: currentRow || {},
            })}
            params={{
              id: currentRow?.name,
            }}
            columns={columns as ProDescriptionsItemProps<API.RuleListItem>[]}
          />
        )}
      </Drawer>

      <CreateModel
        columns={columns}
        onCancel={() => {
          handleModalOpen(false);
        }}
        onSubmit={async (values) => {
          await handleAdd(values);
        }}
        visible={createModalOpen}
      />
    </PageContainer>
  );
};
export default InterfaceInfo;
