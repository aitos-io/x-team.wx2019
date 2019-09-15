package io.aitos.hackathon.blockchain;

public enum CarStatus {
    BOOKED(0), INUSE(1), COMPLETED(2);

    private int status;

    CarStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
