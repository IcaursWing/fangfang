package com.lobot.lobotcontrol.speech.model;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VoiceResult
{
    private int bg;
    private int end;
    private boolean ls;
    private String sn;
    private List<Words> ws;

    public VoiceResult() {}

    public VoiceResult(String paramString, boolean paramBoolean, int paramInt1, int paramInt2, List<Words> paramList)
    {
        this.sn = paramString;
        this.ls = paramBoolean;
        this.bg = paramInt1;
        this.end = paramInt2;
        this.ws = paramList;
    }

    public int getBg()
    {
        return this.bg;
    }

    public int getEnd()
    {
        return this.end;
    }

    public List<Words> getFullWs()
    {
        ArrayList localArrayList = new ArrayList();
        Iterator localIterator = this.ws.iterator();
        while (localIterator.hasNext())
        {
            Words localWords = (Words)localIterator.next();
            if (!TextUtils.isEmpty(localWords.getMostCw())) {
                localArrayList.add(localWords);
            }
        }
        return localArrayList;
    }

    public String getSentence()
    {
        StringBuilder localStringBuilder = new StringBuilder();
        Iterator localIterator = this.ws.iterator();
        while (localIterator.hasNext()) {
            localStringBuilder.append(((Words)localIterator.next()).getMostCw());
        }
        return localStringBuilder.toString();
    }

    public String getSn()
    {
        return this.sn;
    }

    public List<Words> getWs()
    {
        return this.ws;
    }

    public boolean isLs()
    {
        return this.ls;
    }

    public void setBg(int paramInt)
    {
        this.bg = paramInt;
    }

    public void setEnd(int paramInt)
    {
        this.end = paramInt;
    }

    public void setLs(boolean paramBoolean)
    {
        this.ls = paramBoolean;
    }

    public void setSn(String paramString)
    {
        this.sn = paramString;
    }

    public void setWs(List<Words> paramList)
    {
        this.ws = paramList;
    }
}
