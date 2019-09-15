package io.aitos.hackathon.dao;

import io.aitos.hackathon.pojo.VehicleStatus;

import java.util.List;
import java.util.Map;

/**
 * @author apple
 */
public interface OrderCarDao {
    /**
     * 可用车辆
     *
     * @return
     */
    List<Map<String, Object>> availableVehicle();
    /**
     * 将数据添加到vehicle_status
     *
     * @param vehicleStatus
     */
    void addVehicleStatus(VehicleStatus vehicleStatus);

    /**
     * 根据xID查询vinNumber
     * @param xId
     * @return
     */
    Map<String, Object> queryVinNumber(String xId);

    /**
     * 修改车辆状态
     * @param vinNumber
     * @param vehicleStatus
     */
    void updateVehicleStatus(String vinNumber,String vehicleStatus);

    /**
     * 用户和用车的状态对应
     * @param xId
     * @param status
     * @param startDate
     * @param stage
     */
    void updateUseCar(String xId,String status,String startDate,String stage);


    void blackBoxOperation();


}
