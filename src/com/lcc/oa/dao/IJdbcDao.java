package com.lcc.oa.dao;

import java.util.List;
import java.util.Map;

public interface IJdbcDao {

    /**
     * 添加或修改
     */
    public int saveOrUpdate(String sql, Map<String, Object> paramMap);

    /**
     * 删除
     */
    public int delete(String sql, Map<String, Object> paramMap);

    /**
     * 批量增删改
     */
    public int[] batchExecute(String sql, List<Object[]> paramList);

    /**
     * 分页查询
     */
    public List<Map<String, Object>> find(String sql, Map<String, Object> paramMap);

    /**
     * 查询全部
     */
    public List<Map<String, Object>> findAll(String sql, Map<String, Object> paramMap);

    /**
     * 查询记录数
     */
    public int getCount(String sql, Map<String, Object> paramMap);

}
