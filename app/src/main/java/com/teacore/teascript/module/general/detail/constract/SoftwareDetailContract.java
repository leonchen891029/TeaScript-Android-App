package com.teacore.teascript.module.general.detail.constract;

import com.teacore.teascript.module.general.bean.SoftwareDetail;

/**
 * SoftwareDetail Constract
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-20
 */

public interface SoftwareDetailContract {

    interface SoftwarePresenter extends DetailContract.BasePresenter<SoftwareDetail, SoftwareView> {

        // 收藏
        void toFavorite();

        // 分享
        void toShare();
    }

    interface SoftwareView extends DetailContract.BaseView {

        void toFavoriteOk(SoftwareDetail softwareDetail);

    }

}
