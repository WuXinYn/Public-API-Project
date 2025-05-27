package com.wxy.api.backend.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.wxy.api.backend.annotation.AuthCheck;
import com.wxy.api.backend.service.InterfaceInfoService;
import com.wxy.api.backend.service.UserInterfaceInfoService;
import com.wxy.api.backend.service.UserService;
import com.wxy.api.common.common.*;
import com.wxy.api.common.constant.CommonConstant;
import com.wxy.api.common.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.wxy.api.common.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.wxy.api.common.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.wxy.api.common.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.wxy.api.common.model.entity.InterfaceInfo;
import com.wxy.api.common.model.entity.User;
import com.wxy.api.common.model.entity.UserInterfaceInfo;
import com.wxy.api.common.model.enums.HeaderNameType;
import com.wxy.api.common.model.enums.InterfaceInfoStatusEnum;
import com.wxy.api.common.model.enums.UserRoleEnum;
import com.wxy.api.common.service.InnerRedisService;
import com.wxy.api.sdk.client.ApiClient;
import com.wxy.api.sdk.model.BaseRequire;
import com.wxy.api.sdk.utils.CusAccessObjectUtil;
import com.wxy.api.sdk.utils.IPConversion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

/**
 * 接口管理
 *
 * @author wxy
 */
@RestController // 每个方法的返回值都会以 JSON 或 XML 的形式直接写入 HTTP 响应体中，相当于在每个方法上都添加了 @ResponseBody 注解。
@RequestMapping("/interfaceInfo")
@Slf4j // 用作日志输出
public class InterfaceInfoController
{

