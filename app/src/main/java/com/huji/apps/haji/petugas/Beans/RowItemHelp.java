package com.huji.apps.haji.petugas.Beans;

/**
 * Created by Dell_Cleva on 28/03/2017.
 */

public class RowItemHelp {
    String sNama;
    String sImg;
    String sId;
    String sLat;
    String sLon;
    String sPhone;

    public RowItemHelp(String snama, String simg, String sid, String slat, String slon, String sphone) {
        this.sNama = snama;
        this.sImg = simg;
        this.sId = sid;
        this.sLat = slat;
        this.sLon = slon;
        this.sPhone = sphone;
    }


    public String getsNama() {
        return sNama;
    }

    public void setsNama(String sNama) {
        this.sNama = sNama;
    }

    public String getsImg() {
        return sImg;
    }

    public void setsImg(String sImg) {
        this.sImg = sImg;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
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

    public String getsPhone() {
        return sPhone;
    }

    public void setsPhone(String sPhone) {
        this.sPhone = sPhone;
    }
}
