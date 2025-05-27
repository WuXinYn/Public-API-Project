package com.wxy.api.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxy.api.backend.annotation.AuthCheck;
import com.wxy.api.backend.service.PayService;
import com.wxy.api.common.model.dto.fuelpackage.FuelPackageAddRequest;
import com.wxy.api.common.model.dto.fuelpackage.FuelPackageInfoVo;
import com.wxy.api.common.model.dto.fuelpackage.FuelPackageQueryRequest;
import com.wxy.api.common.model.dto.fuelpackage.FuelPackageUpdateRequest;
import com.wxy.api.backend.service.FuelPackageService;
import com.wxy.api.backend.service.UserService;
import com.wxy.api.common.common.BaseResponse;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.common.ResultUtils;
import com.wxy.api.common.constant.CommonConstant;
import com.wxy.api.common.model.dto.userorderinfo.FuelPackageOrderGenerateRequest;
import com.wxy.api.common.model.entity.FuelPackage;
import com.wxy.api.common.model.entity.User;
import com.wxy.api.common.model.enums.PayEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * 加油包控制层
 */
@RestController
@RequestMapping("/fuel_package")
@Slf4j // 用作日志输出
public class FuelPackageController {

    @Resource
    private FuelPackageService fuelPackageService;

    @Resource
    private UserService userService;

    @Resource
    private PayService payService;

    /**
     * 购买加油包生成订单
     */
    @PostMapping("/order/pay")
    public BaseResponse<String> generateFuelPackageOrder(@Validated @RequestBody FuelPackageOrderGenerateRequest generateRequestMsg, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = generateRequestMsg.getUserId();
        Long interfaceInfoId = generateRequestMsg.getInterfaceId();
        Long fuelPackageId = generateRequestMsg.getFuelPackageId();
        Integer payMethod = generateRequestMsg.getPayMethod();
        if (!Objects.equals(payMethod, PayEnums.ALI_PAY.getCode())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR ,"支付方式错误，目前仅支持支付宝支付");
        }
        if (!Objects.equals(loginUser.getId(), userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加油包订单生成-登录用户与操作用户不一致");
        }
        String orderNumber = payService.generateFuelPackageOrderInfo(userId, interfaceInfoId, fuelPackageId, payMethod);
        if (orderNumber == null || orderNumber.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "加油包订单生成-生成订单失败");
        }
        return ResultUtils.success(orderNumber);
    }

    /**
     * 管理员新增加油包信息
     * @param fuelPackageAddRequest 加油包信息
     * @param request 请求
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addFuelPackage(@Validated @RequestBody FuelPackageAddRequest fuelPackageAddRequest, HttpServletRequest request) {
        if (fuelPackageAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (fuelPackageAddRequest.getName().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加油包名称不能为空");
        }
        FuelPackage fuelPackage = new FuelPackage();
        BeanUtils.copyProperties(fuelPackageAddRequest, fuelPackage);

        // 检验登录状态，拿到用户信息
        userService.getLoginUser(request);

        boolean save = fuelPackageService.save(fuelPackage);
        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "新增加油包事务失败");
        }
        return ResultUtils.success(fuelPackage.getId());
    }

    /**
     * 分页查询加油包信息
     */
    @GetMapping("/list")
    public BaseResponse<Page<FuelPackage>> listFuelPackage(FuelPackageQueryRequest fuelPackageQueryRequest, HttpServletRequest request) {
        FuelPackage fuelPackage = new FuelPackage();
        long current = 0;
        long size = 0;
        String sortField = "id";
        String sortOrder = "ascend";
        if (fuelPackageQueryRequest != null) {
            BeanUtils.copyProperties(fuelPackageQueryRequest, fuelPackage);
            // 当前页号,页面大小,排序字段,排序顺序（默认升序）
            current = fuelPackageQueryRequest.getCurrent();
            size = fuelPackageQueryRequest.getPageSize();
            sortField = fuelPackageQueryRequest.getSortField();
            sortOrder = fuelPackageQueryRequest.getSortOrder();
        }
        // description 需支持模糊搜索
        String description = fuelPackage.getDescription();
        fuelPackage.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // mybatis-plus的QueryWrapper
        QueryWrapper<FuelPackage> queryWrapper = new QueryWrapper<>(fuelPackage);
        // isNotBlank判断某字符串是否非空
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<FuelPackage> fuelPackagePage = fuelPackageService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(fuelPackagePage);
    }

    /**
     * 查询所有加油包信息
     */
    @GetMapping("/list/all")
    public BaseResponse<List<FuelPackageInfoVo>> listAllFuelPackage() {
        List<FuelPackage> list = fuelPackageService.list();
        List<FuelPackageInfoVo> listVo = list.stream().map(fuelPackage -> new FuelPackageInfoVo(fuelPackage.getId(), fuelPackage.getPrice(), fuelPackage.getAmount())).toList();
        return ResultUtils.success(listVo);
    }

    /**
     * 修改加油包信息
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateFuelPackage(@Validated @RequestBody FuelPackageUpdateRequest fuelPackageUpdateRequest) {
        FuelPackage fuelPackage = new FuelPackage();
        BeanUtils.copyProperties(fuelPackageUpdateRequest, fuelPackage);
        boolean update = fuelPackageService.updateById(fuelPackage);
        return ResultUtils.success(update);
    }

    /**
     * 删除加油包信息
     */
    @DeleteMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteFuelPackage(@RequestParam(value = "id") long id) {
        boolean remove = fuelPackageService.removeById(id);
        return ResultUtils.success(remove);
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteBatch(@RequestParam(value = "ids") List<Long> ids) {
        boolean remove = fuelPackageService.removeByIds(ids);
        return ResultUtils.success(remove);
    }
}
