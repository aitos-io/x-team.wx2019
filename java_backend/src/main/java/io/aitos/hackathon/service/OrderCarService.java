package io.aitos.hackathon.service;


import io.aitos.hackathon.pojo.CarContract;

import java.util.List;
import java.util.Map;

/**
 * @author apple
 * @Description: 用户进行订车
 */
public interface OrderCarService {

    /**
     * 可用车辆
     *
     * @return
     */
    List<Map<String, Object>> availableVehicle();

    /**
     * 用户开始用车
     * @param xId
     * @return
     */
    String startUseCar(String xId,String tBox);

    /**
     * 用户还车
     * @param xId
     * @return
     */
    String stopUseCar(String xId);

    /**
     * 暗箱操作
     */
    void blackBoxOperation();

    /**
     * tBox修改位置信息
     * @param xId
     * @param tBox
     * @return
     */
    void updateBoxLoc(String xId, String[] tBox);
}
