package com.mishu.cgwy.utils;

import java.util.ArrayList;
import java.util.List;

public class EntityUtils {
	public static <T extends Class> List<T> toWrappers(List entities, T wrapperClass) {

		try {
			List<T> list = new ArrayList<T>();
			for (Object entity : entities) {
				list.add((T) wrapperClass.getConstructor(entity.getClass()).newInstance(entity));
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
