package com.mmjang.quizhelper.ui.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.quizhelper.MyApplication;
import com.mmjang.quizhelper.R;
import com.mmjang.quizhelper.anki.AnkiDroidHelper;
import com.mmjang.quizhelper.data.Settings;
import com.mmjang.quizhelper.data.dict.Collins;
import com.mmjang.quizhelper.data.dict.Definition;
import com.mmjang.quizhelper.data.dict.DictionaryRegister;
import com.mmjang.quizhelper.data.dict.IDictionary;
import com.mmjang.quizhelper.data.model.UserTag;
import com.mmjang.quizhelper.data.plan.OutputPlan;
import com.mmjang.quizhelper.domain.CBWatcherService;
import com.mmjang.quizhelper.domain.PlayAudioManager;
import com.mmjang.quizhelper.domain.PronounceManager;
import com.mmjang.quizhelper.ui.LauncherActivity;
import com.mmjang.quizhelper.ui.widget.BigBangLayout;
import com.mmjang.quizhelper.ui.widget.BigBangLayoutWrapper;
import com.mmjang.quizhelper.util.Constant;
import com.mmjang.quizhelper.util.FieldUtil;
import com.mmjang.quizhelper.util.StringUtil;
import com.mmjang.quizhelper.util.TextSplitter;
import com.mmjang.quizhelper.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.mmjang.quizhelper.util.FieldUtil.getBlankSentence;
import static com.mmjang.quizhelper.util.FieldUtil.getBoldSentence;


public class PopupActivity extends Activity implements BigBangLayoutWrapper.ActionListener {

    List<IDictionary> dictionaryList;
    IDictionary currentDicitonary;
    Settings settings;
    String mTextToProcess;
    String mCurrentKeyWord;
    TextSplitter mTextSplitter;
    String mTargetWord;
    String mFbReaderBookmarkId;
    //views
    AutoCompleteTextView act;
    Button btnSearch;
    Button btnPronounce;
    //RecyclerView recyclerViewDefinitionList;
    ProgressBar progressBar;
    //plan b
    LinearLayout viewDefinitionList;

