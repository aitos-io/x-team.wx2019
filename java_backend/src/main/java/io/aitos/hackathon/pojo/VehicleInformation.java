package io.aitos.hackathon.pojo;

import lombok.Data;

/**
 * @author apple
 * 车辆信息
 */
@Data
public class VehicleInformation {
    String vinNumber;
    String numberPlate;
    String address;
    String privatekey;
    String vehiclePosition;
    String vehicleStatus;
    String carName;
    String carModel;
}
