// INoticeService.aidl
package com.teacore.teascript.service;

// Declare any non-default types here with import statements

interface INoticeService {
    void scheduleNotice();
    void requestNotice();
    void clearNotice(int uid,int type);
}
