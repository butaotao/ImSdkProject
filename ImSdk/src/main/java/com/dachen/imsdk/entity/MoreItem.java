package com.dachen.imsdk.entity;

/**
 * 用于im底部加号数据结构
 * 
 * @author gaozhuo
 * @date 2015年10月24日
 *
 */
public class MoreItem {
	// 名称
	public String name;
	// 图片资源id
	public int resId;
	
	public MoreItem(String name, int resId) {
		super();
		this.name = name;
		this.resId = resId;
	}
}
