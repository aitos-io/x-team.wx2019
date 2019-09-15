package io.aitos.hackathon.pojo;

import lombok.Data;

/**
 * @author apple
 * 车和用户的对应
 */
@Data
public class UserVehicle {
    String xId;
    String vinNumber;
    String identityNumber;
    String randomNumber;
    String status;
}
