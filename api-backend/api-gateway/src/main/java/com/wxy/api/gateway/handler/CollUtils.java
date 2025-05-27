package com.wxy.api.gateway.handler;

import java.util.Collection;
import java.util.Map;

public class CollUtils {

    /**
     * 判断集合是否为空
     *
     * @param collection 集合对象
     * @return 如果集合为空或为 null，返回 true；否则返回 false
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断 Map 是否为空
     *
     * @param map Map 对象
     * @return 如果 Map 为空或为 null，返回 true；否则返回 false
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断数组是否为空
     *
     * @param array 数组对象
     * @return 如果数组为空或为 null，返回 true；否则返回 false
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断集合是否不为空
     *
     * @param collection 集合对象
     * @return 如果集合不为空且不为 null，返回 true；否则返回 false
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断 Map 是否不为空
     *
     * @param map Map 对象
     * @return 如果 Map 不为空且不为 null，返回 true；否则返回 false
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 判断数组是否不为空
     *
     * @param array 数组对象
     * @return 如果数组不为空且不为 null，返回 true；否则返回 false
     */
    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }
}
