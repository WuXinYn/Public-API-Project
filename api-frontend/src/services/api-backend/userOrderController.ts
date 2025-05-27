// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** cancelOrder GET /api/order/cancel */
export async function cancelOrderUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.cancelOrderUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseboolean>('/api/order/cancel', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** deleteFuelPackage DELETE /api/order/delete */
export async function deleteFuelPackageUsingDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteFuelPackageUsingDELETE1Params,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseboolean>('/api/order/delete', {
    method: 'DELETE',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** deleteBatch DELETE /api/order/delete/batch */
export async function deleteBatchUsingDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteBatchUsingDELETE1Params,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseboolean>('/api/order/delete/batch', {
    method: 'DELETE',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** getUserOrderInfoById GET /api/order/get_by_id */
export async function getUserOrderInfoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserOrderInfoByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseUserOrderInfo>('/api/order/get_by_id', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** getUserMenuInfoByUserID GET /api/order/list/page */
export async function getUserMenuInfoByUserIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserMenuInfoByUserIDUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageUserOrderInfo>('/api/order/list/page', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** updateFuelPackage POST /api/order/update */
export async function updateFuelPackageUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.UserOrderInfo,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean>('/api/order/update', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** getAllOrderInfosUsingGet GET /api/order/list/all */
export async function getAllOrderInfosUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserMenuInfoByUserIDUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageUserOrderInfo>('/api/order/list/all', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
