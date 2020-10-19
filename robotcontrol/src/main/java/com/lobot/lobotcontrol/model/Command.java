package com.lobot.lobotcontrol.model;

import java.util.ArrayList;
import java.util.List;

public class Command
{
    String commandStr;
    long delay;

    public Command() {}

    public Command(String paramString)
    {
        this.commandStr = paramString;
        this.delay = 0L;
    }

    public Command(String paramString, long paramLong)
    {
        this.commandStr = paramString;
        this.delay = paramLong;
    }

    public String getCommandStr()
    {
        return this.commandStr;
    }

    public long getDelay()
    {
        return this.delay;
    }

    public void setCommandStr(String paramString)
    {
        this.commandStr = paramString;
    }

    public void setDelay(long paramLong)
    {
        this.delay = paramLong;
    }

    public String toString()
    {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("{ command = ");
        localStringBuilder.append(this.commandStr);
        localStringBuilder.append(", delay = ");
        localStringBuilder.append(this.delay);
        localStringBuilder.append("}");
        return localStringBuilder.toString();
    }

    public static class Builder
    {
        private List<Command> commandList = new ArrayList();

        public Builder addCommand(String paramString, long paramLong)
        {
            this.commandList.add(new Command(paramString, paramLong));
            return this;
        }

        public List<Command> createCommands()
        {
            return this.commandList;
        }
    }
}
