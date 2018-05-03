package com.teacore.teascript.interfaces;

/**监听webview上的图片
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-12
 */

public interface OnWebViewImageListener {

    //点击webview上的图片，展示大图的缩略图，传入大图Url
    void showImagePreview(String bigImageUrl);

}
