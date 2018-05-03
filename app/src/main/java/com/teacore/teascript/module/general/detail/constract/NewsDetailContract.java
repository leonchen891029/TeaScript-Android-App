package com.teacore.teascript.module.general.detail.constract;

import com.teacore.teascript.module.general.bean.Comment;
import com.teacore.teascript.module.general.bean.NewsDetail;

/**
 * NewsDetail Contract
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-20
 */

public interface NewsDetailContract {

    interface NewsPresenter extends DetailContract.BasePresenter<NewsDetail,NewsView> {

        // 收藏
        void toFavorite();

        // 分享
        void toShare();

        // 提交评论
        void toSendComment(long id, long commentId, long commentAuthorId, String comment);

    }


    interface NewsView extends DetailContract.BaseView {

        void toFavoriteOk(NewsDetail newsDetail);

        //void toFollowOk(NewsDetail newsDetail);

        void toSendCommentOk(Comment comment);

    }

}
