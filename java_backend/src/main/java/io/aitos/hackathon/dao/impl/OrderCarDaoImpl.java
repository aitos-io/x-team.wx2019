package io.aitos.hackathon.dao.impl;

import io.aitos.hackathon.dao.OrderCarDao;
import io.aitos.hackathon.pojo.VehicleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author apple
 */
@Repository
public class OrderCarDaoImpl implements OrderCarDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> availableVehicle() {
        String status = "0";
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from vehicle_information where vehicleStatus=?", new Object[]{status});
        return list;
    }

    /**
     * 将数据添加到vehicle_status
     *
     * @param vehicleStatus
     */
    @Override
    public void addVehicleStatus(VehicleStatus vehicleStatus) {
        String xID = vehicleStatus.getXId();
        String gps = vehicleStatus.getGps();
        long createTime = vehicleStatus.getCreateTime();
        String insertSql = "insert into vehicle_status values('" + xID + "','" + gps + "','" + createTime + "')";
        System.out.println("添加数据:"+insertSql);
        jdbcTemplate.update(insertSql);
    }

    @Override
    public void updateUseCar(String xId, String status, String updateTime, String stage) {
        if (stage != null && stage.equals("使用中")) {
            //user_ vehicle表status改为2
            String updateStatus = "update user_vehicle set status=?,startTime=? where xId=?";
            jdbcTemplate.update(updateStatus, new Object[]{status, updateTime, xId});
        } else {
            //user_ vehicle表status改为2
            String updateStatus = "update user_vehicle set status=?,stopTime=? where xId=?";
            jdbcTemplate.update(updateStatus, new Object[]{status, updateTime, xId});
        }

    }

    @Override
    public Map<String, Object> queryVinNumber(String xId) {
        String querySql = "select vinNumber from user_vehicle where xId=?";
        Map<String, Object> result = jdbcTemplate.queryForMap(querySql, xId);
        return result;
    }

    @Override
    public void updateVehicleStatus(String vinNumber, String vehicleStatus) {
        String updateSql = "update vehicle_information set vehicleStatus=? where vinNumber=?";
        jdbcTemplate.update(updateSql, new Object[]{vehicleStatus, vinNumber});
    }

    @Override
    public void blackBoxOperation() {
        String updateSql = "update vehicle_information set vehicleStatus=?";
        String status = "0";
        String deleteSql = "delete from user_vehicle";
        String deleteStatusSql = "delete from vehicle_status";
        String deleteUser = "delete from user_information";
        jdbcTemplate.update(updateSql, status);
        jdbcTemplate.update(deleteSql);
        jdbcTemplate.update(deleteStatusSql);
        jdbcTemplate.update(deleteUser);
    }

}
