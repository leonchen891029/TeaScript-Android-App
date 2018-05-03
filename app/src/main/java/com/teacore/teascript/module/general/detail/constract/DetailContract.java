package com.teacore.teascript.module.general.detail.constract;

/**
 * MVP DetatilConstract接口类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-20
 */

public interface DetailContract {

    interface BasePresenter<Data, DataView extends BaseView> {

        // 获取当前数据
        Data getData();

        void hideLoading();

        void setDataView(DataView view);

        void setCommentCount(int cont);

    }

    interface BaseView {
        // 滚动到评论区域
        void scrollToComment();
    }
}
