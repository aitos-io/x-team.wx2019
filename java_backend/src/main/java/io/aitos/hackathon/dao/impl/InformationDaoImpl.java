package io.aitos.hackathon.dao.impl;

import io.aitos.hackathon.dao.InformationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author apple
 */
@Repository
public class InformationDaoImpl implements InformationDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> queryUserInfo() {
        String querySql="select * from user_information";
        List<Map<String,Object>> list=jdbcTemplate.queryForList(querySql);
        return list;
    }

    @Override
    public List<Map<String, Object>> queryCarInfo() {
        String querySql="select * from vehicle_information";
        List<Map<String,Object>> list=jdbcTemplate.queryForList(querySql);
        return list;
    }

    @Override
    public List<Map<String, Object>> queryContractInfo() {
        String querySql="select xId,bookTime,startTime,stopTime,status from user_vehicle order by bookTime desc ";
        List<Map<String,Object>> list=jdbcTemplate.queryForList(querySql);
        return list;
    }

    @Override
    public List<Map<String, Object>> queryFormalRecord(String xId) {
        String querySql="select * from vehicle_status where xId=?";
        List<Map<String,Object>> list=jdbcTemplate.queryForList(querySql,xId);
        return list;
    }
}
