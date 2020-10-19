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
import com.lobot.lobotcontrol.component.popdialog.DanceDialog;
import com.lobot.lobotcontrol.component.popdialog.DanceDialog.OnDanceDialogtClickListener;
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

public class FragmentNormal
        extends Fragment
        implements OnClickListener, DirectionListener, OnTouchListener
{
    private static final int RETRY_TIMES = 3;
    private static final String TAG = "FragmentNormal";
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
    private boolean firstDetBat = true;
    private boolean isClosingBluetooth = false;
    private boolean isConnected = false;
    private int listIndex;
    private ImageView logo;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice mBluetoothDevice;
    private Handler mHandler;
    private ModeSelect mModeSelect;
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
                FragmentNormal.this.mHandler.sendMessage(localMessage);
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
            case 2131165397:
                this.listIndex = 1;
                break;
            case 2131165396:
                this.listIndex = 0;
                break;
            case 2131165358:
                this.listIndex = 5;
                break;
            case 2131165352:
                this.listIndex = 4;
                break;
            case 2131165309:
                this.listIndex = 3;
                break;
            case 2131165226:
                this.listIndex = 2;
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
        byte[] tmp30_24 = tmp24_18;
        tmp30_24[4] = 0;
        byte[] tmp36_30 = tmp30_24;
        tmp36_30[5] = 1;
        byte[] tmp42_36 = tmp36_30;
        tmp42_36[6] = 0;
        tmp42_36;
        arrayOfByte[4] = ((byte)(byte)(paramInt & 0xFF));
        if (!paramBoolean) {
            arrayOfByte[5] = ((byte)0);
        }
        if (NormalModeActivity.controlMode) {
            arrayOfByte[3] = ((byte)9);
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
        if ((paramActivity instanceof ModeSelect)) {
            this.mModeSelect = ((ModeSelect)paramActivity);
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
                                FragmentNormal.this.setWindow.dismiss();
                                break;
                            case 3:
                                FragmentNormal.this.setWindow.dismiss();
                                RestoreDialog.createDialog(FragmentNormal.this.getActivity(), NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh / 2, FragmentNormal.this.getString(2131427433), FragmentNormal.this.getString(2131427434), true, new OnRestoreDialogtClickListener()
                                {
                                    public void onRestoreDialogClick(boolean paramAnonymous2Boolean)
                                    {
                                        if (paramAnonymous2Boolean) {
                                            FragmentNormal.this.updateButtonText();
                                        }
                                    }
                                }).showDialog();
                                break;
                            case 2:
                                FragmentNormal.this.setWindow.dismiss();
                                WorkModeDialog.createDialog(FragmentNormal.this.getActivity(), NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh / 2, new OnWorkModelickListener()
                                {
                                    public void onWorkModeClick(int paramAnonymous2Int)
                                    {
                                        if (NormalModeActivity.workMode != paramAnonymous2Int) {
                                            FragmentNormal.this.mModeSelect.onModeSelected(paramAnonymous2Int);
                                        }
                                    }
                                }).showDialog();
                                break;
                            case 1:
                                FragmentNormal.this.setWindow.dismiss();
                                ControlModeDialog.createDialog(FragmentNormal.this.getActivity(), NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh / 2, new OnControlModeClickListener()
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
                                FragmentNormal.this.setWindow.dismiss();
                                AboutDialog.createDialog(FragmentNormal.this.getActivity(), NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh / 2).showDialog();
                        }
                    }
                });
                this.setWindow.showDialog();
                break;
            case 2131165289:
                DanceDialog.createDialog(getActivity(), 1, NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh * 5 / 6, new OnDanceDialogtClickListener()
                {
                    public void onDanceDialogClick(int paramAnonymousInt)
                    {
                        FragmentNormal.this.sendActionCmd(paramAnonymousInt, true);
                    }
                }).showDialog();
                break;
            case 2131165243:
                DanceDialog.createDialog(getActivity(), 0, NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh * 5 / 6, new OnDanceDialogtClickListener()
                {
                    public void onDanceDialogClick(int paramAnonymousInt)
                    {
                        FragmentNormal.this.sendActionCmd(paramAnonymousInt, true);
                    }
                }).showDialog();
                break;
            case 2131165239:
                DanceDialog.createDialog(getActivity(), 3, NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh * 5 / 6, new OnDanceDialogtClickListener()
                {
                    public void onDanceDialogClick(int paramAnonymousInt)
                    {
                        FragmentNormal.this.sendActionCmd(paramAnonymousInt, true);
                    }
                }).showDialog();
                break;
            case 2131165226:
            case 2131165309:
            case 2131165352:
            case 2131165358:
            case 2131165396:
            case 2131165397:
                sendActionCmd(((CommandModel)buttonList.get(getActionIndex(i))).getAction(), true);
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
                                    FragmentNormal.this.bleManager.stop();
                                } else {
                                    FragmentNormal.access$402(FragmentNormal.this, false);
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
                                String str = FragmentNormal.TAG;
                                StringBuilder localStringBuilder = new StringBuilder();
                                localStringBuilder.append("bond state = ");
                                localStringBuilder.append(paramAnonymousBluetoothDevice.getBondState());
                                LogUtil.i(str, localStringBuilder.toString());
                                FragmentNormal.access$602(FragmentNormal.this, paramAnonymousBluetoothDevice);
                                FragmentNormal.this.bleManager.connect(paramAnonymousBluetoothDevice);
                                Toast.makeText(FragmentNormal.this.getActivity(), 2131427365, 0).show();
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
        return paramLayoutInflater.inflate(2131296290, paramViewGroup, false);
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

    public void onPause()
    {
        super.onPause();
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
        this.voiceBtn = ((ImageButton)paramView.findViewById(2131165391));
        this.curBatTv = ((TextView)paramView.findViewById(2131165236));
        this.setBtn.setOnClickListener(this);
        this.bluetoothBtn = ((ImageButton)paramView.findViewById(2131165218));
        this.bluetoothBtn.setOnClickListener(this);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.batteryView = ((BatteryView)paramView.findViewById(2131165216));
        this.logo = ((ImageView)paramView.findViewById(2131165286));
        int i = 0;
        this.connectTimes = 0;
        SetTimerTask50ms();
        paramBundle = new View[4];
        paramBundle[0] = paramView.findViewById(2131165243);
        paramBundle[1] = paramView.findViewById(2131165239);
        paramBundle[2] = paramView.findViewById(2131165289);
        paramBundle[3] = paramView.findViewById(2131165391);
        this.customButton = new Button[6];
        this.customButton[0] = ((Button)paramView.findViewById(2131165396));
        this.customButton[1] = ((Button)paramView.findViewById(2131165397));
        this.customButton[2] = ((Button)paramView.findViewById(2131165226));
        this.customButton[3] = ((Button)paramView.findViewById(2131165309));
        this.customButton[4] = ((Button)paramView.findViewById(2131165352));
        this.customButton[5] = ((Button)paramView.findViewById(2131165358));
        int k;
        for (int j = 0;; j++)
        {
            k = i;
            if (j >= paramBundle.length) {
                break;
            }
            paramBundle[j].setOnClickListener(this);
        }
        while (k < this.customButton.length)
        {
            this.customButton[k].setOnTouchListener(this);
            this.customButton[k].setOnClickListener(this);
            k++;
        }
        paramBundle = (HandShake)paramView.findViewById(2131165264);
        this.HandShakeMsgType = -2;
        paramBundle.setDirectionListener(this);
        ImageButton localImageButton = (ImageButton)paramView.findViewById(2131165362);
        paramBundle = (ImageButton)paramView.findViewById(2131165281);
        paramView = (ImageButton)paramView.findViewById(2131165324);
        localImageButton.setOnClickListener(this);
        paramBundle.setOnTouchListener(this);
        paramView.setOnTouchListener(this);
        if (NormalModeActivity.languageType == 2) {
            this.voiceBtn.setVisibility(4);
        }
        updateButtonText();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), 2131427425, 1).show();
        } else {
            this.mBluetoothAdapter.isEnabled();
        }
        mayRequestLocation();
    }

    public void updateButtonText()
    {
        buttonList = new CommandDAO(getContext()).query(2);
        String[] arrayOfString = new String[buttonList.size()];
        int i = 0;
        int k;
        for (int j = 0;; j++)
        {
            k = i;
            if (j >= buttonList.size()) {
                break;
            }
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
        while (k < this.customButton.length)
        {
            this.customButton[k].setText(arrayOfString[k]);
            k++;
        }
    }

    public static abstract interface ModeSelect
    {
        public abstract void onModeSelected(int paramInt);
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
                                    Toast.makeText(FragmentNormal.this.getActivity(), 2131427448, 0).show();
                                    break;
                                case 20:
                                    if (FragmentNormal.this.startCntFlag)
                                    {
                                        paramMessage = FragmentNormal.this;
                                        i = paramMessage.timerCnt;
                                        paramMessage.timerCnt = (i + 1);
                                        if (i > 35)
                                        {
                                            if (FragmentNormal.this.customButton[FragmentNormal.this.listIndex] != null)
                                            {
                                                paramMessage = "";
                                                if (NormalModeActivity.languageType == 0) {
                                                    paramMessage = ((CommandModel)FragmentNormal.buttonList.get(FragmentNormal.this.listIndex)).getTitle();
                                                }
                                                for (;;)
                                                {
                                                    break;
                                                    if (NormalModeActivity.languageType == 1) {
                                                        paramMessage = ((CommandModel)FragmentNormal.buttonList.get(FragmentNormal.this.listIndex)).getTitleTw();
                                                    } else if (NormalModeActivity.languageType == 2) {
                                                        paramMessage = ((CommandModel)FragmentNormal.buttonList.get(FragmentNormal.this.listIndex)).getTitleEn();
                                                    }
                                                }
                                                AddDanceDialog.createDialog(FragmentNormal.this.getActivity(), NormalModeActivity.screenWidth / 2, NormalModeActivity.screenHigh * 2 / 3, 2, ((CommandModel)FragmentNormal.buttonList.get(FragmentNormal.this.listIndex)).getId(), paramMessage, String.valueOf(((CommandModel)FragmentNormal.buttonList.get(FragmentNormal.this.listIndex)).getAction()), new OnAddDanceDialogClickListener()
                                                {
                                                    public void onAddDanceDialogClick(boolean paramAnonymousBoolean)
                                                    {
                                                        FragmentNormal.this.updateButtonText();
                                                    }
                                                }).showDialog();
                                            }
                                            FragmentNormal.this.timerCnt = 0;
                                            FragmentNormal.this.startCntFlag = false;
                                        }
                                    }
                                    paramMessage = FragmentNormal.this;
                                    i = paramMessage.checkBatCnt + 1;
                                    paramMessage.checkBatCnt = i;
                                    if ((i < 40) || (NormalModeActivity.workMode != 0)) {
                                        break;
                                    }
                                    if ((FragmentNormal.this.isConnected) && (!FragmentNormal.this.isClosingBluetooth))
                                    {
                                        paramMessage = new Builder();
                                        paramMessage.addCommand(new byte[] { 85, 85, 2, 15 }, 10L);
                                        FragmentNormal.this.bleManager.send(paramMessage.createCommands());
                                        LogUtil.i(FragmentNormal.TAG, "Normal");
                                    }
                                    if ((FragmentNormal.this.isConnected) && (NormalModeActivity.m_Bat != 65535L))
                                    {
                                        if ((NormalModeActivity.m_Bat >= 6800L) && (NormalModeActivity.m_Bat <= 8400L))
                                        {
                                            FragmentNormal.this.batteryView.setCurBat(NormalModeActivity.m_Bat);
                                        }
                                        else if (NormalModeActivity.m_Bat == 0L)
                                        {
                                            FragmentNormal.this.batteryView.setCurBat(-1L);
                                            FragmentNormal.this.curBatTv.setText("");
                                        }
                                        else if (NormalModeActivity.m_Bat > 8400L)
                                        {
                                            FragmentNormal.this.batteryView.setCurBat(8400L);
                                        }
                                        else if (NormalModeActivity.m_Bat < 6800L)
                                        {
                                            FragmentNormal.this.batteryView.setCurBat(0L);
                                        }
                                        if (FragmentNormal.this.firstDetBat)
                                        {
                                            FragmentNormal.access$1402(FragmentNormal.this, false);
                                            FragmentNormal.access$1602(FragmentNormal.this, NormalModeActivity.m_Bat);
                                            FragmentNormal.this.batteryView.setCurBat(NormalModeActivity.m_Bat);
                                        }
                                        else if (Math.abs(NormalModeActivity.m_Bat - FragmentNormal.this.batBack) <= 100L)
                                        {
                                            FragmentNormal.access$1602(FragmentNormal.this, NormalModeActivity.m_Bat);
                                            FragmentNormal.access$1702(FragmentNormal.this, 0);
                                            FragmentNormal.this.batteryView.setCurBat(NormalModeActivity.m_Bat);
                                        }
                                        else if (FragmentNormal.access$1704(FragmentNormal.this) >= 3)
                                        {
                                            FragmentNormal.access$1702(FragmentNormal.this, 0);
                                            FragmentNormal.access$1602(FragmentNormal.this, NormalModeActivity.m_Bat);
                                            FragmentNormal.this.batteryView.setCurBat(NormalModeActivity.m_Bat);
                                        }
                                        FragmentNormal.this.checkBatCnt = 0;
                                        float f = Math.round((float)NormalModeActivity.m_Bat / 1000.0F * 100.0F) / 100.0F;
                                        if (f <= 7.12D) {
                                            FragmentNormal.this.curBatTv.setTextColor(FragmentNormal.this.getResources().getColor(2130968618));
                                        } else {
                                            FragmentNormal.this.curBatTv.setTextColor(FragmentNormal.this.getResources().getColor(2130968619));
                                        }
                                        paramMessage = new StringBuilder();
                                        paramMessage.append(String.valueOf(f));
                                        paramMessage.append("V");
                                        paramMessage = paramMessage.toString();
                                        FragmentNormal.this.curBatTv.setText(paramMessage);
                                    }
                                    else
                                    {
                                        FragmentNormal.this.batteryView.setCurBat(-1L);
                                        FragmentNormal.this.checkBatCnt = 0;
                                        NormalModeActivity.m_Bat = 65535L;
                                        FragmentNormal.access$1402(FragmentNormal.this, true);
                                        FragmentNormal.this.curBatTv.setText("");
                                    }
                                    break;
                            }
                            break;
                        case 6:
                            Toast.makeText(FragmentNormal.this.getActivity(), 2131427402, 0).show();
                            FragmentNormal.this.setState(false);
                            break;
                        case 5:
                            paramMessage = FragmentNormal.TAG;
                            localObject = new StringBuilder();
                            ((StringBuilder)localObject).append("reconnect bluetooth");
                            ((StringBuilder)localObject).append(FragmentNormal.this.mBluetoothDevice.getName());
                            ((StringBuilder)localObject).append(" ");
                            ((StringBuilder)localObject).append(FragmentNormal.this.connectTimes);
                            LogUtil.i(paramMessage, ((StringBuilder)localObject).toString());
                            FragmentNormal.this.bleManager.connect(FragmentNormal.this.mBluetoothDevice);
                            break;
                        case 4:
                            if (FragmentNormal.this.connectTimes < 3)
                            {
                                FragmentNormal.access$908(FragmentNormal.this);
                                FragmentNormal.this.mHandler.sendEmptyMessageDelayed(5, 300L);
                            }
                            else
                            {
                                FragmentNormal.access$902(FragmentNormal.this, 0);
                                Toast.makeText(FragmentNormal.this.getActivity(), 2131427363, 0).show();
                                FragmentNormal.this.setState(false);
                            }
                            break;
                        case 3:
                            LogUtil.i(FragmentNormal.TAG, "connected ");
                            if (!NormalModeActivity.noShowConnect)
                            {
                                Toast.makeText(FragmentNormal.this.getActivity(), 2131427364, 0).show();
                            }
                            else
                            {
                                Toast.makeText(FragmentNormal.this.getActivity(), 2131427432, 0).show();
                                NormalModeActivity.noShowConnect = false;
                            }
                            FragmentNormal.this.setState(true);
                            break;
                    }
                }
                else
                {
                    FragmentNormal.this.bleManager.send((ByteCommand)paramMessage.obj);
                    localObject = FragmentNormal.this.mHandler.obtainMessage(16, paramMessage.arg1, -1, paramMessage.obj);
                    FragmentNormal.this.mHandler.sendMessageDelayed((Message)localObject, paramMessage.arg1);
                }
            }
            return true;
        }
    }
}