    @Resource //byName来实现依赖注入的
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @DubboReference
    private InnerRedisService innerRedisService;

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest 新增接口请求信息
     * @param request                 请求
     * @return 通用返回类
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@Validated @RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request)
    {
        // 参数校验
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新增接口请求参数为空");
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        // 浅拷贝，类中都是单一的属性
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        // 检验登录状态，拿到用户信息
        User loginUser = userService.getLoginUser(request);
        // 系统赋值用户id传参
        interfaceInfo.setUserID(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "新增接口事务失败");
        }
        long newInterfaceId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceId);
    }

    /**
     * 删除
     *
     * @param deleteRequest 删除请求
     * @param request       请求
     * @return 通用返回类
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request)
    {
        if (deleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserID().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean remove = interfaceInfoService.removeById(id);
        if (!remove) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口删除事务失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateRequest 更新接口请求信息
     * @param request                    请求
     * @return 通用返回类
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@Validated @RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, HttpServletRequest request)
    {
        if (interfaceInfoUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        // 验证登录状态
        User user = userService.getLoginUser(request);
        // 判断是否存在
        long id = interfaceInfoUpdateRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserID().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口更新事务失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取
     *
     * @param id 接口ID
     * @return 通用返回类
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id)
    {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口获取事务失败 或者 该接口不存在");
        }
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest 查询请求
     * @return 通用返回类
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest)
    {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        // QueryWrapper用于构建查询条件，可以通过链式调用的方式组装各种查询条件
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceList);
    }

    /**
     * 获取列表
     *
     * @param interfaceInfoQueryRequest 查询请求
     * @return 通用返回类
     */
    @GetMapping("/list_by_user")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfoByUser(InterfaceInfoQueryRequest interfaceInfoQueryRequest)
    {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.eq("status", 1);
        List<InterfaceInfo> interfaceList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest 查询请求
     * @param request                   请求
     * @return 通用返回类
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request)
    {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        long current = 0;
        long size = 0;
        String sortField = null;
        String sortOrder = "ascend";
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
            // 当前页号,页面大小,排序字段,排序顺序（默认升序）
            current = interfaceInfoQueryRequest.getCurrent();
            size = interfaceInfoQueryRequest.getPageSize();
            sortField = interfaceInfoQueryRequest.getSortField();
            sortOrder = interfaceInfoQueryRequest.getSortOrder();
        }
        // description 需支持模糊搜索
        String description = interfaceInfoQuery.getDescription();
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // mybatis-plus的QueryWrapper
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        // isNotBlank判断某字符串是否非空
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                             sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        // 判断用户权限
        User user = userService.getLoginUser(request);
        if (UserRoleEnum.User.getValue().equals(user.getUserRole())) {
            queryWrapper.eq("status", 1);
        }
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 发布
     *
     * @param idRequest id请求
     * @param request   请求
     * @return 通用返回类
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IDRequest idRequest,
                                                     HttpServletRequest request)
    {
        // 判断参数是否为空
        if (idRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验该接口是否存在
        long id = idRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 通过接口地址访问来判断该接口是否可以调用
        InterfaceInfoInvokeRequest interfaceInfoInvokeRequest = InterfaceInfoInvokeRequest.builder()
                .id(id)
                .isTest(true)  // 直接在请求参数中传递
                .userRequestParams(oldInterfaceInfo.getRequestParams())
                .build();

        BaseResponse<Object> objectBaseResponse = this.invokeInterfaceInfo(interfaceInfoInvokeRequest, request);

        if (objectBaseResponse.getData() != null) {
            String data = objectBaseResponse.getData().toString();
            if (data.contains("接口服务不可用，请稍后重试")) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, data);
            }
        }
        log.info("接口验证成功！！！,{}", objectBaseResponse);

        // 修改接口数据库中的状态为1
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id); // 请求参数中拿到的接口id
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口修改状态事务失败");
        }

        return ResultUtils.success(true);
    }

    /**
     * 下线
     *
     * @param idRequest id请求
     * @param request   请求
     * @return 通用返回类
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> offlineInterfaceInfo(@Validated @RequestBody IDRequest idRequest, HttpServletRequest request)
    {
        // 判断参数是否为空
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验该接口是否存在
        long id = idRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该接口不存在");
        }

        // 修改接口数据库中的状态为1
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id); // 请求参数中拿到的接口id
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口修改状态事务失败");
        }

        return ResultUtils.success(true);
    }

    /**
     * 测试调用
     *
     * @param interfaceInfoInvokeRequest 接口调用请求
     * @param request                    请求
     * @return 通用返回类
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                    HttpServletRequest request) {
        // 判断参数是否为空
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断接口是否存在
        long interfaceInfoId = interfaceInfoInvokeRequest.getId();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(interfaceInfoId);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "被调用接口不存在");
        }
        // 判断接口的状态是否开启（发布时不用判断）
        if (interfaceInfoInvokeRequest.getIsTest() == Boolean.FALSE && oldInterfaceInfo.getStatus() != InterfaceInfoStatusEnum.ONLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }
        // 拿到用户的密钥
        User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        ApiClient tempClient = new ApiClient(accessKey, secretKey);
        // 判断是否是新用户或者新接口，如果则需要在用户接口表新增一条记录
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.checkIsNewUser(interfaceInfoId, userId);
        if (userInterfaceInfo == null) {
            userInterfaceInfoService.addNewDetail(interfaceInfoId, userId);
        }
        else {
            // 判断接口剩余的调用次数(管理员无次数限制)
            int leftNum = userInterfaceInfo.getLeftNum();
            if (leftNum <= 0 && !Objects.equals(loginUser.getUserRole(), UserRoleEnum.Admin.getValue())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口剩余调用次数不足");
            }
        }
        // 测试调用
        String url = oldInterfaceInfo.getUrl();
        JSONObject userParams = JSON.parseObject(userRequestParams, JSONObject.class);
        String method = oldInterfaceInfo.getMethod();
        BaseRequire<JSONObject> stringBaseRequire = new BaseRequire<>(url, userParams);
        String ipV6 = CusAccessObjectUtil.getClientIpFromHttpServletRequest(request); // 拿到实际来源IP(IPv6)（防代理）
        String ipV4;
        try {
            ipV4 = IPConversion.convertIPv6ToIPv4(ipV6); // 转化为IPv4显示
        }
        catch (Exception ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, ex.getMessage());
        }
        String nonceKey = ipV4 + "-" + url + "-" + method + "-" + HeaderNameType.Nonce.getHeaderName() + "-from-" + accessKey;
        // todo 随机数的生成存储逻辑应该放在SDK里面
        String nonce = RandomUtil.randomNumbers(8); // 随机数，防止重发
        innerRedisService.setParams(nonceKey, nonce);
        int index = StringUtils.ordinalIndexOf(url, "/", 3);
        String host = url.substring(0, index);
        innerRedisService.addIPInvokeNumber(host); // IP调用接口次数+1
        String result = tempClient.handle(stringBaseRequire, method, nonce);
        if (result == null) {
            throw new BusinessException(ErrorCode.Request_ERROR, "返回数据为null");
        }
        return ResultUtils.success(result);
    }
}
