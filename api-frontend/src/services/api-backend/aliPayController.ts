// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** payNotify POST /api/alipay/notify */
export async function payNotifyUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/api/alipay/notify', {
    method: 'POST',
    ...(options || {}),
  });
}

/** payByPost POST /api/alipay/pay/post */
export async function payByPostUsingPost(
  body: API.AliPayRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseString>('/api/alipay/pay/post', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** toRefund POST /api/alipay/refund */
export async function toRefundUsingPost(body: API.AliPayRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/api/alipay/refund', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
