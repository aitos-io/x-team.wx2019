package com.aitos.wxhackathonapp.http.response;

public class LoginResponse {
    private String status;
    private String message;
    private String xId;
    private String xidStatus;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getxId() {
        return xId;
    }

    public void setxId(String xId) {
        this.xId = xId;
    }

    public String getXidStatus() {
        return xidStatus;
    }

    public void setXidStatus(String xidStatus) {
        this.xidStatus = xidStatus;
    }
}