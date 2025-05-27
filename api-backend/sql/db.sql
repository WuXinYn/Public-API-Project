-- 用户表
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userName     varchar(256)                                      null comment '用户昵称',
    userAccount  varchar(256)                                      not null comment '账号',
    userAvatar   varchar(1024) default 'https://yupi.icu/logo.png' null comment '用户头像',
    gender       tinyint       default 0                           not null comment '性别(0-男，1-女)',
    userRole     varchar(256)  default 'user'                      not null comment '用户角色：user / admin',
    userPassword varchar(512)                                      not null comment '密码',
    createTime   datetime      default CURRENT_TIMESTAMP           not null comment '创建时间',
    updateTime   datetime      default CURRENT_TIMESTAMP           not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint       default 0                           not null comment '是否删除',
    accessKey    varchar(512)                                      not null comment 'accessKey',
    secretKey    varchar(512)                                      not null comment 'secretKey',
    constraint uni_userAccount
        unique (userAccount)
)
    comment '用户';

create index user_accessKey_index
    on user (accessKey);

create index user_isDelete_index
    on user (isDelete);

-- 用户调用接口信息表
create table user_interface_info
(
    id              bigint auto_increment comment '主键'
        primary key,
    userId          bigint                             not null comment '请求用户id',
    interfaceInfoId bigint                             not null comment '接口id',
    totalNum        int      default 0                 not null comment '总调用次数',
    leftNum         int      default 50                not null comment '剩余调用次数',
    status          int      default 0                 not null comment '0-正常，1-禁用',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是在删除(0-未删，1-已删)'
)
    comment '用户调用接口关系';

create index user_interface_info_interfaceInfoId_index
    on user_interface_info (interfaceInfoId);

create index user_interface_info_isDelete_index
    on user_interface_info (isDelete);

create index user_interface_info_userId_index
    on user_interface_info (userId);

-- 通知信息表
create table notification_info
(
    id         bigint auto_increment comment '通知的唯一标识符'
        primary key,
    user_id    bigint                                 not null comment '接收该通知的用户,0表示所有（具体情况根据业务逻辑区分是所有管理员还是用户）',
    title      varchar(255)                           not null comment '通知的标题',
    content    text                                   not null comment '通知的详细内容',
    status     varchar(255) default 'unread'          not null comment '通知的状态：
unread已读
read未读
treatment处理中
timeout失效
resolved已解决
error重大错误
warning警告，处理时长已经超过24h
',
    created_at timestamp    default CURRENT_TIMESTAMP not null comment '通知的创建时间',
    updated_at timestamp    default CURRENT_TIMESTAMP not null comment '通知的更新时间',
    type       varchar(255) default 'system'          not null comment '通知类型：
''system'' 为系统通知
''user'' 为用户通知
''warning''为警告
''error''重大错误',
    isDelete   int          default 0                 not null comment '软删除标志，0表示未删除，1表示已删除',
    constraint notification_info_id_uindex
        unique (id)
)
    comment '通知信息';

create index notification_info_isDelete_index
    on notification_info (isDelete);

create index notification_info_status_index
    on notification_info (status);

create index notification_info_user_id_index
    on notification_info (user_id);

-- 封禁信息表
create table login_attempt_log
(
    id           int auto_increment
        primary key,
    userAccount  varchar(255)                       not null comment '用户账号',
    ipAddress    varchar(255)                       not null comment '用户登录IP',
    attempt_time datetime default CURRENT_TIMESTAMP null comment '操作时间',
    constraint login_attempt_log_id_uindex
        unique (id)
)
    comment '账号IP封禁信息';

create index login_attempt_log_userAccount_index
    on login_attempt_log (userAccount);

-- 接口信息表
create table interface_info
(
    id             bigint auto_increment comment '主键'
        primary key,
    name           varchar(256)                           not null comment '接口名称',
    description    varchar(256)                           null comment '描述',
    url            varchar(512)                           not null comment '接口地址',
    requestHeader  text                                   null comment '请求头',
    responseHeader text                                   null comment '响应头',
    status         int          default 0                 not null comment '接口状态（0-关闭，1-开启）',
    method         varchar(256)                           not null comment '请求类型',
    userID         bigint                                 not null comment '创建人',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除(0-未删, 1-已删)',
    requestParams  varchar(255) default '{ }'             null comment '请求参数, 默认为空-''{ }'''
)
    comment '接口信息';

create index interface_info_isDelete_index
    on interface_info (isDelete);

create index interface_info_status_index
    on interface_info (status);

-- 加油包信息表
create table fuel_package
(
    id           bigint auto_increment comment '加油包ID'
        primary key,
    name         varchar(255)   default ''                not null comment '加油包名称',
    description  varchar(255)                             null comment '加油包描述',
    price        decimal(10, 2) default 0.00              not null comment '价格',
    amount       int            default 1000              not null comment '加油包含量',
    number       int            default 1                 not null comment '数量',
    created_time datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint        default 0                 not null comment '逻辑删除（0：未删；1：已删）'
)
    comment '加油包表';

create index fuel_package_isDelete_index
    on fuel_package (isDelete);

-- 用户购买套餐记录表
create table user_order_info
(
    id              bigint auto_increment comment '记录编号'
        primary key,
    order_number    varchar(255)   default ''                not null comment '订单编号',
    user_id         bigint                                   not null comment '用户id',
    interface_id    bigint                                   not null comment '接口id',
    set_menu_id     int                                      not null comment '套餐id',
    set_menu_name   varchar(255)   default ''                null comment '套餐名称',
    set_menu_number int            default 0                 not null comment '加油包含量',
    number          int            default 1                 not null comment '数量',
    payment_number  decimal(10, 2) default 0.00              not null comment '支付金额',
    create_time     datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    payment_method  tinyint        default 0                 not null comment '支付方式(0：微信；1：支付宝)',
    order_status    tinyint        default 0                 not null comment '订单状态(0：待支付、1：已支付、2：已取消、3：支付失败)',
    pay_time        datetime                                 null comment '支付时间',
    is_normal       tinyint        default 0                 not null comment '是否生效(0：未生效；1：生效)',
    expired_time    datetime                                 null comment '到期时间',
    isDelete        tinyint        default 0                 not null comment '逻辑删除（0：未删；1：已删）',
    constraint user_menu_info_pk
        unique (order_number)
)
    comment '用户购买套餐记录';

create index user_order_info_isDelete_index
    on user_order_info (isDelete);

create index user_order_info_user_id_index
    on user_order_info (user_id);




