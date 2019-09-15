package io.aitos.hackathon.controller;

import io.aitos.hackathon.service.ExternalInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/externalInterface")
public class ExternalInterfaceController {

    @Autowired
    ExternalInterfaceService externalInterfaceService;


    /**
     * 返回xId
     *
     * @return
     */
    @PostMapping("/queryVehicleXId")
    public String queryVehicleXId() {
        return externalInterfaceService.queryVehicleXId();
    }
}
