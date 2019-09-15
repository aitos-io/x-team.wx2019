package io.aitos.hackathon.service;

import java.util.List;
import java.util.Map;

/**
 * @author apple
 */
public interface InformationService {
    /**
     * 用户信息查询
     * @return
     */
    List<Map<String,Object>> queryUserInfo();

    /**
     * 查询车辆信息
     * @return
     */
    List<Map<String,Object>> queryCarInfo();


    /**
     * 查看合同信息
     * @return
     */
    List<Map<String,Object>> queryContractInfo();

    /**
     * 查询行车记录信息
     * @return
     */
    List<Map<String,Object>> queryFormalRecord(String xId);
}
