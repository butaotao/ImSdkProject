package com.dachen.mdt.util;

import android.text.TextUtils;

import com.dachen.mdt.entity.CheckType;
import com.dachen.mdt.entity.CheckTypeResult;
import com.dachen.mdt.entity.PatientInfo;
import com.dachen.mdt.entity.TempTextParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by Mcp on 2016/8/17.
 */
public class OrderUtils {

    public static String getText(TempTextParam param){
        if(param==null)return null;
        return param.text;
    }

    public static String getText(CheckTypeResult res){
        if(res==null)return null;
        StringBuilder text=new StringBuilder("");
        if(res.typeList!=null){
            for(CheckType type:res.typeList){
                text.append(type.name).append(",");
            }
        }
        if(!TextUtils.isEmpty(res.text)){
            text.append("其他").append(",");
        }
        if(res.pathList!=null&&res.pathList.size()>0){
            text.append(res.pathList.size()+"张图片").append(",");
        }
        return text.toString();
    }

    public static String getSexStr(int sex) {
        String sexString = "";
        if (sex == 1) {
            sexString = "男";
        } else if (sex == 2) {
            sexString = "女";
        } else {
            sexString = "未知";
        }
        return sexString;
    }

    public static String getPatientInfoStr(PatientInfo info){
        String genderStr="";
        if(info.sex==1)genderStr="男  ";
        if(info.sex==2)genderStr="女  ";
        return genderStr+info.age+"岁";
    }

    public static int getSexFromIdCard(String idNum){
        String sexStr=null;
        if(idNum.length()==15){
            sexStr=idNum.substring(14,15);
        }
        if(idNum.length()==18){
            sexStr=idNum.substring(16,17);
        }
        try {
            int n=Integer.parseInt(sexStr);
            if(n%2==0)return 2;
            return 1;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static  boolean validIdCard(String idNum){
        if(idNum==null)return false;
        if(idNum.length()!=15&&idNum.length()!=18)return false;
        Pattern idNumPattern = Pattern.compile("(\\d{14}|\\d{17})[\\d|x|X]");
        if(!idNumPattern.matcher(idNum).matches())
            return false;
        return true;
    }
    public static Date getBirthFromId(String idNum){
        String dateStr=idNum.substring(6,14);
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {
            return 0;
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            }else{
                age--;
            }
        }
        return age;
    }
}