package com.lbq.concurrent.chapter10;

import java.util.ArrayList;
import java.util.List;

public class SimpleClass {
	//在SimpleClass中使用byte[]
	private static byte[] buffer = new byte[8];
	//在SimpleClass中使用String
	private static String str = "";
	//在SimpleClass中使用List
	private static List<String> list = new ArrayList<>();
	
	static {
		buffer[0] = (byte) 1;
		str = "Simple";
		list.add("element");
		System.out.println(buffer[0]);
		System.out.println(str);
		System.out.println(list.get(0));
	}
}
