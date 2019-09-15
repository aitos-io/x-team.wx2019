package io.aitos.hackathon.dao.impl;

import io.aitos.hackathon.dao.ExternalInterfaceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ExternalInterfaceImpl implements ExternalInterfaceDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> queryVehicleXId() {
        String querySql = "select * from user_vehicle where status=?";
        String status = "2";
        List<Map<String, Object>> queryXId = jdbcTemplate.queryForList(querySql, status);
        return queryXId;
    }
}
