package com.qqj.utils;

import java.util.ArrayList;
import java.util.List;

public class EntityUtils {
	public static <T> List<T> toWrappers(List entities, Class<T> wrapperClass) {

		try {
			List<T> list = new ArrayList<T>();
			for (Object entity : entities) {
				list.add(wrapperClass.getConstructor(entity.getClass()).newInstance(entity));
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
