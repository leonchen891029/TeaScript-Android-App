package com.teacore.teascript.module.general.detail.constract;

import com.teacore.teascript.module.general.bean.Comment;
import com.teacore.teascript.module.general.bean.TranslationDetail;

/**
 * TranslateDetail Constract
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-20
 */

public interface TranslationDetailContract {

    interface TransPresenter extends DetailContract.BasePresenter<TranslationDetail, TransView> {

        // 收藏
        void toFavorite();

        // 分享
        void toShare();

        // 提交评价
        void toSendComment(long id, long commentId, long commentAuthorId, String comment);
    }

    interface TransView extends DetailContract.BaseView {

        void toFavoriteOk(TranslationDetail translationDetail);

        void toSendCommentOk(Comment comment);
    }











}
