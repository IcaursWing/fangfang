/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.main;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ble.BluetoothLeService;
import com.example.fangfang_gai.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * For a given BLE device, this Activity provides the user interface to connect,
 * display data, and display GATT services and characteristics supported by the
 * device. The Activity communicates with {@code BluetoothLeService}, which in
 * turn interacts with the Bluetooth LE API.
 */
public class UploadActivity extends Activity implements
		SeekBar.OnSeekBarChangeListener {
	private final static String TAG = UploadActivity.class.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private StringBuffer sbValues;

	private TextView mConnectionState;
	private TextView mDataField;
	private String mDeviceName;
	private String mDeviceAddress;
	private ExpandableListView mGattServicesList;
	private BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private boolean mConnected = false;
	private BluetoothGattCharacteristic mNotifyCharacteristic;

	boolean connect_status_bit = false;

	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";

	private Handler mHandler;

	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 1000;

	private int i = 0;
	private int TIME = 1000;

	int tx_count = 0;
	int connect_count = 0;
	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	// Handles various events fired by the Service.
	// ACTION_GATT_CONNECTED: connected to a GATT server.
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
	// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
	// ACTION_DATA_AVAILABLE: received data from the device. This can be a
	// result of read
	// or notification operations.
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				// mConnected = true;

				connect_status_bit = true;
				invalidateOptionsMenu();

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				mConnected = false;

				updateConnectionState(R.string.disconnected);
				connect_status_bit = false;
				invalidateOptionsMenu();
				clearUI();

				if (connect_count == 0) {
					connect_count = 1;
					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);
				}

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				displayGattServices(mBluetoothLeService
						.getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) // 接收FFE1串口透传数据通道数据
			{
				// displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
				// byte data1;
				// intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);//
				// .getByteExtra(BluetoothLeService.EXTRA_DATA, data1);

				displayData(intent
						.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE1.equals(action)) // 接收FFE2功能配置返回的数据
			{

			}
			// Log.d("", msg)
		}
	};

	// If a given GATT characteristic is selected, check for supported features.
	// This sample
	// demonstrates 'Read' and 'Notify' features. See
	// http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for
	// the complete
	// list of supported characteristic features.
	private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {

			// Log.i("tag", "uu");
			// if (mGattCharacteristics != null) {
			// final BluetoothGattCharacteristic characteristic =
			// mGattCharacteristics.get(groupPosition).get(childPosition);
			// final int charaProp = characteristic.getProperties();
			// if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0)
			// {
			// // If there is an active notification on a characteristic, clear
			// // it first so it doesn't update the data field on the user
			// interface.
			// if (mNotifyCharacteristic != null) {
			// mBluetoothLeService.setCharacteristicNotification(
			// mNotifyCharacteristic, false);
			// mNotifyCharacteristic = null;
			// }
			// mBluetoothLeService.readCharacteristic(characteristic);
			// }
			// if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) >
			// 0) {
			// mNotifyCharacteristic = characteristic;
			// mBluetoothLeService.setCharacteristicNotification(
			// characteristic, true);
			// }
			// return true;
			// }
			return false;
		}
	};

	private void clearUI() {
		// mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
		mDataField.setText(R.string.no_data);
	}

	int robot_type = 1;

	Button send_button;
	Button enable_button;
	Button IBeacon_set_button;

	ImageButton blue_bar, exit_com, cube, humanoid, sit, tetrapod;

	ImageButton up, down, left, right;

	ImageButton takeHi, takeLow, hit, release, climb;

	int action_type;
	int childfunction;
	int ReceivedKey;
	int ReceivedValue;
	String ReceivedDATA;
	String LastSent;
	int[] LastState = { 0, 0 };

	ImageView bat;
	TextView bat_p;

	EditText txd_txt, uuid_1001_ed, rx_data_id_1;

	EditText ibeacon_uuid;
	EditText mayjor_txt, minor_txt;

	EditText dev_Name;
	Button name_button;

	EditText password_ed;// 密码值
	Button password_enable_bt;// 密码开关
	Button password_wrt;// 密码写入Button

	Button adv_time1, adv_time2, adv_time3, adv_time4;

	boolean pass_en = false;

	Button clear_button;

	private Button IO_H_button, IO_L_button;// out io
	Timer timer = new Timer();

	TextView textView5;
	CheckBox checkBox5, checkBox1;

	TextView tx;

	boolean send_hex = true;// HEX格式发送数据 透传
	boolean rx_hex = true;// HEX格式接收数据 透传

	public void delay(int ms) {
		try {
			Thread.currentThread();
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_activity);
		setTitle("透传");

		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

		// setTitle( mDeviceName );

		// Sets up UI references.
		((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
		// mGattServicesList = (ExpandableListView)
		// findViewById(R.id.gatt_services_list);
		// mGattServicesList.setOnChildClickListener(servicesListClickListner);
		mConnectionState = (TextView) findViewById(R.id.connection_state);
		mDataField = (TextView) findViewById(R.id.data_value);

		send_button = (Button) findViewById(R.id.tx_button);// send data 1002
		send_button.setOnClickListener(listener);// 设置监听

		clear_button = (Button) findViewById(R.id.clear_button);// send data
																// 1002
		clear_button.setOnClickListener(listener);// 设置监听

		blue_bar = (ImageButton) findViewById(R.id.BlueBar);
		exit_com = (ImageButton) findViewById(R.id.ExitBar);
		exit_com.setOnClickListener(imagelistener);
		exit_com.setOnTouchListener(touchlistener);

		cube = (ImageButton) findViewById(R.id.GrayCube);
		humanoid = (ImageButton) findViewById(R.id.GrayHumanoid);
		sit = (ImageButton) findViewById(R.id.GraySit);
		tetrapod = (ImageButton) findViewById(R.id.GrayTetrapod);

		up = (ImageButton) findViewById(R.id.GrayUp);
		down = (ImageButton) findViewById(R.id.GrayDown);
		left = (ImageButton) findViewById(R.id.GrayLeft);
		right = (ImageButton) findViewById(R.id.GrayRight);

		takeHi = (ImageButton) findViewById(R.id.GrayTakeHi);
		takeLow = (ImageButton) findViewById(R.id.GrayTakeLow);
		hit = (ImageButton) findViewById(R.id.GrayHit);
		release = (ImageButton) findViewById(R.id.GrayRelease);
		climb = (ImageButton) findViewById(R.id.GrayClimb);

		takeHi.setOnClickListener(imagelistener);
		takeLow.setOnClickListener(imagelistener);
		hit.setOnClickListener(imagelistener);
		release.setOnClickListener(imagelistener);
		climb.setOnClickListener(imagelistener);

		takeHi.setOnTouchListener(touchlistener);
		takeLow.setOnTouchListener(touchlistener);
		hit.setOnTouchListener(touchlistener);
		release.setOnTouchListener(touchlistener);
		climb.setOnTouchListener(touchlistener);

		bat = (ImageView) findViewById(R.id.bat);
		bat_p = (TextView) findViewById(R.id.bat_precent);

		txd_txt = (EditText) findViewById(R.id.tx_text);// 1002 data
		txd_txt.setText("0102030405060708090A");
		txd_txt.clearFocus();

		rx_data_id_1 = (EditText) findViewById(R.id.rx_data_id_1);// 1002 data
		rx_data_id_1.setText("");

		textView5 = (TextView) findViewById(R.id.textView5);
		tx = (TextView) findViewById(R.id.tx);

		sbValues = new StringBuffer();

		mHandler = new Handler();

		checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
		checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
		checkBox5
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							rx_hex = true;
							// rx_data_id_1.setText(
							// mBluetoothLeService.String_to_HexString(sbValues.toString())
							// );
							// Toast.makeText(jdy_Activity.this, "接收十六进制格式",
							// Toast.LENGTH_SHORT).show();
						} else {
							rx_hex = false;
							// rx_data_id_1.setText( sbValues );
							// Toast.makeText(jdy_Activity.this, "接收字符串格式",
							// Toast.LENGTH_SHORT).show();
						}
					}
				});
		checkBox1
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							send_hex = false;
						} else {
							send_hex = true;
						}
					}
				});

		// timer.schedule(task, 3000, 3000); // 1s后执行task,经过1s再次执行
		Message message = new Message();
		message.what = 1;
		handler.sendMessage(message);

		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null) {

			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
		}

		boolean sg;
		getActionBar().setTitle(mDeviceName + "  方方");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		sg = bindService(gattServiceIntent, mServiceConnection,
				BIND_AUTO_CREATE);
		// getActionBar().setTitle( "="+BluetoothLeService );
		// mDataField.setText("="+sg );
		updateConnectionState(R.string.connecting);

		get_pass();

	}

	public void enable_pass() {
		mBluetoothLeService.Delay_ms(100);
		mBluetoothLeService.set_APP_PASSWORD(password_value);
	}

	String password_value = "123456";

	public void get_pass() {
		password_value = getSharedPreference("DEV_PASSWORD_LEY_1000");
		if (password_value != null || password_value != "") {
			if (password_value.length() == 6) {

			} else
				password_value = "123456";
		} else
			password_value = "123456";

	}

	// ---------------------------------------------------------------------------------应用于存储选择TAB的列表index
	public String getSharedPreference(String key) {
		// 同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
		SharedPreferences sharedPreferences = getSharedPreferences("test",
				Activity.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值
		String name = sharedPreferences.getString(key, "");
		return name;
	}

	public void setSharedPreference(String key, String values) {
		// 实例化SharedPreferences对象（第一步）
		SharedPreferences mySharedPreferences = getSharedPreferences("test",
				Activity.MODE_PRIVATE);
		// 实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putString(key, values);
		// 提交当前数据
		editor.commit();
		// 使用toast信息提示框提示成功写入数据
		// Toast.makeText(this, values ,
		// Toast.LENGTH_LONG).show();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				// tvShow.setText(Integer.toString(i++));
				// scanLeDevice(true);
				Boolean ble = mBluetoothLeService != null;
				Log.i("test", msg.what + " " + ble + " " + mConnected);
				if (mBluetoothLeService != null) {
					if (mConnected == false) {
						updateConnectionState(R.string.connecting);
						final boolean result = mBluetoothLeService
								.connect(mDeviceAddress);
						Log.d(TAG, "Connect request result=" + result);
					}
				}
			}
			if (msg.what == 2) {
				try {
					Thread.currentThread();
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mBluetoothLeService.enable_JDY_ble(0);
				try {
					Thread.currentThread();
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mBluetoothLeService.enable_JDY_ble(0);
				try {
					Thread.currentThread();
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mBluetoothLeService.enable_JDY_ble(1);
				try {
					Thread.currentThread();
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				byte[] WriteBytes = new byte[2];
				WriteBytes[0] = (byte) 0xE7;
				WriteBytes[1] = (byte) 0xf6;
				mBluetoothLeService.function_data(WriteBytes);// 发送读取所有IO状态
			}
			super.handleMessage(msg);

			switch (msg.what) {
			case 21:
				cube.setEnabled(true);
				humanoid.setEnabled(false);
				sit.setEnabled(false);
				tetrapod.setEnabled(false);
				cube.setImageResource(R.drawable.blue_cube);
				humanoid.setImageResource(R.drawable.blue_humanoid_pressed);
				sit.setImageResource(R.drawable.gray_sit);
				tetrapod.setImageResource(R.drawable.gray_tetrapod);

				EnableAllButtons(false, true);
				break;

			case 31:
				cube.setEnabled(true);
				humanoid.setEnabled(false);
				sit.setEnabled(false);
				tetrapod.setEnabled(true);
				cube.setImageResource(R.drawable.blue_cube);
				humanoid.setImageResource(R.drawable.gray_humanoid);
				sit.setImageResource(R.drawable.blue_sit_pressed);
				tetrapod.setImageResource(R.drawable.blue_tetrapod);

				EnableAllButtons(false, true);
				ChildFunction(3, true, true);
				break;

			case 12:
				cube.setEnabled(false);
				humanoid.setEnabled(true);
				sit.setEnabled(true);
				tetrapod.setEnabled(false);
				cube.setImageResource(R.drawable.blue_cube_pressed);
				humanoid.setImageResource(R.drawable.blue_humanoid);
				sit.setImageResource(R.drawable.blue_sit);
				tetrapod.setImageResource(R.drawable.gray_tetrapod);

				EnableAllButtons(false, true);
				break;

			case 13:
				cube.setEnabled(false);
				humanoid.setEnabled(true);
				sit.setEnabled(true);
				tetrapod.setEnabled(false);
				cube.setImageResource(R.drawable.blue_cube_pressed);
				humanoid.setImageResource(R.drawable.blue_humanoid);
				sit.setImageResource(R.drawable.blue_sit);
				tetrapod.setImageResource(R.drawable.gray_tetrapod);

				EnableAllButtons(false, true);
				break;

			case 43:
				cube.setEnabled(false);
				humanoid.setEnabled(false);
				sit.setEnabled(true);
				tetrapod.setEnabled(false);
				cube.setImageResource(R.drawable.gray_cube);
				humanoid.setImageResource(R.drawable.gray_humanoid);
				sit.setImageResource(R.drawable.blue_sit);
				tetrapod.setImageResource(R.drawable.blue_tetrapod_pressed);

				EnableAllButtons(false, true);
				ChildFunction(3, true, true);
				break;

			case 34:
				cube.setEnabled(true);
				humanoid.setEnabled(false);
				sit.setEnabled(false);
				tetrapod.setEnabled(true);
				cube.setImageResource(R.drawable.blue_cube);
				humanoid.setImageResource(R.drawable.gray_humanoid);
				sit.setImageResource(R.drawable.blue_sit_pressed);
				tetrapod.setImageResource(R.drawable.blue_tetrapod);

				EnableAllButtons(false, true);
				ChildFunction(3, true, true);
				break;

			case 100:
				up.setEnabled(true);
				down.setEnabled(true);
				left.setEnabled(true);
				right.setEnabled(true);
				up.setImageResource(R.drawable.blue_up);
				down.setImageResource(R.drawable.blue_down);
				left.setImageResource(R.drawable.blue_left);
				right.setImageResource(R.drawable.blue_right);
				break;

			case 331:
				cube.setEnabled(true);
				humanoid.setEnabled(false);
				sit.setEnabled(false);
				tetrapod.setEnabled(true);
				cube.setImageResource(R.drawable.blue_cube);
				humanoid.setImageResource(R.drawable.gray_humanoid);
				sit.setImageResource(R.drawable.blue_sit_pressed);
				tetrapod.setImageResource(R.drawable.blue_tetrapod);

				EnableAllButtons(false, true);
				ChildFunction(3, true, true);
				break;

			case 332:
				cube.setEnabled(true);
				humanoid.setEnabled(false);
				sit.setEnabled(false);
				tetrapod.setEnabled(true);
				cube.setImageResource(R.drawable.blue_cube);
				humanoid.setImageResource(R.drawable.gray_humanoid);
				sit.setImageResource(R.drawable.blue_sit_pressed);
				tetrapod.setImageResource(R.drawable.blue_tetrapod);

				EnableAllButtons(false, true);
				ChildFunction(3, true, true);
				break;

			case 333:
				cube.setEnabled(true);
				humanoid.setEnabled(false);
				sit.setEnabled(false);
				tetrapod.setEnabled(true);
				cube.setImageResource(R.drawable.blue_cube);
				humanoid.setImageResource(R.drawable.gray_humanoid);
				sit.setImageResource(R.drawable.blue_sit_pressed);
				tetrapod.setImageResource(R.drawable.blue_tetrapod);

				EnableAllButtons(false, true);
				ChildFunction(3, true, true);
				break;

			case 334:
				cube.setEnabled(true);
				humanoid.setEnabled(false);
				sit.setEnabled(false);
				tetrapod.setEnabled(true);
				cube.setImageResource(R.drawable.blue_cube);
				humanoid.setImageResource(R.drawable.gray_humanoid);
				sit.setImageResource(R.drawable.blue_sit_pressed);
				tetrapod.setImageResource(R.drawable.blue_tetrapod);

				EnableAllButtons(false, true);
				ChildFunction(3, true, true);
				break;

			case 335:
				cube.setEnabled(true);
				humanoid.setEnabled(false);
				sit.setEnabled(false);
				tetrapod.setEnabled(true);
				cube.setImageResource(R.drawable.blue_cube);
				humanoid.setImageResource(R.drawable.gray_humanoid);
				sit.setImageResource(R.drawable.blue_sit_pressed);
				tetrapod.setImageResource(R.drawable.blue_tetrapod);

				EnableAllButtons(false, true);
				ChildFunction(3, true, true);
				break;

			case 1000:
				int bat_precent = Integer.valueOf(
						ReceivedDATA.substring(10, 12), 16);
				if (bat_precent >= 75) {
					bat.setImageResource(R.drawable.bat100);
				} else if (bat_precent < 75 && bat_precent >= 50) {
					bat.setImageResource(R.drawable.bat75);
				} else if (bat_precent < 50 && bat_precent >= 25) {
					bat.setImageResource(R.drawable.bat50);
				} else if (bat_precent < 25 && bat_precent >= 0) {
					bat.setImageResource(R.drawable.bat25);
				}
				bat_p.setText("电量：" + bat_precent + "%");
				break;

			case 1001:
				mBluetoothLeService.txxx("68970102000316", false);
				break;

			default:
				break;
			}
		};
	};
	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			// 需要做的事:发送消息
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

	ToggleButton.OnClickListener OnClickListener_listener = new ToggleButton.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mConnected) {
				byte bit = (byte) 0x00;
				if (v.getId() == R.id.toggleButton1) {
					bit = (byte) 0xf1;
				} else if (v.getId() == R.id.toggleButton2) {
					bit = (byte) 0xf2;
				} else if (v.getId() == R.id.toggleButton3) {
					bit = (byte) 0xf3;
				} else if (v.getId() == R.id.toggleButton4) {
					bit = (byte) 0xf4;

					// byte[] WriteBytes = new byte[2];
					// WriteBytes[0] = (byte) 0xE7;
					// WriteBytes[1] = (byte) 0xf6;
					// //WriteBytes[2] = (byte)0x01;
					// mBluetoothLeService.function_data( WriteBytes );

				}
				if (bit != (byte) 0x00) {
					boolean on = ((ToggleButton) v).isChecked();
					if (on) {
						// Enable here
						// Toast.makeText(jdy_Activity.this, "Enable here",
						// Toast.LENGTH_SHORT).show();
						// E7F101
						byte[] WriteBytes = new byte[3];
						WriteBytes[0] = (byte) 0xE7;
						WriteBytes[1] = bit;
						WriteBytes[2] = (byte) 0x01;
						mBluetoothLeService.function_data(WriteBytes);
					} else {
						// Disable here
						// Toast.makeText(jdy_Activity.this, "Disable here",
						// Toast.LENGTH_SHORT).show();
						byte[] WriteBytes = new byte[3];
						WriteBytes[0] = (byte) 0xE7;
						WriteBytes[1] = bit;
						WriteBytes[2] = (byte) 0x00;
						mBluetoothLeService.function_data(WriteBytes);
					}
				}
			}
		}

	};

	Button.OnClickListener listener = new Button.OnClickListener() {// 创建监听对象
		public void onClick(View v) {
			// String strTmp="点击Button02";
			// Ev1.setText(strTmp);
			switch (v.getId()) {
			case R.id.tx_button:// uuid1002 数传通道发送数据
				if (connect_status_bit) {
					if (mConnected) {
						String tx_string = txd_txt.getText().toString().trim();
						tx_count += mBluetoothLeService.txxx(tx_string,
								send_hex);// 发送字符串数据
						tx.setText("发送数据：" + tx_count);
						// mBluetoothLeService.txxx( tx_string,false );//发送HEX数据
					}
				} else {
					// Toast.makeText(this, "Deleted Successfully!",
					// Toast.LENGTH_LONG).show();
					Toast toast = Toast.makeText(UploadActivity.this,
							"设备没有连接！", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;
			case R.id.clear_button: {
				sbValues.delete(0, sbValues.length());
				len_g = 0;
				da = "";
				rx_data_id_1.setText(da);
				mDataField.setText("" + len_g);
				tx_count = 0;
				tx.setText("发送数据：" + tx_count);
			}
				break;
			default:
				break;
			}
		}

	};

	Button.OnTouchListener touchlistener = new Button.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (v.getId()) {
			case R.id.ExitBar:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					exit_com.setImageResource(R.drawable.exit_com_pressed);
					break;
				case MotionEvent.ACTION_UP:
					exit_com.setImageResource(R.drawable.exit_com);
					break;
				case MotionEvent.ACTION_CANCEL:
					exit_com.setImageResource(R.drawable.exit_com);
					break;
				default:
					break;
				}
				break;

			case R.id.GrayCube:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					cube.setImageResource(R.drawable.blue_cube_pressed);
					break;
				case MotionEvent.ACTION_UP:
					cube.setImageResource(R.drawable.blue_cube);
					break;
				case MotionEvent.ACTION_CANCEL:
					cube.setImageResource(R.drawable.blue_cube);
					break;
				default:
					break;
				}
				break;

			case R.id.GrayHumanoid:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					humanoid.setImageResource(R.drawable.blue_humanoid_pressed);
					break;
				case MotionEvent.ACTION_UP:
					humanoid.setImageResource(R.drawable.blue_humanoid);
					break;
				case MotionEvent.ACTION_CANCEL:
					humanoid.setImageResource(R.drawable.blue_humanoid);
					break;
				default:
					break;
				}
				break;

			case R.id.GraySit:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					sit.setImageResource(R.drawable.blue_sit_pressed);
					break;
				case MotionEvent.ACTION_UP:
					sit.setImageResource(R.drawable.blue_sit);
					break;
				case MotionEvent.ACTION_CANCEL:
					sit.setImageResource(R.drawable.blue_sit);
					break;
				default:
					break;
				}
				break;

			case R.id.GrayTetrapod:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tetrapod.setImageResource(R.drawable.blue_tetrapod_pressed);
					break;
				case MotionEvent.ACTION_UP:
					tetrapod.setImageResource(R.drawable.blue_tetrapod);
					break;
				case MotionEvent.ACTION_CANCEL:
					tetrapod.setImageResource(R.drawable.blue_tetrapod);
					break;
				default:
					break;
				}
				break;

			case R.id.GrayUp:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					up.setImageResource(R.drawable.blue_up_pressed);
					mBluetoothLeService.txxx(MoveControl(1, 50), false);
					break;
				case MotionEvent.ACTION_UP:
					up.setImageResource(R.drawable.blue_up);
					mBluetoothLeService.txxx(MoveControl(0, 0), false);
					DisableAllButtons(false, true);
					action_type = 100;
					Thread th1 = new UpdataUI();
					th1.start();
					break;
				case MotionEvent.ACTION_CANCEL:
					up.setImageResource(R.drawable.blue_up);
					mBluetoothLeService.txxx(MoveControl(0, 0), false);
					DisableAllButtons(false, true);
					action_type = 100;
					Thread th2 = new UpdataUI();
					th2.start();

				default:
					break;
				}
				break;

			case R.id.GrayDown:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					down.setImageResource(R.drawable.blue_down_pressed);
					mBluetoothLeService.txxx(MoveControl(2, 50), false);
					break;
				case MotionEvent.ACTION_UP:
					down.setImageResource(R.drawable.blue_down);
					mBluetoothLeService.txxx(MoveControl(0, 0), false);
					DisableAllButtons(false, true);
					action_type = 100;
					Thread th1 = new UpdataUI();
					th1.start();
					break;
				case MotionEvent.ACTION_CANCEL:
					down.setImageResource(R.drawable.blue_down);
					mBluetoothLeService.txxx(MoveControl(0, 0), false);
					DisableAllButtons(false, true);
					action_type = 100;
					Thread th2 = new UpdataUI();
					th2.start();
				default:
					break;
				}
				break;

			case R.id.GrayLeft:

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					left.setImageResource(R.drawable.blue_left_pressed);
					mBluetoothLeService.txxx(MoveControl(3, 50), false);
					break;
				case MotionEvent.ACTION_UP:
					left.setImageResource(R.drawable.blue_left);
					mBluetoothLeService.txxx(MoveControl(0, 0), false);
					DisableAllButtons(false, true);
					action_type = 100;
					Thread th1 = new UpdataUI();
					th1.start();
					break;
				case MotionEvent.ACTION_CANCEL:
					left.setImageResource(R.drawable.blue_left);
					mBluetoothLeService.txxx(MoveControl(0, 0), false);
					DisableAllButtons(false, true);
					action_type = 100;
					Thread th2 = new UpdataUI();
					th2.start();

					break;
				default:
					break;
				}

				break;

			case R.id.GrayRight:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					right.setImageResource(R.drawable.blue_right_pressed);
					mBluetoothLeService.txxx(MoveControl(4, 50), false);
					break;
				case MotionEvent.ACTION_UP:
					right.setImageResource(R.drawable.blue_right);
					mBluetoothLeService.txxx(MoveControl(0, 0), false);
					DisableAllButtons(false, true);
					action_type = 100;
					Thread th1 = new UpdataUI();
					th1.start();
					break;
				case MotionEvent.ACTION_CANCEL:
					right.setImageResource(R.drawable.blue_right);
					mBluetoothLeService.txxx(MoveControl(0, 0), false);
					DisableAllButtons(false, true);
					action_type = 100;
					Thread th2 = new UpdataUI();
					th2.start();
				default:
					break;
				}
				break;

			case R.id.GrayTakeHi:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					takeHi.setImageResource(R.drawable.blue_takehi_pressed);
					break;
				case MotionEvent.ACTION_UP:
					takeHi.setImageResource(R.drawable.blue_takehi);
					break;
				case MotionEvent.ACTION_CANCEL:
					takeHi.setImageResource(R.drawable.blue_takehi);
					break;
				default:
					break;
				}
				break;

			case R.id.GrayTakeLow:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					takeLow.setImageResource(R.drawable.blue_takelow_pressed);
					break;
				case MotionEvent.ACTION_UP:
					takeLow.setImageResource(R.drawable.blue_takelow);
					break;
				case MotionEvent.ACTION_CANCEL:
					takeLow.setImageResource(R.drawable.blue_takelow);
					break;
				default:
					break;
				}
				break;

			case R.id.GrayHit:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					hit.setImageResource(R.drawable.blue_hit_pressed);
					break;
				case MotionEvent.ACTION_UP:
					hit.setImageResource(R.drawable.blue_hit);
					break;
				case MotionEvent.ACTION_CANCEL:
					hit.setImageResource(R.drawable.blue_hit);
					break;
				default:
					break;
				}
				break;

			case R.id.GrayRelease:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					release.setImageResource(R.drawable.blue_release_pressed);
					break;
				case MotionEvent.ACTION_UP:
					release.setImageResource(R.drawable.blue_release);
					break;
				case MotionEvent.ACTION_CANCEL:
					release.setImageResource(R.drawable.blue_release);
					break;
				default:
					break;
				}
				break;

			case R.id.GrayClimb:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					climb.setImageResource(R.drawable.blue_climb_pressed);
					break;
				case MotionEvent.ACTION_UP:
					climb.setImageResource(R.drawable.blue_climb);
					break;
				case MotionEvent.ACTION_CANCEL:
					climb.setImageResource(R.drawable.blue_climb);
					break;
				default:
					break;
				}
				break;

			default:
				break;
			}

			return false;
		}
	};

	ImageButton.OnClickListener imagelistener = new ImageButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ExitBar:
				finish();
				break;

			case R.id.GrayCube:
				if (connect_status_bit) {
					if (mConnected) {
						mBluetoothLeService.txxx(sendAction(1, robot_type),
								false);
						DisableAllButtons(true, true);
						TypeChange(1, robot_type);
						LastState[0] = 1;
						LastState[1] = robot_type;

						ChildFunction(3, false, false);
					}
				} else {
					Toast toast = Toast.makeText(UploadActivity.this,
							"设备没有连接！", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;

			case R.id.GrayHumanoid:
				if (connect_status_bit) {
					if (mConnected) {
						mBluetoothLeService.txxx(sendAction(2, robot_type),
								false);
						DisableAllButtons(true, true);
						TypeChange(2, robot_type);
						LastState[0] = 2;
						LastState[1] = robot_type;
					}
				} else {
					Toast toast = Toast.makeText(UploadActivity.this,
							"设备没有连接！", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;

			case R.id.GraySit:
				if (connect_status_bit) {
					if (mConnected) {
						mBluetoothLeService.txxx(sendAction(3, robot_type),
								false);
						DisableAllButtons(true, true);
						TypeChange(3, robot_type);
						LastState[0] = 3;
						LastState[1] = robot_type;

					}
				} else {
					Toast toast = Toast.makeText(UploadActivity.this,
							"设备没有连接！", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;

			case R.id.GrayTetrapod:
				if (connect_status_bit) {
					if (mConnected) {
						mBluetoothLeService.txxx(sendAction(4, robot_type),
								false);
						DisableAllButtons(true, true);
						TypeChange(4, robot_type);
						LastState[0] = 4;
						LastState[1] = robot_type;

						ChildFunction(3, false, false);
					}
				} else {
					Toast toast = Toast.makeText(UploadActivity.this,
							"设备没有连接！", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;

			case R.id.GrayTakeHi:
				if (connect_status_bit) {
					if (mConnected) {
						mBluetoothLeService.txxx(sendAction(31, robot_type),
								false);
						DisableAllButtons(true, true);
						childfunction = 331;
						ChildFunction(3, true, false);
						new UpdataUI_function().start();
					}
				} else {
					Toast toast = Toast.makeText(UploadActivity.this,
							"设备没有连接！", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;

			case R.id.GrayTakeLow:
				if (connect_status_bit) {
					if (mConnected) {
						mBluetoothLeService.txxx(sendAction(32, robot_type),
								false);
						DisableAllButtons(true, true);
						childfunction = 332;
						ChildFunction(3, true, false);
						new UpdataUI_function().start();
					}
				} else {
					Toast toast = Toast.makeText(UploadActivity.this,
							"设备没有连接！", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;

			case R.id.GrayHit:
				if (connect_status_bit) {
					if (mConnected) {
						mBluetoothLeService.txxx(sendAction(33, robot_type),
								false);
						DisableAllButtons(true, true);
						childfunction = 333;
						ChildFunction(3, true, false);
						new UpdataUI_function().start();
					}
				} else {
					Toast toast = Toast.makeText(UploadActivity.this,
							"设备没有连接！", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;

			case R.id.GrayRelease:
				if (connect_status_bit) {
					if (mConnected) {
						mBluetoothLeService.txxx(sendAction(34, robot_type),
								false);
						DisableAllButtons(true, true);
						childfunction = 334;
						ChildFunction(3, true, false);
						new UpdataUI_function().start();
					}
				} else {
					Toast toast = Toast.makeText(UploadActivity.this,
							"设备没有连接！", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;

			case R.id.GrayClimb:
				if (connect_status_bit) {
					if (mConnected) {
						mBluetoothLeService.txxx(sendAction(35, robot_type),
								false);
						DisableAllButtons(true, true);
						childfunction = 335;
						ChildFunction(3, true, false);
						new UpdataUI_function().start();
					}
				} else {
					Toast toast = Toast.makeText(UploadActivity.this,
							"设备没有连接！", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();
		// unregisterReceiver(mGattUpdateReceiver);
		// mBluetoothLeService.disconnect();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
		mBluetoothLeService.disconnect();
		mBluetoothLeService = null;
		timer.cancel();
		timer = null;
		UploadActivity.this.unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.gatt_services, menu);
		// if (mConnected) {
		// menu.findItem(R.id.menu_connect).setVisible(false);
		// menu.findItem(R.id.menu_disconnect).setVisible(true);
		// } else {
		// menu.findItem(R.id.menu_connect).setVisible(true);
		// menu.findItem(R.id.menu_disconnect).setVisible(false);
		// }
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// case R.id.menu_connect:
		// mBluetoothLeService.connect(mDeviceAddress);
		// return true;
		// case R.id.menu_disconnect:
		// mBluetoothLeService.disconnect();
		// return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateConnectionState(final int resourceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mConnectionState.setText(resourceId);
				switch (resourceId) {
				case R.string.connected:
					blue_bar.setImageResource(R.drawable.blue_bar_connected);
					cube.setImageResource(R.drawable.blue_cube_pressed);
					humanoid.setImageResource(R.drawable.blue_humanoid);
					sit.setImageResource(R.drawable.blue_sit);
					// tetrapod.setImageResource(R.drawable.blue_tetrapod);
					cube.setOnClickListener(imagelistener);
					cube.setOnTouchListener(touchlistener);
					humanoid.setOnClickListener(imagelistener);
					humanoid.setOnTouchListener(touchlistener);
					sit.setOnClickListener(imagelistener);
					sit.setOnTouchListener(touchlistener);
					tetrapod.setOnClickListener(imagelistener);
					tetrapod.setOnTouchListener(touchlistener);
					tetrapod.setEnabled(false);

					up.setOnTouchListener(touchlistener);
					down.setOnTouchListener(touchlistener);
					left.setOnTouchListener(touchlistener);
					right.setOnTouchListener(touchlistener);
					up.setImageResource(R.drawable.blue_up);
					down.setImageResource(R.drawable.blue_down);
					left.setImageResource(R.drawable.blue_left);
					right.setImageResource(R.drawable.blue_right);

					mBluetoothLeService.txxx("68970102000316", false);

					new Thread() {
						@Override
						public void run() {
							super.run();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							Message message = new Message();
							message.what = 1001;
							handler.sendMessage(message);
						}
					}.start();

					break;
				case R.string.connecting:
					blue_bar.setImageResource(R.drawable.blue_bar_connecting);
					break;

				default:
					break;
				}
			}
		});
	}

	String da = "";
	int len_g = 0;

	private void displayData(byte[] data1) // 接收FFE1串口透传数据通道数据
	{
		// String head1,data_0;
		/*
		 * head1=data1.substring(0,2); data_0=data1.substring(2);
		 */
		// da = da+data1+"\n";
		if (data1 != null && data1.length > 0) {
			// //sbValues.insert(0, data1);
			// //sbValues.indexOf( data1 );

			// //mDataField.setText( data1 ); getStringByBytes
			// len_g += data1.length;
			// //da = data1+da;

			// rx_data_id_1.setText( mBluetoothLeService.bytesToHexString(data1)
			// );//
			if (rx_hex) {
				final StringBuilder stringBuilder = new StringBuilder(
						sbValues.length());
				byte[] WriteBytes = mBluetoothLeService.hex2byte(stringBuilder
						.toString().getBytes());

				for (byte byteChar : data1)
					stringBuilder.append(String.format(" %02X", byteChar));

				String da = stringBuilder.toString();
				// sbValues.append( stringBuilder.toString() ) ;
				// rx_data_id_1.setText(
				// mBluetoothLeService.String_to_HexString(sbValues.toString())
				// );

				// String res = new String( da.getBytes() );
				sbValues.append(da);
				rx_data_id_1.setText(sbValues.toString());
				ReceivedDATA = sbValues.toString();
				Thread th = new HandleReceivedMessage();
				th.start();
			} else {
				String res = new String(data1);
				sbValues.append(res);
				rx_data_id_1.setText(sbValues.toString());
			}

			len_g += data1.length;

			// // data1 );
			if (sbValues.length() <= rx_data_id_1.getText().length())
				rx_data_id_1.setSelection(sbValues.length());

			if (sbValues.length() >= 5000)
				sbValues.delete(0, sbValues.length());
			mDataField.setText("" + len_g);

			// rx_data_id_1.setGravity(Gravity.BOTTOM);
			// rx_data_id_1.setSelection(rx_data_id_1.getText().length());
		}

	}

	// Demonstrates how to iterate through the supported GATT
	// Services/Characteristics.
	// In this sample, we populate the data structure that is bound to the
	// ExpandableListView
	// on the UI.
	private void displayGattServices(List<BluetoothGattService> gattServices) {

		if (gattServices == null)
			return;

		if (gattServices.size() > 0
				&& mBluetoothLeService.get_connected_status(gattServices) == 2)// 表示为JDY-06、JDY-08系列蓝牙模块
		{
			connect_count = 0;
			if (connect_status_bit) {
				mConnected = true;
				mBluetoothLeService.Delay_ms(100);
				mBluetoothLeService.enable_JDY_ble(0);
				mBluetoothLeService.Delay_ms(100);
				mBluetoothLeService.enable_JDY_ble(1);
				mBluetoothLeService.Delay_ms(100);

				byte[] WriteBytes = new byte[2];
				WriteBytes[0] = (byte) 0xE7;
				WriteBytes[1] = (byte) 0xf6;
				mBluetoothLeService.function_data(WriteBytes);// 发送读取所有IO状态

				updateConnectionState(R.string.connected);

				enable_pass();
			} else {
				// Toast.makeText(this, "Deleted Successfully!",
				// Toast.LENGTH_LONG).show();
				Toast toast = Toast.makeText(UploadActivity.this, "设备没有连接！",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		} else if (gattServices.size() > 0
				&& mBluetoothLeService.get_connected_status(gattServices) == 1)// 表示为JDY-09、JDY-10系列蓝牙模块
		{
			connect_count = 0;
			if (connect_status_bit) {
				mConnected = true;

				mBluetoothLeService.Delay_ms(100);
				mBluetoothLeService.enable_JDY_ble(0);

				updateConnectionState(R.string.connected);

				// enable_pass();
			} else {
				// Toast.makeText(this, "Deleted Successfully!",
				// Toast.LENGTH_LONG).show();
				Toast toast = Toast.makeText(UploadActivity.this, "设备没有连接！",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		} else {
			Toast toast = Toast.makeText(UploadActivity.this,
					"提示！此设备不为JDY系列BLE模块", Toast.LENGTH_SHORT);
			toast.show();
		}
		// SimpleExpandableListAdapter gattServiceAdapter = new
		// SimpleExpandableListAdapter(
		// this,
		// gattServiceData,
		// android.R.layout.simple_expandable_list_item_2,
		// new String[] {LIST_NAME, LIST_UUID},
		// new int[] { android.R.id.text1, android.R.id.text2 },
		// gattCharacteristicData,
		// android.R.layout.simple_expandable_list_item_2,
		// new String[] {LIST_NAME, LIST_UUID},
		// new int[] { android.R.id.text1, android.R.id.text2 }
		// );
		//
		// mGattServicesList.setAdapter(gattServiceAdapter);

	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE1);
		return intentFilter;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (mConnected) {
			mBluetoothLeService.set_PWM_ALL_pulse(seekBar.getProgress(),
					seekBar.getProgress(), seekBar.getProgress(),
					seekBar.getProgress());
			textView5.setText("暂空比：" + seekBar.getProgress());
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// mBluetoothLeService.set_PWM_ALL_pulse( seekBar.getProgress(),
		// seekBar.getProgress(), seekBar.getProgress(), seekBar.getProgress()
		// );
		// Toast.makeText(jdy_Activity.this, "pulse"+seekBar.getProgress(),
		// Toast.LENGTH_SHORT).show();
	}

	public static String str2HexStr(String str) {
		char[] chars = str.toCharArray();
		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString((int) chars[i]));
		}
		return hex.toString();
	}

	public String sendAction(int action, int type) {
		int actionID = -1;
		if (action == 2 && type == 1) {
			actionID = 0;
		} else if (action == 3 && type == 1) {
			actionID = 2;
		} else if (action == 1 && type == 2) {
			actionID = 1;
		} else if (action == 1 && type == 3) {
			actionID = 3;
		} else if (action == 4 && type == 3) {
			actionID = 4;
		} else if (action == 3 && type == 4) {
			actionID = 5;
		} else {
			switch (action) {
			case 31:
				actionID = 13;
				break;
			case 32:
				actionID = 12;
				break;
			case 33:
				actionID = 14;
				break;
			case 34:
				actionID = 11;
				break;
			case 35:
				actionID = 15;
				break;

			default:
				break;
			}
		}

		String maction;
		maction = "01" + str2HexStr(String.valueOf(actionID)) + str2HexStr("-");

		int length = 2 + String.valueOf(actionID).length();
		int check = 0;
		if (actionID > 9) {
			check = (length
					+ 21
					+ 1
					+ Integer.parseInt(str2HexStr(String
							.valueOf((actionID - actionID % 10) / 10)), 16)
					+ Integer.parseInt(
							str2HexStr(String.valueOf(actionID % 10)), 16) + Integer
					.parseInt(str2HexStr("-"), 16)) % 256;
		} else {
			check = (length
					+ 21
					+ 1
					+ Integer
							.parseInt(str2HexStr(String.valueOf(actionID)), 16) + Integer
					.parseInt(str2HexStr("-"), 16)) % 256;
		}

		String hex_check = Integer.toHexString(check);

		String result = "6897"
				+ "0"
				+ Integer.toHexString(length)
				+ "15"
				+ maction
				+ hex_check.substring(hex_check.length() - 2,
						hex_check.length()) + "16";
		LastSent = result;
		return result;
	}

	public void TypeChange(int action, int type) {

		if (action < 100) {
			robot_type = action;
			if (action == 2 && type == 1) {
				action_type = 21;
			} else if (action == 3 && type == 1) {
				action_type = 31;
			} else if (action == 1 && type == 2) {
				action_type = 12;
			} else if (action == 1 && type == 3) {
				action_type = 13;
			} else if (action == 4 && type == 3) {
				action_type = 43;
			} else if (action == 3 && type == 4) {
				action_type = 34;
			}
		} else if (action > 100) {
			childfunction = action;
		}
		Thread th = new UpdataUI();
		th.start();
	}

	public String MoveControl(int direction, int speed) {
		/** 1 = w, 2 = s, 3 = a, 4 = d , 0 = stop */
		int temp = speed;
		String speed_hex = Integer.toHexString(temp);
		String speed_hex_ = Integer.toHexString(temp + 128);
		String final_control = null;
		if (speed < 10) {
			speed_hex = "0" + speed_hex;
		}
		int check = 0;
		switch (direction) {
		case 0:
			final_control = speed_hex + speed_hex;
			check = (2 + 0x13 + 0 + 0) % 256;
			break;
		case 1:
			final_control = speed_hex_ + speed_hex;
			check = (2 + 0x13 + speed + 128 + speed) % 256;
			break;
		case 2:
			final_control = speed_hex + speed_hex_;
			check = (2 + 0x13 + speed + speed + 128) % 256;
			break;
		case 3:
			final_control = speed_hex + speed_hex;
			check = (2 + 0x13 + speed + speed) % 256;
			break;
		case 4:
			final_control = speed_hex_ + speed_hex_;
			check = (2 + 0x13 + speed + 128 + speed + 128) % 256;
			break;

		default:
			break;
		}

		String result = "68970213" + final_control + Integer.toHexString(check)
				+ "16";
		return result;
	}

	class UpdataUI extends Thread {
		@Override
		public void run() {
			super.run();

			try {
				switch (action_type) {
				case 21:
					Thread.sleep(5000);
					break;

				case 31:
					Thread.sleep(3500);
					break;

				case 12:
					Thread.sleep(4600);
					break;

				case 13:
					Thread.sleep(3100);
					break;

				case 43:
					Thread.sleep(4000);
					break;

				case 34:
					Thread.sleep(4500);
					break;

				case 100:
					Thread.sleep(300);
					break;

				default:
					break;
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (ReceivedValue != 0x00) {
				mBluetoothLeService.txxx(LastSent, true);
				TypeChange(LastState[0], LastState[1]);
				this.interrupt();
			} else if (action_type <= 100) {
				Message message = new Message();
				message.what = action_type;
				handler.sendMessage(message);
			}
		}
	}

	class UpdataUI_function extends Thread {

		@Override
		public void run() {
			super.run();
			try {
				switch (childfunction) {
				case 331:
					Thread.sleep(4000);
					break;

				case 332:
					Thread.sleep(4000);
					break;

				case 333:
					Thread.sleep(2200);
					break;

				case 334:
					Thread.sleep(3000);
					break;

				case 335:
					Thread.sleep(18000);
					break;

				default:
					break;
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Message message = new Message();
			message.what = childfunction;
			handler.sendMessage(message);

		}
	}

	public void DisableAllButtons(Boolean type, Boolean direction) {
		if (type) {
			cube.setEnabled(false);
			humanoid.setEnabled(false);
			sit.setEnabled(false);
			tetrapod.setEnabled(false);
			cube.setImageResource(R.drawable.gray_cube);
			humanoid.setImageResource(R.drawable.gray_humanoid);
			sit.setImageResource(R.drawable.gray_sit);
			tetrapod.setImageResource(R.drawable.gray_tetrapod);
		}

		if (direction) {
			up.setEnabled(false);
			down.setEnabled(false);
			left.setEnabled(false);
			right.setEnabled(false);
			up.setImageResource(R.drawable.gray_up);
			down.setImageResource(R.drawable.gray_down);
			left.setImageResource(R.drawable.gray_left);
			right.setImageResource(R.drawable.gray_right);
		}

	}

	public void EnableAllButtons(Boolean type, Boolean direction) {
		if (type) {
			cube.setEnabled(true);
			humanoid.setEnabled(true);
			sit.setEnabled(true);
			tetrapod.setEnabled(true);
			cube.setImageResource(R.drawable.gray_cube);
			humanoid.setImageResource(R.drawable.gray_humanoid);
			sit.setImageResource(R.drawable.gray_sit);
			tetrapod.setImageResource(R.drawable.gray_tetrapod);
		}

		if (direction) {
			up.setEnabled(true);
			down.setEnabled(true);
			left.setEnabled(true);
			right.setEnabled(true);
			up.setImageResource(R.drawable.blue_up);
			down.setImageResource(R.drawable.blue_down);
			left.setImageResource(R.drawable.blue_left);
			right.setImageResource(R.drawable.blue_right);
		}

	}

	public void ChildFunction(int function, Boolean visibility, Boolean enable) {
		switch (function) {
		case 1:
			break;
		case 2:
			break;
		case 3:
			if (visibility) {
				takeHi.setVisibility(View.VISIBLE);
				takeLow.setVisibility(View.VISIBLE);
				hit.setVisibility(View.VISIBLE);
				release.setVisibility(View.VISIBLE);
				climb.setVisibility(View.VISIBLE);

				takeHi.setImageResource(R.drawable.blue_takehi);
				takeLow.setImageResource(R.drawable.blue_takelow);
				hit.setImageResource(R.drawable.blue_hit);
				release.setImageResource(R.drawable.blue_release);
				climb.setImageResource(R.drawable.blue_climb);

			} else {
				takeHi.setVisibility(View.GONE);
				takeLow.setVisibility(View.GONE);
				hit.setVisibility(View.GONE);
				release.setVisibility(View.GONE);
				climb.setVisibility(View.GONE);
			}

			if (enable) {
				takeHi.setEnabled(true);
				takeLow.setEnabled(true);
				hit.setEnabled(true);
				release.setEnabled(true);
				climb.setEnabled(true);

				takeHi.setImageResource(R.drawable.blue_takehi);
				takeLow.setImageResource(R.drawable.blue_takelow);
				hit.setImageResource(R.drawable.blue_hit);
				release.setImageResource(R.drawable.blue_release);
				climb.setImageResource(R.drawable.blue_climb);
			} else {
				takeHi.setEnabled(false);
				takeLow.setEnabled(false);
				hit.setEnabled(false);
				release.setEnabled(false);
				climb.setEnabled(false);

				takeHi.setImageResource(R.drawable.gray_takehi);
				takeLow.setImageResource(R.drawable.gray_takelow);
				hit.setImageResource(R.drawable.gray_hit);
				release.setImageResource(R.drawable.gray_release);
				climb.setImageResource(R.drawable.gray_climb);
			}

			break;
		case 4:
			break;

		default:
			break;
		}
	}

	class HandleReceivedMessage extends Thread {

		@Override
		public void run() {
			super.run();
			Looper.prepare();
			Log.i("test", "Receive = " + ReceivedDATA);

			ReceivedDATA = ReceivedDATA.replaceAll(" ", "");
			ReceivedKey = Integer.valueOf(ReceivedDATA.substring(6, 8), 16);
			if (ReceivedKey == 15) {
				ReceivedValue = Integer.valueOf(ReceivedDATA.substring(8, 10),
						16);
			}

			switch (ReceivedKey) {
			case 0x15:
				switch (ReceivedValue) {
				case 0x00:
					Toast.makeText(UploadActivity.this, "发送成功！",
							Toast.LENGTH_LONG).show();
					break;
				case 0x01:
					Toast.makeText(UploadActivity.this, "发送失败-参数空文件名。",
							Toast.LENGTH_LONG).show();
					break;
				case 0x02:
					Toast.makeText(UploadActivity.this, "发送失败-电池电量低。",
							Toast.LENGTH_LONG).show();
					break;
				case 0x03:
					Toast.makeText(UploadActivity.this, "动作不存在！",
							Toast.LENGTH_LONG).show();
					break;

				default:
					break;
				}
				break;

			case 0x02:
				Message message = new Message();
				message.what = 1000;
				handler.sendMessage(message);
				break;

			default:
				break;
			}
			ReceivedValue = 0;
			Looper.loop();
		}
	}

	class UpdataBat extends Thread {

		@Override
		public void run() {
			super.run();

			while (true) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mBluetoothLeService.txxx("68970102000316", false);
			}
		}

	}
}
