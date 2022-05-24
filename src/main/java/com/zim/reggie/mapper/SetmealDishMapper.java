package com.zim.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zim.reggie.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
}
