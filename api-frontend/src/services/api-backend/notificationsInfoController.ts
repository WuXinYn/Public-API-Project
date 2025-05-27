// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';


  /**
   * 发送通知
   */
/** addNewNotification POST /api/notificationInfo/add */
export async function addNewNotificationUsingPost(
  body: API.NotificationInfoAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseboolean>('/api/notificationInfo/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

  /**
   * 删除通知（普通用户按id删除自己的通知）
   */
/** deleteNotification POST /api/notificationInfo/delete */
export async function deleteNotificationUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseboolean>('/api/notificationInfo/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

  /**
   * 普通用户获取自己的通知列表
   */
/** getNotificationList GET /api/notificationInfo/list */
export async function listInterfaceInfoByUserUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.GetNotificationInfoListUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseNotificationInfoList>('/api/notificationInfo/list', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

  /**
   * 获取列表（管理员分页获取所有通知的列表）
   */
/** listAllNotificationInfos GET /api/notificationInfo/list/all/page */
export async function listAllNotificationInfosUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listNotificationInfoByPageUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageNotificationInfo>('/api/notificationInfo/list/all/page', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

  /**
   * 分页获取列表
   */
/** listNotificationInfoByPage GET /api/notificationInfo/list/page */
export async function listNotificationInfoByPageUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listNotificationInfoByPageUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageNotificationInfo>('/api/notificationInfo/list/page', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/**
 * 更新通知状态为已读
 */
/** updateNotificationStatus POST /api/notificationInfo/update/status */
export async function updateNotificationStatusUsingPost(
  body: API.UpdateNotificationStatusRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseboolean>('/api/notificationInfo/update/status', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}