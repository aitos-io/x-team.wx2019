package io.aitos.hackathon.service.impl;

import io.aitos.hackathon.blockchain.CarService;
import io.aitos.hackathon.blockchain.CarServiceUtils;
import io.aitos.hackathon.dao.InformationDao;
import io.aitos.hackathon.service.InformationService;
import io.aitos.hackathon.utils.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Hash;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.*;

/**
 * @author apple
 */
@Service
public class InformationServiceImpl implements InformationService {

    @Autowired
    InformationDao informationDao;

    @Autowired
    CarServiceUtils carServiceUtils;

    @Override
    public List<Map<String, Object>> queryUserInfo() {
        List<Map<String, Object>> list = informationDao.queryUserInfo();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            String str = (String) map.get("identityNumber");
            int len = str.length();
            String replaceStr = str.replaceAll("(.{" + (len < 12 ? 3 : 6) + "})(.*)(.{4})", "$1" + "****" + "$3");
            map.put("identityNumber", replaceStr);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> queryCarInfo() {
        return informationDao.queryCarInfo();
    }

    @Override
    public List<Map<String, Object>> queryContractInfo() {
        return informationDao.queryContractInfo();
    }

    @Override
    public List<Map<String, Object>> queryFormalRecord(String xId) {
        List<Map<String, Object>> listFormalRecord = informationDao.queryFormalRecord(xId);
        CarService carService = carServiceUtils.getCarService();
        List<String> listLoc = carServiceUtils.readLocLists(carService, xId);

        List<Map<String, Object>> returnValue = new ArrayList<>();


        for (int i = 0; i < listFormalRecord.size(); i++) {
            Map<String, Object> map = listFormalRecord.get(i);
            String gps = (String) map.get("gps");
            long time = (long) map.get("createTime");

            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date date = new Date(time);
            String str = sdf.format(date);

            String uInfo = time + ";" + gps;
            byte[] hashedUinfoBytes = Hash.sha3(uInfo.getBytes());
            String hashedInfo = HexUtils.bytesToHexString(hashedUinfoBytes);
            String hashedLocInfo = listLoc.get(i);

            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("xId", map.get("xId"));
            returnMap.put("gps", map.get("gps"));
            returnMap.put("createTime", str);
            returnMap.put("hashedInfo", hashedInfo);
            returnMap.put("hashedLocInfo", hashedLocInfo);

            if (hashedInfo.equals(hashedLocInfo)) {
                returnMap.put("judgment", true);
            } else {
                returnMap.put("judgment", false);
            }

            returnValue.add(returnMap);
        }

        return returnValue;
    }
}
