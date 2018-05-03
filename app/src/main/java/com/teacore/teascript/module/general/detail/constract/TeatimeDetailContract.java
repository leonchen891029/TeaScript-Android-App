package com.teacore.teascript.module.general.detail.constract;

import com.teacore.teascript.bean.Comment;
import com.teacore.teascript.bean.Teatime;
import com.teacore.teascript.bean.User;

/**
 * TeatimeDetail Constract
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-20
 */

public interface TeatimeDetailContract {


    interface Presenter {

        Teatime getTeatime();

        void toReply(Comment comment);

        void onScroll();
    }

    interface ICommentView {

        void onCommentSuccess(Comment comment);

    }

    interface IThumbupView {

        void onLikeSuccess(boolean isUp, User user);

    }

    interface IAgencyView {

        void resetLikeCount(int count);

        void resetCmnCount(int count);
    }

}
