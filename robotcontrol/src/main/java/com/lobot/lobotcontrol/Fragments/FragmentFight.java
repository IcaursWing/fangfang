package com.lobot.lobotcontrol.Fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import androidx.core.app.ActivityCompat;
import android.support.v4.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.lobot.lobotcontrol.NormalModeActivity;
import com.lobot.lobotcontrol.VoiceActivity;
import com.lobot.lobotcontrol.component.BatteryView;
import com.lobot.lobotcontrol.component.HandShake;
import com.lobot.lobotcontrol.component.HandShake.DirectionListener;
import com.lobot.lobotcontrol.component.popdialog.AboutDialog;
import com.lobot.lobotcontrol.component.popdialog.AddDanceDialog;
import com.lobot.lobotcontrol.component.popdialog.AddDanceDialog.OnAddDanceDialogClickListener;
import com.lobot.lobotcontrol.component.popdialog.ControlModeDialog;
import com.lobot.lobotcontrol.component.popdialog.ControlModeDialog.OnControlModeClickListener;
import com.lobot.lobotcontrol.component.popdialog.NormalDialog;
import com.lobot.lobotcontrol.component.popdialog.NormalDialog.OnNormalDialogtClickListener;
import com.lobot.lobotcontrol.component.popdialog.RestoreDialog;
import com.lobot.lobotcontrol.component.popdialog.RestoreDialog.OnRestoreDialogtClickListener;
import com.lobot.lobotcontrol.component.popdialog.SearchDeviceDialog;
import com.lobot.lobotcontrol.component.popdialog.SearchDeviceDialog.OnDeviceSelectedListener;
import com.lobot.lobotcontrol.component.popdialog.SetDialog;
import com.lobot.lobotcontrol.component.popdialog.SetDialog.OnSetDialogtClickListener;
import com.lobot.lobotcontrol.component.popdialog.WorkModeDialog;
import com.lobot.lobotcontrol.component.popdialog.WorkModeDialog.OnWorkModelickListener;
import com.lobot.lobotcontrol.connect.BLEManager;
import com.lobot.lobotcontrol.db.CommandDAO;
import com.lobot.lobotcontrol.model.ByteCommand;
import com.lobot.lobotcontrol.model.ByteCommand.Builder;
import com.lobot.lobotcontrol.model.CommandModel;
import com.lobot.lobotcontrol.uitls.LogUtil;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentFight
        extends Fragment
        implements OnClickListener, OnTouchListener, DirectionListener
{
    private static final int FIRST_INDEX_OF_ACTION = 6;
    private static final int RETRY_TIMES = 3;
    private static final String TAG = FragmentNormal.class.getSimpleName();
    public static List<CommandModel> buttonList;
    private int HandShakeMsgType;
    private long batBack = 0L;
    private int batCnt = 0;
    private BatteryView batteryView;
    private BLEManager bleManager;
    private ImageButton bluetoothBtn;
    public int checkBatCnt = 0;
    private int connectTimes;
    private TextView curBatTv;
    private Button[] customButton;
    private ImageButton fallBtn;
    private boolean firstDetBat = true;
    private boolean isClosingBluetooth = false;
    private boolean isConnected = false;
    private int listIndex;
    private ImageView logo;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice mBluetoothDevice;
    private Handler mHandler;
    private FragmentNormal.ModeSelect mModeSelect;
    private ImageButton setBtn;
    private SetDialog setWindow;
    public boolean startCntFlag;
    Timer timer = new Timer();
    public int timerCnt = 0;
    private ImageButton voiceBtn;

    private void SetTimerTask50ms()
    {
        this.timer.schedule(new TimerTask()
        {
            public void run()
            {
                Message localMessage = new Message();
                localMessage.what = 20;
                FragmentFight.this.mHandler.sendMessage(localMessage);
            }
        }, 0L, 50L);
    }

    private void cmdSend(byte[] paramArrayOfByte, int paramInt)
    {
        if ((!this.isConnected) && (!NormalModeActivity.noShowConnect))
        {
            Toast.makeText(getActivity(), 2131427449, 0).show();
            return;
        }
        if (NormalModeActivity.noShowConnect) {
            return;
        }
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
            Builder localBuilder = new Builder();
            localBuilder.addCommand(arrayOfByte, paramInt);
            this.bleManager.send(localBuilder.createCommands());
        }
    }

    private int getActionIndex(int paramInt)
    {
        switch (paramInt)
        {
            default:
                this.listIndex = 0;
                break;
            case 2131165402:
                this.listIndex = 6;
                break;
            case 2131165379:
                this.listIndex = 9;
                break;
            case 2131165378:
                this.listIndex = 8;
                break;
            case 2131165357:
                this.listIndex = 5;
                break;
            case 2131165320:
                this.listIndex = 3;
                break;
            case 2131165319:
                this.listIndex = 1;
                break;
            case 2131165278:
                this.listIndex = 2;
                break;
            case 2131165277:
                this.listIndex = 0;
                break;
            case 2131165276:
                this.listIndex = 7;
                break;
            case 2131165263:
                this.listIndex = 4;
        }
        return this.listIndex;
    }

    private void handShakeStop()
    {
        switch (this.HandShakeMsgType)
        {
            default:
                break;
            case 3:
                sendActionCmd(11, true);
                break;
            case 2:
                sendActionCmd(2, true);
                break;
            case 1:
                sendActionCmd(12, true);
                break;
            case 0:
                sendActionCmd(1, true);
        }
    }

    private void mayRequestLocation()
    {
        if ((this.mBluetoothAdapter.isEnabled()) && (Build.VERSION.SDK_INT >= 23) && (ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_COARSE_LOCATION") != 0))
        {
            ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), "android.permission.ACCESS_COARSE_LOCATION");
            ActivityCompat.requestPermissions(getActivity(), new String[] { "android.permission.ACCESS_COARSE_LOCATION" }, 0);
            return;
        }
    }

    private void sendActionCmd(int paramInt, boolean paramBoolean)
    {
        byte[] arrayOfByte = new byte[7];
        byte[] tmp6_5 = arrayOfByte;
        tmp6_5[0] = 85;
        byte[] tmp12_6 = tmp6_5;
        tmp12_6[1] = 85;
        byte[] tmp18_12 = tmp12_6;
        tmp18_12[2] = 5;
        byte[] tmp24_18 = tmp18_12;
        tmp24_18[3] = 6;
        byte[] tmp29_24 = tmp24_18;
        tmp29_24[4] = 0;
        byte[] tmp35_29 = tmp29_24;
        tmp35_29[5] = 1;
        byte[] tmp41_35 = tmp35_29;
        tmp41_35[6] = 0;
        tmp41_35;
        arrayOfByte[4] = ((byte)(byte)(paramInt & 0xFF));
        if (!paramBoolean) {
            arrayOfByte[5] = ((byte)0);
        }
        if (NormalModeActivity.controlMode) {
            arrayOfByte[3] = ((byte)9);
        }
        cmdSend(arrayOfByte, 20);
    }

    private void sendFallStand()
    {
        byte[] arrayOfByte = new byte[4];
        byte[] tmp5_4 = arrayOfByte;
        tmp5_4[0] = 85;
        byte[] tmp11_5 = tmp5_4;
        tmp11_5[1] = 85;
        byte[] tmp17_11 = tmp11_5;
        tmp17_11[2] = 2;
        byte[] tmp23_17 = tmp17_11;
        tmp23_17[3] = 17;
        tmp23_17;
        if (NormalModeActivity.controlMode) {
            arrayOfByte[3] = ((byte)10);
        }
        cmdSend(arrayOfByte, 20);
    }

    private void sendStop()
    {
        byte[] arrayOfByte = new byte[4];
        byte[] tmp5_4 = arrayOfByte;
        tmp5_4[0] = 85;
        byte[] tmp11_5 = tmp5_4;
        tmp11_5[1] = 85;
        byte[] tmp17_11 = tmp11_5;
        tmp17_11[2] = 2;
        byte[] tmp23_17 = tmp17_11;
        tmp23_17[3] = 7;
        tmp23_17;
        if (NormalModeActivity.controlMode) {
            arrayOfByte[3] = ((byte)10);
        }
        cmdSend(arrayOfByte, 20);
    }

    private void setState(boolean paramBoolean)
    {
        String str = TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("isConnected = ");
        localStringBuilder.append(paramBoolean);
        LogUtil.i(str, localStringBuilder.toString());
        if (paramBoolean)
        {
            this.isConnected = true;
            this.bluetoothBtn.setBackgroundResource(2131099753);
        }
        else
        {
            this.isConnected = false;
            this.bluetoothBtn.setBackgroundResource(2131099769);
            this.isClosingBluetooth = false;
        }
        ((AnimationDrawable)this.bluetoothBtn.getBackground()).start();
    }

    public void onAttach(Activity paramActivity)
    {
        super.onAttach(paramActivity);
        if ((paramActivity instanceof FragmentNormal.ModeSelect)) {
            this.mModeSelect = ((FragmentNormal.ModeSelect)paramActivity);
        }
    }

    public void onClick(View paramView)
    {
        int i = paramView.getId();
        switch (i)
        {
            default:
                break;
            case 2131165391:
                startActivity(new Intent(getActivity(), VoiceActivity.class));
                break;
            case 2131165362:
                sendStop();
                sendActionCmd(0, true);
                break;
            case 2131165346:
                this.setWindow = SetDialog.createDialog(getActivity(), NormalModeActivity.screenWidth / 5, NormalModeActivity.screenHigh, new OnSetDialogtClickListener()
                {
                    public void onSetDialogClick(int paramAnonymousInt)
                    {
                        switch (paramAnonymousInt)
                        {
                            default:
                                break;
                            case 4:
                                FragmentFight.this.setWindow.dismiss();
                                break;
                            case 3:
                                FragmentFight.this.setWindow.dismiss();
                                RestoreDialog.createDialog(FragmentFight.this.getActivity(), NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh / 2, FragmentFight.this.getString(2131427433), FragmentFight.this.getString(2131427434), true, new OnRestoreDialogtClickListener()
                                {
                                    public void onRestoreDialogClick(boolean paramAnonymous2Boolean)
                                    {
                                        if (paramAnonymous2Boolean) {
                                            FragmentFight.this.updateButtonText();
                                        }
                                    }
                                }).showDialog();
                                break;
                            case 2:
                                FragmentFight.this.setWindow.dismiss();
                                WorkModeDialog.createDialog(FragmentFight.this.getActivity(), NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh / 2, new OnWorkModelickListener()
                                {
                                    public void onWorkModeClick(int paramAnonymous2Int)
                                    {
                                        if (NormalModeActivity.workMode != paramAnonymous2Int) {
                                            FragmentFight.this.mModeSelect.onModeSelected(paramAnonymous2Int);
                                        }
                                        NormalModeActivity.workMode = paramAnonymous2Int;
                                    }
                                }).showDialog();
                                break;
                            case 1:
                                FragmentFight.this.setWindow.dismiss();
                                ControlModeDialog.createDialog(FragmentFight.this.getActivity(), NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh / 2, new OnControlModeClickListener()
                                {
                                    public void onControlModeClick(boolean paramAnonymous2Boolean1, boolean paramAnonymous2Boolean2)
                                    {
                                        if (paramAnonymous2Boolean1) {
                                            NormalModeActivity.controlMode = paramAnonymous2Boolean2;
                                        }
                                    }
                                }).showDialog();
                                break;
                            case 0:
                                FragmentFight.this.setWindow.dismiss();
                                AboutDialog.createDialog(FragmentFight.this.getActivity(), NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh / 2).showDialog();
                        }
                    }
                });
                this.setWindow.showDialog();
                break;
            case 2131165263:
            case 2131165276:
            case 2131165277:
            case 2131165278:
            case 2131165319:
            case 2131165320:
            case 2131165357:
            case 2131165378:
            case 2131165379:
            case 2131165402:
                i = ((CommandModel)buttonList.get(getActionIndex(i) + 6)).getAction();
                if (i != 255) {
                    sendActionCmd(i, true);
                }
                break;
            case 2131165258:
                sendFallStand();
                break;
            case 2131165218:
                mayRequestLocation();
                if (this.mBluetoothAdapter.isEnabled())
                {
                    if (this.isConnected)
                    {
                        this.isClosingBluetooth = true;
                        NormalDialog.createDialog(getActivity(), NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh / 2, getString(2131427403), getString(2131427401), false, new OnNormalDialogtClickListener()
                        {
                            public void onNormalDialogClick(boolean paramAnonymousBoolean)
                            {
                                if (paramAnonymousBoolean) {
                                    FragmentFight.this.bleManager.stop();
                                } else {
                                    FragmentFight.access$402(FragmentFight.this, false);
                                }
                            }
                        }).showDialog();
                    }
                    else
                    {
                        SearchDeviceDialog.createDialog(getActivity(), NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh * 5 / 6, new OnDeviceSelectedListener()
                        {
                            public void onDeviceSelected(BluetoothDevice paramAnonymousBluetoothDevice)
                            {
                                String str = FragmentFight.TAG;
                                StringBuilder localStringBuilder = new StringBuilder();
                                localStringBuilder.append("bond state = ");
                                localStringBuilder.append(paramAnonymousBluetoothDevice.getBondState());
                                LogUtil.i(str, localStringBuilder.toString());
                                FragmentFight.access$602(FragmentFight.this, paramAnonymousBluetoothDevice);
                                FragmentFight.this.bleManager.connect(paramAnonymousBluetoothDevice);
                                Toast.makeText(FragmentFight.this.getActivity(), 2131427365, 0).show();
                            }
                        }).showDialog();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), 2131427459, 0).show();
                    startActivity(new Intent("android.settings.BLUETOOTH_SETTINGS"));
                }
                break;
        }
    }

    public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
        return paramLayoutInflater.inflate(2131296288, paramViewGroup, false);
    }

    public void onDestroy()
    {
        super.onDestroy();
        this.timer.cancel();
    }

    public void onDetach()
    {
        super.onDetach();
        this.mModeSelect = null;
    }

    public void onDirection(int paramInt)
    {
        if (!this.isConnected) {
            return;
        }
        switch (paramInt)
        {
            default:
                break;
            case 3:
                sendActionCmd(11, false);
                this.HandShakeMsgType = 3;
                break;
            case 2:
                sendActionCmd(2, false);
                this.HandShakeMsgType = 2;
                break;
            case 1:
                sendActionCmd(12, false);
                this.HandShakeMsgType = 1;
                break;
            case 0:
                sendActionCmd(1, false);
                this.HandShakeMsgType = 0;
                break;
            case -1:
                handShakeStop();
                this.HandShakeMsgType = -1;
                break;
            case -2:
                handShakeStop();
                this.HandShakeMsgType = -2;
        }
    }

    public void onResume()
    {
        super.onResume();
        this.bleManager = BLEManager.getInstance();
        this.isConnected = this.bleManager.isConnected();
        this.bleManager.setHandler(this.mHandler);
        Object localObject = TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("onResume isConnected= ");
        localStringBuilder.append(this.isConnected);
        LogUtil.i((String)localObject, localStringBuilder.toString());
        if (this.isConnected) {
            this.bluetoothBtn.setBackgroundResource(2131099753);
        } else {
            this.bluetoothBtn.setBackgroundResource(2131099769);
        }
        ((AnimationDrawable)this.bluetoothBtn.getBackground()).start();
        localObject = AnimationUtils.loadAnimation(getActivity(), 2130771980);
        this.setBtn.startAnimation((Animation)localObject);
    }

    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
        int i = paramView.getId();
        if (paramMotionEvent.getAction() == 0)
        {
            if (i == 2131165324)
            {
                sendActionCmd(4, false);
            }
            else if (i == 2131165281)
            {
                sendActionCmd(3, false);
            }
            else
            {
                getActionIndex(i);
                this.startCntFlag = true;
                this.timerCnt = 0;
            }
        }
        else if (paramMotionEvent.getAction() == 1) {
            if (i == 2131165324)
            {
                sendActionCmd(4, true);
            }
            else if (i == 2131165281)
            {
                sendActionCmd(3, true);
            }
            else
            {
                this.startCntFlag = false;
                this.timerCnt = 0;
            }
        }
        return false;
    }

    public void onViewCreated(View paramView, Bundle paramBundle)
    {
        super.onViewCreated(paramView, paramBundle);
        this.mHandler = new Handler(new MsgCallBack());
        this.setBtn = ((ImageButton)paramView.findViewById(2131165346));
        this.bluetoothBtn = ((ImageButton)paramView.findViewById(2131165218));
        this.fallBtn = ((ImageButton)paramView.findViewById(2131165258));
        this.voiceBtn = ((ImageButton)paramView.findViewById(2131165391));
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.curBatTv = ((TextView)paramView.findViewById(2131165236));
        this.logo = ((ImageView)paramView.findViewById(2131165286));
        int i = 0;
        this.connectTimes = 0;
        SetTimerTask50ms();
        paramBundle = (ImageButton)paramView.findViewById(2131165391);
        this.setBtn.setOnClickListener(this);
        this.bluetoothBtn.setOnClickListener(this);
        paramBundle.setOnClickListener(this);
        this.customButton = new Button[10];
        this.customButton[0] = ((Button)paramView.findViewById(2131165277));
        this.customButton[1] = ((Button)paramView.findViewById(2131165319));
        this.customButton[2] = ((Button)paramView.findViewById(2131165278));
        this.customButton[3] = ((Button)paramView.findViewById(2131165320));
        this.customButton[4] = ((Button)paramView.findViewById(2131165263));
        this.customButton[5] = ((Button)paramView.findViewById(2131165357));
        this.customButton[6] = ((Button)paramView.findViewById(2131165402));
        this.customButton[7] = ((Button)paramView.findViewById(2131165276));
        this.customButton[8] = ((Button)paramView.findViewById(2131165378));
        this.customButton[9] = ((Button)paramView.findViewById(2131165379));
        while (i < this.customButton.length)
        {
            this.customButton[i].setOnTouchListener(this);
            this.customButton[i].setOnClickListener(this);
            i++;
        }
        ImageButton localImageButton1 = (ImageButton)paramView.findViewById(2131165362);
        ImageButton localImageButton2 = (ImageButton)paramView.findViewById(2131165281);
        ImageButton localImageButton3 = (ImageButton)paramView.findViewById(2131165324);
        paramBundle = (HandShake)paramView.findViewById(2131165264);
        this.HandShakeMsgType = -2;
        paramBundle.setDirectionListener(this);
        localImageButton1.setOnClickListener(this);
        localImageButton2.setOnTouchListener(this);
        localImageButton3.setOnTouchListener(this);
        this.fallBtn.setOnClickListener(this);
        if (NormalModeActivity.languageType == 2) {
            this.voiceBtn.setVisibility(4);
        }
        ((AnimationDrawable)this.fallBtn.getBackground()).start();
        updateButtonText();
        this.batteryView = ((BatteryView)paramView.findViewById(2131165216));
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), 2131427425, 1).show();
        } else if (!this.mBluetoothAdapter.isEnabled()) {
            this.mBluetoothAdapter.isEnabled();
        }
        mayRequestLocation();
    }

    public void updateButtonText()
    {
        buttonList = new CommandDAO(getContext()).query(2);
        String[] arrayOfString = new String[buttonList.size()];
        int i = 0;
        for (int j = 0; j < buttonList.size(); j++) {
            if (NormalModeActivity.languageType == 0)
            {
                arrayOfString[j] = ((CommandModel)buttonList.get(j)).getTitle();
            }
            else if (NormalModeActivity.languageType == 1)
            {
                arrayOfString[j] = ((CommandModel)buttonList.get(j)).getTitleTw();
            }
            else if (NormalModeActivity.languageType == 2)
            {
                arrayOfString[j] = ((CommandModel)buttonList.get(j)).getTitleEn();
                this.logo.setImageDrawable(getResources().getDrawable(2131099790));
            }
        }
        if (buttonList.size() >= 24) {
            for (j = i; j < this.customButton.length; j++) {
                this.customButton[j].setText(arrayOfString[(j + 6)]);
            }
        }
    }

    class MsgCallBack
            implements Callback
    {
        MsgCallBack() {}

        public boolean handleMessage(Message paramMessage)
        {
            int i = paramMessage.what;
            if ((i != 11) && (i != 13))
            {
                Object localObject;
                if (i != 16)
                {
                    switch (i)
                    {
                        default:
                            switch (i)
                            {
                                default:
                                    break;
                                case 21:
                                    Toast.makeText(FragmentFight.this.getActivity(), 2131427448, 0).show();
                                    break;
                                case 20:
                                    if (FragmentFight.this.startCntFlag)
                                    {
                                        paramMessage = FragmentFight.this;
                                        i = paramMessage.timerCnt;
                                        paramMessage.timerCnt = (i + 1);
                                        if (i > 35)
                                        {
                                            if (FragmentFight.this.customButton[FragmentFight.this.listIndex] != null)
                                            {
                                                paramMessage = "";
                                                if (NormalModeActivity.languageType == 0) {
                                                    paramMessage = ((CommandModel)FragmentFight.buttonList.get(FragmentFight.this.listIndex + 6)).getTitle();
                                                }
                                                for (;;)
                                                {
                                                    break;
                                                    if (NormalModeActivity.languageType == 1) {
                                                        paramMessage = ((CommandModel)FragmentFight.buttonList.get(FragmentFight.this.listIndex + 6)).getTitleTw();
                                                    } else if (NormalModeActivity.languageType == 2) {
                                                        paramMessage = ((CommandModel)FragmentFight.buttonList.get(FragmentFight.this.listIndex + 6)).getTitleEn();
                                                    }
                                                }
                                                AddDanceDialog.createDialog(FragmentFight.this.getActivity(), NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh * 2 / 3, 2, ((CommandModel)FragmentFight.buttonList.get(FragmentFight.this.listIndex + 6)).getId(), paramMessage, String.valueOf(((CommandModel)FragmentFight.buttonList.get(FragmentFight.this.listIndex + 6)).getAction()), new OnAddDanceDialogClickListener()
                                                {
                                                    public void onAddDanceDialogClick(boolean paramAnonymousBoolean)
                                                    {
                                                        FragmentFight.this.updateButtonText();
                                                    }
                                                }).showDialog();
                                            }
                                            FragmentFight.this.timerCnt = 0;
                                            FragmentFight.this.startCntFlag = false;
                                        }
                                    }
                                    paramMessage = FragmentFight.this;
                                    i = paramMessage.checkBatCnt + 1;
                                    paramMessage.checkBatCnt = i;
                                    if ((i < 40) || (NormalModeActivity.workMode != 2)) {
                                        break;
                                    }
                                    if ((FragmentFight.this.isConnected) && (!FragmentFight.this.isClosingBluetooth))
                                    {
                                        paramMessage = new Builder();
                                        paramMessage.addCommand(new byte[] { 85, 85, 2, 15 }, 10L);
                                        FragmentFight.this.bleManager.send(paramMessage.createCommands());
                                        LogUtil.i(FragmentFight.TAG, "Fight");
                                    }
                                    if ((FragmentFight.this.isConnected) && (NormalModeActivity.m_Bat != 65535L))
                                    {
                                        if ((NormalModeActivity.m_Bat >= 6800L) && (NormalModeActivity.m_Bat <= 8400L))
                                        {
                                            FragmentFight.this.batteryView.setCurBat(NormalModeActivity.m_Bat);
                                        }
                                        else if (NormalModeActivity.m_Bat == 0L)
                                        {
                                            FragmentFight.this.batteryView.setCurBat(-1L);
                                            FragmentFight.this.curBatTv.setText("");
                                        }
                                        else if (NormalModeActivity.m_Bat > 8400L)
                                        {
                                            FragmentFight.this.batteryView.setCurBat(8400L);
                                        }
                                        else if (NormalModeActivity.m_Bat < 6800L)
                                        {
                                            FragmentFight.this.batteryView.setCurBat(0L);
                                        }
                                        if (FragmentFight.this.firstDetBat)
                                        {
                                            FragmentFight.access$1402(FragmentFight.this, false);
                                            FragmentFight.access$1502(FragmentFight.this, NormalModeActivity.m_Bat);
                                            FragmentFight.this.batteryView.setCurBat(NormalModeActivity.m_Bat);
                                        }
                                        else if (Math.abs(NormalModeActivity.m_Bat - FragmentFight.this.batBack) <= 100L)
                                        {
                                            FragmentFight.access$1502(FragmentFight.this, NormalModeActivity.m_Bat);
                                            FragmentFight.access$1602(FragmentFight.this, 0);
                                            FragmentFight.this.batteryView.setCurBat(NormalModeActivity.m_Bat);
                                        }
                                        else if (FragmentFight.access$1604(FragmentFight.this) >= 3)
                                        {
                                            FragmentFight.access$1602(FragmentFight.this, 0);
                                            FragmentFight.access$1502(FragmentFight.this, NormalModeActivity.m_Bat);
                                            FragmentFight.this.batteryView.setCurBat(NormalModeActivity.m_Bat);
                                        }
                                        FragmentFight.this.checkBatCnt = 0;
                                        float f = Math.round((float)NormalModeActivity.m_Bat / 1000.0F * 100.0F) / 100.0F;
                                        if (f <= 7.12D) {
                                            FragmentFight.this.curBatTv.setTextColor(FragmentFight.this.getResources().getColor(2130968618));
                                        } else {
                                            FragmentFight.this.curBatTv.setTextColor(FragmentFight.this.getResources().getColor(2130968619));
                                        }
                                        paramMessage = new StringBuilder();
                                        paramMessage.append(String.valueOf(f));
                                        paramMessage.append("V");
                                        paramMessage = paramMessage.toString();
                                        FragmentFight.this.curBatTv.setText(paramMessage);
                                    }
                                    else
                                    {
                                        FragmentFight.this.batteryView.setCurBat(-1L);
                                        FragmentFight.this.curBatTv.setText("");
                                        FragmentFight.this.checkBatCnt = 0;
                                        NormalModeActivity.m_Bat = 65535L;
                                        FragmentFight.access$1402(FragmentFight.this, true);
                                    }
                                    break;
                            }
                            break;
                        case 6:
                            Toast.makeText(FragmentFight.this.getActivity(), 2131427402, 0).show();
                            FragmentFight.this.setState(false);
                            break;
                        case 5:
                            paramMessage = FragmentFight.TAG;
                            localObject = new StringBuilder();
                            ((StringBuilder)localObject).append("reconnect bluetooth");
                            ((StringBuilder)localObject).append(FragmentFight.this.mBluetoothDevice.getName());
                            ((StringBuilder)localObject).append(" ");
                            ((StringBuilder)localObject).append(FragmentFight.this.connectTimes);
                            LogUtil.i(paramMessage, ((StringBuilder)localObject).toString());
                            FragmentFight.this.bleManager.connect(FragmentFight.this.mBluetoothDevice);
                            break;
                        case 4:
                            if (FragmentFight.this.connectTimes < 3)
                            {
                                FragmentFight.access$808(FragmentFight.this);
                                FragmentFight.this.mHandler.sendEmptyMessageDelayed(5, 300L);
                            }
                            else
                            {
                                FragmentFight.access$802(FragmentFight.this, 0);
                                Toast.makeText(FragmentFight.this.getActivity(), 2131427363, 0).show();
                                FragmentFight.this.setState(false);
                            }
                            break;
                        case 3:
                            LogUtil.i(FragmentFight.TAG, "connected ");
                            if (!NormalModeActivity.noShowConnect)
                            {
                                Toast.makeText(FragmentFight.this.getActivity(), 2131427364, 0).show();
                            }
                            else
                            {
                                Toast.makeText(FragmentFight.this.getActivity(), 2131427432, 0).show();
                                NormalModeActivity.noShowConnect = false;
                            }
                            FragmentFight.this.setState(true);
                            break;
                    }
                }
                else
                {
                    FragmentFight.this.bleManager.send((ByteCommand)paramMessage.obj);
                    localObject = FragmentFight.this.mHandler.obtainMessage(16, paramMessage.arg1, -1, paramMessage.obj);
                    FragmentFight.this.mHandler.sendMessageDelayed((Message)localObject, paramMessage.arg1);
                }
            }
            return true;
        }
    }
}
