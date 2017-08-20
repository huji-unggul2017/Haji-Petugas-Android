package com.huji.apps.haji.petugas.Utils;

/**
 * Created by Dell_Cleva on 13/02/2017.
 */

public class Constant {

    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME =
            "com.huji.apps.haji.petugas";

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static final String PrefTAG = "com.huji.apps.haji.petugas";

    public static final String Username = "huji_api";
    public static final String Password = "kemenangan_berlanjut123";

    public static final String POSITION = "Indonesia";

    public static String MainPage = "https://app.huji.co.id/";
    public static String ApiVersion = "api/haji/v1/";

    public static String Full = MainPage + ApiVersion;

    public static String APP_TYPE = "petugas_app";
    public static String LoginPetugas = Full + "petugas_login";
    public static String InitData = Full + "init_data";
    public static String InitPIN = Full + "init_pin";
    public static String InitProfile = Full + "reg_identity";
    public static String PushLocation = Full + "push_location";
    public static String MyTracking = Full + "my_tracking";
    public static String Update_FCM = Full + "update_fcm/access_token/";
    public static String Send_Broadcast = Full + "send_broadcast/access_token/";
    public static String HelpResponse = Full + "help_response/access_token/";
    public static String HelpSolution = Full + "help_fill_solution/access_token/";
    public static String ListJemaah = Full + "list_jemaah/access_token/";
    public static String ListBroadcast = Full + "get_broadcast/access_token/";
    public static String GetPeriodic = Full + "get_periodic_location/access_token/";
    public static String SaveHotel = Full + "add_hotel/access_token/";
    public static String DeleteHotel = Full + "delete_myhotel/access_token/";
    public static String ListHotel = Full + "get_myhotel/access_token/";
    public static String ResetPIN = Full + "reset_pin/access_token/";
    public static String UpdateProfile = Full + "update_basicprofile/access_token/";
    public static String UpdatePicture = Full + "update_picture/access_token/";
    public static String UpdateLocation = Full + "get_single_location/access_token/";
    public static String Buzzer = Full + "help_buzzer/access_token/";
    public static String BuzzerCancel = Full + "help_buzzer_stop/access_token/";
    public static String Arrive = Full + "help_arrived_tkp/access_token/";
    public static String GetKloter = Full + "get_list_kloter";
    public static String GetRombongan = Full + "get_list_rombongan";
    public static String GetGroup = Full + "get_list_group";
    public static String UpdateKloter = Full + "update_kloter_haji";
    public static String UpdateRombongan = Full + "update_rombongan_haji";
    public static String UpdateGroup = Full + "update_group_haji";
    public static String CancelHelp = Full + "help_cancel/access_token/";
    public static String HelpMe = Full + "help_me/access_token/";
    public static String UpdateImei = Full + "update_myimei/access_token/";

    public static String RESPONSE = "response";
    public static String STATUS = "status";
}
