package com.dachen.mdt.entity;

public class OrderDetailVO {
	public String orderId;
	public String firstDiag;
	public String basicDisease;
	public String concomitant;

	/*患者信息*/
	public PatientInfo patient;
	
	/*mdt编号*/
	public String mdtNum;
	
	/*发起人*/
	public String creator;
	public String userName;//主管医生姓名
	/*会诊分类*/
	public String diseaseTypeId;
	public String diseaseType;
	
	/*mdt小组*/
	public String mdtGroupId;
	public String mdtGroupName;
	
	/*会诊目的*/
	public String target;
	
//	public MDTReportInfo report;
	
	/*病情资料*/
	public DiseaseInfo disease;
	
	/*会诊结束时间*/
	public Long expectEndTime;
	public Long realEndTime;
	public Long startTime;
	
	public String groupId;

//	public MDTReportInfo getReport() {
//		return report;
//	}
//
//	public void setReport(MDTReportInfo report) {
//		this.report = report;
//	}
	
}
