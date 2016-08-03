package com.dachen.imsdk.entity;

import java.util.Map;

/**
 * 图文消息明细
 */
public class ImgTextMsgV2  {

	/**
	 * ---------组合（可有多种样式组合成一条消息，类似公众号发送的消息） 1=标题title 2=标题+大图title+pic
	 * 3=正文+小图content+pic 4=正文+大图content+pic 5=详情title
	 * ---------独立（每种样式都可作为一条独立的消息，比如系统通知类） 6=标题+正文+小图+正文副本(可空)
	 * title+content+pic+remark 7=标题+正文 title+content
	 * 
	 * 10=名片 title+content+remark+footer+pic 11=订单通知 title+content+order(订单)
	 * ===================================================
	 * 如果需要显示时间，则需带上time字段(标题下面显示时间)； 如果带链接，则需带上url字段（底部显示查看详情部分）
	 * ====================================================
	 */
	public int style;
	/**
	 * 标题 小于18字符*2行
	 */
	public String title;
	/**
	 * 图片地址（大小按照不同的样式固定）
	 */
	public String pic;

	/**
	 * 正文（大号显示）
	 */
	public String content;

	/**
	 * 正文副文本(小号字体显示) 20字符*8行(不用)
	 */
	public String digest;
	public String remark;
	
	public String specification;
	/**
	 * 底部显示内容
	 */
	public String footer;
	/**
	 * 链接地址
	 */
	public String url;
	/**
	 * 时间
	 */
	public Long time;
	/**
	 * 价格 ：￥100
	 */
	public String price;
	public String action;
	/**
	 * 如果带业务， key=bizType：表示业务类型 key=bizId：表示业务Id
	 */
	public Map<String, String> bizParam;
	public Map<String, String> param;
	public int getStyle() {
		return style;
	}
	public void setStyle(int style) {
		this.style = style;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSpecification() {
		return specification;
	}
	public void setSpecification(String specification) {
		this.specification = specification;
	}
	public String getFooter() {
		return footer;
	}
	public void setFooter(String footer) {
		this.footer = footer;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Map<String, String> getBizParam() {
		return bizParam;
	}
	public void setBizParam(Map<String, String> bizParam) {
		this.bizParam = bizParam;
	}

	public int getBizType(){
		int type=0;
		if(bizParam!=null){
			try {
				type=Integer.parseInt(bizParam.get("bizType"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return  type;
	}
}
