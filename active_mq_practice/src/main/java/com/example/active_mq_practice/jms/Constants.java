package com.example.active_mq_practice.jms;

public class Constants {
	
	public static final String route1 = "jms:topic:yogeshTest?asyncConsumer=true";
	public static final String route2 = "jms:queue:testQueue";
	public static final String testMessage ="jms:topic:testMessage?asyncConsumer=true";

}
