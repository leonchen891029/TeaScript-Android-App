package com.teacore.teascript.util;

import android.app.Activity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.Notebook;
import com.teacore.teascript.database.NotebookDatabase;
import com.teacore.teascript.network.TSHttpClient;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * 一个文件云同步解决方案（便签同步）
 *
 * @author kymjs (https://github.com/kymjs)
 */
public class SynchronizeController {

    public interface SynchronizeListener {
        public void onSuccess(byte[] arg2);

        public void onFailure();
    }

    private NotebookDatabase noteDb;
    private List<Notebook> localDatas;
    private Activity aty;
    private static long sPreviousRefreshTime = 0; // 为了流浪考虑，不能刷新过于频繁

    public void doSynchronize(Activity aty, SynchronizeListener cb) {
        noteDb = new NotebookDatabase(aty);
        localDatas = noteDb.query();
        this.aty = aty;

        doSynchronizeWithWIFI(cb);
    }

    /**
     * GPRS环境下：使用二重循环遍历差异文件并更新云端文件达到同步
     */
    private void doSynchronizeWithGPRS() {
    }

    /**
     * WIFI环境下：客户端直接提交全部文件，交由服务器做同步判断<br>
     * 由于量可能会比较大，采用json传输，而不是from
     *
     * @author kymjs (https://github.com/kymjs)
     */
    private void doSynchronizeWithWIFI(final SynchronizeListener cb) {
        long currentTime = System.currentTimeMillis();
        // 为流量和服务器考虑，限制请求频率，一分钟刷新一次
        if (currentTime - sPreviousRefreshTime < 60000) {
            return;
        } else {
            // sPreviousRefreshTime = currentTime;
        }

        int uid = AppContext.getInstance().getLoginUid();
        StringBuilder jsonData = new StringBuilder();
        int size = localDatas.size();
        jsonData.append("{\"uid\":").append(uid).append(",\"stickys\":[");
        boolean isFirst = true;
        for (int i = 0; i < size; i++) {
            Notebook data = localDatas.get(i);
            if (isFirst) {
                isFirst = false;
            } else {
                jsonData.append(",");
            }
            jsonData.append("{");
            jsonData.append("\"id\":").append(data.getId()).append(",");
            jsonData.append("\"iid\":").append(data.getIid()).append(",");
            String content = data.getContent();
            content.replaceAll("\"", "\\\"");
            jsonData.append("\"content\":\"").append(content).append("\",");
            jsonData.append("\"color\":\"").append(data.getColorText())
                    .append("\",");

            if (StringUtils.isEmpty(data.getServerUpdateTime())) {
                data.setServerUpdateTime(TimeUtils
                        .getSystemTime("yyyy-MM-dd HH:mm:ss"));
            }

            jsonData.append("\"createtime\":\"")
                    .append(data.getServerUpdateTime()).append("\",");
            jsonData.append("\"updatetime\":\"").append(
                    data.getServerUpdateTime());
            jsonData.append("\"}");
        }
        jsonData.append("]}");

//        KJLoger.debug("==" + jsonData.toString());

        AsyncHttpClient client = TSHttpClient.getHttpClient();
        client.addHeader("Content-Type", "application/json; charset=UTF-8");
        client.post(aty, TSHttpClient.getAbsoluteApiUrl("action/api/team_stickynote_batch_update"),
                new StringEntity(jsonData.toString(), HTTP.UTF_8), "application/json; charset=UTF-8",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1,
                                          byte[] arg2) {
                        cb.onSuccess(arg2);
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1,
                                          byte[] arg2, Throwable arg3) {
                        cb.onFailure();
                    }

                });
        TSHttpClient.setHttpClient(client);
    }
}