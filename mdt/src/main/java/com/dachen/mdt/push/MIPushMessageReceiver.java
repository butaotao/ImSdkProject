package com.dachen.mdt.push;

import android.content.Context;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.dachen.common.utils.Logger;
import com.dachen.imsdk.ImSdk;
import com.dachen.imsdk.net.ImPolling;
import com.dachen.imsdk.utils.PushUtils;
import com.dachen.mdt.util.ChatActivityUtilsV2;
import com.dachen.mdt.util.SpUtils;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;
import java.util.Map;

/**
 * 1、PushMessageReceiver是个抽象类，该类继承了BroadcastReceiver。
 * 2、需要将自定义的DemoMessageReceiver注册在AndroidManifest.xml文件中
 * <receiver android:exported="true" android:name=
 * "com.xiaomi.mipushdemo.DemoMessageReceiver"> <intent-filter> <action
 * android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" /> </intent-filter>
 * <intent-filter> <action android:name="com.xiaomi.mipush.ERROR" />
 * </intent-filter>
 * <intent-filter> <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 * </intent-filter> </receiver>
 * 3、DemoMessageReceiver的onReceivePassThroughMessage方法用来接收服务器向客户端发送的透传消息
 * 4、DemoMessageReceiver的onNotificationMessageClicked方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发
 * 5、DemoMessageReceiver的onNotificationMessageArrived方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数
 * 6、DemoMessageReceiver的onCommandResult方法用来接收客户端向服务器发送命令后的响应结果
 * 7、DemoMessageReceiver的onReceiveRegisterResult方法用来接收客户端向服务器发送注册命令后的响应结果
 * 8、以上这些方法运行在非UI线程中
 *
 * @author mayixiang
 */
public class MIPushMessageReceiver extends PushMessageReceiver {

    private static final String TAG = MIPushMessageReceiver.class.getSimpleName();

    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;

