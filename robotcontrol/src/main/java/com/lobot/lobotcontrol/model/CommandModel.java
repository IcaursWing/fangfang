package com.lobot.lobotcontrol.model;

public class CommandModel
{
    public static final int ROW_SUM = 36;
    public static final int TYPE_ACTION = 1;
    public static final int TYPE_CUSTOM_ACTION = 3;
    public static final int TYPE_DANCE = 0;
    public static final int TYPE_SELF_BUTTON = 2;
    private int action;
    private boolean canEdit;
    private int id;
    private String title;
    private String title_en;
    private String title_tw;
    private int type;

    public CommandModel() {}

    public CommandModel(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2, boolean paramBoolean, int paramInt3)
    {
        this.id = paramInt1;
        this.title = paramString1;
        this.title_en = paramString2;
        this.title_tw = paramString3;
        this.action = paramInt2;
        this.canEdit = paramBoolean;
        this.type = paramInt3;
    }

    public CommandModel(String paramString1, String paramString2, String paramString3, int paramInt1, boolean paramBoolean, int paramInt2)
    {
        this.title = paramString1;
        this.title_en = paramString2;
        this.title_tw = paramString3;
        this.action = paramInt1;
        this.canEdit = paramBoolean;
        this.type = paramInt2;
    }

    public int getAction()
    {
        return this.action;
    }

    public int getId()
    {
        return this.id;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getTitleEn()
    {
        return this.title_en;
    }

    public String getTitleTw()
    {
        return this.title_tw;
    }

    public int getType()
    {
        return this.type;
    }

    public boolean isCanEdit()
    {
        return this.canEdit;
    }

    public void setAction(int paramInt)
    {
        this.action = paramInt;
    }

    public void setCanEdit(boolean paramBoolean)
    {
        this.canEdit = paramBoolean;
    }

    public void setId(int paramInt)
    {
        this.id = paramInt;
    }

    public void setTitle(String paramString)
    {
        this.title = paramString;
    }

    public void setTitleEn(String paramString)
    {
        this.title_en = paramString;
    }

    public void setTitleTw(String paramString)
    {
        this.title_tw = paramString;
    }

    public void setType(int paramInt)
    {
        this.type = paramInt;
    }
}