    ProgressDialog progressDialog;
    //async event
    private static final int PROCESS_DEFINITION_LIST = 1;
    private static final int ASYNC_SEARCH_FAILED = 2;
    private static final int SET_ADDED = 3;
    private static final int SET_ADD_FAILED = 4;
    //async
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESS_DEFINITION_LIST:
                    showSearchButton();
                    List<Definition> definitionList = (List<Definition>) msg.obj;
                    processDefinitionList(definitionList);
                    break;
                case ASYNC_SEARCH_FAILED:
                    showSearchButton();
                    Toast.makeText(PopupActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case SET_ADDED:
                    progressDialog.hide();
                    ImageButton btn = (ImageButton) msg.obj;
                    btn.setBackgroundResource(R.drawable.ic_add_grey);
                    btn.setEnabled(false);
                    Toast.makeText(PopupActivity.this, R.string.term_added, Toast.LENGTH_SHORT).show();
                    break;
                case SET_ADD_FAILED:
                    progressDialog.hide();
                    String message = (String) msg.obj;
                    Toast.makeText(PopupActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    private BigBangLayout bigBangLayout;
    private BigBangLayoutWrapper bigBangLayoutWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        setContentView(R.layout.activity_popup);
        //set animation
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        //
        assignViews();
        initBigBangLayout();
        loadData(); //dictionaryList;
        setActAdapter(currentDicitonary);
        setEventListener();
        handleIntent();
        if (settings.getMoniteClipboardQ()) {
            startCBService();
        }

        bigBangLayout.post(new Runnable() {
            @Override
            public void run() {
                setTargetWord();
            }
        });

    }

    private void setTargetWord() {
        if (!TextUtils.isEmpty(mTargetWord)) {
            for (BigBangLayout.Line line : bigBangLayout.getLines()) {
                List<BigBangLayout.Item> items = line.getItems();
                for (BigBangLayout.Item item : items) {
                    if (item.getText().equals(mTargetWord)) {
                        item.setSelected(true);
                    }
                }
            }
            act.setText(mTargetWord);
            asyncSearch(mTargetWord);
        }
    }

    private void setStatusBarColor() {
        int statusBarColor = 0;
        if (Build.VERSION.SDK_INT >= 21) {
            statusBarColor = getWindow().getStatusBarColor();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(statusBarColor);
        }
    }

    private void assignViews() {
        act = (AutoCompleteTextView) findViewById(R.id.edit_text_hwd);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnPronounce = ((Button) findViewById(R.id.btn_pronounce));
        //recyclerViewDefinitionList = (RecyclerView) findViewById(R.id.recycler_view_definition_list);
        viewDefinitionList = (LinearLayout) findViewById(R.id.view_definition_list);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        bigBangLayout = (BigBangLayout) findViewById(R.id.bigbang);
        bigBangLayoutWrapper = (BigBangLayoutWrapper) findViewById(R.id.bigbang_wrapper);
        progressDialog = new ProgressDialog(PopupActivity.this);
        // 设置进度条风格，风格为长形
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 标题
        progressDialog.setTitle(getString(R.string.str_wait));
    }

    private void loadData() {
        dictionaryList = DictionaryRegister.getDictionaryObjectList();
        settings = Settings.getInstance(this);
        //check if is logined
        if (settings.getQuizySetId() == 0) {
            Toast.makeText(this, R.string.no_login_message, Toast.LENGTH_SHORT).show();
        }
        currentDicitonary = new Collins(this);
    }


    private void initBigBangLayout() {
        bigBangLayout.setShowSymbol(true);
        bigBangLayout.setShowSpace(true);
        bigBangLayout.setItemSpace(0);
        bigBangLayout.setLineSpace(0);
        bigBangLayout.setTextPadding(5);
        bigBangLayout.setTextPaddingPort(5);
        bigBangLayoutWrapper.setStickHeader(true);
        bigBangLayoutWrapper.setActionListener(this);

    }

    private void setEventListener() {

        //auto finish
        Button btnCancelBlank = (Button) findViewById(R.id.btn_cancel_blank);
        Button btnCancelBlankAboveCard = (Button) findViewById(R.id.btn_cancel_blank_above_card);
        btnCancelBlank.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
        btnCancelBlankAboveCard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        btnSearch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String word = act.getText().toString();
                        if (!word.isEmpty()) {
                            asyncSearch(word);
                            Utils.hideSoftKeyboard(PopupActivity.this);
                        }
                    }
                }
        );

        btnPronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String word = act.getText().toString();
                PlayAudioManager.playPronounceVoice(PopupActivity.this, word);
            }
        });

    }

    private IDictionary getDictionaryFromOutputPlan(OutputPlan outputPlan) {
        String dictionaryName = outputPlan.getDictionaryKey();
        for (IDictionary dict : dictionaryList) {
            if (dict.getDictionaryName().equals(dictionaryName)) {
                return dict;
            }
        }
        return null;
    }

    private void processDefinitionList(List<Definition> definitionList) {
        if (definitionList.isEmpty()) {
            Toast.makeText(this, R.string.definition_not_found, Toast.LENGTH_SHORT).show();
        } else {
//            DefinitionAdapter defAdapter = new DefinitionAdapter(PopupActivity.this, definitionList, mTextSplitter, currentOutputPlan);
//            LinearLayoutManager llm = new LinearLayoutManager(this);
//            //llm.setAutoMeasureEnabled(true);
//            recyclerViewDefinitionList.setLayoutManager(llm);
//            //recyclerViewDefinitionList.getRecycledViewPool().setMaxRecycledViews(0,0);
//            //recyclerViewDefinitionList.setHasFixedSize(true);
//            //recyclerViewDefinitionList.setNestedScrollingEnabled(false);
//            recyclerViewDefinitionList.setAdapter(defAdapter);
            viewDefinitionList.removeAllViewsInLayout();
            for (Definition def : definitionList) {
                viewDefinitionList.addView(getCardFromDefinition(def));
            }
        }
    }


    private void handleIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (intent == null) {
            return;
        }
        if (type == null) {
            return;
        }
        //getStringExtra() may return null
        if (Intent.ACTION_SEND.equals(action) && type.equals("text/plain")) {
            mTextToProcess = intent.getStringExtra(Intent.EXTRA_TEXT);
            mTargetWord = intent.getStringExtra(Constant.INTENT_ANKIHELPER_TARGET_WORD);
            //mFbReaderBookmarkId = intent.getStringExtra(Constant.INTENT_ANKIHELPER_FBREADER_BOOKMARK_ID);
            String noteEditedByUser = intent.getStringExtra(Constant.INTENT_ANKIHELPER_NOTE);
            String updateId = intent.getStringExtra(Constant.INTENT_ANKIHELPER_NOTE_ID);
        }
        if(Intent.ACTION_PROCESS_TEXT.equals(action)&&type.equals("text/plain"))
        {
        mTextToProcess = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT);
        }
        if(mTextToProcess ==null)
        {
        return;
        }
        populateWordSelectBox();
    }

    private void populateWordSelectBox() {
        List<String> localSegments = TextSplitter.getLocalSegments(mTextToProcess);
        for (String localSegment : localSegments) {
            bigBangLayout.addTextItem(localSegment);
        }
        ;
    }


    private void asyncSearch(final String word) {
        if (word.length() == 0) {
            showPronounce(false);
            return;
        }
        showProgressBar();
        showPronounce(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Your code goes here
                    Log.d("clicked", "yes");
                    List<Definition> d = currentDicitonary.wordLookup(word);
                    Message message = mHandler.obtainMessage();
                    message.obj = d;
                    message.what = PROCESS_DEFINITION_LIST;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    String error = e.getMessage();
                    Message message = mHandler.obtainMessage();
                    message.obj = error;
                    message.what = ASYNC_SEARCH_FAILED;
                    mHandler.sendMessage(message);
                }
            }
        });
        thread.start();
    }

    private void setActAdapter(IDictionary dict) {
        SimpleCursorAdapter sca = (SimpleCursorAdapter) dict.getAutoCompleteAdapter(PopupActivity.this,
                android.R.layout.simple_spinner_dropdown_item);
        if (sca != null) {
            act.setAdapter(sca);
        }
    }

    //plan B
    private View getCardFromDefinition(final Definition def) {
        View view;
        if(settings.getLeftHandModeQ()){
            view = LayoutInflater.from(PopupActivity.this)
                    .inflate(R.layout.definition_item_left, null);
        }
        else{
            view = LayoutInflater.from(PopupActivity.this)
                    .inflate(R.layout.definition_item, null);
        }
        final TextView textVeiwDefinition = (TextView) view.findViewById(R.id.textview_definition);
        final ImageButton btnAddDefinition = (ImageButton) view.findViewById(R.id.btn_add_definition);
        final LinearLayout btnAddDefinitionLarge = (LinearLayout) view.findViewById(R.id.btn_add_definition_large);
        btnAddDefinitionLarge.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnAddDefinition.callOnClick();
                    }
                }
        );
        //final Definition def = mDefinitionList.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textVeiwDefinition.setText(Html.fromHtml(def.getDisplayHtml(), Html.FROM_HTML_MODE_COMPACT));
        }
        else{
            textVeiwDefinition.setText(Html.fromHtml(def.getDisplayHtml()));

        }
        //holder.itemView.setAnimation(AnimationUtils.loadAnimation(mActivity, android.R.anim.fade_in));
        //holder.textVeiwDefinition.setTextColor(Color.BLACK);
        btnAddDefinition.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ImageButton btn = (ImageButton) v.findViewById(R.id.btn_add_definition);
                        vibarate(Constant.VIBRATE_DURATION);
                        //placeholder
                        String blankedDef =  StringUtil.boldToBlank(def.getExportElement("释义"))
                                .replaceAll("<b>(.+?)</b>", "___")
                                .replaceAll("[\n]+","")
                                .replaceAll("[\r]+","")
                                .replaceAll("<br/>","")
                                .replaceAll("<span .+?>","")
                                .replaceAll("</span>","")
                                .replaceAll("<i>","")
                                .replaceAll("</i>","");
                        String blankedSetence = StringUtil.boldToBlank(getBoldSentence(bigBangLayout.getLines()));
                        final String defString = blankedSetence + "\n\n(" + blankedDef + ")";
                        Log.d("btn add", defString);
                        final String term = act.getText().toString();
                        progressDialog.show();
                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .addHeader("Host", "api.quizlet.com")
                                        .addHeader("Authorization","Bearer " + settings.getAccessToken())
                                        .url("https://api.quizlet.com/2.0/sets/"+ settings.getQuizySetId() + "/terms")
                                        .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                                                "whitespace=true" +
                                                        "&term=" + term +
                                                        "&definition=" + defString))
                                        .build();
                                try {
                                    Response response = client.newCall(request).execute();
                                    if(response.code() == 201){
                                        Message message = mHandler.obtainMessage();
                                        message.what = SET_ADDED;
                                        message.obj = btn;
                                        mHandler.sendMessage(message);
                                    }
                                    else{
                                        Message message = mHandler.obtainMessage();
                                        message.what = SET_ADD_FAILED;
                                        message.obj = response.body().string();
                                        mHandler.sendMessage(message);
                                    }
                                }
                                catch (IOException ioe){
                                    Message message = mHandler.obtainMessage();
                                    message.what = SET_ADD_FAILED;
                                    message.obj = getResources().getText(R.string.network_error);
                                    mHandler.sendMessage(message);
                                }
                            }
                        };
                        thread.start();
                    }
                });
        return view;
    }
    //cancel auto completetextview focus
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof AutoCompleteTextView) {
            View currentFocus = getCurrentFocus();
            int screenCoords[] = new int[2];
            currentFocus.getLocationOnScreen(screenCoords);
            float x = event.getRawX() + currentFocus.getLeft() - screenCoords[0];
            float y = event.getRawY() + currentFocus.getTop() - screenCoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < currentFocus.getLeft() ||
                    x >= currentFocus.getRight() ||
                    y < currentFocus.getTop() ||
                    y > currentFocus.getBottom())) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                v.clearFocus();
            }
        }
        return ret;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    private void startCBService() {
        Intent intent = new Intent(this, CBWatcherService.class);
        startService(intent);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        btnSearch.setVisibility(View.GONE);
    }

    private void showSearchButton() {
        progressBar.setVisibility(View.GONE);
        btnSearch.setVisibility(View.VISIBLE);
    }

    private void showPronounce(boolean shouldShow) {
        btnPronounce.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSelected(String text) {
        String currentWord = FieldUtil.getSelectedText(bigBangLayout.getLines());
        if (!currentWord.equals(act.getText().toString())) {
            mCurrentKeyWord = currentWord;
            act.setText(currentWord);
            asyncSearch(currentWord);
        }
    }

    @Override
    public void onSearch(String text) {

    }

    @Override
    public void onShare(String text) {

    }

    @Override
    public void onCopy(String text) {

    }

    @Override
    public void onTrans(String text) {

    }

    @Override
    public void onDrag() {

    }

    @Override
    public void onSwitchType(boolean isLocal) {

    }

    @Override
    public void onSwitchSymbol(boolean isShow) {

    }

    @Override
    public void onSwitchSection(boolean isShow) {

    }

    @Override
    public void onDragSelection() {

    }

    @Override
    public void onCancel() {
        act.setText("");
        asyncSearch("");
    }

    void vibarate(int ms) {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(ms);
    }
}
