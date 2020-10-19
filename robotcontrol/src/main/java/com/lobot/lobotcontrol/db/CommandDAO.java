package com.lobot.lobotcontrol.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.lobot.lobotcontrol.model.CommandModel;
import com.lobot.lobotcontrol.uitls.LogUtil;
import java.util.ArrayList;
import java.util.List;

public class CommandDAO
{
    private DBHelper dbHelper;

    public CommandDAO(Context paramContext)
    {
        this.dbHelper = new DBHelper(paramContext, null);
    }

    public boolean delete(int paramInt)
    {
        SQLiteDatabase localSQLiteDatabase = this.dbHelper.getWritableDatabase();
        boolean bool = true;
        if (localSQLiteDatabase.delete("command", "id=?", new String[] { String.valueOf(paramInt) }) != 1L) {
            bool = false;
        }
        return bool;
    }

    public boolean insert(CommandModel paramCommandModel)
    {
        SQLiteDatabase localSQLiteDatabase = this.dbHelper.getWritableDatabase();
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title_en", paramCommandModel.getTitleEn());
        localContentValues.put("title", paramCommandModel.getTitle());
        localContentValues.put("title_tw", paramCommandModel.getTitleTw());
        localContentValues.put("action", String.valueOf(paramCommandModel.getAction()));
        localContentValues.put("can_edit", Boolean.valueOf(paramCommandModel.isCanEdit()));
        localContentValues.put("type", Integer.valueOf(paramCommandModel.getType()));
        long l = localSQLiteDatabase.insert("command", null, localContentValues);
        localSQLiteDatabase.close();
        boolean bool;
        if (l != -1L) {
            bool = true;
        } else {
            bool = false;
        }
        return bool;
    }

    public List<CommandModel> query(int paramInt)
    {
        ArrayList localArrayList = new ArrayList();
        SQLiteDatabase localSQLiteDatabase = this.dbHelper.getReadableDatabase();
        Cursor localCursor = localSQLiteDatabase.query("command", null, "type=?", new String[] { String.valueOf(paramInt) }, null, null, null);
        localCursor.moveToFirst();
        while (!localCursor.isAfterLast())
        {
            int i = localCursor.getInt(localCursor.getColumnIndex("id"));
            String str1 = localCursor.getString(localCursor.getColumnIndex("title"));
            String str2 = localCursor.getString(localCursor.getColumnIndex("title_en"));
            String str3 = localCursor.getString(localCursor.getColumnIndex("title_tw"));
            int j = localCursor.getInt(localCursor.getColumnIndex("action"));
            boolean bool;
            if (localCursor.getInt(localCursor.getColumnIndex("can_edit")) != 0) {
                bool = true;
            } else {
                bool = false;
            }
            localArrayList.add(new CommandModel(i, str1, str2, str3, j, bool, paramInt));
            localCursor.moveToNext();
        }
        localCursor.close();
        localSQLiteDatabase.close();
        return localArrayList;
    }

    public boolean update(CommandModel paramCommandModel)
    {
        SQLiteDatabase localSQLiteDatabase = this.dbHelper.getWritableDatabase();
        ContentValues localContentValues = new ContentValues();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("id = ");
        localStringBuilder.append(paramCommandModel.getId());
        LogUtil.d("update", localStringBuilder.toString());
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("can = ");
        localStringBuilder.append(paramCommandModel.isCanEdit());
        LogUtil.d("update", localStringBuilder.toString());
        localContentValues.put("title_en", paramCommandModel.getTitleEn());
        localContentValues.put("title_tw", paramCommandModel.getTitleTw());
        localContentValues.put("title", paramCommandModel.getTitle());
        localContentValues.put("action", String.valueOf(paramCommandModel.getAction()));
        boolean bool = true;
        long l = localSQLiteDatabase.update("command", localContentValues, "id=?", new String[] { String.valueOf(paramCommandModel.getId()) });
        localSQLiteDatabase.close();
        if (l != 1L) {
            bool = false;
        }
        return bool;
    }
}
