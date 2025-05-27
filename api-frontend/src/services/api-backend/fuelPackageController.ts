// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** addFuelPackage POST /api/fuel_package/add */
export async function addFuelPackageUsingPost(
  body: API.FuelPackageAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponselong>('/api/fuel_package/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteFuelPackage DELETE /api/fuel_package/delete */
export async function deleteFuelPackageUsingDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteFuelPackageUsingDELETEParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseboolean>('/api/fuel_package/delete', {
    method: 'DELETE',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** deleteBatch DELETE /api/fuel_package/delete/batch */
export async function deleteBatchUsingDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteBatchUsingDELETEParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseboolean>('/api/fuel_package/delete/batch', {
    method: 'DELETE',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listFuelPackage GET /api/fuel_package/list */
export async function listFuelPackageUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listFuelPackageUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageFuelPackage>('/api/fuel_package/list', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** generateFuelPackageOrder POST /api/fuel_package/order/pay */
export async function generateFuelPackageOrderUsingPost(
  body: API.FuelPackageOrderGenerateRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseString>('/api/fuel_package/order/pay', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** updateFuelPackage POST /api/fuel_package/update */
export async function updateFuelPackageUsingPost(
  body: API.FuelPackageUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseboolean>('/api/fuel_package/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** listAllFuelPackage GET /api/fuel_package/list/all */
export async function listAllFuelPackageUsingGet(
  options?: { [key: string]: any },
){
  return request<API.BaseResponseListFuelPackageInfoVo>('/api/fuel_package/list/all', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    ...(options || {}),
  })
}
