package com.teacore.teascript.module.general.detail.constract;

import com.teacore.teascript.bean.EventApplyData;
import com.teacore.teascript.module.general.bean.EventDetail;

/**
 * EventDetail Contract
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-20
 */

public interface EventDetailContract {

    interface Operator extends DetailContract.BasePresenter<EventDetail, BaseView> {

        void toFavorite();

        void toSignUp(EventApplyData data);
    }

    interface BaseView extends DetailContract.BaseView {

        void toFavoriteOk(EventDetail detail);

        void toSignUpOk(EventDetail detail);
    }

}
