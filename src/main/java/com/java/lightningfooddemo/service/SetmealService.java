package com.java.lightningfooddemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.java.lightningfooddemo.dto.SetmealDto;
import com.java.lightningfooddemo.entity.Setmeal;
import org.springframework.stereotype.Service;


public interface SetmealService extends IService<Setmeal> {

    //新增套餐 保存套餐和菜品的关系
    public void saveWithDish(SetmealDto setmealDto);

    //删除套餐 关联的菜也删掉
    public void removeWithDish(Long[] ids);

    //根据id 回显菜品 图片
    public SetmealDto getByIdWithDish(Long id);

    //确定修改
    public void sureupdate(SetmealDto setmealDto);
}
