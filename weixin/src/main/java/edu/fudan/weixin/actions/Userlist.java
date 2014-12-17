package edu.fudan.weixin.actions;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import edu.fudan.eservice.common.struts.GuestActionBase;
import edu.fudan.eservice.common.utils.MongoUtil;

@SuppressWarnings("serial")
@ParentPackage(value = "servicebase")
@Actions({@Action(value = "getUserlist")})
@Results({@Result(name = "success",location = "userlist.jsp")})

public class Userlist extends GuestActionBase {
	
	private List<DBObject> list = null;
	
	
	public String execute(){
		
		DBCollection collection = MongoUtil.getInstance().getDB().getCollection("weixinuser");		
		list = collection.find(new BasicDBObject("subscribe",1)).toArray();
		
	
		return SUCCESS;		
		
		
	}

	public List<DBObject> getList() {
		return list;
	}

	public void setList(List<DBObject> list) {
		this.list = list;
	}

	
	

}