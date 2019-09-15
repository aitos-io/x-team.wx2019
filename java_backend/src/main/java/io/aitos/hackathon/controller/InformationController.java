package io.aitos.hackathon.controller;

import io.aitos.hackathon.blockchain.CarServiceUtils;
import io.aitos.hackathon.service.InformationService;
import io.aitos.hackathon.utils.ByteUtils;
import io.aitos.hackathon.utils.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Hash;

import java.util.List;
import java.util.Map;

/**
 * @author apple
 */
@RestController
@RequestMapping("/informationController")
public class InformationController {

    @Autowired
    InformationService informationService;

    /**
     * 查询用户信息
     * @return
     */
    @RequestMapping(value = "/queryUserInfo",method = {RequestMethod.GET,RequestMethod.POST})
    public List<Map<String,Object>> queryUserInfo(){
        return informationService.queryUserInfo();
    }

    /**
     * 查询车辆信息
     * @return
     */
    @RequestMapping(value = "/queryCarInfo",method = {RequestMethod.GET,RequestMethod.POST})
    public List<Map<String,Object>> queryCarInfo(){
        return informationService.queryCarInfo();
    }

    /**
     * 查询合同信息
     * @return
     */
    @RequestMapping(value = "/queryContractInfo",method = {RequestMethod.GET,RequestMethod.POST})
    public List<Map<String,Object>> queryContractInfo(){
        List<Map<String,Object>> list=informationService.queryContractInfo();
        for (int i=0;i<list.size();i++){
            Map<String,Object> map=list.get(i);
            String xId=(String)map.get("xId");
            map.put("xId",xId);
            byte[] hashedXid = Hash.sha3(xId.getBytes());
            String hashedXidStr = HexUtils.bytesToHexString(hashedXid);
            map.put("hashId",hashedXidStr);
        }
        return list;
    }

    /**
     * 查询行车记录信息
     * @return
     */
    @RequestMapping(value = "/queryFormalRecord",method = {RequestMethod.GET})
    public List<Map<String,Object>> queryFormalRecord(String xId){
        List<Map<String,Object>> list=informationService.queryFormalRecord(xId);

        for (int i=0;i<list.size();i++){
            Map<String,Object> map=list.get(i);
            String xID=(String)map.get("xId");
            byte[] hashedXid = Hash.sha3(xID.getBytes());
            String hashedXidStr = HexUtils.bytesToHexString(hashedXid);
            map.put("xId",hashedXidStr);
        }

        return list;
    }
}
