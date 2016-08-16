package com.dachen.mdt.entity;

public class OrderParam {
	public String orderId;
	/*患者信息*/
	public PatientInfo patient;
	/**
	 * 会诊分类
	 */
	public String diseaseTypeId;

	public String firstDiag;
	/*mdt小组Id*/
	public String mdtGroupId;

	/*会诊目的*/
	public String target;
	
	/*病情资料*/
	public DiseaseInfo disease;
	
	/*会诊结束时间*/
	public Long expectEndTime;
	
}
