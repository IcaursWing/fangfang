package com.lobot.lobotcontrol.uitls;

import java.util.UUID;

public abstract interface Configs
{
    public static final String BLUETOOTH_PIN_STRING = "1234";
    public static final int CHANGE_INTERVAL = 50;
    public static final String CHAR_SET = "UTF-8";
    public static final String COMMAND_CHANGE = "PL0\r\n";
    public static final String COMMAND_DOWN = "PL0SQ5SM100ONCE\r\n";
    public static final String COMMAND_DOWN_STOP = "PL0SQ6SM100ONCE\r\n";
    public static final String COMMAND_LEFT = "PL0SQ15SM100ONCE\r\n";
    public static final String COMMAND_RIGHT = "PL0SQ16SM100ONCE\r\n";
    public static final String COMMAND_START_DOWN = "PL0SQ4SM100ONCE\r\n";
    public static final String COMMAND_START_UP = "PL0SQ1SM100ONCE\r\n";
    public static final String COMMAND_STOP = "PL0SQ3SM100ONCE\r\n";
    public static final String COMMAND_TURN_LEFT = "PL0SQ7SM100ONCE\r\n";
    public static final String COMMAND_TURN_RIGHT = "PL0SQ8SM100ONCE\r\n";
    public static final String COMMAND_UP = "PL0SQ2SM100ONCE\r\n";
    public static final UUID LOROT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String NEW_LINE = "\r\n";
    public static final int SEND_INTERVAL = 400;
}
