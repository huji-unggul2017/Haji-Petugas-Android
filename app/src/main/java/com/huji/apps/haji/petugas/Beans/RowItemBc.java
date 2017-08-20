package com.huji.apps.haji.petugas.Beans;

/**
 * Created by Dell_Cleva on 02/03/2017.
 */

public class RowItemBc {
    String bcNama;
    String bcIsi;
    String bcWaktu;
    String bcJudul;
    String bcImage;
    String bcPimage;

    public RowItemBc(String BCPIMAGE, String BCNAMA, String BCWAKTU, String BCJUDUL, String BCISI, String BCIMAGE ) {
        this.bcNama = BCNAMA;
        this.bcPimage = BCPIMAGE;
        this.bcWaktu = BCWAKTU;
        this.bcJudul = BCJUDUL;
        this.bcIsi = BCISI;
        this.bcImage = BCIMAGE;

    }

    public String getBcPimage() {
        return bcPimage;
    }

    public void setBcPimage(String bcPimage) {
        this.bcPimage = bcPimage;
    }

    public String getBcNama() {
        return bcNama;
    }

    public void setBcNama(String bcNama) {
        this.bcNama = bcNama;
    }

    public String getBcIsi() {
        return bcIsi;
    }

    public void setBcIsi(String bcIsi) {
        this.bcIsi = bcIsi;
    }

    public String getBcWaktu() {
        return bcWaktu;
    }

    public void setBcWaktu(String bcWaktu) {
        this.bcWaktu = bcWaktu;
    }

    public String getBcJudul() {
        return bcJudul;
    }

    public void setBcJudul(String bcJudul) {
        this.bcJudul = bcJudul;
    }

    public String getBcImage() {
        return bcImage;
    }

    public void setBcImage(String bcImage) {
        this.bcImage = bcImage;
    }
}
