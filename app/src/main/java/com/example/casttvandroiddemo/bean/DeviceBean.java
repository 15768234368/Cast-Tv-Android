package com.example.casttvandroiddemo.bean;

public class DeviceBean {
    private String userDeviceName;
    private String userDeviceLocation;
    private String userDeviceIpAddress;
    //isOnline: 1-在线且已连接；2-在线；0-不在线
    private int isOnline;
    public DeviceBean(String userDeviceName, String userDeviceLocation, String userDeviceIpAddress) {
        this.userDeviceName = userDeviceName;
        this.userDeviceLocation = userDeviceLocation;
        this.userDeviceIpAddress = userDeviceIpAddress;
    }

    public DeviceBean(String userDeviceName, String userDeviceLocation, String userDeviceIpAddress, int isOnline) {
        this.userDeviceName = userDeviceName;
        this.userDeviceLocation = userDeviceLocation;
        this.userDeviceIpAddress = userDeviceIpAddress;
        this.isOnline = isOnline;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public void setUserDeviceName(String userDeviceName) {
        this.userDeviceName = userDeviceName;
    }

    public void setUserDeviceLocation(String userDeviceLocation) {
        this.userDeviceLocation = userDeviceLocation;
    }


    public void setUserDeviceIpAddress(String userDeviceIpAddress) {
        this.userDeviceIpAddress = userDeviceIpAddress;
    }

    public String getUserDeviceName() {
        return userDeviceName;
    }

    public String getUserDeviceLocation() {
        return userDeviceLocation;
    }


    public String getUserDeviceIpAddress() {
        return userDeviceIpAddress;
    }
}
