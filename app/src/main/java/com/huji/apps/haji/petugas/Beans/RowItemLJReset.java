package com.huji.apps.haji.petugas.Beans;

/**
 * Created by Dell_Cleva on 07/03/2017.
 */

public class RowItemLJReset {
    private String sNama;
    private String sStatus;
    private String sImage;
    public RowItemLJReset(String sImages, String sNamas, String sStatuss) {
        this.sImage = sImages;
        this.sNama = sNamas;
        this.sStatus = sStatuss;
    }

    public String getsNama() {
        return sNama;
    }

    public void setsNama(String sNama) {
        this.sNama = sNama;
    }

    public String getsStatus() {
        return sStatus;
    }

    public void setsStatus(String sStatus) {
        this.sStatus = sStatus;
    }

    public String getsImage() {
        return sImage;
    }

    public void setsImage(String sImage) {
        this.sImage = sImage;
    }

}
