
declare namespace API {
  type BaseResponseboolean = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseInterfaceInfo = {
    code?: number;
    data?: InterfaceInfo;
    message?: string;
  };

  type BaseResponseListInterfaceInfo = {
    code?: number;
    data?: InterfaceInfo[];
    message?: string;
  };

  type BaseResponseListInterfaceInfoVO = {
    code?: number;
    data?: InterfaceInfoVO[];
    message?: string;
  };

  type BaseResponseListPost = {
    code?: number;
    data?: Post[];
    message?: string;
  };

  type BaseResponseListUserInterfaceInfo = {
    code?: number;
    data?: UserInterfaceInfo[];
    message?: string;
  };

  type BaseResponseListUserVO = {
    code?: number;
    data?: UserVO[];
    message?: string;
  };

  type BaseResponselong = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseobject = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponsePageInterfaceInfo = {
    code?: number;
    data?: PageInterfaceInfo;
    message?: string;
  };

  type BaseResponsePagePost = {
    code?: number;
    data?: PagePost;
    message?: string;
  };

  type BaseResponsePageUserInterfaceInfo = {
    code?: number;
    data?: PageUserInterfaceInfo;
    message?: string;
  };

  type BaseResponsePageUserInterfaceInfoAndName = {
    code?: number;
    data?: PageUserInterfaceInfoAndName;
    message?: string;
  };

  type BaseResponsePageUserVO = {
    code?: number;
    data?: PageUserVO;
    message?: string;
  };

  type BaseResponsePost = {
    code?: number;
    data?: Post;
    message?: string;
  };

  type BaseResponseUser = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserInterfaceInfo = {
    code?: number;
    data?: UserInterfaceInfo;
    message?: string;
  };

  type BaseResponseUserVO = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type DeleteRequest = {
    id?: number;
  };

  type getInterfaceInfoByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getPostByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type GetUserInterfaceInfoByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type SmsUserLoginRequest = {
    code?: string;
    userAccount?: string;
  };

  type IDRequest = {
    id?: number;
  };

  type InterfaceInfo = {
    createTime?: string;
    description?: string;
    id?: number;
    isDelete?: number;
    method?: string;
    name?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    status?: number;
    updateTime?: string;
    url?: string;
    userID?: number;
  };

  type InterfaceInfoAddRequest = {
    description?: string;
    method?: string;
    name?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    url?: string;
  };

  type InterfaceInfoInvokeRequest = {
    id?: number;
    userRequestParams?: string;
    isTest: boolean;
  };

  type InterfaceInfoUpdateRequest = {
    description?: string;
    id?: number;
    method?: string;
    name?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    status?: number;
    url?: string;
  };

  type InterfaceInfoVO = {
    createTime?: string;
    description?: string;
    id?: number;
    isDelete?: number;
    method?: string;
    name?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    status?: number;
    totalNum?: number;
    updateTime?: string;
    url?: string;
    userID?: number;
  };

  type listInterfaceInfoByPageUsingGETParams = {
    current?: number;
    description?: string;
    id?: number;
    method?: string;
    name?: string;
    pageSize?: number;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    url?: string;
    userID?: number;
  };

  type listInterfaceInfoUsingGETParams = {
    current?: number;
    description?: string;
    id?: number;
    method?: string;
    name?: string;
    pageSize?: number;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    url?: string;
    userID?: number;
  };

  type listPostByPageUsingGETParams = {
    age?: number;
    contact?: string;
    content?: string;
    current?: number;
    education?: string;
    gender?: number;
    job?: string;
    loveExp?: string;
    pageSize?: number;
    place?: string;
    reviewStatus?: number;
    sortField?: string;
    sortOrder?: string;
    userId?: number;
  };

  type listPostUsingGETParams = {
    age?: number;
    contact?: string;
    content?: string;
    current?: number;
    education?: string;
    gender?: number;
    job?: string;
    loveExp?: string;
    pageSize?: number;
    place?: string;
    reviewStatus?: number;
    sortField?: string;
    sortOrder?: string;
    userId?: number;
  };

  type listUserByPageUsingGETParams = {
    createTime?: string;
    current?: number;
    gender?: number;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userRole?: string;
  };

  type ListUserInterfaceInfoByPageUsingGETParams = {
    current?: number;
    id?: number;
    interfaceInfoId?: number;
    leftNum?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    totalNum?: number;
    userId?: number;
  };

