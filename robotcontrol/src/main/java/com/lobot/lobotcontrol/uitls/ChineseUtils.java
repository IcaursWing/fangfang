package com.lobot.lobotcontrol.uitls;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class ChineseUtils
{
    public static String chinese2Spell(String paramString)
    {
        if (TextUtils.isEmpty(paramString)) {
            return null;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        char[] arrayOfChar = paramString.toCharArray();
        HanyuPinyinOutputFormat localHanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
        localHanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        localHanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        int i = arrayOfChar.length;
        for (int j = 0; j < i; j++)
        {
            char c = arrayOfChar[j];
            if (c > '?')
            {
                try
                {
                    paramString = net.sourceforge.pinyin4j.PinyinHelper.toHanyuPinyinStringArray(c, localHanyuPinyinOutputFormat)[0];
                }
                catch (BadHanyuPinyinOutputFormatCombination paramString)
                {
                    paramString.printStackTrace();
                    paramString = String.valueOf(c);
                }
                localStringBuilder.append(paramString);
            }
            else
            {
                localStringBuilder.append(c);
            }
        }
        return localStringBuilder.toString();
    }

    public static List<String> chinese2SpellList(String paramString)
    {
        if (TextUtils.isEmpty(paramString)) {
            return null;
        }
        ArrayList localArrayList = new ArrayList();
        char[] arrayOfChar = paramString.toCharArray();
        HanyuPinyinOutputFormat localHanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
        localHanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        localHanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        int i = arrayOfChar.length;
        for (int j = 0; j < i; j++)
        {
            char c = arrayOfChar[j];
            if (c > '?')
            {
                try
                {
                    paramString = net.sourceforge.pinyin4j.PinyinHelper.toHanyuPinyinStringArray(c, localHanyuPinyinOutputFormat)[0];
                }
                catch (BadHanyuPinyinOutputFormatCombination paramString)
                {
                    paramString.printStackTrace();
                    paramString = String.valueOf(c);
                }
                localArrayList.add(paramString);
            }
            else
            {
                localArrayList.add(String.valueOf(c));
            }
        }
        return localArrayList;
    }
}
