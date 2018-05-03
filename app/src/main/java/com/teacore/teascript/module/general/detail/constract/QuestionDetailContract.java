package com.teacore.teascript.module.general.detail.constract;

import com.teacore.teascript.module.general.bean.CommentQ;
import com.teacore.teascript.module.general.bean.QuestionDetail;

/**
 * QuestionDetail Contract
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-20
 */

public interface QuestionDetailContract {

    interface QuestionPresenter extends DetailContract.BasePresenter<QuestionDetail, QuestionView> {

        // 收藏
        void toFavorite();

        // 分享
        void toShare();

        // 举报
        void toReport();

        // 提交评价
        void toSendComment(long id, long commentId, long commentAuthorId, String comment);
    }

    interface QuestionView extends DetailContract.BaseView {
        void toFavoriteOk(QuestionDetail questionDetail);

        void toSendCommentOk(CommentQ commentEX);
    }
}
