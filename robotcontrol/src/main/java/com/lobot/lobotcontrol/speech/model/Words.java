package com.lobot.lobotcontrol.speech.model;

import java.util.List;

public class Words
{
    private int bg;
    private List<ChineseWord> cw;

    public Words() {}

    public Words(int paramInt, List<ChineseWord> paramList)
    {
        this.bg = paramInt;
        this.cw = paramList;
    }

    public int getBg()
    {
        return this.bg;
    }

    public List<ChineseWord> getCw()
    {
        return this.cw;
    }

    public String getMostCw()
    {
        if (this.cw.isEmpty()) {
            return null;
        }
        int i = this.cw.size();
        int j = 1;
        if (i == 1) {
            return ((ChineseWord)this.cw.get(0)).getW();
        }
        Object localObject2;
        for (Object localObject1 = (ChineseWord)this.cw.get(0); j < this.cw.size(); localObject1 = localObject2)
        {
            ChineseWord localChineseWord = (ChineseWord)this.cw.get(j);
            localObject2 = localObject1;
            if (((ChineseWord)localObject1).getSc() < localChineseWord.getSc()) {
                localObject2 = localChineseWord;
            }
            j++;
        }
        return ((ChineseWord)localObject1).getW();
    }

    public void setBg(int paramInt)
    {
        this.bg = paramInt;
    }

    public void setCw(List<ChineseWord> paramList)
    {
        this.cw = paramList;
    }
}
