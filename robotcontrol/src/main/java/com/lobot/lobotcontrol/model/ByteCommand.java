package com.lobot.lobotcontrol.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ByteCommand
{
    long delay;
    ByteBuffer m_commandArray;

    public ByteCommand() {}

    public ByteCommand(byte[] paramArrayOfByte)
    {
        this.m_commandArray = ByteBuffer.wrap(paramArrayOfByte);
        this.delay = 0L;
    }

    public ByteCommand(byte[] paramArrayOfByte, long paramLong)
    {
        this.m_commandArray = ByteBuffer.wrap(paramArrayOfByte);
        this.delay = paramLong;
    }

    public ByteBuffer getCommandByteBuffer()
    {
        return this.m_commandArray;
    }

    public long getDelay()
    {
        return this.delay;
    }

    public void setCommandBuffer(byte[] paramArrayOfByte)
    {
        this.m_commandArray = ByteBuffer.wrap(paramArrayOfByte);
    }

    public void setDelay(long paramLong)
    {
        this.delay = paramLong;
    }

    public static class Builder
    {
        private List<ByteCommand> commandList = new ArrayList();

        public Builder addCommand(byte[] paramArrayOfByte, long paramLong)
        {
            this.commandList.add(new ByteCommand(paramArrayOfByte, paramLong));
            return this;
        }

        public List<ByteCommand> createCommands()
        {
            return this.commandList;
        }
    }
}
