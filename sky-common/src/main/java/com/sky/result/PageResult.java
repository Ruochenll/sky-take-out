package com.sky.result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    // 总记录数
    private  Long total;
    // 记录
    private List<T> records;
}

/**
 * List<T>的用法：
 * List<T> 是Java集合框架中的一个接口，表示一个有序的元素集合，允许重复元素。
 * T 是泛型参数，代表列表中存储的元素类型。
 * 常见操作包括：
 * - 添加元素：list.add(element)
 * - 获取元素：list.get(index)
 * - 删除元素：list.remove(index) 或 list.remove(element)
 * - 获取大小：list.size()
 * - 遍历列表：for (T element : list) { ... }
 * 在PageResult类中，List<T>用于存储分页查询的结果数据。
 */
