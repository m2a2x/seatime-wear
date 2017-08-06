package com.maks.seatimewear;


public final class SystemConfiguration {
    private static final String localUrl="http://10.0.2.2:3000/apiDevice/";
    private static final String url = "https://seatime.herokuapp.com/apiDevice/";

    public static int NETWORK_REQUEST_DELAY = 15000;

    public static int NETWORK_REPEAT_REQUEST_DELAY = 5000;


    public static boolean isProduction = false;

    public static String getService() {
        if (isProduction) {
            return url;
        }
        return localUrl;
    }
}
