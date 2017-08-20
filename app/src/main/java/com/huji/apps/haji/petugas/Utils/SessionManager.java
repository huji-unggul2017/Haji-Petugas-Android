package com.huji.apps.haji.petugas.Utils;

/**
 * Created by Dell_Cleva on 18/02/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.huji.apps.haji.petugas.MainActivity;
import com.huji.apps.haji.petugas.MapsActivity;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "HUJIUMROH";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Phone number (make variable public to access from outside)
    public static final String KEY_TELP = "telp";

    // Phone number (make variable public to access from outside)
    public static final String KEY_TOKEN = "token";

    // Phone number (make variable public to access from outside)
    public static final String KEY_TOKEN_FCM = "tokenfcm";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";
    public static final String KEY_HELP = "help";

    public static final String KEY_PIN = "pin";

    public static final String KEY_PASSPORT= "passport";

    public static final String KEY_TYPE= "type";

    public static final String KEY_EMAILBENER = "emailbener";

    public static final String KEY_HELPS = "helps";
    public static final String KEY_IMEI = "imei";

    public static final String KEY_HE7 = "he7";
    public static final String KEY_HE8 = "he8";

    public static final String KEY_HE1 = "he1";
    public static final String KEY_HE2 = "he2";
    public static final String KEY_HE3 = "he3";
    public static final String KEY_HE4 = "he4";
    public static final String KEY_HE5 = "he5";
    public static final String KEY_HE6 = "he6";

    public static final String KEY_KUID = "iskuid";
    public static final String KEY_ROM = "isrom";
    public static final String KEY_GROUP = "isgroup";

    public static final String KEY_KUID_DATA = "iskuiddata";
    public static final String KEY_ROM_DATA = "isromdata";
    public static final String KEY_GROUP_DATA = "isgroupdata";

    public static final String KEY_LAT_HOTEL = "lathotel";
    public static final String KEY_LON_HOTEL = "lonhotel";
    public static final String KEY_NAME_HOTEL = "namahotel";
    public static final String KEY_ADDRESS_HOTEL = "alamathotel";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void CreateisHelp(boolean helps) {
        editor.putBoolean(KEY_HELPS, helps);
        editor.commit();
    }
    public boolean isHelps() {
        return pref.getBoolean(KEY_HELPS, false);
    }


    // TODO
    public void CreateisKuid(String helps) {
        editor.putString(KEY_KUID, helps);
        editor.commit();
    }
    public String isKuid() {
        return pref.getString(KEY_KUID, "0");
    }

    public void CreateisRom(String helps) {
        editor.putString(KEY_ROM, helps);
        editor.commit();
    }
    public String isRom() {
        return pref.getString(KEY_ROM, "0");
    }

    public void CreateisGroup(String helps) {
        editor.putString(KEY_GROUP, helps);
        editor.commit();
    }
    public String isGroup() {
        return pref.getString(KEY_GROUP, "0");
    }

    public void CreateisKuid_DATA(String helps) {
        editor.putString(KEY_KUID_DATA, helps);
        editor.commit();
    }
    public String isKuid_DATA() {
        return pref.getString(KEY_KUID_DATA, "0");
    }

    public void CreateisRom_DATA(String helps) {
        editor.putString(KEY_ROM_DATA, helps);
        editor.commit();
    }
    public String isRom_DATA() {
        return pref.getString(KEY_ROM_DATA, "0");
    }

    public void CreateisGroup_DATA(String helps) {
        editor.putString(KEY_GROUP_DATA, helps);
        editor.commit();
    }
    public String isGroup_DATA() {
        return pref.getString(KEY_GROUP_DATA, "0");
    }

    public String getImei() {
        return pref.getString(KEY_IMEI, "0");
    }

    public void CreateIMEI(String helps) {
        editor.putString(KEY_IMEI, helps);
        editor.commit();
    }

    public void createDataHelp(String a, String b,String c,String d,String e,String f,int g,String h){
        editor.putString(KEY_HE1, a);
        editor.putString(KEY_HE2, b);
        editor.putString(KEY_HE3, c);
        editor.putString(KEY_HE4, d);
        editor.putString(KEY_HE5, e);
        editor.putString(KEY_HE6, f);
        editor.putInt(KEY_HE7, g);
        editor.putString(KEY_HE8, h);
        editor.commit();
    }
    public void createDataHelp(String a, String b,String c,String d,String e,String f){
        editor.putString(KEY_HE1, a);
        editor.putString(KEY_HE2, b);
        editor.putString(KEY_HE3, c);
        editor.putString(KEY_HE4, d);
        editor.putString(KEY_HE5, e);
        editor.putString(KEY_HE6, f);
        editor.commit();
    }

    public String getA() {
        return pref.getString(KEY_HE1, "nama");
    }
    public String getB() {
        return pref.getString(KEY_HE2, "nama");
    }
    public String getC() {
        return pref.getString(KEY_HE3, "21.422510");
    }
    public String getD() {
        return pref.getString(KEY_HE4, "39.826168");
    }
    public String getE() {
        return pref.getString(KEY_HE5, "nama");
    }
    public String getF() {
        return pref.getString(KEY_HE6, "nama");
    }
    public int getG() {
        return pref.getInt(KEY_HE7, 0);
    }
    public String getH() {
        return pref.getString(KEY_HE8, "0");
    }

    public void createHelp(String help) {
        editor.putString(KEY_HELP, help);
        editor.commit();
    }

    public String getHelp() {
        return pref.getString(KEY_HELP, "kosong");
    }


    public String getIds(){
        String id = pref.getString(KEY_EMAIL, null);
        return id;
    }
    public String getIdsuper(){
        String id = pref.getString(KEY_EMAILBENER, null);
        return id;
    }
    public String getToken(){
        String tokens = pref.getString(KEY_TOKEN, "kosong");
        return tokens;
    }

    public String getTokenFCM(){
        String tokens = pref.getString(KEY_TOKEN_FCM, "kosong");
        return tokens;
    }

    public void createTokenFcm(String tokenfcm) {
        editor.putString(KEY_TOKEN_FCM, tokenfcm);
        editor.commit();
    }

    public void createLatLon(String slat, String slon) {
        editor.putString(KEY_LAT, slat);
        editor.putString(KEY_LON, slon);
        editor.commit();
        Log.w("TAG", "createLatLon: " + slat + "|" + slon );
    }

    public String getLat() {
        return pref.getString(KEY_LAT, "21.422510");
    }

    public String getLon() {
        return pref.getString(KEY_LON, "39.826168");
    }

    public String getEmails(){
        String id = pref.getString(KEY_EMAILBENER, null);
        return id;
    }

    public void createPassport(String help) {
        editor.putString(KEY_PASSPORT, help);
        editor.commit();
    }
    public String getPassport(){
        String tokens = pref.getString(KEY_PASSPORT, "0");
        return tokens;
    }

    public String getPin(){
        String tokens = pref.getString(KEY_PIN, "0");
        return tokens;
    }

    public void createType(String help) {
        editor.putString(KEY_TYPE, help);
        editor.commit();
    }

    public String getType() {
        return pref.getString(KEY_TYPE, "kosong");
    }


    /**
     * Create Hotel session
     * */
    public void createMyHotel(String lat, String lon, String nama, String alamat){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_LAT_HOTEL, lat);

        // Storing name in pref
        editor.putString(KEY_LON_HOTEL, lon);

        // Storing email in pref
        editor.putString(KEY_NAME_HOTEL, nama);

        // Storing telp in pref
        editor.putString(KEY_ADDRESS_HOTEL, alamat);


        // commit changes
        editor.commit();
    }

    public String getLatHotel() {
        return pref.getString(KEY_LAT_HOTEL, "0");
    }
    public String getLonHotel() {
        return pref.getString(KEY_LON_HOTEL, "0");
    }
    public String getNamaHotel() {
        return pref.getString(KEY_NAME_HOTEL, "0");
    }
    public String getAlamatHotel() {
        return pref.getString(KEY_ADDRESS_HOTEL, "0");
    }


    /**
     * Create login session
     * */
    public void createLoginSession(String passport, String name, String email, String telp, String token, String pin, String type){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing telp in pref
        editor.putString(KEY_TELP, telp);

        editor.putString(KEY_TOKEN, token);

        editor.putString(KEY_PIN, pin);

        editor.putString(KEY_PASSPORT, passport);

        editor.putString(KEY_TYPE, type);
        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, MapsActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
