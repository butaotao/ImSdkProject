package com.dachen.imsdk.utils;

import java.lang.ref.WeakReference;

public class RefTool<T> {
	private WeakReference<T> ref;
	public RefTool(WeakReference<T> ref) {
		this.ref=ref;
	}
	public T getRef(){
		if (ref == null)
			return null;
		return ref.get();
	}
}
