package com.example.conga.tvo.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.conga.tvo.R;
import com.example.conga.tvo.htmltextview.HtmlTextView;
import com.example.conga.tvo.models.RssItem;
import com.example.conga.tvo.variables.Values;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import static android.os.Build.VERSION_CODES;

/**
 * Created by ConGa on 12/04/2016.
 */
public class ReadRssActivity extends AppCompatActivity {
    private static String TAG = ReadRssActivity.class.getSimpleName();
    private WebView webView;
    private ProgressDialog mProgressDialog;
    private String link;
    private String linkTag;
    public static Activity mActivity;
    public static Context mContext;
    private Button button;
    HtmlTextView text;
    String result;
    private View mCustomView;
    private int mOriginalSystemUiVisibility;
    private int mOriginalOrientation;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    protected FrameLayout mFullscreenContainer;
    private Handler mHandler;
    private  int mKey;
    private int mPosition;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on ctreate Read webpage");
        setContentView(R.layout.readrssitemlayout);
       // nhan data
        mKey = getIntent().getExtras().getInt(Values.key);
        mPosition = getIntent().getExtras().getInt(Values.position);
        RssItem item = Values.MAP.get(mKey).get(mPosition);
        text = (HtmlTextView) findViewById(R.id.html_text);
        // text.setRemoveFromHtmlSpace(true);
        setTitle(item.getTitle());
        //  mFloatingActionButton = (FloatingActionButton) findViewById(R.id.overview_floating_action_button);
        // button = (Button) findViewById(R.id.btn_ok);
        link = item.getLink();
//        linkTag =item.getLinkTag();
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new SaveContentRssAsyncTask().execute();
//            }
//        });
        webView = (WebView) findViewById(R.id.webView);
        setUpWebViewDefaults(webView);
//        mContext = getApplicationContext();
//        mActivity = ReadRssActivity.this;

//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setSupportZoom(true);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.setScrollbarFadingEnabled(false);
//        webView.setScrollBarStyle(webView.SCROLLBARS_OUTSIDE_OVERLAY);
//        webView.setInitialScale(1);
//        webView.getSettings().setLightTouchEnabled(true);
//        webView.getSettings().setSupportMultipleWindows(true);
//     //   webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//      //  webView.setWebViewClient(new MyWebViewClient());
//        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
//
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
//            webView.getSettings().setDisplayZoomControls(false);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            WebView.setWebContentsDebuggingEnabled(true);
//        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(link);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            //
//            @Override
//            public Bitmap getDefaultVideoPoster() {
//
//                return BitmapFactory.decodeResource(getApplicationContext().getResources(),
//                        R.drawable.video_poster);
//            }

            @Override
            public void onShowCustomView(View view,
                                         WebChromeClient.CustomViewCallback callback) {
                // if a view already exists then immediately terminate the new one
                if (mCustomView != null) {
                    onHideCustomView();
                    return;
                }

                // 1. Stash the current state
                mCustomView = view;
                mOriginalSystemUiVisibility =getWindow().getDecorView().getSystemUiVisibility();
                mOriginalOrientation = getRequestedOrientation();

                // 2. Stash the custom view callback
                mCustomViewCallback = callback;

                // 3. Add the custom view to the view hierarchy
                FrameLayout decor = (FrameLayout)getWindow().getDecorView();
                decor.addView(mCustomView, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));


                // 4. Change the state of the window
               getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_IMMERSIVE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onHideCustomView() {
                // 1. Remove the custom view
                FrameLayout decor = (FrameLayout)getWindow().getDecorView();
                decor.removeView(mCustomView);
                mCustomView = null;

                // 2. Restore the state to it's original form
                getWindow().getDecorView()
                        .setSystemUiVisibility(mOriginalSystemUiVisibility);
                setRequestedOrientation(mOriginalOrientation);

                // 3. Call the custom view callback
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;

            }



