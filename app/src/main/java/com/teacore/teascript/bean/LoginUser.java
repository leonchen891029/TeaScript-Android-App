package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**登录用户实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-17.
 */
@XStreamAlias("loginUser")
public class LoginUser extends Entity{

    @XStreamAlias("user")
    private User user;

    @XStreamAlias("result")
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
