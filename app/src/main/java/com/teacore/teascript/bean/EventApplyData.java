package com.teacore.teascript.bean;


/**活动报名实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-2
 */
public class EventApplyData extends Entity{

    //活动的id
    private int eventId;
    //用户的id
    private int userId;
    //用户的名称
    private String name;
    //用户的性别
    private String gender;
    //用户的电话
    private String mobile;
    //用户的单位
    private String company;
    //用户的职业
    private String job;
    //活动的备注
    private String remark;


    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return mobile;
    }

    public void setPhone(String phone) {
        this.mobile = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


}
