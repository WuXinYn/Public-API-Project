package com.wxy.api.backend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wxy.api.backend.mapper.InterfaceInfoMapper;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.model.entity.InterfaceInfo;
import com.wxy.api.common.model.enums.InterfaceInfoStatusEnum;
import com.wxy.api.common.service.InnerInterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 内部接口Service
 *
 */
@DubboService
@Slf4j
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService
{
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    /**
     * 查询接口
     *
     * @param url 访问接口地址
     * @param method 访问方式
     * @return 接口信息
     */
    @Override
//    @Cacheable(value = "interface_info") // 缓存
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url",url);
        queryWrapper.eq("method",method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }

    /**
     * 查询所有接口信息(排除逻辑删除和已经下线的接口)
     */
    @Override
    public List<InterfaceInfo> getAllInterfaceInfoInfo(){
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", InterfaceInfoStatusEnum.ONLINE.getValue());
        return interfaceInfoMapper.selectList(queryWrapper);
    }

    /**
     * 下线异常接口
     */
    @Override
    public boolean offlineInterface(long interfaceId){
        UpdateWrapper<InterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", interfaceId);
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.Abnormal.getValue());
        log.error("接口{} 出现异常! ", interfaceId);
        return interfaceInfoMapper.update(interfaceInfo, updateWrapper) == 1;
    }

    /**
     * 给接口状态修改为正常
     */
    @Override
    public boolean onlineInterface(long interfaceId) {
        UpdateWrapper<InterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", interfaceId);
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        log.error("接口{} 已经重新上线！", interfaceId);
        return interfaceInfoMapper.update(interfaceInfo, updateWrapper) == 1;
    }

    /**
     * 判断接口是否在线
     *
     * @param item 接口信息
     * @return 接口在线-True，否则-False
     */
    @Override
    public boolean isInterfaceOnline(InterfaceInfo item)
    {
        String url = item.getUrl();
        String method = item.getMethod();
        String requestParams = item.getRequestParams();
        try {
            URL endpoint = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(5000); // 设置连接超时时间
            connection.setReadTimeout(5000);    // 设置读取超时时间
            connection.setRequestProperty("Content-Type", "application/json");
            // Json字符串：{\n\t"x":1\n}
            if (requestParams.length() >= 11) {
                connection.setRequestProperty("Content-Length", String.valueOf(requestParams.getBytes().length));
                connection.setDoOutput(true); // 允许向服务器写入写出数据
                connection.setDoInput(true);
                OutputStream outputStream = connection.getOutputStream(); // 获取输出流，向服务器写入数据
                outputStream.write(requestParams.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            connection.connect();

            // 获取响应码，判断请求是否成功
            int responseCode = connection.getResponseCode();
            log.info("接口：{}:{} 的responseCode：{}", url, method, responseCode);
            if (responseCode == 200) { // 判断状态码是否为 200
                return true;
            }
        }
        catch (IOException e) {
            return false; // 出现异常，认为接口不在线
        }
        return false; // 出现异常，认为接口不在线
    }


}
