package edu.fudan.weixin.model.message;

import java.util.HashMap;

/**
 * 生成客服接口使用的消息对象
 * @author wking
 *
 */
public class JSONMessageBuilder extends TextMessageBuilder {

	public JSONMessageBuilder() {
		super();	
	}
	
	
	
	
	/**
	 * @see TextMessageBuilder#setTo(Object)
	 */
	public void setTo(Object value)
	{
		message.put("touser", value);
	}
	
	
	public void setContent(Object textContent) {
		HashMap<String,Object> content = new HashMap<String,Object>();
		content.put("content", textContent);
		message.put("text", content);
		set("msgtype","text");
	}
	
}