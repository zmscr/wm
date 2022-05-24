package com.zim.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zim.reggie.common.CustomException;
import com.zim.reggie.dto.SetmealDto;
import com.zim.reggie.entity.Setmeal;
import com.zim.reggie.entity.SetmealDish;
import com.zim.reggie.mapper.SetmealMapper;
import com.zim.reggie.service.SetmealDishService;
import com.zim.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;



     /*
     *
     * 新增套餐同时保存套餐和菜品的关联关系
     *
     * */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((zim)->{
            zim.setSetmealId(setmealDto.getId());
            return zim;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    public void deleteWithDish(List<Long> ids) {
        //查询套餐状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);

        //如果不行，抛异常
        if (count > 0){
            throw new CustomException("");
        }

        //如果可以删除，先删除套餐表中的数据setmeal
        this.removeByIds(ids);


        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.in(SetmealDish::getSetmealId,ids);

        //删除关系表中的数据setmeal_dish
        setmealDishService.remove(lqw);


    }
}
