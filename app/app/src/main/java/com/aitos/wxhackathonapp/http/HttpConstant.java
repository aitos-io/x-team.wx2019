package com.aitos.wxhackathonapp.http;

public final class HttpConstant {

    private static final String CAR_LEASE_HOST = "http://172.20.10.2:6789";//local

    public static final class CAR_LEASE_URI {
        public static final String LOGIN = CAR_LEASE_HOST + "/userController/userLogin";
        public static final String REGISTER = CAR_LEASE_HOST + "/userController/userRegister";
        public static final String BOOK_CAR = CAR_LEASE_HOST + "/orderCar/bookingProcess";
        public static final String START_USER_CAR = CAR_LEASE_HOST + "/orderCar/startUseCar";
        public static final String STOP_USER_CAR = CAR_LEASE_HOST + "/orderCar/stopUseCar";
    }

    public static final class EID_URI {
        public static final String AUTH = CAR_LEASE_HOST + "/getCardInfo";
    }

    public static final class RequestParams {

        public static final String IMAGE = "image";

    }

    public static final class ResponseKey {

        //公共部分
        public static final String RESP_CODE = "resp_code";

    }
}
