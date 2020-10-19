package com.lobot.lobotcontrol.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import com.lobot.lobotcontrol.uitls.LogUtil;

public class DBHelper
        extends SQLiteOpenHelper
{
    public static final String DB_NAME = "robot";
    public static final int DB_VERSION = 3;
    public static final String TABLE_NAME_COMMAND = "command";
    public static final String TABLE_NAME_VOICE_COMMAND = "voice_command";
    private static final String TAG = "DBHelper";
    private Context context;

    public DBHelper(Context paramContext, CursorFactory paramCursorFactory)
    {
        super(paramContext, "robot", paramCursorFactory, 3);
        this.context = paramContext;
        if ((!isTableExist("command")) || (!isTableExist("voice_command")))
        {
            copyDBFile();
            getWritableDatabase().setVersion(3);
        }
    }

    /* Error */
    protected void copyDBFile()
    {
        // Byte code:
        //   0: new 52	java/lang/StringBuilder
        //   3: dup
        //   4: invokespecial 54	java/lang/StringBuilder:<init>	()V
        //   7: astore_1
        //   8: aload_1
        //   9: ldc 56
        //   11: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   14: pop
        //   15: aload_1
        //   16: aload_0
        //   17: invokevirtual 64	com/lobot/lobotcontrol/db/DBHelper:getDatabaseName	()Ljava/lang/String;
        //   20: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   23: pop
        //   24: ldc 20
        //   26: aload_1
        //   27: invokevirtual 67	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   30: invokestatic 73	com/lobot/lobotcontrol/uitls/LogUtil:i	(Ljava/lang/String;Ljava/lang/String;)I
        //   33: pop
        //   34: aconst_null
        //   35: astore_2
        //   36: aconst_null
        //   37: astore_3
        //   38: aload_0
        //   39: getfield 29	com/lobot/lobotcontrol/db/DBHelper:context	Landroid/content/Context;
        //   42: invokevirtual 79	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
        //   45: astore 4
        //   47: new 52	java/lang/StringBuilder
        //   50: astore_1
        //   51: aload_1
        //   52: invokespecial 54	java/lang/StringBuilder:<init>	()V
        //   55: aload_1
        //   56: aload_0
        //   57: invokevirtual 64	com/lobot/lobotcontrol/db/DBHelper:getDatabaseName	()Ljava/lang/String;
        //   60: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   63: pop
        //   64: aload_1
        //   65: ldc 81
        //   67: invokevirtual 60	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   70: pop
        //   71: aload 4
        //   73: aload_1
        //   74: invokevirtual 67	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   77: invokevirtual 87	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
        //   80: astore_1
        //   81: aload_0
        //   82: getfield 29	com/lobot/lobotcontrol/db/DBHelper:context	Landroid/content/Context;
        //   85: aload_0
        //   86: invokevirtual 64	com/lobot/lobotcontrol/db/DBHelper:getDatabaseName	()Ljava/lang/String;
        //   89: invokevirtual 91	android/content/Context:getDatabasePath	(Ljava/lang/String;)Ljava/io/File;
        //   92: astore 4
        //   94: new 93	java/io/FileOutputStream
        //   97: astore_3
        //   98: aload_3
        //   99: aload 4
        //   101: invokespecial 96	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
        //   104: aload 4
        //   106: invokevirtual 102	java/io/File:exists	()Z
        //   109: ifne +9 -> 118
        //   112: aload 4
        //   114: invokevirtual 105	java/io/File:mkdirs	()Z
        //   117: pop
        //   118: sipush 8192
        //   121: newarray <illegal type>
        //   123: astore 4
        //   125: aload_1
        //   126: aload 4
        //   128: invokevirtual 111	java/io/InputStream:read	([B)I
        //   131: istore 5
        //   133: iload 5
        //   135: ifle +19 -> 154
        //   138: aload_3
        //   139: aload 4
        //   141: iconst_0
        //   142: iload 5
        //   144: invokevirtual 115	java/io/FileOutputStream:write	([BII)V
        //   147: aload_3
        //   148: invokevirtual 118	java/io/FileOutputStream:flush	()V
        //   151: goto -26 -> 125
        //   154: aload_1
        //   155: ifnull +7 -> 162
        //   158: aload_1
        //   159: invokevirtual 121	java/io/InputStream:close	()V
        //   162: aload_3
        //   163: ifnull +91 -> 254
        //   166: aload_3
        //   167: invokevirtual 122	java/io/FileOutputStream:close	()V
        //   170: goto +84 -> 254
        //   173: astore 4
        //   175: aload_3
        //   176: astore_2
        //   177: goto +85 -> 262
        //   180: astore_2
        //   181: aload_3
        //   182: astore 4
        //   184: goto +34 -> 218
        //   187: astore 4
        //   189: aconst_null
        //   190: astore_2
        //   191: goto +71 -> 262
        //   194: astore_2
        //   195: aconst_null
        //   196: astore 4
        //   198: goto +20 -> 218
        //   201: astore 4
        //   203: aconst_null
        //   204: astore_3
        //   205: aload_2
        //   206: astore_1
        //   207: aload_3
        //   208: astore_2
        //   209: goto +53 -> 262
        //   212: astore_2
        //   213: aconst_null
        //   214: astore 4
        //   216: aload_3
        //   217: astore_1
        //   218: aload_2
        //   219: invokevirtual 125	java/io/IOException:printStackTrace	()V
        //   222: aload_1
        //   223: ifnull +14 -> 237
        //   226: aload_1
        //   227: invokevirtual 121	java/io/InputStream:close	()V
        //   230: goto +7 -> 237
        //   233: astore_1
        //   234: goto +16 -> 250
        //   237: aload 4
        //   239: ifnull +15 -> 254
        //   242: aload 4
        //   244: invokevirtual 122	java/io/FileOutputStream:close	()V
        //   247: goto +7 -> 254
        //   250: aload_1
        //   251: invokevirtual 125	java/io/IOException:printStackTrace	()V
        //   254: return
        //   255: astore_3
        //   256: aload 4
        //   258: astore_2
        //   259: aload_3
        //   260: astore 4
        //   262: aload_1
        //   263: ifnull +14 -> 277
        //   266: aload_1
        //   267: invokevirtual 121	java/io/InputStream:close	()V
        //   270: goto +7 -> 277
        //   273: astore_1
        //   274: goto +14 -> 288
        //   277: aload_2
        //   278: ifnull +14 -> 292
        //   281: aload_2
        //   282: invokevirtual 122	java/io/FileOutputStream:close	()V
        //   285: goto +7 -> 292
        //   288: aload_1
        //   289: invokevirtual 125	java/io/IOException:printStackTrace	()V
        //   292: aload 4
        //   294: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	295	0	this	DBHelper
        //   7	220	1	localObject1	Object
        //   233	34	1	localIOException1	java.io.IOException
        //   273	16	1	localIOException2	java.io.IOException
        //   35	142	2	localObject2	Object
        //   180	1	2	localIOException3	java.io.IOException
        //   190	1	2	localObject3	Object
        //   194	12	2	localIOException4	java.io.IOException
        //   208	1	2	localObject4	Object
        //   212	7	2	localIOException5	java.io.IOException
        //   258	24	2	localObject5	Object
        //   37	180	3	localFileOutputStream1	java.io.FileOutputStream
        //   255	5	3	localObject6	Object
        //   45	95	4	localObject7	Object
        //   173	1	4	localObject8	Object
        //   182	1	4	localFileOutputStream2	java.io.FileOutputStream
        //   187	1	4	localObject9	Object
        //   196	1	4	localObject10	Object
        //   201	1	4	localObject11	Object
        //   214	79	4	localObject12	Object
        //   131	12	5	i	int
        // Exception table:
        //   from	to	target	type
        //   104	118	173	finally
        //   118	125	173	finally
        //   125	133	173	finally
        //   138	151	173	finally
        //   104	118	180	java/io/IOException
        //   118	125	180	java/io/IOException
        //   125	133	180	java/io/IOException
        //   138	151	180	java/io/IOException
        //   81	104	187	finally
        //   81	104	194	java/io/IOException
        //   38	81	201	finally
        //   38	81	212	java/io/IOException
        //   158	162	233	java/io/IOException
        //   166	170	233	java/io/IOException
        //   226	230	233	java/io/IOException
        //   242	247	233	java/io/IOException
        //   218	222	255	finally
        //   266	270	273	java/io/IOException
        //   281	285	273	java/io/IOException
    }

    protected boolean isTableExist(String paramString)
    {
        Object localObject = getReadableDatabase();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("select Count(*) from sqlite_master where type=\"table\" and name=\"");
        localStringBuilder.append(paramString);
        localStringBuilder.append("\"");
        paramString = ((SQLiteDatabase)localObject).rawQuery(localStringBuilder.toString(), null);
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("isClosed = ");
        ((StringBuilder)localObject).append(paramString.isClosed());
        LogUtil.i("DBHelper", ((StringBuilder)localObject).toString());
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("count = ");
        ((StringBuilder)localObject).append(paramString.getCount());
        LogUtil.i("DBHelper", ((StringBuilder)localObject).toString());
        if (paramString.moveToFirst())
        {
            int i = paramString.getInt(0);
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append("check table count = ");
            ((StringBuilder)localObject).append(i);
            LogUtil.i("DBHelper", ((StringBuilder)localObject).toString());
            if (i > 0) {
                return true;
            }
        }
        paramString.close();
        return false;
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase) {}

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
        paramSQLiteDatabase = new StringBuilder();
        paramSQLiteDatabase.append("oldVersion = ");
        paramSQLiteDatabase.append(paramInt1);
        paramSQLiteDatabase.append(",  newVersion = ");
        paramSQLiteDatabase.append(paramInt2);
        LogUtil.d("DBHelper", paramSQLiteDatabase.toString());
    }
}
