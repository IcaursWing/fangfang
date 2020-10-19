package com.lobot.lobotcontrol.speech.model;

public class ChineseWord
{
    private int sc;
    private String w;

    public ChineseWord() {}

    public ChineseWord(String paramString, int paramInt)
    {
        this.w = paramString;
        this.sc = paramInt;
    }

    public int getSc()
    {
        return this.sc;
    }

    public String getW()
    {
        return this.w;
    }

    public void setSc(int paramInt)
    {
        this.sc = paramInt;
    }

    public void setW(String paramString)
    {
        this.w = paramString;
    }

    public String toString()
    {
        return this.w;
    }
}
