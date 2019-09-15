package com.aitos.wxhackathonapp.http.response;

public class BookCarResponse {
    private String xId;
    private String numberPlate;
    private String vehicleStatus;
    private String vehiclePosition;
    private String vinNumber;

    public String getxId() {
        return xId;
    }

    public void setxId(String xId) {
        this.xId = xId;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getVehiclePosition() {
        return vehiclePosition;
    }

    public void setVehiclePosition(String vehiclePosition) {
        this.vehiclePosition = vehiclePosition;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }
}