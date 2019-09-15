package io.aitos.hackathon.service.impl;

import io.aitos.hackathon.blockchain.CarService;
import io.aitos.hackathon.blockchain.CarServiceUtils;
import io.aitos.hackathon.blockchain.CarStatus;
import io.aitos.hackathon.dao.OrderCarDao;
import io.aitos.hackathon.pojo.*;
import io.aitos.hackathon.service.OrderCarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author apple
 * @Description: 身份认证接口
 */
@Service
public class OrderCarServiceImpl implements OrderCarService {
    private static final Logger logger = LoggerFactory.getLogger(OrderCarServiceImpl.class);

    @Autowired
    OrderCarDao orderCarDao;

    @Autowired
    CarServiceUtils carServiceUtils;

    @Override
    public List<Map<String, Object>> availableVehicle() {
        return orderCarDao.availableVehicle();
    }

    @Override
    public String startUseCar(String xId, String tBox) {
        Map<String, Object> map = orderCarDao.queryVinNumber(xId);
        String vinNumber = (String) map.get("vinNumber");
        if (vinNumber != null && !vinNumber.equals("")) {
            //1、vehicle_information表中的vehicleStatus改成2
            String vehicleStatus = "2";
            orderCarDao.updateVehicleStatus(vinNumber, vehicleStatus);
            //2、user_vehicle表status改为2
            String status = "2";
            Date nowDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String updateTime = sdf.format(nowDate);
            orderCarDao.updateUseCar(xId, status, updateTime, "使用中");

            if ("00".equals(tBox)) {
                long carTime = nowDate.getTime();
                CarService carService = carServiceUtils.getCarService();
                carServiceUtils.updateStatus(carService, xId, CarStatus.INUSE, carTime);

                uploadData(xId, carService);
            }
            return "2";
        }
        return null;
    }

    @Override
    public String stopUseCar(String xId) {
        Map<String, Object> map = orderCarDao.queryVinNumber(xId);
        String vinNumber = (String) map.get("vinNumber");
        if (vinNumber != null && !vinNumber.equals("")) {
            Date nowDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String updateTime = sdf.format(nowDate);
            //1、vehicle_information表中的vehicleStatus改成0
            String vehicleStatus = "0";
            orderCarDao.updateVehicleStatus(vinNumber, vehicleStatus);
            //2、user_vehicle表status改为3
            String status = "3";
            orderCarDao.updateUseCar(xId, status, updateTime, "已还车");

            Long carTime = nowDate.getTime();
            CarService carService = carServiceUtils.getCarService();
            carServiceUtils.updateStatus(carService, xId, CarStatus.COMPLETED, carTime);

            return "3";
        }
        return null;
    }

    @Override
    public void blackBoxOperation() {
        orderCarDao.blackBoxOperation();
    }

    @Override
    public void updateBoxLoc(String xId, String[] tBox) {
        System.out.println("得到xId"+xId);
        Map<String, Object> map = orderCarDao.queryVinNumber(xId);
        String vinNumber = (String) map.get("vinNumber");
        if (vinNumber != null && !vinNumber.equals("")) {
            //1、vehicle_information表中的vehicleStatus改成2
            String statusValue = "2";
            orderCarDao.updateVehicleStatus(vinNumber, statusValue);
            //2、user_vehicle表status改为2
            String status = "2";
            Date nowDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String updateTime = sdf.format(nowDate);
            orderCarDao.updateUseCar(xId, status, updateTime, "使用中");


            String time = tBox[0];
            String loc = tBox[1];
            Long createTime = Long.parseLong(time);
            Date createDate = new Date(createTime);
            logger.info("位置信息:"+createDate.toLocaleString());

            VehicleStatus vehicleStatus = new VehicleStatus();
            vehicleStatus.setXId(xId);
            vehicleStatus.setCreateTime(createDate.getTime());
            vehicleStatus.setGps(loc);

            orderCarDao.addVehicleStatus(vehicleStatus);

        }
    }

    /**
     * 上传数据到区块链
     *
     * @param xId
     * @param carService
     */
    public void uploadData(String xId, CarService carService) {
        String[] locations = new String[1];

        long time = System.currentTimeMillis();
        int count = 0;
        for (int i = 0; i < locations.length && count < 10; i = i + 2) {
            String loc = locations[i];
            time += 5000;

            VehicleStatus vehicleStatus = new VehicleStatus();
            vehicleStatus.setXId(xId);
            Date nowDate = new Date(time);
            vehicleStatus.setCreateTime(nowDate.getTime());
            vehicleStatus.setGps(loc);
            orderCarDao.addVehicleStatus(vehicleStatus);

            carServiceUtils.updateCarLocation(carService, xId, time, loc);
            count++;
        }
    }
}
