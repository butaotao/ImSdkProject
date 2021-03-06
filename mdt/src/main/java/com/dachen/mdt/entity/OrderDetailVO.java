package com.dachen.mdt.entity;

import java.io.Serializable;

public class OrderDetailVO implements Serializable {
	public String orderId;
	public MdtOptionResult firstDiag;
	public MdtOptionResult basicDisease;
	public MdtOptionResult concomitant;

	/*患者信息*/
	public PatientInfo patient;
	
	/*mdt编号*/
	public String mdtNum;
	
	/*发起人*/
	public String creator;
	public String userName;//主管医生姓名
	/*会诊分类*/
	public String diseaseTypeId;
	public String topDiseaseId;

	/*mdt小组*/
	public String mdtGroupId;
	public String mdtGroupName;
	
	/*会诊目的*/
	public MdtOptionResult target;
	
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
