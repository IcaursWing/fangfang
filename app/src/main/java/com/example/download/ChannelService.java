package com.example.download;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

/**
 * This service is used to decode the xml files that contain to do list
 * information. An input stream from the Http Url need applied and return the
 * set of tasks.
 */
public class ChannelService {

    public static List<Channel> getChannels(InputStream is) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "utf-8");
        List<Channel> channels = new ArrayList<Channel>();
        int type = parser.getEventType();
        Channel channel = null;
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_TAG:
                    if ("channel".equals(parser.getName())) {
                        channel = new Channel();
                        //String id = parser.getAttributeValue(0);
                        //channel.setId(id);
                    } else if ("name".equals(parser.getName())) {
                        String name = parser.nextText();
                        channel.setName(name);
                    } else if ("time".equals(parser.getName())) {
                        String time = parser.nextText();
                        channel.setTime(time);
                    } else if ("content".equals(parser.getName())) {
                        String content = parser.nextText();
                        channel.setContent(content);
                    }

                    break;

                case XmlPullParser.END_TAG:
                    if ("channel".equals(parser.getName())) {
                        channels.add(channel);
                        channel = null;
                    }
                    break;
            }
            type = parser.next();
        }
        return channels;
    }
}
