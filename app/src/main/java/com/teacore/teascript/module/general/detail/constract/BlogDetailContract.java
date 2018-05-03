package com.teacore.teascript.module.general.detail.constract;

import com.teacore.teascript.module.general.bean.BlogDetail;
import com.teacore.teascript.module.general.bean.Comment;

/**
 * BlogDetail Const
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-20
 */

public interface BlogDetailContract {

    interface BlogPresenter extends DetailContract.BasePresenter<BlogDetail,BlogView> {
        // 收藏
        void toFavorite();

        // 分享
        void toShare();

        // 关注
        void toFollow();

        // 提交评价
        void toSendComment(long id, long commentId, long commentAuthorId, String comment);
    }

    interface BlogView extends DetailContract.BaseView {
        void toFavoriteOk(BlogDetail blogDetail);

        void toFollowOk(BlogDetail blogDetail);

        void toSendCommentOk(Comment comment);
    }


}
