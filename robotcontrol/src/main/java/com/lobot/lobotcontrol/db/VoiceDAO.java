package com.lobot.lobotcontrol.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.lobot.lobotcontrol.model.VoiceModel;
import com.lobot.lobotcontrol.speech.model.Words;
import com.lobot.lobotcontrol.uitls.ChineseUtils;
import com.lobot.lobotcontrol.uitls.LogUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoiceDAO
{
    private DBHelper dbHelper;

    public VoiceDAO(Context paramContext)
    {
        this.dbHelper = new DBHelper(paramContext, null);
    }

    public List<VoiceModel> containKeys(String paramString, Integer paramInteger)
    {
        ArrayList localArrayList = new ArrayList();
        SQLiteDatabase localSQLiteDatabase = this.dbHelper.getReadableDatabase();
        Object localObject1 = new StringBuilder();
        if (paramInteger != null) {
            ((StringBuilder)localObject1).append("id<>? and ");
        }
        ((StringBuilder)localObject1).append("((voice like ?) or (use_pinyin=1 and voice_pinyin like ?))");
        paramInteger = ((StringBuilder)localObject1).toString();
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("%");
        ((StringBuilder)localObject1).append(paramString);
        ((StringBuilder)localObject1).append("%");
        localObject1 = ((StringBuilder)localObject1).toString();
        Object localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("%");
        ((StringBuilder)localObject2).append(ChineseUtils.chinese2Spell(paramString));
        ((StringBuilder)localObject2).append("%");
        paramString = localSQLiteDatabase.query("voice_command", null, paramInteger, new String[] { localObject1, ((StringBuilder)localObject2).toString() }, null, null, null);
        paramString.moveToFirst();
        while (!paramString.isAfterLast())
        {
            int i = paramString.getInt(paramString.getColumnIndex("id"));
            paramInteger = paramString.getString(paramString.getColumnIndex("name"));
            localObject2 = paramString.getString(paramString.getColumnIndex("voice"));
            localObject1 = paramString.getString(paramString.getColumnIndex("voice_pinyin"));
            boolean bool1;
            if (paramString.getInt(paramString.getColumnIndex("use_pinyin")) != 0) {
                bool1 = true;
            } else {
                bool1 = false;
            }
            int j = paramString.getInt(paramString.getColumnIndex("action"));
            int k = paramString.getInt(paramString.getColumnIndex("type"));
            boolean bool2;
            if (paramString.getInt(paramString.getColumnIndex("can_edit")) != 0) {
                bool2 = true;
            } else {
                bool2 = false;
            }
            localArrayList.add(new VoiceModel(i, paramInteger, (String)localObject2, (String)localObject1, bool1, Integer.valueOf(j), Integer.valueOf(k), bool2));
            paramString.moveToNext();
        }
        paramString.close();
        localSQLiteDatabase.close();
        return localArrayList;
    }

    public boolean delete(int paramInt)
    {
        SQLiteDatabase localSQLiteDatabase = this.dbHelper.getWritableDatabase();
        boolean bool = true;
        if (localSQLiteDatabase.delete("voice_command", "id=?", new String[] { String.valueOf(paramInt) }) != 1L) {
            bool = false;
        }
        return bool;
    }

    public boolean insert(VoiceModel paramVoiceModel)
    {
        SQLiteDatabase localSQLiteDatabase = this.dbHelper.getWritableDatabase();
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("name", paramVoiceModel.getName());
        localContentValues.put("voice", paramVoiceModel.getAcceptVoice());
        localContentValues.put("voice_pinyin", paramVoiceModel.getVoicePinyin());
        localContentValues.put("use_pinyin", Integer.valueOf(paramVoiceModel.isUsePinyin()));
        localContentValues.put("action", Integer.valueOf(paramVoiceModel.getAction()));
        localContentValues.put("type", paramVoiceModel.getType());
        boolean bool = true;
        localContentValues.put("can_edit", Integer.valueOf(1));
        long l = localSQLiteDatabase.insert("voice_command", null, localContentValues);
        localSQLiteDatabase.close();
        if (l == -1L) {
            bool = false;
        }
        return bool;
    }

    public List<VoiceModel> query(int paramInt, boolean paramBoolean)
    {
        ArrayList localArrayList = new ArrayList();
        SQLiteDatabase localSQLiteDatabase = this.dbHelper.getReadableDatabase();
        String str1;
        if (paramBoolean) {
            str1 = "action";
        } else {
            str1 = null;
        }
        Cursor localCursor = localSQLiteDatabase.query("voice_command", null, "type=?", new String[] { String.valueOf(paramInt) }, null, null, str1);
        localCursor.moveToFirst();
        while (!localCursor.isAfterLast())
        {
            int i = localCursor.getInt(localCursor.getColumnIndex("id"));
            String str2 = localCursor.getString(localCursor.getColumnIndex("name"));
            str1 = localCursor.getString(localCursor.getColumnIndex("voice"));
            String str3 = localCursor.getString(localCursor.getColumnIndex("voice_pinyin"));
            if (localCursor.getInt(localCursor.getColumnIndex("use_pinyin")) != 0) {
                paramBoolean = true;
            } else {
                paramBoolean = false;
            }
            paramInt = localCursor.getInt(localCursor.getColumnIndex("action"));
            int j = localCursor.getInt(localCursor.getColumnIndex("type"));
            boolean bool;
            if (localCursor.getInt(localCursor.getColumnIndex("can_edit")) != 0) {
                bool = true;
            } else {
                bool = false;
            }
            localArrayList.add(new VoiceModel(i, str2, str1, str3, paramBoolean, Integer.valueOf(paramInt), Integer.valueOf(j), bool));
            localCursor.moveToNext();
        }
        localCursor.close();
        localSQLiteDatabase.close();
        return localArrayList;
    }

    public List<VoiceModel> query(List<Words> paramList)
    {
        ArrayList localArrayList = new ArrayList();
        SQLiteDatabase localSQLiteDatabase = this.dbHelper.getReadableDatabase();
        int i = paramList.size();
        Object localObject1 = new StringBuilder("(voice like ?");
        Object localObject2 = new StringBuilder("(use_pinyin=1 and (voice_pinyin like ?");
        for (int j = 0; j < i - 1; j++)
        {
            ((StringBuilder)localObject1).append(" or voice like ?");
            ((StringBuilder)localObject2).append(" or voice_pinyin like ?");
        }
        ((StringBuilder)localObject1).append(") or ");
        ((StringBuilder)localObject1).append(((StringBuilder)localObject2).toString());
        ((StringBuilder)localObject1).append("))");
        localObject2 = new String[i * 2];
        String str;
        for (j = 0; j < i; j++)
        {
            str = ((Words)paramList.get(j)).getMostCw();
            if (!TextUtils.isEmpty(str))
            {
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append("%");
                localStringBuilder.append(str);
                localStringBuilder.append("%");
                localObject2[j] = localStringBuilder.toString();
                localStringBuilder = new StringBuilder();
                localStringBuilder.append("%");
                localStringBuilder.append(ChineseUtils.chinese2Spell(str));
                localStringBuilder.append("%");
                localObject2[(i + j)] = localStringBuilder.toString();
            }
        }
        paramList = new StringBuilder();
        paramList.append(((StringBuilder)localObject1).toString());
        paramList.append(" --- ");
        paramList.append(Arrays.toString((Object[])localObject2));
        LogUtil.i("VoiceDao", paramList.toString());
        localSQLiteDatabase.enableWriteAheadLogging();
        localObject1 = localSQLiteDatabase.query("voice_command", null, ((StringBuilder)localObject1).toString(), (String[])localObject2, null, null, null);
        ((Cursor)localObject1).moveToFirst();
        while (!((Cursor)localObject1).isAfterLast())
        {
            i = ((Cursor)localObject1).getInt(((Cursor)localObject1).getColumnIndex("id"));
            localObject2 = ((Cursor)localObject1).getString(((Cursor)localObject1).getColumnIndex("name"));
            paramList = ((Cursor)localObject1).getString(((Cursor)localObject1).getColumnIndex("voice"));
            str = ((Cursor)localObject1).getString(((Cursor)localObject1).getColumnIndex("voice_pinyin"));
            boolean bool1;
            if (((Cursor)localObject1).getInt(((Cursor)localObject1).getColumnIndex("use_pinyin")) != 0) {
                bool1 = true;
            } else {
                bool1 = false;
            }
            int k = ((Cursor)localObject1).getInt(((Cursor)localObject1).getColumnIndex("action"));
            j = ((Cursor)localObject1).getInt(((Cursor)localObject1).getColumnIndex("type"));
            boolean bool2;
            if (((Cursor)localObject1).getInt(((Cursor)localObject1).getColumnIndex("can_edit")) != 0) {
                bool2 = true;
            } else {
                bool2 = false;
            }
            localArrayList.add(new VoiceModel(i, (String)localObject2, paramList, str, bool1, Integer.valueOf(k), Integer.valueOf(j), bool2));
            ((Cursor)localObject1).moveToNext();
        }
        ((Cursor)localObject1).close();
        localSQLiteDatabase.close();
        paramList = new StringBuilder();
        paramList.append("result size = ");
        paramList.append(localArrayList.size());
        LogUtil.i("VoiceDAO", paramList.toString());
        return localArrayList;
    }

    public List<VoiceModel> query(boolean paramBoolean)
    {
        ArrayList localArrayList = new ArrayList();
        SQLiteDatabase localSQLiteDatabase = this.dbHelper.getReadableDatabase();
        String str1;
        if (paramBoolean) {
            str1 = "action";
        } else {
            str1 = null;
        }
        Cursor localCursor = localSQLiteDatabase.query("voice_command", null, null, null, null, null, str1);
        localCursor.moveToFirst();
        while (!localCursor.isAfterLast())
        {
            int i = localCursor.getInt(localCursor.getColumnIndex("id"));
            String str2 = localCursor.getString(localCursor.getColumnIndex("name"));
            String str3 = localCursor.getString(localCursor.getColumnIndex("voice"));
            str1 = localCursor.getString(localCursor.getColumnIndex("voice_pinyin"));
            if (localCursor.getInt(localCursor.getColumnIndex("use_pinyin")) != 0) {
                paramBoolean = true;
            } else {
                paramBoolean = false;
            }
            int j = localCursor.getInt(localCursor.getColumnIndex("action"));
            int k = localCursor.getInt(localCursor.getColumnIndex("type"));
            boolean bool;
            if (localCursor.getInt(localCursor.getColumnIndex("can_edit")) != 0) {
                bool = true;
            } else {
                bool = false;
            }
            localArrayList.add(new VoiceModel(i, str2, str3, str1, paramBoolean, Integer.valueOf(j), Integer.valueOf(k), bool));
            localCursor.moveToNext();
        }
        localCursor.close();
        localSQLiteDatabase.close();
        return localArrayList;
    }

    public boolean update(VoiceModel paramVoiceModel)
    {
        LogUtil.i("VoiceDAO", paramVoiceModel.toString());
        SQLiteDatabase localSQLiteDatabase = this.dbHelper.getWritableDatabase();
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("name", paramVoiceModel.getName());
        localContentValues.put("voice", paramVoiceModel.getAcceptVoice());
        localContentValues.put("voice_pinyin", paramVoiceModel.getVoicePinyin());
        localContentValues.put("use_pinyin", Integer.valueOf(paramVoiceModel.isUsePinyin()));
        localContentValues.put("action", Integer.valueOf(paramVoiceModel.getAction()));
        localContentValues.put("type", paramVoiceModel.getType());
        boolean bool = true;
        localContentValues.put("can_edit", Integer.valueOf(1));
        long l = localSQLiteDatabase.update("voice_command", localContentValues, "id=?", new String[] { String.valueOf(paramVoiceModel.getId()) });
        localSQLiteDatabase.close();
        if (l != 1L) {
            bool = false;
        }
        return bool;
    }
}
