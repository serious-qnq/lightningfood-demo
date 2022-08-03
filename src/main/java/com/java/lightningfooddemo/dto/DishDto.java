package com.java.lightningfooddemo.dto;

import com.java.lightningfooddemo.entity.Dish;
import com.java.lightningfooddemo.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
