package com.wfwgyy.imsa.common;

/**
 * 具有两个元素的元组类，属性为只读且无序
 * @author 闫涛 2018.01.26 v0.0.1
 *
 * @param <T1>
 * @param <T2>
 */
public class Turple2<T1, T2> {
	public final T1 v1;
	public final T2 v2;
	
	public Turple2(T1 v1, T2 v2) {
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public String toString() {
		return "(" + v1 + ", " + v2 + ")";
	}
}