    /*
     * 用来接收服务器发送的透传消息
     */
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
//        final PowerManager.WakeLock wakeLock = getWakeLock(context);
//        wakeLock.acquire();
//        Log.v(TAG, "onReceivePassThroughMessage is called. " + message.toString());
//        if (!TextUtils.isEmpty(message.getTopic())) {
//            mTopic = message.getTopic();
//        } else if (!TextUtils.isEmpty(message.getAlias())) {
//            mAlias = message.getAlias();
//        }
//        wakeLock.release();
    }

    /*
     * 用来接收服务器发来的通知栏消息（用户点击通知栏时触发）
     */
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.v(TAG, "onNotificationMessageClicked is called. " + message.toString());

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

        if(TextUtils.isEmpty(ImSdk.getInstance().accessToken))
            return;
        Map<String, String> extra = message.getExtra();
        Logger.d(TAG, "extra=" + extra);
        if (extra != null) {
            String groudId = extra.get("groupId");
            String rtype = extra.get("rtype");
            Log.w(TAG, "onNotificationMessageClicked is OPEN UI.  groudId:" + groudId);

            ChatActivityUtilsV2.openUI(context, groudId, rtype);

        }

        // Message msg = Message.obtain();
        // if (message.isNotified()) {
        // // msg.obj = log;
        // }
        // MIPushApplication.getHandler().sendMessage(msg);

    }

    /*
     * 用来接收服务器发来的通知栏消息（消息到达客户端时触发，并且可以接收应用在前台时不弹出通知的通知消息）
     */
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Logger.d(TAG, "onNotificationMessageArrived is called. " + message.toString());
        // String log = context.getString(R.string.arrive_notification_message,
        // message.getContent());
        // MainActivity.logList.add(0, getSimpleDate() + " " + log);

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }


        // 根据通知类型播放相关提示音，type =0 表示默认的消息提醒，type =1表示日程提醒，现在是web端处理
        // Map<String, String> extra = message.getExtra();
        // if (extra != null)
        // {
        // String type = extra.get("type");
        // if(type!=null)
        // {
        // if(type.equals("1"))
        // {
        // NotificationManager manger = (NotificationManager)
        // context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Notification notification = new Notification();
        // //从sp中取出自定义的声音文件名
        // String sound = UserSp.getInstance(context).getNotification_Sound("");
        // if(!sound.equals("")){
        // Logger.v("MessageArrived", sound);
        // //自定义声音 声音文件放在raw目录下，没有此目录自己创建一个
        // notification.sound=Uri.parse("android.resource://" +
        // context.getPackageName() + "/" +sound);
        // }
        // else {
        // //使用系统默认声音用下面这条
        // notification.defaults=Notification.DEFAULT_SOUND;
        // }
        // manger.notify(1, notification);
        // }
        // }
        // }

		/*
         * Message msg = Message.obtain(); // msg.obj = log;
		 * MIPushApplication.getHandler().sendMessage(msg);
		 */
    }


    /**
     * 判断推送消息是否有效，消息的时间早于当前时间30s则认为指令无效
     *
     * @param ts
     * @return false-无效指令 true-有效指令
     */
    private boolean isValidEvent(long ts) {
        //当前服务器时间
        long currentServerTime = System.currentTimeMillis() + ImPolling.getServerTimeDiff();
        if (currentServerTime - ts < 30 * 1000) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * 用来接收客户端向服务器发送命令消息后返回的响应
     */
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Logger.d(TAG, "onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log = "";
        // if (MiPushClient.COMMAND_REGISTER.equals(command)) {
        // if (message.getResultCode() == ErrorCode.SUCCESS) {
        // mRegId = cmdArg1;
        // log = context.getString(R.string.register_success);
        // } else {
        // log = context.getString(R.string.register_fail);
        // }
        // } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
        // if (message.getResultCode() == ErrorCode.SUCCESS) {
        // mAlias = cmdArg1;
        // log = context.getString(R.string.set_alias_success, mAlias);
        // } else {
        // log = context.getString(R.string.set_alias_fail,
        // message.getReason());
        // }
        // } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
        // if (message.getResultCode() == ErrorCode.SUCCESS) {
        // mAlias = cmdArg1;
        // log = context.getString(R.string.unset_alias_success, mAlias);
        // } else {
        // log = context.getString(R.string.unset_alias_fail,
        // message.getReason());
        // }
        // } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
        // if (message.getResultCode() == ErrorCode.SUCCESS) {
        // mAccount = cmdArg1;
        // log = context.getString(R.string.set_account_success, mAccount);
        // } else {
        // log = context.getString(R.string.set_account_fail,
        // message.getReason());
        // }
        // } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
        // if (message.getResultCode() == ErrorCode.SUCCESS) {
        // mAccount = cmdArg1;
        // log = context.getString(R.string.unset_account_success, mAccount);
        // } else {
        // log = context.getString(R.string.unset_account_fail,
        // message.getReason());
        // }
        // } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
        // if (message.getResultCode() == ErrorCode.SUCCESS) {
        // mTopic = cmdArg1;
        // log = context.getString(R.string.subscribe_topic_success, mTopic);
        // } else {
        // log = context.getString(R.string.subscribe_topic_fail,
        // message.getReason());
        // }
        // } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
        // if (message.getResultCode() == ErrorCode.SUCCESS) {
        // log = context.getString(R.string.unsubscribe_topic_success, mTopic);
        // } else {
        // log = context.getString(R.string.unsubscribe_topic_fail,
        // message.getReason());
        // }
        // } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
        // if (message.getResultCode() == ErrorCode.SUCCESS) {
        // mStartTime = cmdArg1;
        // mEndTime = cmdArg2;
        // log = context.getString(R.string.set_accept_time_success, mStartTime,
        // mEndTime);
        // } else {
        // log = context.getString(R.string.set_accept_time_fail,
        // message.getReason());
        // }
        // } else {
        // log = message.getReason();
        // }
        // MainActivity.logList.add(0, getSimpleDate() + " " + log);

		/*
         * Message msg = Message.obtain(); msg.obj = log;
		 * MIPushApplication.getHandler().sendMessage(msg);
		 */
    }

    /*
     * 用来接受客户端向服务器发送注册命令消息后返回的响应。
     */
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Logger.w(TAG, "onReceiveRegisterResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String log;
        Logger.w(TAG, "cmdArg1==" + cmdArg1);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                SpUtils.edit().putString(SpUtils.KEY_XIAOMI_TOKEN,mRegId).apply();
                if(!TextUtils.isEmpty(ImSdk.getInstance().userId)){
                    PushUtils.registerDevice(0,mRegId,null);
                }
            } else {
            }
        } else {
            log = message.getReason();
        }

		/*
		 * Message msg = Message.obtain(); // msg.obj = log;
		 * MIPushApplication.getHandler().sendMessage(msg);
		 */
    }

    private PowerManager.WakeLock getWakeLock(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MIPushMessageReceiver");
    }

}
