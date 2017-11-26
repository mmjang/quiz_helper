package com.mmjang.quizhelper.util;

public class ConstantUtil {

    public static final String CONTENT="content://";
    public static final String AUTHORITY="com.forfun.bigbang";
    public static final String SEPARATOR= "/";
    public static final String CONTENT_URI =CONTENT+AUTHORITY;

    public static final String TYPE_STRING="string";
    public static final String TYPE_INT="int";
    public static final String TYPE_LONG="long";
    public static final String TYPE_FLOAT="float";
    public static final String TYPE_BOOLEAN="boolean";
    public static final String TYPE_CONTAIN="contain";
    public static final String DEFAULT_CURSOR_NAME= "default";
    public static final String VALUE= "value";
    public static final String NULL_STRING= "null";


    public static final String BROADCAST_RELOAD_SETTING="broadcast_reload_setting";
    public static final String BROADCAST_BIGBANG_MONITOR_SERVICE_MODIFIED ="broadcast_bigbang_monitor_service_modified";

    public static final String BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED="broadcast_clipboard_listen_service_modified";

    public static final String BROADCAST_SET_TO_CLIPBOARD="broadcast_set_to_clipboard";
    public static final String BROADCAST_SET_TO_CLIPBOARD_MSG="broadcast_set_to_clipboard_msg";




    //shareCard
    public static final String HAD_SHARED="had_shared";
    public static final String SETTING_OPEN_TIMES="setting_open_times";

    //FunctionSettingCard
    public static final String MONITOR_CLIP_BOARD="monitor_clip_board";
    public static final String MONITOR_CLICK="monitor_click";
    public static final String TOTAL_SWITCH="total_switch";
    public static final String SHOW_FLOAT_VIEW="show_float_view";
    public static final String REMAIN_SYMBOL="remain_symbol";
    public static final String REMAIN_SECTION="remain_section";
    public static final String DEFAULT_LOCAL="default_local";


    public static final String AUTO_OPEN_SETTING="auto_open_setting";

    //floatview
    public static final String FLOAT_SWITCH_STATE="float_switch_state";
    public static final String FLOAT_VIEW_LAND_X="float_view_land_x";
    public static final String FLOAT_VIEW_LAND_Y="float_view_land_Y";
    public static final String FLOAT_VIEW_PORT_X="float_view_port_x";
    public static final String FLOAT_VIEW_PORT_Y="float_view_port_y";



    public static final String IS_SHOW_NOTIFY="is_show_notify";
    public static final String NOTIFY_DISABLED_IGNORE="notify_disabled_ignore";


    //FeedBackAndUpdateCard

    //MonitorSettingCard
    public static final String TEXT_ONLY="text_only";
    public static final String QQ_SELECTION="qq_selection";
    public static final String WEIXIN_SELECTION="weixin_selection";
    public static final String OTHER_SELECTION="other_selection";

    public static final String BROWSER_SELECTION="browser_selection";


    public static final String Setting_content_Changes ="tencent_contents_change";
    public static final String SHOW_TENCENT_SETTINGS = "tencent_settings";


    public static final String ONLINE_CONFIG_OPEN_UPDATE="online_config_open_update";
    public static final String DOUBLE_CLICK_INTERVAL="double_click_interval";
    public static final int DEFAULT_DOUBLE_CLICK_INTERVAL = 1000;


    //SettingBigBangActivity
    public static final String TEXT_SIZE="text_size";
    public static final String LINE_MARGIN="line_margin";
    public static final String ITEM_MARGIN="item_margin";
    public static final String ITEM_PADDING="item_padding";
    public static final String BIGBANG_ALPHA="bigbang_alpha";
    public static final String USE_LOCAL_WEBVIEW="use_local_webview";
    public static final String USE_FLOAT_VIEW_TRIGGER="use_float_view_trigger";
    public static final String BIGBANG_DIY_BG_COLOR ="bigbang_diy_bg_color";
    public static final String IS_FULL_SCREEN ="is_full_screen";
    public static final String IS_STICK_HEADER ="is_stick_header";
    public static final String IS_STICK_SHAREBAR ="is_stick_sharebar";
    public static final String AUTO_ADD_BLANKS="auto_add_blanks";
    public static final String TREAT_BLANKS_AS_SYMBOL="treat_blanks_as_symbol";



    public static final String FLOATVIEW_SIZE ="floatview_size_";
    public static final String FLOATVIEW_ALPHA ="floatview_alpha";
    public static final String FLOATVIEW_DIY_BG_COLOR ="floatview_diy_bg_color";
    public static final String FLOATVIEW_IS_STICK ="floatview_is_stick";


    public static final int DEFAULT_TEXT_SIZE=14;
    public static final int DEFAULT_LINE_MARGIN=8;
    public static final int DEFAULT_ITEM_MARGIN=0;
    public static final int DEFAULT_ITEM_PADDING=10;


    //whiteListActivity
    public static final String WHITE_LIST_COUNT ="white_list_count";
    public static final String WHITE_LIST ="white_list";
    public static final String REFRESH_WHITE_LIST_BROADCAST ="refresh_white_list_broadcast";


    //FloatwhiteListActivity
    public static final String FLOAT_WHITE_LIST_COUNT ="float_white_list_count";
    public static final String FLOAT_WHITE_LIST ="float_white_list_";
    public static final String FLOAT_REFRESH_WHITE_LIST_BROADCAST ="float_refresh_white_list_broadcast";

    public static final String HAS_ADDED_LAUNCHER_AS_WHITE_LIST="has_added_launcher_as_white_list";


    public static final String UNIVERSAL_COPY_BROADCAST="universal_copy_broadcast";
    public static final String UNIVERSAL_COPY_BROADCAST_DELAY="universal_copy_broadcast_delay";
    public static final String SCREEN_CAPTURE_OVER_BROADCAST="screen_capture_over_broadcast";


    public static final String TOTAL_SWITCH_BROADCAST = "total_switch_broadcast";
    public static final String MONITOR_CLICK_BROADCAST = "monitor_click_broadcast";
    public static final String MONITOR_CLIPBOARD_BROADCAST = "monitor_clipboard_broadcast";

    public static final String NOTIFY_UNIVERSAL_COPY_BROADCAST="notify_universal_copy_broadcast";
    public static final String NOTIFY_SCREEN_CAPTURE_OVER_BROADCAST="notify_screen_capture_over_broadcast";

    public static final String OCR_TIME = "ocr_time";
    public static final int OCR_TIME_TO_ALERT = 5;
    public static final String SHOULD_SHOW_DIY_OCR = "should_show_diy_ocr";

    public static final String DIY_OCR_KEY="diy_ocr_key";


    public static final String EFFECT_AFTER_REBOOT_BROADCAST ="effect_after_reboot_broadcast";

    public static final String LONG_PRESS_KEY_INDEX ="long_press_key_index";

    public static final String SHARE_APPS_DIS ="share_app_dis";
    public static final String HAD_ENTER_INTRO="had_enter_intro";
    public static final String SHARE_APP_INDEX = "share_app_index";

    public static final String HAD_SHOW_LONG_PRESS_TOAST="had_show_long_press_toast";

    //copyActivity
    public static final String IS_FULL_SCREEN_COPY="is_full_screen_copy";

    //feedback
//    public static final String ALI_APP_KEY= BigBangApp.getInstance().getString(R.string.ali_feedback_key);
    //xp全局复制
    public static final String UNIVERSAL_COPY_BROADCAST_XP="universal_copy_broadcast_xp";

}