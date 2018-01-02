package io.itit.smartjdbc.util;

/**
 * 
 * @author skydu
 *
 */
public class ArrayUtils {

	//
	public static Object[] convert(int[] t) {
		if(t==null) {
			return null;
		}
		Object[] ret=new Object[t.length];
		for(int i=0;i<t.length;i++) {
			ret[i]=t[i];
		}
		return ret;
	}
	//
	public static  Object[] convert(byte[] t) {
		if(t==null) {
			return null;
		}
		Object[] ret=new Object[t.length];
		for(int i=0;i<t.length;i++) {
			ret[i]=t[i];
		}
		return ret;
	}
	//
	public static  Object[] convert(short[] t) {
		if(t==null) {
			return null;
		}
		Object[] ret=new Object[t.length];
		for(int i=0;i<t.length;i++) {
			ret[i]=t[i];
		}
		return ret;
	}
	//
	public static  <T> Object[] convert(T[] t) {
		if(t==null) {
			return null;
		}
		Object[] ret=new Object[t.length];
		for(int i=0;i<t.length;i++) {
			ret[i]=t[i];
		}
		return ret;
	}
}
