package com.teacore.teascript.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.teacore.teascript.bean.Notebook;

import java.util.ArrayList;
import java.util.List;

/**
 * 便签数据库
 * @author  陈晓帆
 * @version 1.0
 * Created 2017-4-30
 */

public class NotebookDatabase {

    private final DatabaseHelper mHelper;

    public NotebookDatabase(Context context){
        mHelper=new DatabaseHelper(context);
    }

    //增操作
    public void insert(Notebook data){
        String sql="insert into "+DatabaseHelper.NOTE_TABLE_NAME;

        sql += "(_id, iid, time, date, content, color) values(?, ?, ?, ?, ?, ?)";

        SQLiteDatabase sqlite = mHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[] { data.getId() + "",
                data.getIid() + "", data.getUnixTime() + "", data.getDate(),
                data.getContent(), data.getColor() + "" });
        sqlite.close();
    }

    //删操作
    public void delete(int id) {
        SQLiteDatabase sqlite = mHelper.getWritableDatabase();
        String sql = ("delete from " + DatabaseHelper.NOTE_TABLE_NAME + " where _id=?");
        sqlite.execSQL(sql, new Integer[] { id });
        sqlite.close();
    }

    //更新操作
    public void update(Notebook data) {
        SQLiteDatabase sqlite = mHelper.getWritableDatabase();
        String sql = ("update " + DatabaseHelper.NOTE_TABLE_NAME + " set iid=?, time=?, date=?, content=?, color=? where _id=?");
        sqlite.execSQL(sql,
                new String[] { data.getIid() + "", data.getUnixTime() + "",
                        data.getDate(), data.getContent(),
                        data.getColor() + "", data.getId() + "" });
        sqlite.close();
    }

    public List<Notebook> query() {
        return query(" ");
    }
    //差操作
    public List<Notebook> query(String where) {

        SQLiteDatabase sqlite = mHelper.getReadableDatabase();

        ArrayList<Notebook> data =new ArrayList<Notebook>();

        Cursor cursor = sqlite.rawQuery("select * from "
                + DatabaseHelper.NOTE_TABLE_NAME + where, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Notebook notebookData = new Notebook();
            notebookData.setId(cursor.getInt(0));
            notebookData.setIid(cursor.getInt(1));
            notebookData.setUnixTime(cursor.getString(2));
            notebookData.setDate(cursor.getString(3));
            notebookData.setContent(cursor.getString(4));
            notebookData.setColor(cursor.getInt(5));
            data.add(notebookData);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();

        return data;
    }

    //重置数据
    public void reset(List<Notebook> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = mHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + DatabaseHelper.NOTE_TABLE_NAME);
            // 重新添加
            for (Notebook data : datas) {
                insert(data);
            }
            sqlite.close();
        }
    }

    //保存一条数据到本地(如果已经存在则直接覆盖)
    public void save(Notebook data) {
        List<Notebook> datas = query(" where _id=" + data.getId());
        if (datas != null && !datas.isEmpty()) {
            update(data);
        } else {
            insert(data);
        }
    }

    public void destroy() {
        mHelper.close();
    }

}