  type AddUserInterfaceInfoLeftNumRequest = {
    id?: number;
    num?: number;
  };

  type ListUserInterfaceInfoUsingGETParams = {
    current?: number;
    id?: number;
    interfaceInfoId?: number;
    leftNum?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    totalNum?: number;
    userId?: number;
  };

  type listUserUsingGETParams = {
    createTime?: string;
    current?: number;
    gender?: number;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userRole?: string;
  };

  type ModelAndView = {
    empty?: boolean;
    model?: Record<string, any>;
    modelMap?: Record<string, any>;
    reference?: boolean;
    status?:
      | 'ACCEPTED'
      | 'ALREADY_REPORTED'
      | 'BAD_GATEWAY'
      | 'BAD_REQUEST'
      | 'BANDWIDTH_LIMIT_EXCEEDED'
      | 'CHECKPOINT'
      | 'CONFLICT'
      | 'CONTINUE'
      | 'CREATED'
      | 'DESTINATION_LOCKED'
      | 'EXPECTATION_FAILED'
      | 'FAILED_DEPENDENCY'
      | 'FORBIDDEN'
      | 'FOUND'
      | 'GATEWAY_TIMEOUT'
      | 'GONE'
      | 'HTTP_VERSION_NOT_SUPPORTED'
      | 'IM_USED'
      | 'INSUFFICIENT_SPACE_ON_RESOURCE'
      | 'INSUFFICIENT_STORAGE'
      | 'INTERNAL_SERVER_ERROR'
      | 'I_AM_A_TEAPOT'
      | 'LENGTH_REQUIRED'
      | 'LOCKED'
      | 'LOOP_DETECTED'
      | 'METHOD_FAILURE'
      | 'METHOD_NOT_ALLOWED'
      | 'MOVED_PERMANENTLY'
      | 'MOVED_TEMPORARILY'
      | 'MULTIPLE_CHOICES'
      | 'MULTI_STATUS'
      | 'NETWORK_AUTHENTICATION_REQUIRED'
      | 'NON_AUTHORITATIVE_INFORMATION'
      | 'NOT_ACCEPTABLE'
      | 'NOT_EXTENDED'
      | 'NOT_FOUND'
      | 'NOT_IMPLEMENTED'
      | 'NOT_MODIFIED'
      | 'NO_CONTENT'
      | 'OK'
      | 'PARTIAL_CONTENT'
      | 'PAYLOAD_TOO_LARGE'
      | 'PAYMENT_REQUIRED'
      | 'PERMANENT_REDIRECT'
      | 'PRECONDITION_FAILED'
      | 'PRECONDITION_REQUIRED'
      | 'PROCESSING'
      | 'PROXY_AUTHENTICATION_REQUIRED'
      | 'REQUESTED_RANGE_NOT_SATISFIABLE'
      | 'REQUEST_ENTITY_TOO_LARGE'
      | 'REQUEST_HEADER_FIELDS_TOO_LARGE'
      | 'REQUEST_TIMEOUT'
      | 'REQUEST_URI_TOO_LONG'
      | 'RESET_CONTENT'
      | 'SEE_OTHER'
      | 'SERVICE_UNAVAILABLE'
      | 'SWITCHING_PROTOCOLS'
      | 'TEMPORARY_REDIRECT'
      | 'TOO_EARLY'
      | 'TOO_MANY_REQUESTS'
      | 'UNAUTHORIZED'
      | 'UNAVAILABLE_FOR_LEGAL_REASONS'
      | 'UNPROCESSABLE_ENTITY'
      | 'UNSUPPORTED_MEDIA_TYPE'
      | 'UPGRADE_REQUIRED'
      | 'URI_TOO_LONG'
      | 'USE_PROXY'
      | 'VARIANT_ALSO_NEGOTIATES';
    view?: View;
    viewName?: string;
  };

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type PageInterfaceInfo = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: InterfaceInfo[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PagePost = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: Post[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserInterfaceInfo = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserInterfaceInfo[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserInterfaceInfoAndName = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserInterfaceInfoAndName[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserVO = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type Post = {
    age?: number;
    contact?: string;
    content?: string;
    createTime?: string;
    education?: string;
    gender?: number;
    id?: number;
    isDelete?: number;
    job?: string;
    loveExp?: string;
    photo?: string;
    place?: string;
    reviewMessage?: string;
    reviewStatus?: number;
    thumbNum?: number;
    updateTime?: string;
    userId?: number;
    viewNum?: number;
  };

  type PostAddRequest = {
    age?: number;
    contact?: string;
    content?: string;
    education?: string;
    gender?: number;
    job?: string;
    loveExp?: string;
    photo?: string;
    place?: string;
  };

  type PostUpdateRequest = {
    age?: number;
    contact?: string;
    content?: string;
    education?: string;
    gender?: number;
    id?: number;
    job?: string;
    loveExp?: string;
    photo?: string;
    place?: string;
    reviewMessage?: string;
    reviewStatus?: number;
  };

  type User = {
    accessKey?: string;
    createTime?: string;
    gender?: number;
    id?: number;
    isDelete?: number;
    secretKey?: string;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userRole?: string;
  };

  type UserAddRequest = {
    gender?: number;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userRole?: string;
  };

  type UserInterfaceInfo = {
    createTime?: string;
    id?: number;
    interfaceInfoId?: number;
    isDelete?: number;
    leftNum?: number;
    status?: number;
    totalNum?: number;
    updateTime?: string;
    userId?: number;
  };

  type UserInterfaceInfoAddRequest = {
    interfaceInfoId?: number;
    leftNum?: number;
    status?: number;
    totalNum?: number;
    userId?: number;
  };

  type UserInterfaceInfoUpdateRequest = {
    id?: number;
    leftNum?: number;
    status?: number;
    totalNum?: number;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserRegisterRequest = {
    checkPassword?: string;
    userAccount?: string;
    userPassword?: string;
  };

  type UserUpdateRequest = {
    gender?: number;
    id?: number;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userRole?: string;
  };

  type UserVO = {
    createTime?: string;
    gender?: number;
    id?: number;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userRole?: string;
  };

  type View = {
    contentType?: string;
  };

  type NotificationInfoAddRequest = {
    user_id?: number; // 接收该通知的用户
    title?: string; // 通知的标题
    content?: string; // 通知的详细内容
    /**
     * 通知类型
     * 'system' 系统通知；'user' 用户通知；'warning'为警告；'error'重大错误
     */
    type?: string;
  };

  type GetNotificationInfoByIdUsingGETParams = {
    id?: number;
  };

  type BaseResponseNotificationInfo = {
    code?: number;
    data?: NotificationInfo;
    message?: string;
  };

  type NotificationInfo = {
    id?: number;
    user_id?: number;
    title?: string;
    content?: string;
    status?: string;
    type?: string;
    create_time?: Date;
    updated_at?: Date;
    isDelete?: number;
  };

  type GetNotificationInfoListUsingGETParams = {
    id?: number;
    user_id?: number;
    title?: string;
    content?: string;
    status?: string;
    type?: string;
    create_time?: Date;
    updated_at?: Date;
  };

  type BaseResponseNotificationInfoList = {
    code?: number;
    data?: NotificationInfo[];
    message?: string;
  };

  type BaseResponsePageNotificationInfo = {
    code?: number;
    data?: PageNotificationInfo;
    message?: string;
  };

  type PageNotificationInfo = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pageSize?: number;
    records?: NotificationInfo[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type listNotificationInfoByPageUsingGETParams = {
    current?: number;
    id?: number;
    pageSize?: number;
    user_id?: number;
    id?: number;
    user_id?: number;
    title?: string;
    content?: string;
    status?: string;
    type?: string;
    create_time?: Date;
    updated_at?: Date;
  };

  type UpdateNotificationStatusRequest = {
    id?: number;
  };

  type UserInterfaceInfoAndName = {
    id?: number;
    userId?: number; // 请求用户id
    interfaceInfoId?: number; // 接口id
    name?: string; // 接口名称
    description?: string; // 描述
    url?: string; // 接口地址
    totalNum?: number; // 总调用次数
    leftNum?: number; // 剩余调用次数
    status?: number; // 0-正常，1-禁用
    createTime?: Date; // 创建时间
    updateTime?: Date; // 更新时间
  };

  type FuelPackageInfoVo = {
    id?: number;
    amount: number; // 加油包含量
    price: number; // 价格
  };

  type BaseResponseListFuelPackageInfoVo = {
    code?: number;
    data?: FuelPackageInfoVo[];
    message?: string;
  }

}
