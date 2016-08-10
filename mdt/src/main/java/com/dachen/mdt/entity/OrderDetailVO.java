package com.dachen.mdt.entity;

public class OrderDetailVO {
	public String orderId;
	
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

	public DiseaseInfo getDisease() {
		return disease;
	}

	public void setDisease(DiseaseInfo disease) {
		this.disease = disease;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public PatientInfo getPatient() {
		return patient;
	}

	public void setPatient(PatientInfo patient) {
		this.patient = patient;
	}

	public String getMdtNum() {
		return mdtNum;
	}

	public void setMdtNum(String mdtNum) {
		this.mdtNum = mdtNum;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDiseaseTypeId() {
		return diseaseTypeId;
	}

	public void setDiseaseTypeId(String diseaseTypeId) {
		this.diseaseTypeId = diseaseTypeId;
	}

	public String getDiseaseType() {
		return diseaseType;
	}

	public void setDiseaseType(String diseaseType) {
		this.diseaseType = diseaseType;
	}

	public String getMdtGroupId() {
		return mdtGroupId;
	}

	public void setMdtGroupId(String mdtGroupId) {
		this.mdtGroupId = mdtGroupId;
	}

	public String getMdtGroupName() {
		return mdtGroupName;
	}

	public void setMdtGroupName(String mdtGroupName) {
		this.mdtGroupName = mdtGroupName;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Long getExpectEndTime() {
		return expectEndTime;
	}

	public void setExpectEndTime(Long expectEndTime) {
		this.expectEndTime = expectEndTime;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Long getRealEndTime() {
		return realEndTime;
	}

	public void setRealEndTime(Long realEndTime) {
		this.realEndTime = realEndTime;
	}

//	public MDTReportInfo getReport() {
//		return report;
//	}
//
//	public void setReport(MDTReportInfo report) {
//		this.report = report;
//	}
	
}
