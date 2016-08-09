package com.dachen.mdt.entity;

import java.util.List;

public class DiseaseInfo {
	/*初步诊断*/
	public String firstDiag;
	/*主诉*/
	public String desc;
	/*体征 TODO*/
	public String symptom;
	/*现病史*/
	public String diseaseNow;
    //既往史
    public String diseaseOld;
    //家族史
    public String diseaseFamily;
    //个人史
    public String diseaseSelf;
	
	/*影像学检查(图片url，不超过8张)*/
	public List<String> imaging;
	/*病理学检查(图片url，不超过8张)*/
	public List<String> pathology;
	/*诊疗经过*/
	public String checkProcess;
	/*检验结果*/
	public String result;

	public DiseaseInfo(){
//		this.firstDiag="初步诊断";
//		this.desc="主诉";
//		this.symptom="体征";
//		this.diseaseNow="现病史";
//		this.diseaseOld="既往史";
//		this.diseaseFamily="家族史";
//		this.diseaseSelf="个人史";
//		this.checkProcess="诊疗经过";
//		this.result="检验结果";
//		
//		pathology=new ArrayList<>();
//		pathology.add("病理学检查:图片url1");
//		pathology.add("病理学检查:图片url2");
//		
//		imaging=new ArrayList<>();
//		imaging.add("影像学检查:图片url1");
//		imaging.add("影像学检查:图片url2");
		
	}

	public static class Symptom{
		
	}
	
}
