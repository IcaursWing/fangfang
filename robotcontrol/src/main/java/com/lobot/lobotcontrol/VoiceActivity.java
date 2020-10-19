package com.lobot.lobotcontrol;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Vibrator;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.lobot.lobotcontrol.connect.BLEManager;
import com.lobot.lobotcontrol.db.VoiceDAO;
import com.lobot.lobotcontrol.model.ByteCommand.Builder;
import com.lobot.lobotcontrol.model.VoiceModel;
import com.lobot.lobotcontrol.speech.SpeechManager;
import com.lobot.lobotcontrol.speech.model.VoiceResult;
import com.lobot.lobotcontrol.uitls.ChineseUtils;
import com.lobot.lobotcontrol.uitls.LogUtil;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class VoiceActivity
        extends Activity
        implements OnClickListener
{
    private static final int MSG_HANDLE_RESULT = 1;
    private static final int MSG_STOP_VOICE = 2;
    private static final String TAG = "VoiceActivity";
    private Handler bgHandler;
    private BLEManager bleManager;
    private VoiceDAO dao;
    private boolean isPress;
    private Handler mHandler;
    private RecognizerListener mRecognizerListener = new RecognizerListener()
    {
        public void onBeginOfSpeech()
        {
            LogUtil.e("VoiceActivity", "onBeginOfSpeech");
            VoiceActivity.this.resultList.clear();
            VoiceActivity.this.showTips(2131427429, false);
            VoiceActivity.this.bgHandler.sendEmptyMessageDelayed(2, 55000L);
        }

        public void onEndOfSpeech()
        {
            LogUtil.e("VoiceActivity", "onEndOfSpeech");
            VoiceActivity.this.bgHandler.removeMessages(2);
            Object localObject1 = new LinkedList();
            Object localObject2 = VoiceActivity.this.resultList.iterator();
            for (;;)
            {
                if (!((Iterator)localObject2).hasNext()) {
                    break label346;
                }
                Object localObject3 = (VoiceResult)((Iterator)localObject2).next();
                Object localObject4 = VoiceActivity.this.dao.query(((VoiceResult)localObject3).getFullWs());
                String str = ((VoiceResult)localObject3).getSentence();
                localObject3 = new SpannableStringBuilder(str);
                if (!((List)localObject4).isEmpty())
                {
                    Object localObject5 = ((List)localObject4).iterator();
                    if (((Iterator)localObject5).hasNext())
                    {
                        localObject4 = (VoiceModel)((Iterator)localObject5).next();
                        int i = ((VoiceModel)localObject4).containKey(str);
                        Object localObject6 = new StringBuilder();
                        ((StringBuilder)localObject6).append("index = ");
                        ((StringBuilder)localObject6).append(i);
                        LogUtil.e("VoiceActivity", ((StringBuilder)localObject6).toString());
                        if (i < 0) {
                            break;
                        }
                        localObject6 = localObject4.getAcceptVoice().split(";")[i];
                        localObject5 = new StringBuilder();
                        ((StringBuilder)localObject5).append("recognizer : ");
                        ((StringBuilder)localObject5).append(localObject4);
                        ((StringBuilder)localObject5).append("  key = ");
                        ((StringBuilder)localObject5).append((String)localObject6);
                        LogUtil.e("VoiceActivity", ((StringBuilder)localObject5).toString());
                        localObject5 = VoiceActivity.this.getKey(str, (String)localObject6);
                        if (!TextUtils.isEmpty((CharSequence)localObject5)) {
                            i = str.indexOf((String)localObject5);
                        } else {
                            i = -1;
                        }
                        if (i != -1)
                        {
                            ((SpannableStringBuilder)localObject3).replace(i, ((String)localObject6).length() + i, (CharSequence)localObject6);
                            ((SpannableStringBuilder)localObject3).setSpan(new ForegroundColorSpan(Color.parseColor("#0F75DE")), i, ((String)localObject6).length() + i, 33);
                        }
                        ((LinkedList)localObject1).add(localObject4);
                    }
                }
            }
            label346:
            if (((LinkedList)localObject1).isEmpty())
            {
                LogUtil.e("VoiceActivity", "not recognize command");
                VoiceActivity.this.showTips(2131427431, -65536, true);
            }
            else if (((LinkedList)localObject1).size() == 1)
            {
                VoiceActivity.this.onRecognizedVoice((VoiceModel)((LinkedList)localObject1).get(0));
                VoiceActivity.this.dismissResult(5000L);
            }
            else
            {
                localObject2 = new StringBuilder(VoiceActivity.this.getString(2131427430));
                ((StringBuilder)localObject2).append("��");
                localObject1 = ((LinkedList)localObject1).iterator();
                while (((Iterator)localObject1).hasNext())
                {
                    ((StringBuilder)localObject2).append(((VoiceModel)((Iterator)localObject1).next()).getName());
                    ((StringBuilder)localObject2).append("��");
                }
                ((StringBuilder)localObject2).replace(((StringBuilder)localObject2).length() - 1, ((StringBuilder)localObject2).length(), "��");
                VoiceActivity.this.showTips(((StringBuilder)localObject2).toString(), true);
            }
            VoiceActivity.this.playVibrate();
        }

        public void onError(SpeechError paramAnonymousSpeechError)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("onError ");
            localStringBuilder.append(paramAnonymousSpeechError.getPlainDescription(true));
            LogUtil.e("VoiceActivity", localStringBuilder.toString());
            VoiceActivity.this.showTips(paramAnonymousSpeechError.getErrorDescription(), -65536, true);
            VoiceActivity.this.playVibrate();
            VoiceActivity.this.resultList.clear();
        }

        public void onEvent(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, Bundle paramAnonymousBundle)
        {
            LogUtil.e("VoiceActivity", "onEvent");
        }

        public void onResult(RecognizerResult paramAnonymousRecognizerResult, boolean paramAnonymousBoolean)
        {
            paramAnonymousRecognizerResult = paramAnonymousRecognizerResult.getResultString();
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("isLast ");
            localStringBuilder.append(paramAnonymousBoolean);
            LogUtil.e("VoiceActivity", localStringBuilder.toString());
            if (TextUtils.isEmpty(paramAnonymousRecognizerResult))
            {
                LogUtil.e("VoiceActivity", "Result is null");
            }
            else
            {
                localStringBuilder = new StringBuilder();
                localStringBuilder.append("Result:");
                localStringBuilder.append(paramAnonymousRecognizerResult);
                LogUtil.e("VoiceActivity", localStringBuilder.toString());
                paramAnonymousRecognizerResult = (VoiceResult)JSON.parseObject(paramAnonymousRecognizerResult, VoiceResult.class);
                VoiceActivity.this.resultList.add(paramAnonymousRecognizerResult);
                VoiceActivity.this.bgHandler.obtainMessage(1, paramAnonymousRecognizerResult).sendToTarget();
                if (paramAnonymousBoolean)
                {
                    VoiceActivity.this.resultList.clear();
                    VoiceActivity.this.dismissResult(5000L);
                    VoiceActivity.this.bgHandler.removeMessages(2);
                }
            }
        }

        public void onVolumeChanged(int paramAnonymousInt, byte[] paramAnonymousArrayOfByte) {}
    };
    private List<VoiceResult> resultList;
    private TextView resultTv;
    private SpeechManager speechManager;
    private Vibrator vibrator;

    private void cmdSend(byte[] paramArrayOfByte, int paramInt)
    {
        int i = paramArrayOfByte.length;
        int j;
        int k;
        if (paramArrayOfByte.length > 20)
        {
            j = 2;
            k = paramArrayOfByte.length % 20;
            i = 20;
        }
        else
        {
            k = 0;
            j = 1;
        }
        for (int m = 0; m < j; m++)
        {
            int n;
            if (m == 0)
            {
                n = i;
            }
            else
            {
                paramInt = 0;
                n = k;
            }
            byte[] arrayOfByte = new byte[n];
            for (int i1 = 0; i1 < n; i1++) {
                arrayOfByte[i1] = ((byte)paramArrayOfByte[(20 * m + i1)]);
            }
            ByteCommand.Builder localBuilder = new ByteCommand.Builder();
            localBuilder.addCommand(arrayOfByte, paramInt);
            this.bleManager.send(localBuilder.createCommands());
        }
    }

    private void dismissResult(long paramLong)
    {
        Message localMessage = this.mHandler.obtainMessage(17);
        this.mHandler.sendMessageDelayed(localMessage, paramLong);
    }

    private boolean equals(List<String> paramList1, List<String> paramList2)
    {
        if (paramList1.size() != paramList2.size()) {
            return false;
        }
        for (int i = 0; i < paramList1.size(); i++) {
            if (!((String)paramList1.get(i)).equals(paramList2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private String getKey(String paramString1, String paramString2)
    {
        if ((!TextUtils.isEmpty(paramString1)) && (!TextUtils.isEmpty(paramString2)))
        {
            List localList1 = ChineseUtils.chinese2SpellList(paramString1);
            List localList2 = ChineseUtils.chinese2SpellList(paramString2);
            if ((localList1 != null) && (!localList1.isEmpty()) && (localList2 != null) && (!localList2.isEmpty()))
            {
                int i = localList1.indexOf(localList2.get(0));
                if (i == -1) {
                    return null;
                }
                int j = localList2.size() + i;
                if (equals(localList2, localList1.subList(i, j))) {
                    return paramString1.substring(i, j);
                }
                return getKey(paramString1.substring(i + 1), paramString2);
            }
            return null;
        }
        return null;
    }

    private void mayRequestAudio()
    {
        if (ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != 0)
        {
            ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.RECORD_AUDIO");
            ActivityCompat.requestPermissions(this, new String[] { "android.permission.RECORD_AUDIO" }, 0);
            return;
        }
    }

    private void onRecognizedVoice(VoiceModel paramVoiceModel)
    {
        if (paramVoiceModel != null) {
            switch (paramVoiceModel.getType().intValue())
            {
                default:
                    break;
                case 3:
                    sendStop();
                    sendActionCmd(0, true);
                    break;
                case 2:
                    sendStop();
                    break;
                case 1:
                    sendActionCmd(paramVoiceModel.getAction() & 0xFF, false);
                    break;
                case 0:
                case 4:
                    sendActionCmd(paramVoiceModel.getAction() & 0xFF, true);
            }
        }
    }

    private void sendActionCmd(int paramInt, boolean paramBoolean)
    {
        byte[] arrayOfByte = new byte[7];
        byte[] tmp6_5 = arrayOfByte;
        tmp6_5[0] = 85;
        byte[] tmp11_6 = tmp6_5;
        tmp11_6[1] = 85;
        byte[] tmp16_11 = tmp11_6;
        tmp16_11[2] = 5;
        byte[] tmp21_16 = tmp16_11;
        tmp21_16[3] = 6;
        byte[] tmp26_21 = tmp21_16;
        tmp26_21[4] = 0;
        byte[] tmp31_26 = tmp26_21;
        tmp31_26[5] = 1;
        byte[] tmp36_31 = tmp31_26;
        tmp36_31[6] = 0;
        tmp36_31;
        arrayOfByte[4] = ((byte)(byte)(paramInt & 0xFF));
        if (!paramBoolean) {
            arrayOfByte[5] = ((byte)0);
        }
        if (NormalModeActivity.controlMode) {
            arrayOfByte[3] = ((byte)9);
        }
        cmdSend(arrayOfByte, 100);
    }

    private void sendStop()
    {
        byte[] arrayOfByte = new byte[4];
        byte[] tmp5_4 = arrayOfByte;
        tmp5_4[0] = 85;
        byte[] tmp10_5 = tmp5_4;
        tmp10_5[1] = 85;
        byte[] tmp15_10 = tmp10_5;
        tmp15_10[2] = 2;
        byte[] tmp20_15 = tmp15_10;
        tmp20_15[3] = 7;
        tmp20_15;
        if (NormalModeActivity.controlMode) {
            arrayOfByte[3] = ((byte)10);
        }
        cmdSend(arrayOfByte, 100);
    }

    public void onBackPressed()
    {
        finish();
    }

    public void onClick(View paramView)
    {
        int i = paramView.getId();
        if (i != 2131165213)
        {
            if (i != 2131165223)
            {
                if (i == 2131165393) {
                    startActivity(new Intent(this, VoiceInfoActivity.class));
                }
            }
            else {
                sendStop();
            }
        }
        else {
            finish();
        }
    }

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(2131296285);
        SysApplication.getInstance().addActivity(this);
        this.speechManager = SpeechManager.init(this, this.mRecognizerListener);
        ImageButton localImageButton = (ImageButton)findViewById(2131165223);
        paramBundle = (ImageButton)findViewById(2131165213);
        TextView localTextView = (TextView)findViewById(2131165393);
        this.resultTv = ((TextView)findViewById(2131165311));
        View localView = findViewById(2131165312);
        paramBundle.setOnClickListener(this);
        localImageButton.setOnClickListener(this);
        localTextView.setOnClickListener(this);
        localView.setOnTouchListener(new OnTouchListener()
        {
            public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
            {
                switch (paramAnonymousMotionEvent.getAction())
                {
                    default:
                        if (VoiceActivity.this.isPress)
                        {
                            VoiceActivity.access$002(VoiceActivity.this, false);
                            VoiceActivity.this.speechManager.cancelListen();
                            VoiceActivity.this.showTips("", false);
                            LogUtil.e("VoiceActivity", "action cancel");
                        }
                        break;
                    case 1:
                        if (VoiceActivity.this.isPress)
                        {
                            VoiceActivity.access$002(VoiceActivity.this, false);
                            if (VoiceActivity.this.speechManager.isListening())
                            {
                                VoiceActivity.this.speechManager.stopListen();
                                VoiceActivity.this.showTips(2131427428, false);
                                LogUtil.e("VoiceActivity", "action stop");
                            }
                        }
                        break;
                    case 0:
                    case 2:
                        if ((!VoiceActivity.this.isPress) && (!VoiceActivity.this.speechManager.isListening()))
                        {
                            VoiceActivity.this.speechManager.startListen();
                            VoiceActivity.access$002(VoiceActivity.this, true);
                            LogUtil.e("VoiceActivity", "start listen");
                        }
                        break;
                }
                return false;
            }
        });
        localTextView.setOnClickListener(this);
        this.resultList = new LinkedList();
        this.dao = new VoiceDAO(this);
        this.mHandler = new Handler(new MsgCallback());
        this.bleManager = BLEManager.getInstance();
        this.vibrator = ((Vibrator)getSystemService("vibrator"));
        paramBundle = new HandlerThread("VoiceThread");
        paramBundle.start();
        this.bgHandler = new Handler(paramBundle.getLooper(), new VoiceCallback());
        mayRequestAudio();
    }

    protected void onDestroy()
    {
        super.onDestroy();
        this.speechManager.destroy();
    }

    protected void onPause()
    {
        super.onPause();
        this.speechManager.cancelListen();
    }

    protected void onResume()
    {
        super.onResume();
        this.bleManager.setHandler(this.mHandler);
    }

    public void playVibrate()
    {
        this.vibrator.vibrate(40L);
    }

    void showTips(int paramInt1, int paramInt2, boolean paramBoolean)
    {
        showTips(getString(paramInt1, new Object[] { Integer.valueOf(paramInt2) }), paramBoolean);
    }

    void showTips(int paramInt, boolean paramBoolean)
    {
        showTips(paramInt, Color.parseColor("#0F75DE"), paramBoolean);
    }

    void showTips(CharSequence paramCharSequence, int paramInt, boolean paramBoolean)
    {
        try
        {
            this.mHandler.removeMessages(17);
            this.resultTv.clearAnimation();
            this.resultTv.setTextColor(paramInt);
            this.resultTv.setText(paramCharSequence);
            if (this.resultTv.getVisibility() != 0) {
                this.resultTv.setVisibility(0);
            }
            if (paramBoolean) {
                dismissResult(3000L);
            }
            return;
        }
        finally {}
    }

    void showTips(CharSequence paramCharSequence, boolean paramBoolean)
    {
        showTips(paramCharSequence, Color.parseColor("#0F75DE"), paramBoolean);
    }

    class MsgCallback
            implements Callback
    {
        MsgCallback() {}

        public boolean handleMessage(Message paramMessage)
        {
            try
            {
                int i = paramMessage.what;
                if (i != 6)
                {
                    if (i != 11) {
                        switch (i)
                        {
                            default:
                                break;
                            case 18:
                                i = paramMessage.arg1;
                                boolean bool;
                                if (paramMessage.arg2 != 0) {
                                    bool = true;
                                } else {
                                    bool = false;
                                }
                                paramMessage = (CharSequence)paramMessage.obj;
                                VoiceActivity.this.showTips(paramMessage, i, bool);
                                break;
                            case 17:
                                paramMessage = AnimationUtils.loadAnimation(VoiceActivity.this.getApplicationContext(), 17432577);
                                AnimationListener local1 = new com/lobot/lobotcontrol/VoiceActivity$MsgCallback$1;
                                local1.<init>(this);
                                paramMessage.setAnimationListener(local1);
                                VoiceActivity.this.resultTv.startAnimation(paramMessage);
                                break;
                        }
                    } else {
                        VoiceActivity.this.speechManager.speak(VoiceActivity.this.getString(2131427449), null);
                    }
                }
                else
                {
                    VoiceActivity.this.speechManager.speak(VoiceActivity.this.getString(2131427402), null);
                    VoiceActivity.this.finish();
                }
                return true;
            }
            finally {}
        }
    }

    class VoiceCallback
            implements Callback
    {
        VoiceCallback() {}

        public boolean handleMessage(Message paramMessage)
        {
            switch (paramMessage.what)
            {
                default:
                    break;
                case 2:
                    LogUtil.e("VoiceActivity", "voice time out. stop listen");
                    VoiceActivity.this.speechManager.stopListen();
                    break;
                case 1:
                    Object localObject1 = (VoiceResult)paramMessage.obj;
                    paramMessage = ((VoiceResult)localObject1).getSentence();
                    Object localObject2 = new StringBuilder();
                    ((StringBuilder)localObject2).append("sentence:");
                    ((StringBuilder)localObject2).append(paramMessage);
                    Log.e("VoiceActivity", ((StringBuilder)localObject2).toString());
                    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(paramMessage);
                    localObject2 = new LinkedList();
                    localObject1 = ((VoiceResult)localObject1).getFullWs();
                    localObject1 = VoiceActivity.this.dao.query((List)localObject1);
                    if (!((List)localObject1).isEmpty())
                    {
                        Object localObject3 = ((List)localObject1).iterator();
                        while (((Iterator)localObject3).hasNext())
                        {
                            localObject1 = (VoiceModel)((Iterator)localObject3).next();
                            StringBuilder localStringBuilder = new StringBuilder();
                            localStringBuilder.append("voiceModel:");
                            localStringBuilder.append(((VoiceModel)localObject1).toString());
                            Log.e("VoiceActivity", localStringBuilder.toString());
                            int i = ((VoiceModel)localObject1).containKey(paramMessage);
                            localStringBuilder = new StringBuilder();
                            localStringBuilder.append("index = ");
                            localStringBuilder.append(i);
                            LogUtil.e("VoiceActivity", localStringBuilder.toString());
                            if (i >= 0)
                            {
                                localObject3 = new StringBuilder();
                                ((StringBuilder)localObject3).append("model.getAcceptVoice:");
                                ((StringBuilder)localObject3).append(((VoiceModel)localObject1).getAcceptVoice());
                                Log.e("VoiceActivity", ((StringBuilder)localObject3).toString());
                                localObject3 = localObject1.getAcceptVoice().split(";")[i];
                                localStringBuilder = new StringBuilder();
                                localStringBuilder.append("recognizer : ");
                                localStringBuilder.append(localObject1);
                                localStringBuilder.append("  key = ");
                                localStringBuilder.append((String)localObject3);
                                LogUtil.e("VoiceActivity", localStringBuilder.toString());
                                String str = VoiceActivity.this.getKey(paramMessage, (String)localObject3);
                                localStringBuilder = new StringBuilder();
                                localStringBuilder.append("ordinaryKey:");
                                localStringBuilder.append(str);
                                Log.e("VoiceActivity", localStringBuilder.toString());
                                if (!TextUtils.isEmpty(str)) {
                                    i = paramMessage.indexOf(str);
                                } else {
                                    i = -1;
                                }
                                if (i != -1)
                                {
                                    localSpannableStringBuilder.replace(i, ((String)localObject3).length() + i, (CharSequence)localObject3);
                                    localSpannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#0F75DE")), i, ((String)localObject3).length() + i, 33);
                                }
                                ((LinkedList)localObject2).add(localObject1);
                            }
                        }
                    }
                    VoiceActivity.this.mHandler.obtainMessage(18, -65536, 1, localSpannableStringBuilder).sendToTarget();
                    if (((LinkedList)localObject2).size() == 1) {
                        VoiceActivity.this.onRecognizedVoice((VoiceModel)((LinkedList)localObject2).get(0));
                    }
                    break;
            }
            return true;
        }
    }
}
