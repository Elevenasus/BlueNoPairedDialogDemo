package com.seeingvoice.www.bluenopaireddialogdemo;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Date:2019/8/2
 * Time:15:59
 * auther:zyy
 */
public class BluetoothReceiver extends BroadcastReceiver {
    String pin = "0000";  //此处为你要连接的蓝牙设备的初始密钥，一般为1234或0000,我们是0000
    public BluetoothReceiver() {
    }

    //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction(); //得到action
        BluetoothDevice btDevice = null;  //创建一个蓝牙device对象
        // 从Intent中获取设备对象
        btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if(BluetoothDevice.ACTION_FOUND.equals(action)){  //发现设备
            Log.e("发现设备:", "["+btDevice.getName()+"]"+":"+btDevice.getAddress());

            if(btDevice.getName().contains("SV-H1")){//SV-H1设备如果有多个，第一个搜到的那个会被尝试。
                if (btDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.e("ywq", "attemp to bond:"+"["+btDevice.getName()+"]");
                    try {
                        //通过工具类ClsUtils,调用createBond方法
                        ClsUtils.createBond(btDevice.getClass(), btDevice);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //如果见声耳机已经配对，可以尝试连接媒体音频和手机音频
            }else
                //没有找到见声耳机
                Log.e("error", "Is faild");
        }else if(action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {//再次得到的action，会等于PAIRING_REQUEST
            if(btDevice.getName().contains("SV-H1")){
                try {
                    //1.确认配对
                    ClsUtils.setPairingConfirmation(btDevice.getClass(), btDevice, true);
                    //2.终止有序广播
                    Log.i("order...", "isOrderedBroadcast:"+isOrderedBroadcast()+",isInitialStickyBroadcast:"+isInitialStickyBroadcast());
                    abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                    //3.调用setPin方法进行配对...
                    boolean ret = ClsUtils.setPin(btDevice.getClass(), btDevice, pin);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else
                Log.e("提示信息", "这个设备不是目标蓝牙设备");
        }
    }
}
