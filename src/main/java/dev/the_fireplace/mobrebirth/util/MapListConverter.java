package dev.the_fireplace.mobrebirth.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public final class MapListConverter {
    //TODO integrate these with FL 6.0.0
    public static final String MAP_SEPARATOR = "=";

    public static List<String> mapToList(Map<String, Integer> map) {
        List<String> stringList = Lists.newArrayList();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            stringList.add(entry.getKey() + MAP_SEPARATOR + entry.getValue().toString());
        }

        return stringList;
    }

    public static Map<String, Integer> listToMap(List<String> list) {
        Map<String, Integer> map = Maps.newHashMap();
        for (String str : list) {
            String[] parts = str.split(MAP_SEPARATOR);
            map.put(parts[0], Integer.parseInt(parts[1]));
        }

        return map;
    }
}
