package com.mmjang.quizhelper.ui;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.quizhelper.R;
import com.mmjang.quizhelper.anki.AnkiDroidHelper;
import com.mmjang.quizhelper.data.plan.DefaultPlan;
import com.mmjang.quizhelper.data.plan.OutputPlan;
import com.mmjang.quizhelper.domain.CBWatcherService;
import com.mmjang.quizhelper.MyApplication;
import com.mmjang.quizhelper.data.Settings;
import com.mmjang.quizhelper.ui.about.AboutActivity;
import com.mmjang.quizhelper.ui.customdict.CustomDictionaryActivity;
import com.mmjang.quizhelper.ui.plan.PlansManagerActivity;
import com.mmjang.quizhelper.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import ca.mimic.oauth2library.OAuth2Client;
import ca.mimic.oauth2library.OAuthError;
import ca.mimic.oauth2library.OAuthResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LauncherActivity extends AppCompatActivity {

    Settings settings;
    //views
    Switch switchMoniteClipboard;
    Switch switchCancelAfterAdd;
    Switch switchLeftHandMode;
    TextView textViewAbout;
    TextView textViewHelp;
    TextView textViewQuizLogin;
    ProgressDialog progressDialog;
    private static final int AUTH_RESULT = 0;
    private static final int TOKEN_RESULT_OK = 1;
    private static final int TOKEN_RESULT_BAD = 2;
    private static final int NETWORK_ERROR = 3;
    private static final int SET_INIT_DONE = 4;
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOKEN_RESULT_OK:
                    Toast.makeText(LauncherActivity.this, R.string.auth_done, Toast.LENGTH_SHORT).show();
                    initQuizlet();
                    break;
                case TOKEN_RESULT_BAD:
                    progressDialog.hide();
                    Toast.makeText(LauncherActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                    break;
//                    Toast.makeText(CustomDictionaryActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
//                    setProgressBar(false);
//                    break;
                case NETWORK_ERROR:
                    progressDialog.hide();
                    Toast.makeText(LauncherActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                    break;
//                    Toast.makeText(CustomDictionaryActivity.this, "自定义词典已清空！", Toast.LENGTH_SHORT).show();
//                    reFreshData();
                case SET_INIT_DONE:
                    progressDialog.hide();
                    Toast.makeText(LauncherActivity.this, R.string.init_done, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void initQuizlet() {
        progressDialog.show();
        textViewQuizLogin.setText(getString(R.string.user_propt) + settings.getUserName());
        final String token = settings.getAccessToken();
        Thread thread = new Thread(){
            @Override
            public void run() {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .addHeader("Host", "api.quizlet.com")
                                .addHeader("Authorization","Bearer " + token)
                                .url("https://api.quizlet.com/2.0/users/" + settings.getUserName())
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            if(response.code() == 200){
                                String json = response.body().string();
                                JSONObject data = null;
                                Log.e("json", json);
                                try{
                                    data = new JSONObject(json);
                                    JSONArray sets = data.getJSONArray("sets");
                                    for(int i = 0; i < sets.length(); i ++){
                                        JSONObject jsonObject = (JSONObject) sets.get(i);
                                        if(jsonObject.getString("title").equals(Constant.QUIZY_APP_SET)){
                                            settings.setQuizySetId(jsonObject.getInt("id"));
                                            break;
                                        }
                                    }
                                    if(settings.getQuizySetId() > 0){
                                        //quizy set already exists
                                        Message message = mHandler.obtainMessage();
                                        message.what = SET_INIT_DONE;
                                        mHandler.sendMessage(message);
                                    }
                                    else{
                                        //add new set
                                        Request request2 = new Request.Builder()
                                                .addHeader("Host", "api.quizlet.com")
                                                .addHeader("Authorization", "Bearer " + token)
                                                .url("https://api.quizlet.com/2.0/sets")
                                                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                                                    "title=" + Constant.QUIZY_APP_SET +
                                                    "&terms[]=" + "term1" +
                                                    "&terms[]=" + "term2" +
                                                    "&definitions[]=" + "def1" +
                                                    "&definitions[]=" + "def2" +
                                                    "&lang_terms=en&lang_definitions=en"
                                                ))
                                                .build();
                                        try{
                                            Response response1 = client.newCall(request2).execute();
                                            if(response1.code() == 201){
                                                String jsonStr = response1.body().string();
                                                JSONObject jsonSet = null;
                                                try{
                                                    jsonSet = new JSONObject(jsonStr);
                                                    settings.setQuizySetId(jsonSet.getInt("set_id"));
                                                    Message message = mHandler.obtainMessage();
                                                    message.what = SET_INIT_DONE;
                                                    mHandler.sendMessage(message);
                                                }
                                                catch (JSONException jse){
                                                    jse.printStackTrace();
                                                }
                                            }
                                        }
                                        catch(IOException ioe){
                                            ioe.printStackTrace();
                                            Message message = mHandler.obtainMessage();
                                            message.what = NETWORK_ERROR;
                                            mHandler.sendMessage(message);
                                        }
                                    }
                                }
                                catch (JSONException e){
                                    e.printStackTrace();
                                    Message message = mHandler.obtainMessage();
                                    message.what = TOKEN_RESULT_BAD;
                                    mHandler.sendMessage(message);
                                }
                            }else{
                                Message message = mHandler.obtainMessage();
                                message.what = TOKEN_RESULT_BAD;
                                mHandler.sendMessage(message);
                            }
                        }
                        catch (IOException ioe){
                            Message message = mHandler.obtainMessage();
                            message.what = TOKEN_RESULT_BAD;
                            mHandler.sendMessage(message);
                        }
                    }
        };
        thread.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        settings = Settings.getInstance(this);

        switchMoniteClipboard = (Switch) findViewById(R.id.switch_monite_clipboard);
        switchCancelAfterAdd = (Switch) findViewById(R.id.switch_cancel_after_add);
        switchLeftHandMode = (Switch) findViewById(R.id.left_hand_mode);
        textViewAbout = (TextView) findViewById(R.id.btn_about_and_support);
        textViewHelp = (TextView) findViewById(R.id.btn_help);
        textViewQuizLogin = (TextView) findViewById(R.id.btn_quiz_login);

        textViewAbout.setText(Html.fromHtml("<font color='red'>❤</font>" + getResources().getString(R.string.btn_about_and_support_str)));

        textViewQuizLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(settings.getQuizySetId() > 0){
                            new AlertDialog.Builder(LauncherActivity.this)
                                    .setTitle(R.string.confirm_relogin)
                                    //.setMessage("Do you really want to whatever?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            Intent intent = new Intent(LauncherActivity.this, AuthActivity.class);
                                            startActivityForResult(intent, AUTH_RESULT);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                        }
                        else

                    {
                        Intent intent = new Intent(LauncherActivity.this, AuthActivity.class);
                        startActivityForResult(intent, AUTH_RESULT);
                    }
                   }
                }
        );



        switchMoniteClipboard.setChecked(
                settings.getMoniteClipboardQ()
        );

        switchCancelAfterAdd.setChecked(
                settings.getAutoCancelPopupQ()
        );

        switchMoniteClipboard.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.setMoniteClipboardQ(isChecked);
                        if (isChecked) {
                            startCBService();
                        } else {
                            stopCBService();
                        }
                    }
                }
        );

        switchLeftHandMode.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.setLeftHandModeQ(isChecked);
                    }
                }
        );

        switchCancelAfterAdd.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.setAutoCancelPopupQ(isChecked);
                    }
                }
        );

        textViewAbout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LauncherActivity.this, AboutActivity.class);
                        startActivity(intent);
                    }
                }
        );

        textViewHelp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://zhuanlan.zhihu.com/p/25857322";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
        );

        if (settings.getMoniteClipboardQ()) {
            startCBService();
        }

        progressDialog = new ProgressDialog(LauncherActivity.this);
        // 设置进度条风格，风格为长形
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 标题
        progressDialog.setTitle(getString(R.string.str_wait));

        if(!settings.getUserName().isEmpty()){
            textViewQuizLogin.setText(getResources().getString(R.string.user_propt) + settings.getUserName());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", Integer.toString(resultCode));
        switch (requestCode){
            case (AUTH_RESULT):{
                if(resultCode == Activity.RESULT_OK){
                    String authCode = data.getStringExtra("code");
                    if(!authCode.isEmpty()){
                        getAuth(authCode);
                    }
                }
            }
        }
    }

    private void getAuth(final String authCode) {
        progressDialog.show();
        Thread thread = new Thread(){
            @Override
            public void run() {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .addHeader("Host", "api.quizlet.com")
                                .addHeader("Authorization","Basic " + Constant.QUIZLET_AUTH_STRING)
                                .url("https://api.quizlet.com/oauth/token")
                                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                                        "grant_type=authorization_code&code=" + authCode +
                                                "&redirect_uri=" + Constant.REDIRECT_URL))
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            if(response.code() == 200){
                                String json = response.body().string();
                                JSONObject data = null;
                                Log.e("json", json);
                                try{
                                    data = new JSONObject(json);
                                    String accessToken = data.optString("access_token");
                                    String userName = data.optString("user_id");
                                    settings.setAccessToken(accessToken);
                                    settings.setUserName(userName);
                                    Message message = mHandler.obtainMessage();
                                    message.what = TOKEN_RESULT_OK;
                                    mHandler.sendMessage(message);
                                }
                                catch (JSONException e){
                                    e.printStackTrace();
                                    Message message = mHandler.obtainMessage();
                                    message.what = TOKEN_RESULT_BAD;
                                    mHandler.sendMessage(message);
                                }
                            }else{
                                Message message = mHandler.obtainMessage();
                                message.what = TOKEN_RESULT_BAD;
                                mHandler.sendMessage(message);
                            }
                        }
                        catch (IOException ioe){
                            Message message = mHandler.obtainMessage();
                            message.what = TOKEN_RESULT_BAD;
                            mHandler.sendMessage(message);
                        }
                    }
        };
        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_about_menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void startCBService() {
        Intent intent = new Intent(this, CBWatcherService.class);
        startService(intent);
    }

    private void stopCBService() {
        Intent intent = new Intent(this, CBWatcherService.class);
        stopService(intent);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
}
