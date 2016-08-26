package com.dachen.mdt.push;

/**
 * 1、为了打开客户端的日志，便于在开发过程中调试，需要自定义一个Application。
 * 并将自定义的application注册在AndroidManifest.xml文件中
 * 2、为了提高push的注册率，您可以在Application的onCreate中初始化push。你也可以根据需要，在其他地方初始化push。
 * 
 * @author wangkuiwei
 * 
 * 
健康伽-医生
应用类型： Android
创建时间： 2015-08-14 12:41:34
包名： com.dachen.dgroupdoctor
AppID： 2882303761517373145
AppKey： 5141737316145
AppSecret： eWcQUJOmRMLCozxyjqmyXQ==

健康伽-患者A
应用类型： Android
创建时间： 2015-08-17 12:38:48
包名： com.dachen.dgrouppatient
AppID： 2882303761517373879
AppKey： 5211737345879
AppSecret： lIuxGWYnYoKDfHaWHurbhg==

健康伽-医助A
应用类型： Android
创建时间： 2015-08-17 15:32:05
包名： com.dachen.dgroupassistant
AppID： 2882303761517373999
AppKey： 5731737345999
AppSecret： xgwgmX/PkDka8D3Yq5EXXA==

 */
public class MIPushApplication {

	// user your appid the key.
	private static String APP_ID = "2882303761517499362";
	// user your appid the key.
	private static String APP_KEY = "5341749977362";

	// 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep
	// com.xiaomi.mipushdemo
	public static final String TAG = "com.xiaomi.mipushdemo";

	public static String getID() {
		return APP_ID;
	}
	
	public static String getKey() {
		return APP_KEY;
	}
}