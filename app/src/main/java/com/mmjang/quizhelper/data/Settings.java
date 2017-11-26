package com.mmjang.quizhelper.data;

/**
 * Created by liao on 2017/4/13.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.mmjang.quizhelper.domain.PronounceManager;

/**
 * 单例，getInstance()得到实例
 */
public class Settings {

    private static Settings settings = null;

    private final static String PREFER_NAME = "settings";    //应用设置名称
    private final static String MODEL_ID = "model_id";       //应用设置项 模版id
    private final static String DECK_ID = "deck_id";         //应用设置项 牌组id
    private final static String DEFAULT_MODEL_ID = "default_model_id"; //默认模版id，如果此选项存在，则已写入配套模版
    private final static String FIELDS_MAP = "fields_map";   //字段映射
    private final static String MONITE_CLIPBOARD_Q = "show_clipboard_notification_q";   //是否监听剪切板
    private final static String AUTO_CANCEL_POPUP_Q = "auto_cancel_popup";              //点加号后是否退出
    private final static String DEFAULT_PLAN = "default_plan";
    private final static String LAST_SELECTED_PLAN = "last_selected_plan";
    private final static String DEFAULT_TAG = "default_tag";
    private final static String SET_AS_DEFAULT_TAG = "set_as_default_tag";
    private final static String LAST_PRONOUNCE_LANGUAGE = "last_pronounce_language";
    private final static String LEFT_HAND_MODE_Q = "left_hand_mode_q";
    private final static String ACCESS_TOKEN = "access_token";
    private final static String USER_NAME = "user_name";
    private final static String QUIZY_SET_ID = "quizy_set_id";

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private Settings(Context context) {
        sp = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 获得单例
     *
     * @return
     */
    public static Settings getInstance(Context context) {
        if (settings == null) {
            settings = new Settings(context);
        }
        return settings;
    }

    /*************/

    Long getModelId() {
        return sp.getLong(MODEL_ID, 0);
    }

    void setModelId(Long modelId) {
        editor.putLong(MODEL_ID, modelId);
        editor.commit();
    }

    /**************/

    Long getDeckId() {
        return sp.getLong(DECK_ID, 0);
    }

    void setDeckId(Long deckId) {
        editor.putLong(DECK_ID, deckId);
        editor.commit();
    }

    /**************/

    Long getDefaultModelId() {
        return sp.getLong(DEFAULT_MODEL_ID, 0);
    }

    void setDefaultModelId(Long defaultModelId) {
        editor.putLong(DEFAULT_MODEL_ID, defaultModelId);
        editor.commit();
    }

    /**************/

    String getFieldsMap() {
        return sp.getString(FIELDS_MAP, "");
    }

    void setFieldsMap(String filedsMap) {
        editor.putString(FIELDS_MAP, filedsMap);
        editor.commit();
    }

    /**************/

    public boolean getMoniteClipboardQ() {
        return sp.getBoolean(MONITE_CLIPBOARD_Q, false);
    }

    public void setMoniteClipboardQ(boolean moniteClipboardQ) {
        editor.putBoolean(MONITE_CLIPBOARD_Q, moniteClipboardQ);
        editor.commit();
    }

    /**************/

    public boolean getAutoCancelPopupQ() {
        return sp.getBoolean(AUTO_CANCEL_POPUP_Q, false);
    }

    public void setAutoCancelPopupQ(boolean autoCancelPopupQ) {
        editor.putBoolean(AUTO_CANCEL_POPUP_Q, autoCancelPopupQ);
        editor.commit();
    }

    /**************/
    public String getDefaultPlan() {
        return sp.getString(DEFAULT_PLAN, "");
    }

    public void setDefaultPlan(String defaultPlan) {
        editor.putString(DEFAULT_PLAN, defaultPlan);
        editor.commit();
    }

    /******************/

    public String getLastSelectedPlan() {
        return sp.getString(LAST_SELECTED_PLAN, "");
    }

    public void setLastSelectedPlan(String lastSelectedPlan) {
        editor.putString(LAST_SELECTED_PLAN, lastSelectedPlan);
        editor.commit();
    }

    /*****************/

    /****************/
    public String getAccessToken(){
        return  sp.getString(ACCESS_TOKEN, "");
    }

    public void setAccessToken(String accessToken){
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.commit();
    }
    /****************/
    public String getUserName(){
        return sp.getString(USER_NAME, "");
    }

    public void setUserName(String userName){
        editor.putString(USER_NAME, userName);
        editor.commit();
    }
    /****************/
    public String getDefaulTag() {
        return sp.getString(DEFAULT_TAG, "");
    }

    public void setDefaultTag(String defaultTag) {
        editor.putString(DEFAULT_TAG, defaultTag);
        editor.commit();
    }

    /****************/
    public boolean getSetAsDefaultTag() {
        return sp.getBoolean(SET_AS_DEFAULT_TAG, false);
    }

    public void setSetAsDefaultTag(boolean setAsDefaultTag) {
        editor.putBoolean(SET_AS_DEFAULT_TAG, setAsDefaultTag);
        editor.commit();
    }

    public int getLastPronounceLanguage() {
        return sp.getInt(LAST_PRONOUNCE_LANGUAGE, PronounceManager.LANGUAGE_ENGLISH_INDEX);
    }

    public void setLastPronounceLanguage(int lastPronounceLanguageIndex) {
        editor.putInt(LAST_PRONOUNCE_LANGUAGE, lastPronounceLanguageIndex);
        editor.commit();
    }

    public boolean getLeftHandModeQ(){
        return  sp.getBoolean(LEFT_HAND_MODE_Q, false);
    }

    public void setLeftHandModeQ(boolean leftHandModeQ){
        editor.putBoolean(LEFT_HAND_MODE_Q, leftHandModeQ);
        editor.commit();
    }

    /**************/

    public int getQuizySetId(){
        return sp.getInt(QUIZY_SET_ID, 0);
    }

    public void setQuizySetId(int quizySetId){
        editor.putInt(QUIZY_SET_ID, quizySetId);
        editor.commit();
    }
    boolean hasKey(String key) {
        return sp.contains(key);
    }



}