package com.example.active_mq_practice.jms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.MathTool;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.mongodb.client.model.Filters;

@Component
public class JmsReadRoute extends RouteBuilder{
	
	private List<String> collections = new ArrayList<String>(Arrays.asList("assignment" , "workflow_instance" , "kpi_data"));

	
	
	@Override
	public void configure() throws Exception {
		
		from("scheduler://myscheduler?delay=10s")
		.log("this is message")
		//from(Constants.route1)
		.log("Body before ===> ${body}")
		.bean(this,"testbean")
		.bean(this,"convertingTime")
		.to("velocity:myfile.vm")
		.log("Body after ===> ${body}");
		
		// find collectionsize
		//.loop(header("count"))
		//.bean(this,"updateRoute")
//		.bean(this , "setExecutionID")
//		.bean(this ,"findCollections")
//		.log("Log body ----------> ${body}")
//		.log("Log Headers----------> ${headers}")
//		.loop(header("count")).copy()
//		.bean(this,"updateRoute")
//		.log("yogesh ----------> ${headers.collection}")
//		.toD("mongodb:mongo?database=test_db&collection=${headers.collection}&operation=update")
//		.log("Log----------> ${body}")
//		.log("Log----------> ${headers}")
//		.end();
	
	}

	

	
	// this method is use for update in mongodb
  public void updateRoute(Exchange exchange) {
	  Map<String, Object> headers = exchange.getIn().getHeaders();
	   Document body = exchange.getIn().getBody(Document.class);
	   System.out.println("headers ----> " + headers);
	   System.out.println("Body -----> " + body);
	   
	   Configuration con = Configuration.defaultConfiguration().setOptions(Option.SUPPRESS_EXCEPTIONS);
	   DocumentContext documentCon = JsonPath.using(con).parse(body);
	   
	   System.out.println("Document Con ----> " + documentCon);
	   
	   List<Bson> listbody = new ArrayList<Bson>();
//	   String userId = documentCon.read("eventSourceObject.userId",String.class);
//	   System.out.println("User id = " + userId);
	   
//	   String userInitials = documentCon.read("eventSourceObject.userInitials",String.class);
//	   System.out.println("UserInitials = " + userInitials);
	   
//	   String new_user_Name = documentCon.read("eventSourceObject.new_user_Name",String.class);
//	   System.out.println("new user = " + new_user_Name);
	   
//	   String userInitials = documentCon.read("eventSourceObject.userInitials",String.class); 
//	   String new_userInitials = documentCon.read("eventSourceObject.new_userInitials",String.class); 
	   
	   String workspaceID = documentCon.read("eventSourceObject.workspaceId" , String.class);
	  // String car = documentCon.read("eventSourceObject.car");
	   String new_car = documentCon.read("eventSourceObject.new_car");
	   
//	   Bson bson = Filters.and(Filters.eq("userId",userId ),Filters.eq("userInitials",userInitials));
//	   listbody.add(bson);
//	   System.out.println("bson data = " + bson);
//	   System.out.println("list of body ---->  " +listbody.toString());
	   
	   Bson bson = Filters.and(Filters.eq("workspaceId", workspaceID));
	   listbody.add(bson);
	   System.out.println("bson data ------->" + bson);
	   
	 
	   
//	   BsonDocument bsonDocument = new BsonDocument("userName" , new BsonString(new_user_Name));
//	   BsonDocument updateDocument = new BsonDocument().append("$set", bsonDocument);
//	   System.out.println("update document ------> " + updateDocument);
//	   listbody.add(updateDocument);
//	   exchange.getIn().setBody(listbody);
	 
	   
	   BsonDocument bsonDocument = new BsonDocument("car" , new BsonString(new_car));
	   BsonDocument updateDocument = new BsonDocument().append("$set",bsonDocument);
	   
	   listbody.add(updateDocument);
	   exchange.getIn().setBody(listbody);
	   
	   // properties for multiple update 
	   headers.put("mongodb","test_db");
	   headers.put(MongoDbConstants.MULTIUPDATE, true);
	   
	   
	    
	   // getting current index value 
	   Integer index = (Integer) exchange.getProperty(Exchange.LOOP_INDEX);
	   headers.put("collection", collections.get(index));
	   exchange.getIn().setHeaders(headers);
	   System.out.println();
	   System.out.println();
	   System.out.println("Index value is ========>" + index);
	   System.out.println("Index value is ========>" + headers);
	   System.out.println();
	   System.out.println();
	   System.out.println("list Body ------>  " + listbody);
  }
  
  public void findCollections(Exchange exchange) {
	 Map<String,Object > headers = exchange.getIn().getHeaders();
	 System.out.println("headers are -------->" + headers);
	 int listSize = collections.size();
	 headers.put("count", listSize);
	 exchange.getIn().setHeaders(headers);
	 System.out.println("headers are -------->" + headers); 
  }
  
  // testing scheduler by printing message
  public void testbean(Exchange exchange) {
	  org.apache.camel.Message exchangeIn = exchange.getIn();
	  java.util.Map<String, Object> headers = exchangeIn.getHeaders();
	  headers.put("message", "thanks you for your hard work");
	  headers.put("name", "Yogesh");
	  exchange.getIn().setBody(headers);
	  exchangeIn.setHeaders(headers);
  }
  
  // getting current time
  public void convertingTime(Exchange exchange){
	 
	  Map<String, Object> headers = exchange.getIn().getHeaders();
	  
	  Date date = new Date();
	 // System.out.println("Current date before conversion =======>" + date);
	  exchange.getIn().setHeader("timestamp", date);
	
	  
	  // adding  unix time (by adding depedency)
	  Calendar cal = Calendar.getInstance();
	  String unixTime = String.valueOf(cal.getTimeInMillis());  
//	  DateUnixtimeSecondsTypeAdapter unix = new DateUnixtimeSecondsTypeAdapter(true);
//	  String unixTime = unix.toJson(date);
//	 // System.out.println(" unix==========>" + unixTime);
	  exchange.getIn().setHeader("unixTime", unixTime);
	  
	  // converting unix timestamp in date time
	  
//	 long  unixSeconds  = Long.parseLong(unixTime);
//	 Date date1 = new Date(unixSeconds*1000L);
//	 // format of the date
//	   SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
//	   jdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
//	   String java_date = jdf.format(date);
//	   System.out.println("\n"+java_date+"\n");
//	   
//	   exchange.getIn().setHeader("unix_Time", java_date);
	
	  VelocityContext velocityContext = new VelocityContext();
	   velocityContext.put("date", new DateTool());
	   velocityContext.put("math", new MathTool());
	   headers.put("VelocityContext", velocityContext);
	   
	   exchange.getIn().setHeaders(headers);
	   System.out.println("Headers =====> " + headers);
	
  }
  
  
  
	
}