            //
//permission request API in WebChromeClient:
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                Log.d(TAG, "onPermissionRequest");
                runOnUiThread(new Runnable() {
                    @TargetApi(VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        if (request.getOrigin().toString().equals(link)) {
                            request.grant(request.getResources());
                        } else {
                            request.deny();
                        }
                    }
                });
            }

        });



    }



    private void setUpWebViewDefaults(WebView webView) {
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        WebSettings settings = webView.getSettings();
        webView.getSettings().setSupportZoom(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        // Enable Javascript
        settings.setJavaScriptEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);
                settings.setSupportMultipleWindows(true);

        if (Build.VERSION.SDK_INT > VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.setWebViewClient(new WebViewClient());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie( true);
    }

        class MyWebViewClient extends WebViewClient {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                mProgressDialog = ProgressDialog.show(getApplicationContext(), "", "loading");
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                Log.d("finish", url);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

            private static final String APP_SCHEME = "example-app:";

//    @Override
//    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        if (url.startsWith(APP_SCHEME)) {
//            String urlData=null;
//            try {
//                urlData = URLDecoder.decode(url.substring(APP_SCHEME.length()), "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            respondToData(urlData);
//            return true;
//        }
//        return false;
//    }


//    }
//
//    private class MyWebChromeClient extends WebChromeClient {
//
//        @Override
//        public void onProgressChanged(WebView view, int newProgress) {
//            super.onProgressChanged(view, newProgress);
//          //  mProgressDialog = ProgressDialog.show(ReadRssActivity.this, "" ,"loading");
////            mActivity.setProgress(newProgress * 1000);
////
//            if (newProgress == 100 && mProgressDialog.isShowing()) {
//                mProgressDialog.dismiss();
//            }
//        }
//
//        @Override
//        public boolean onJsAlert(WebView view, String url, String mes"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623sage, JsResult result) {
//
//            Log.d("finish", url);
//               return super.onJsAlert(view, url, message, result);
//
//        }
        }

        //
        private class SaveContentRssAsyncTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (linkTag.contains("vnexpress.net")) {
                        Document document = Jsoup.connect(linkTag).
                                userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36").
                                get();
                        Elements elements = document.select("div [class= fck_detail width_common]");
                        result = elements.toString();
                    }
                    if (linkTag.contains("www.24h.com")) {
                        Document document = Jsoup.connect(linkTag).
                                userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36").get();
                        Elements elements = document.select("div.text-conent");
                        result = elements.toString();
                    }
                    if (linkTag.contains("dantri.com.vn")) {
                        Document document = Jsoup.connect(linkTag).
                                userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36").get();
                        Elements elements = document.select("div.VCSortableInPreviewMode");
                        result = elements.toString();
                    }
                    if (linkTag.contains("vietnamnet.vn")) {

                        Document document = Jsoup.connect(linkTag).
                                userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36").get();
                        Elements elements = document.select("div.ArticleDetail");
                        result = elements.toString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                // text.setHtmlFromString(result, new com.example.conga.tvo.htmltextview.HtmlTextView.RemoteImageGetter(null));
                text.setHtmlFromString(result, new HtmlTextView.RemoteImageGetter());
                Toast.makeText(mContext, "" + linkTag, Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "" + text, Toast.LENGTH_SHORT).show();
                Log.d("error", text + "");
            }
        }

    //xem lại trang đã xem trước đó
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // Check if the key event was the Back button and if there's history
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
//           webView.goBack();
//            return true;
//        }
//        return super.getActivity().onKeyDown(keyCode, event);
//    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "START READRSSACTIVITY");

    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "PAUSE READRSSACTIVITY");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "STOP READRSSACTIVITY");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "RESTART READRSSACTIVITY");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "DESTROY READRSSACTIVITY");

    }
// HANDLE PHẦN BACK LẠI , TRỞ VỀ TRANG BÁO TRƯỚC ĐÓ , KHÔNG PHẢI LÀ THOÁT LUÔN
    // HANDLE LẠI PHẦN VIDEO , CÓ VẤN ĐỀ Ở ĐÂY : NHẤN BACK , XOAY POTRAIT LÀ TRỞ VỀ TRẠNG THÁI BAN ĐẦU ,
    // HANDLE PHẦN CHECK MẠNG Ở ĐÂY , ĐANG ĐỌC MÀ MẤT MẠNG

}
