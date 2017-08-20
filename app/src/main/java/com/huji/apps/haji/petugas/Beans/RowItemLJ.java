package com.huji.apps.haji.petugas.Beans;

/**
 * Created by Dell_Cleva on 07/03/2017.
 */

public class RowItemLJ {
    private String sNama;
    private Boolean sStatus;
    private String sImage;
    private String sTelp;
    private String sLat;
    private String sLon;
    private String sTime;

    public RowItemLJ(String sImages, String sNamas, Boolean sStatuss, String Telp, String Lat, String Lon, String Time) {
        this.sImage = sImages;
        this.sNama = sNamas;
        this.sStatus = sStatuss;
        this.sTelp = Telp;
        this.sLat = Lat;
        this.sLon = Lon;
        this.sTime = Time;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String getsTelp() {
        return sTelp;
    }

    public void setsTelp(String sTelp) {
        this.sTelp = sTelp;
    }

    public String getsLat() {
        return sLat;
    }

    public void setsLat(String sLat) {
        this.sLat = sLat;
    }

    public String getsLon() {
        return sLon;
    }

    public void setsLon(String sLon) {
        this.sLon = sLon;
    }

    public String getsNama() {
        return sNama;
    }

    public void setsNama(String sNama) {
        this.sNama = sNama;
    }

    public Boolean getsStatus() {
        return sStatus;
    }

    public void setsStatus(Boolean sStatus) {
        this.sStatus = sStatus;
    }

    public String getsImage() {
        return sImage;
    }

    public void setsImage(String sImage) {
        this.sImage = sImage;
    }

}
