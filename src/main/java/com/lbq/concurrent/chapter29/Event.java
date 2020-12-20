package com.lbq.concurrent.chapter29;
/**
 * 29.1.1 Events
 * Events是EDA中重要角色，一个Event至少需要包含两个属性：类型和数据，
 * Event的类型决定了它会被哪个Handler处理，数据是在Handler中代加工的材料。
 * 
 * Event只包含了该Event所属类型和所包含的数据
 * @author 14378
 *
 */
public class Event {

	private final String type;
	private final String data;
	public Event(String type, String data) {
		this.type = type;
		this.data = data;
	}
	public String getType() {
		return type;
	}
	public String getData() {
		return data;
	}
	
}
