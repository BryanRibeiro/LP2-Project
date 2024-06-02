package com.projectlp2.util;

import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {

    public static <T, R> List<R> mapList(List<T> list, Class<R> returnType, Mapper<T, R> mapper) {
        return list.stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    public interface Mapper<T, R> {
        R map(T item);
    }
}
