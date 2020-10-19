package com.lobot.lobotcontrol.model;

import android.text.TextUtils;
import com.lobot.lobotcontrol.uitls.ChineseUtils;

public class VoiceModel
{
    public static final int TYPE_CONTINUOUS = 1;
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_SELF_ADD = 4;
    public static final int TYPE_STAND = 3;
    public static final int TYPE_STOP = 2;
    private String acceptVoice;
    private Integer action;
    private boolean canEdit;
    private int id;
    private String name;
    private Integer type;
    private boolean usePinyin;
    private String voicePinyin;

    public VoiceModel() {}

    public VoiceModel(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean1, Integer paramInteger1, Integer paramInteger2, boolean paramBoolean2)
    {
        this.id = paramInt;
        this.name = paramString1;
        this.acceptVoice = paramString2;
        this.voicePinyin = paramString3;
        this.usePinyin = paramBoolean1;
        this.action = paramInteger1;
        this.type = paramInteger2;
        this.canEdit = paramBoolean2;
    }

    public VoiceModel(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, Integer paramInteger1, Integer paramInteger2, boolean paramBoolean2)
    {
        this.name = paramString1;
        this.acceptVoice = paramString2;
        this.voicePinyin = paramString3;
        this.usePinyin = paramBoolean1;
        this.action = paramInteger1;
        this.type = paramInteger2;
        this.canEdit = paramBoolean2;
    }

    public int containKey(String paramString)
    {
        if (TextUtils.isEmpty(paramString)) {
            return -1;
        }
        String[] arrayOfString2;
        if (this.usePinyin)
        {
            String[] arrayOfString1 = this.voicePinyin.split(";");
            String str = ChineseUtils.chinese2Spell(paramString);
            arrayOfString2 = arrayOfString1;
            if (str != null)
            {
                paramString = str;
                arrayOfString2 = arrayOfString1;
            }
        }
        else
        {
            arrayOfString2 = this.acceptVoice.split(";");
        }
        int i = 0;
        int k;
        for (int j = 0;; j++)
        {
            k = i;
            if (j >= arrayOfString2.length) {
                break;
            }
            if (paramString.equals(arrayOfString2[j])) {
                return j;
            }
        }
        while (k < arrayOfString2.length)
        {
            if (paramString.contains(arrayOfString2[k])) {
                return k;
            }
            k++;
        }
        return -1;
    }

    public String getAcceptVoice()
    {
        return this.acceptVoice;
    }

    public int getAction()
    {
        return this.action.intValue();
    }

    public int getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public Integer getType()
    {
        return this.type;
    }

    public String getVoicePinyin()
    {
        return this.voicePinyin;
    }

    public boolean isCanEdit()
    {
        return this.canEdit;
    }

    public boolean isUsePinyin()
    {
        return this.usePinyin;
    }

    public void setAcceptVoice(String paramString)
    {
        this.acceptVoice = paramString;
    }

    public void setAction(Integer paramInteger)
    {
        this.action = paramInteger;
    }

    public void setCanEdit(boolean paramBoolean)
    {
        this.canEdit = paramBoolean;
    }

    public void setId(int paramInt)
    {
        this.id = paramInt;
    }

    public void setName(String paramString)
    {
        this.name = paramString;
    }

    public void setType(Integer paramInteger)
    {
        this.type = paramInteger;
    }

    public void setUsePinyin(boolean paramBoolean)
    {
        this.usePinyin = paramBoolean;
    }

    public void setVoicePinyin(String paramString)
    {
        this.voicePinyin = paramString;
    }

    public String toString()
    {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("[id = ");
        localStringBuilder.append(this.id);
        localStringBuilder.append(" name = ");
        localStringBuilder.append(this.name);
        localStringBuilder.append(" acceptVoice = ");
        localStringBuilder.append(this.acceptVoice);
        localStringBuilder.append(" voicePinyin = ");
        localStringBuilder.append(this.voicePinyin);
        localStringBuilder.append(" usePinyin = ");
        localStringBuilder.append(this.usePinyin);
        localStringBuilder.append(" action = ");
        localStringBuilder.append(this.action);
        localStringBuilder.append(" type = ");
        localStringBuilder.append(this.type);
        localStringBuilder.append(" canEdit = ");
        localStringBuilder.append(this.canEdit);
        localStringBuilder.append("]");
        return localStringBuilder.toString();
    }
}
