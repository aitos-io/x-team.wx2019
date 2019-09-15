package com.aitos.wxhackathonapp.constants;

public class Constants {

    private final static int INIT_HANDLER_MSG = 1000;

    public static final class HandlerMSG{

        public final static int EID_AUTH_FAILURE = INIT_HANDLER_MSG + 1;
        public final static int EID_AUTH_RESPONSE = INIT_HANDLER_MSG + 2;

        public final static int CAR_BOOK_FAILURE = INIT_HANDLER_MSG + 3;
        public final static int CAR_BOOK_RESPONSE = INIT_HANDLER_MSG + 4;

        public final static int START_CAR_FAILURE = INIT_HANDLER_MSG + 5;
        public final static int START_CAR_RESPONSE = INIT_HANDLER_MSG + 6;

        public final static int STOP_CAR_FAILURE = INIT_HANDLER_MSG + 7;
        public final static int STOP_CAR_RESPONSE = INIT_HANDLER_MSG + 8;

        public final static int REGISTER_FAILURE = INIT_HANDLER_MSG + 9;
        public final static int REGISTER_RESPONSE = INIT_HANDLER_MSG + 10;

        public final static int LOGIN_FAILURE = INIT_HANDLER_MSG + 11;
        public final static int LOGIN_RESPONSE = INIT_HANDLER_MSG + 12;

    }

    public static final class Key{
        public final static String EID_NUM = "eid_idnum";
        public final static String USER_APP_ID = "userAppId";
        public final static String XID = "xid";
        public final static String XID_STATUS = "xid_status";
    }

    public static final class RespStatus{
        public final static String SUCCESS = "00";
    }

    public static final class Contract{
        //车辆状态(值为使用中2或者未使用0或者锁定1)
        //合同状态为：已订车1、使用中2、已还车3
        public final static String STATUS_BOOKED = "1";
        public final static String STATUS_INUSE = "2";
        public final static String STATUS_RETURNED = "3";
    }

}
