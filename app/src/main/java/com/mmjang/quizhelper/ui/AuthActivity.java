package com.mmjang.quizhelper.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Contacts;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mmjang.quizhelper.R;
import com.mmjang.quizhelper.util.Constant;

public class AuthActivity extends AppCompatActivity {

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(Constant.QUIZLET_AUTH_URL);
        webView.setWebViewClient(
                new WebViewClient(){
                    boolean authComplete = false;
                    Intent resultIntent = new Intent();

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    String authCode;

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        Log.e("page finished", url);
                        if(url.contains("code=") && authComplete != true){
                            Uri uri = Uri.parse(url);
                            authCode = uri.getQueryParameter("code");
                            Toast.makeText(AuthActivity.this, authCode, Toast.LENGTH_SHORT).show();
                            resultIntent.putExtra("code", authCode);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                        else{
                            if(url.contains("error=")){
                                resultIntent.putExtra("code", ""); //auth code is empty
                                Toast.makeText(AuthActivity.this, R.string.access_denied, Toast.LENGTH_SHORT).show();
                                setResult(Activity.RESULT_OK, resultIntent);
                            }
                        }

                    }
                }
        );
    }
}
