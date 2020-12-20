package com.lbq.concurrent.jmh;

import java.net.URL;
import java.net.URLClassLoader;

public class AlexClassLoader extends URLClassLoader {

	private final byte[] bytes;
	
	public AlexClassLoader(byte[] bytes) {
		super(new URL[0], ClassLoader.getSystemClassLoader());
		this.bytes = bytes;
	}

	protected Class<?> findClass(String name){
		return defineClass(name, bytes, 0, bytes.length);
	}
}
