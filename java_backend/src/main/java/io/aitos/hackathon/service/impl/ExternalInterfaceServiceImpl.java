package io.aitos.hackathon.service.impl;

import io.aitos.hackathon.dao.ExternalInterfaceDao;
import io.aitos.hackathon.service.ExternalInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExternalInterfaceServiceImpl implements ExternalInterfaceService {

    @Autowired
    ExternalInterfaceDao externalInterfaceDao;

//    private static final String HEX_PREFIX = "0x";

    @Override
    public String queryVehicleXId() {
        List<Map<String, Object>> queryList = externalInterfaceDao.queryVehicleXId();
        if (queryList.size() > 0) {
            Map<String, Object> queryMap = queryList.get(0);
            String xId = (String) queryMap.get("xId");
//            byte[] hashedXidByte = Hash.sha3(xId.getBytes());
//            System.out.println("字节数组的长度========>"+hashedXidByte.length);
//            String hashedXidString=HEX_PREFIX+HexUtils.bytesToHexString(hashedXidByte);
            System.out.println("返回xId================>"+xId);
            return xId;
        }
        return "error";
    }
}
