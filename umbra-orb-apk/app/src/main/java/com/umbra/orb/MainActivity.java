package com.umbra.orb;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 暗星之球 · 安卓外壳
 * 关键点：WebView 关闭一切缩放通道（捏合、双击、控件、文本缩放），
 * 页面固定全屏，不存在地址栏/工具栏，因此不存在"还能缩放"的可能。
 */
public class MainActivity extends Activity {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUi();

        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);          // 存档（localStorage）需要
        s.setDatabaseEnabled(true);

        /* ---- 缩放全部关掉 ---- */
        s.setSupportZoom(false);               // 关掉双指捏合 / 双击缩放
        s.setBuiltInZoomControls(false);       // 不加载内置缩放手势
        s.setDisplayZoomControls(false);       // 不显示 +/- 缩放按钮
        s.setTextZoom(100);                    // 跟随系统字体放大的通道一起堵死
        s.setUseWideViewPort(false);           // 不启用宽视口，避免"整页缩放"式的自适应
        s.setLoadWithOverviewMode(false);

        s.setMediaPlaybackRequiresUserGesture(false);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);

        web.setOverScrollMode(View.OVER_SCROLL_NEVER);   // 去掉边缘回弹/发光
        web.setVerticalScrollBarEnabled(false);
        web.setHorizontalScrollBarEnabled(false);
        web.setBackgroundColor(0xFF04060F);
        web.setWebViewClient(new WebViewClient());

        /* 资源打包在 APK 内，离线可玩，无需任何网络 */
        web.loadUrl("file:///android_asset/index.html");
        setContentView(web);
    }

    /** 沉浸式全屏：隐藏状态栏与导航栏，从顶部下拉才出现，随后自动隐藏 */
    @SuppressWarnings("deprecation")
    private void hideSystemUi() {
        View d = getWindow().getDecorView();
        d.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
        if (web != null) web.onResume();
    }

    @Override
    protected void onPause() {
        if (web != null) web.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.loadUrl("about:blank");
            web.destroy();
            web = null;
        }
        super.onDestroy();
    }
}
