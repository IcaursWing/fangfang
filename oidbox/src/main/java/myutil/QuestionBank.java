package myutil;

import android.content.Context;
import android.content.res.Resources;

public class QuestionBank {

    public static int getStringResId(String name, Context context) {
        Resources r = context.getResources();
        int id = r.getIdentifier(name, "string", context.getPackageName());
        return id;
    }

}