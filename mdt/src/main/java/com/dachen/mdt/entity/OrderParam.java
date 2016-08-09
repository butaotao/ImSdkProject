package com.dachen.mdt.entity;

public class OrderParam {
	public String orderId;
	/*患者信息*/
	public PatientInfo patient;
	/**
	 * 会诊分类
	 */
	public String diseaseTypeId;
	/*mdt小组Id*/
	public String mdtGroupId;
	
	/*会诊目的*/
	public String target;
	
	/*病情资料*/
	public DiseaseInfo disease;
	
	/*会诊结束时间*/
	public Long expectEndTime;
	

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

	public String getDiseaseTypeId() {
		return diseaseTypeId;
	}

	public void setDiseaseTypeId(String diseaseTypeId) {
		this.diseaseTypeId = diseaseTypeId;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public DiseaseInfo getDisease() {
		return disease;
	}

	public void setDisease(DiseaseInfo disease) {
		this.disease = disease;
	}

	public Long getExpectEndTime() {
		return expectEndTime;
	}

	public void setExpectEndTime(Long expectEndTime) {
		this.expectEndTime = expectEndTime;
	}

	public String getMdtGroupId() {
		return mdtGroupId;
	}

	public void setMdtGroupId(String mdtGroupId) {
		this.mdtGroupId = mdtGroupId;
	}
}
