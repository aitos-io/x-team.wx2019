package io.aitos.hackathon.controller;

import io.aitos.hackathon.pojo.CarContract;
import io.aitos.hackathon.pojo.TBox;
import io.aitos.hackathon.service.OrderCarService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author apple
 * @Description: 用户预定车
 */
@Controller
@RequestMapping("/orderCar")
public class OrderCarController {

    private static final Logger logger = LoggerFactory.getLogger(OrderCarController.class);

    @Autowired
    OrderCarService orderCarService;

    /**
     * 可用车辆
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/availableVehicle", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> availableVehicle() {
        try {
            List<Map<String, Object>> list = orderCarService.availableVehicle();
            return list.get(0);
        } catch (Exception e) {
            logger.error("查询可用车辆出现异常");
        }
        return null;
    }

    /**
     * 功能：用户开始用车
     */
    @ResponseBody
    @RequestMapping(value = "/startUseCar", method = RequestMethod.POST)
    public String startUseCar(String xId, String tBox) {
        if (xId != null && !xId.equals("")) {
            logger.info("用户使用车============>");
            String string = orderCarService.startUseCar(xId, tBox);
            return string;
        }
        return null;
    }


    @ResponseBody
    @RequestMapping(value = "/updateBoxLoc", method = RequestMethod.POST)
    public void updateBoxLoc(@RequestBody TBox tBox) {
        String xId=tBox.getxId();
        String locationInfo=tBox.getLocationInfo();
        System.out.println("xId==========>" + xId);
        System.out.println("location==========>" + locationInfo);
        if (xId != null && !xId.equals("") && locationInfo != null && !locationInfo.equals("")) {
            String[] boxLocation = locationInfo.split(";");
            orderCarService.updateBoxLoc(xId, boxLocation);
        }
    }

    /**
     * 功能：用户结束用车
     */
    @ResponseBody
    @RequestMapping(value = "/stopUseCar", method = RequestMethod.POST)
    public String stopUseCar(String xId) {
        if (xId != null && !xId.equals("")) {
            logger.info("用户结束用车============>");
            String string = orderCarService.stopUseCar(xId);
            return string;
        }
        return null;
    }


    @ResponseBody
    @RequestMapping(value = "/blackBoxOperation", method = {RequestMethod.POST, RequestMethod.GET})
    public void blackBoxOperation() {
        orderCarService.blackBoxOperation();
    }
}
