package com.zim.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zim.reggie.common.CustomException;
import com.zim.reggie.entity.Category;
import com.zim.reggie.entity.Dish;
import com.zim.reggie.entity.Setmeal;
import com.zim.reggie.mapper.CategoryMapper;
import com.zim.reggie.service.CateGoryService;
import com.zim.reggie.service.DishService;
import com.zim.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CateGoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    //根据id删除分类
    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Dish> diqw = new LambdaQueryWrapper<Dish>();
        //添加查询条件
        diqw.eq(Dish::getCategoryId,ids);
        int count1 = dishService.count(diqw);


        //查询当前分类是否关联菜品，是则抛异常
        if(count1>0){
            throw new CustomException("当前分类关联了菜品，不能删除");
        }



        //查询当前分类是否关联套餐，是则抛异常
        LambdaQueryWrapper<Setmeal> slqw = new LambdaQueryWrapper<Setmeal>();
        slqw.eq(Setmeal::getCategoryId,ids);
        int count2 = setmealService.count(slqw);

        if (count2>0){
            throw new CustomException("当前分类关联了套餐，不能删除");
        }

        //正常删除
        super.removeById(ids);
    }
}
