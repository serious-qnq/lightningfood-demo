package com.java.lightningfooddemo.dto;


import com.java.lightningfooddemo.entity.Setmeal;
import com.java.lightningfooddemo.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
