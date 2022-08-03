package com.java.lightningfooddemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.java.lightningfooddemo.entity.Category;
import com.java.lightningfooddemo.entity.Employee;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
