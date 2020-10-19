package com.lobot.lobotcontrol.speech;

import android.content.Context;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.lobot.lobotcontrol.uitls.LogUtil;

public class SpeechManager
{
    private static final String APP_ID = "5604fd35";
    private static final String TAG = "SpeechManager";
    private static final String VOICE_PATH = "/voice";
    private SpeechRecognizer mIat;
    private SpeechSynthesizer mTts;
    private RecognizerListener recognizerListener;

    public SpeechManager(Context paramContext)
    {
        this.mIat = SpeechRecognizer.createRecognizer(paramContext, null);
        this.mIat.setParameter("domain", "iat");
        this.mIat.setParameter("language", "zh_cn");
        this.mIat.setParameter("accent", "mandarin ");
        this.mIat.setParameter("engine_type", "cloud");
        this.mIat.setParameter("text_encoding", "utf-8");
        this.mIat.setParameter("asr_ptt", "0");
        this.mTts = SpeechSynthesizer.createSynthesizer(paramContext, null);
        this.mTts.setParameter("voice_name", "xiaoyan");
        this.mTts.setParameter("speed", "50");
        this.mTts.setParameter("volume", "80");
        this.mTts.setParameter("engine_type", "cloud");
    }

    public static SpeechManager init(Context paramContext, RecognizerListener paramRecognizerListener)
    {
        SpeechUtility.createUtility(paramContext, "appid=5604fd35");
        paramContext = new SpeechManager(paramContext);
        paramContext.recognizerListener = paramRecognizerListener;
        return paramContext;
    }

    public void cancelListen()
    {
        if ((this.mIat != null) && (this.mIat.isListening())) {
            this.mIat.cancel();
        }
    }

    public void destroy()
    {
        cancelListen();
        stopSpeaking();
        if (this.mIat != null) {
            this.mIat.destroy();
        }
        if (this.mTts != null) {
            this.mTts.destroy();
        }
    }

    public boolean isListening()
    {
        return this.mIat.isListening();
    }

    public void speak(String paramString, SynthesizerListener paramSynthesizerListener)
    {
        this.mTts.startSpeaking(paramString, paramSynthesizerListener);
    }

    public void startListen()
    {
        try
        {
            this.mIat.startListening(this.recognizerListener);
        }
        catch (Exception localException)
        {
            LogUtil.w("SpeechManager", localException.getMessage());
            stopListen();
        }
    }

    public void stopListen()
    {
        if ((this.mIat != null) && (this.mIat.isListening())) {
            this.mIat.stopListening();
        }
    }

    public void stopSpeaking()
    {
        if ((this.mTts != null) && (this.mTts.isSpeaking())) {
            this.mTts.stopSpeaking();
        }
    }
}
