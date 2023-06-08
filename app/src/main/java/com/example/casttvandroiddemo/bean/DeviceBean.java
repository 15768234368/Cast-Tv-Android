package com.example.casttvandroiddemo.bean;

public class DeviceBean {
    private String userDeviceName;
    private String userDeviceLocation;
    private String userDeviceIpAddress;
    //isOnline: 1-在线且已连接；2-在线；0-不在线
    private int isOnline;
//    删除的逻辑,平常的isDelete都是为0，当值为1的时候，删除框出现，当值为2的时候，选中删除框
    private int isDelete;
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

    public DeviceBean() {
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
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
