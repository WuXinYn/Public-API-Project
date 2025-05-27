// @ts-ignore
/* eslint-disable */

declare namespace API {
  type CurrentUser = {
    name?: string;
    avatar?: string;
    userid?: string;
    email?: string;
    signature?: string;
    title?: string;
    group?: string;
    tags?: { key?: string; label?: string }[];
    notifyCount?: number;
    unreadCount?: number;
    country?: string;
    access?: string;
    geographic?: {
      province?: { label?: string; key?: string };
      city?: { label?: string; key?: string };
    };
    address?: string;
    phone?: string;
  };

  type LoginResult = {
    status?: string;
    type?: string;
    currentAuthority?: string;
  };

  type PageParams = {
    current?: number;
    pageSize?: number;
  };

  type RuleListItem = {
    key?: number;
    disabled?: boolean;
    href?: string;
    avatar?: string;
    name?: string;
    owner?: string;
    desc?: string;
    callNo?: number;
    status?: number;
    updatedAt?: string;
    createdAt?: string;
    progress?: number;
  };

  type RuleList = {
    data?: RuleListItem[];
    /** 列表的内容总数 */
    total?: number;
    success?: boolean;
  };

  type FakeCaptcha = {
    code?: number;
    status?: string;
  };

  type LoginParams = {
    username?: string;
    password?: string;
    autoLogin?: boolean;
    type?: string;
  };

  type ErrorResponse = {
    /** 业务约定的错误码 */
    errorCode: string;
    /** 业务上的错误信息 */
    errorMessage?: string;
    /** 业务上的请求是否成功 */
    success?: boolean;
  };

  type NoticeIconList = {
    data?: NoticeIconItem[];
    /** 列表的内容总数 */
    total?: number;
    success?: boolean;
  };

  type NoticeIconItemType = 'notification' | 'message' | 'event';

  type NoticeIconItem = {
    id?: string;
    extra?: string;
    key?: string;
    read?: boolean;
    avatar?: string;
    title?: string;
    status?: string;
    datetime?: string;
    description?: string;
    type?: NoticeIconItemType;
  };

  type BaseResponse<T> = {
    code: number;
    data: T;
    message: string;
    success: boolean;
  };

  type InterfaceInfo = {
    id: number;
    name: string;
    description: string;
    url: string;
    method: string;
    requestParams: string;
    responseParams: string;
    status: number;
    userId: number;
    createTime: string;
    updateTime: string;
  };

  type InterfaceInfoVO = {
    name: string;
    description: string;
    url: string;
    method: string;
    requestParams?: string;
    responseParams?: string;
    status: number;
    documentation?: any;
  };

  type AliPayRequest = {
    amount?: number;
    fuelPackageId?: number;
    number?: number;
    orderNumber?: string;
    serviceName?: string;
    totalAmount?: number;
    userId?: number;
  };

  type BaseResponsePageFuelPackage = {
    code?: number;
    data?: PageFuelPackage;
    message?: string;
  };

  type BaseResponseBoolean= {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type cancelOrderUsingGETParams = {
    /** id */
    id: number;
  };

  type deleteBatchUsingDELETE1Params = {
    /** ids */
    ids: number;
  };

  type deleteBatchUsingDELETEParams = {
    /** ids */
    ids: number;
  };

  type deleteFuelPackageUsingDELETE1Params = {
    /** id */
    id: number;
  };

  type deleteFuelPackageUsingDELETEParams = {
    /** id */
    id: number;
  };

  type FuelPackage = {
    amount?: number;
    createdTime?: string;
    description?: string;
    id?: number;
    isDelete?: number;
    name?: string;
    number?: number;
    price?: number;
    updateTime?: string;
  };

  type FuelPackageAddRequest = {
    amount: number;
    description?: string;
    name?: string;
    price: number;
  };

  type FuelPackageOrderGenerateRequest = {
    fuelPackageId?: number;
    payMethod?: number;
    userId?: number;
    interfaceId?: number;
  };

  type FuelPackageUpdateRequest = {
    amount: number;
    description?: string;
    id: number;
    name?: string;
    price: number;
  };

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type PageFuelPackage = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: FuelPackage[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserOrderInfo = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserOrderInfo[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type UserInterfaceInfoAddNumRequest = {
    id?: number;
    num?: number;
  };

  type UserInterfaceInfoAndNameResponse = {
    createTime?: string;
    description?: string;
    id?: number;
    interfaceInfoId?: number;
    leftNum?: number;
    name?: string;
    status?: number;
    totalNum?: number;
    updateTime?: string;
    url?: string;
    userId?: number;
  };

  type UserOrderInfo = {
    createTime?: string;
    id?: number;
    isDelete?: number;
    isNormal?: number;
    number?: number;
    orderNumber?: string;
    orderStatus?: number;
    payTime?: string;
    paymentMethod?: number;
    paymentNumber?: number;
    setMenuId?: number;
    setMenuName?: string;
    setMenuNumber?: number;
    updateTime?: string;
    userId?: number;
    interfaceId?: number;
  };

  type View = {
    contentType?: string;
  };

  type PhoneUsingGETParams = {
    /** userAccount */
    userAccount: string;
  };

  type BaseResponseString = {
    code?: number;
    data?: string;
    message?: string;
  };

  type listFuelPackageUsingGETParams = {
    createdTime?: string;
    current?: number;
    description?: string;
    id?: number;
    name?: string;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    updateTime?: string;
  };

  type getUserOrderInfoByIdUsingGETParams = {
    /** id */
    id: number;
  };

  type BaseResponseUserOrderInfo = {
    code?: number;
    data?: UserOrderInfo;
    message?: string;
  };

  type getUserMenuInfoByUserIDUsingGETParams = {
    current?: number;
    id?: number;
    orderNumber?: string;
    orderStatus?: number;
    pageSize?: number;
    payTime?: string;
    paymentMethod?: number;
    paymentNumber?: number;
    setMenuId?: number;
    setMenuName?: string;
    sortField?: string;
    sortOrder?: string;
    userId?: number;
  };

  type BaseResponsePageUserOrderInfo = {
    code?: number;
    data?: PageUserOrderInfo;
    message?: string;
  };
}
