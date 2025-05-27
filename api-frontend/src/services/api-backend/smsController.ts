// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** phone GET /api/sms/getCode */
export async function phoneUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.PhoneUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseString>('/api/sms/getCode', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
