package com.zim.reggie.dto;


import com.zim.reggie.entity.Setmeal;
import com.zim.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
