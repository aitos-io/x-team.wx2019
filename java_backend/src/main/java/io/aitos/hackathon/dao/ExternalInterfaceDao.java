package io.aitos.hackathon.dao;

import java.util.List;
import java.util.Map;

public interface ExternalInterfaceDao {

    /**
     * 查询xId
     * @return
     */
    List<Map<String, Object>> queryVehicleXId();
}
