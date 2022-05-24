package com.zim.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zim.reggie.dto.DishDto;
import com.zim.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);


    //根据id查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);


    //更新菜品信息，包括口味信息
    void updateWithFlavor(DishDto dishDto);
}
