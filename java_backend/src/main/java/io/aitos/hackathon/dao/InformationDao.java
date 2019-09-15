package io.aitos.hackathon.dao;

import java.util.List;
import java.util.Map;

/**
 * @author apple
 */
public interface InformationDao {

    /**
     * 查询用户信息
     * @return
     */
    List<Map<String, Object>> queryUserInfo();

    /**
     * 查询车辆信息
     * @return
     */
    List<Map<String, Object>> queryCarInfo();


    /**
     * 查看合同状态
     * @return
     */
    List<Map<String, Object>> queryContractInfo();

    /**
     * 查询行车记录
     * @param xId
     * @return
     */
    List<Map<String, Object>> queryFormalRecord(String xId);

}
