package thd.bd.sms.utils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by llg on 2016/11/28.
 */

public class CollectionUtils {

    /**
     * 去除重复元素
     * @param list
     * @return
     */
    public static List removeDuplicate(List list)

    {
        Set set = new LinkedHashSet();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    /**
     * 去除回车换行
     * @param src
     * @return
     */
    public static String trimEnter(String src){

        String str = src;
        str.replace("\n", "");
        String result = str.trim();
        return result;
    }
}
