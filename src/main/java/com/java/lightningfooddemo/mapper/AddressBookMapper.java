package com.java.lightningfooddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java.lightningfooddemo.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
