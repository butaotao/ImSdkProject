package com.dachen.mdt.entity;

public class MDTReportVO {
	public String orderId;
	
	/*主管医生*/
	public String userName;
	public PatientInfo patient;
	
	public String diseaseType;
	public String mdtGroupName;
	
	/*会诊目的*/
	public String target;
	/*初步诊断*/
	public String firstDiag;
	/*诊断意见*/
	public String diagSuggest;
	/*检查意见*/
	public String checkSuggest;
	
	/*治疗建议*/
	public String treatSuggest;
	
	/*其他*/
	public String other;
	/*会诊结束时间*/
	public Long expectEndTime;
	public Long realEndTime;
	public Long startTime;
	
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getFirstDiag() {
		return firstDiag;
	}
	public void setFirstDiag(String firstDiag) {
		this.firstDiag = firstDiag;
	}
	public String getDiagSuggest() {
		return diagSuggest;
	}
	public void setDiagSuggest(String diagSuggest) {
		this.diagSuggest = diagSuggest;
	}
	public String getCheckSuggest() {
		return checkSuggest;
	}
	public void setCheckSuggest(String checkSuggest) {
		this.checkSuggest = checkSuggest;
	}
	public String getTreatSuggest() {
		return treatSuggest;
	}
	public void setTreatSuggest(String treatSuggest) {
		this.treatSuggest = treatSuggest;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public PatientInfo getPatient() {
		return patient;
	}
	public void setPatient(PatientInfo patient) {
		this.patient = patient;
	}
	public String getDiseaseType() {
		return diseaseType;
	}
	public void setDiseaseType(String diseaseType) {
		this.diseaseType = diseaseType;
	}
	public String getMdtGroupName() {
		return mdtGroupName;
	}
	public void setMdtGroupName(String mdtGroupName) {
		this.mdtGroupName = mdtGroupName;
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
	public Long getRealEndTime() {
		return realEndTime;
	}
	public void setRealEndTime(Long realEndTime) {
		this.realEndTime = realEndTime;
	}
}
