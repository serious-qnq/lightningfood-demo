package com.java.lightningfooddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java.lightningfooddemo.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
