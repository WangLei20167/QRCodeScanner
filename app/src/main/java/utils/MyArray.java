package utils;

import java.lang.reflect.Array;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class MyArray {
    /**
     * 自增后原数据保存在新数组中
     * @param o   需要自增的对象
     * @param increase  自增几个单位长度
     * @return
     */
    public static Object arrayGrow(Object o, int increase) {
        Class cl = o.getClass();
        if (!cl.isArray()) return null;
        Class componentType = cl.getComponentType();
        int length = Array.getLength(o);
        //int newLength = length * 11 / 10 + 10;
        int newLength = length + increase;
        Object newArray = Array.newInstance(componentType, newLength);
        System.arraycopy(o, 0, newArray, 0, length);
        return newArray;
    }
}
