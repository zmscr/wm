package com.zim.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.zim.reggie.common.R;
import com.zim.reggie.dto.SetmealDto;
import com.zim.reggie.entity.Category;
import com.zim.reggie.entity.Dish;
import com.zim.reggie.entity.Setmeal;
import com.zim.reggie.entity.SetmealDish;
import com.zim.reggie.service.CateGoryService;
import com.zim.reggie.service.SetmealDishService;
import com.zim.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import javafx.scene.chart.ValueAxis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api("套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CateGoryService cateGoryService;


    /*
     *
     * 新增套餐
     *
     * */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true /*删除setmealCache分类下的所有缓存数据*/)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info(setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }


    /*
     * 套餐分页查询
     * */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        //添加查询条件
        queryWrapper.like(name != null, Setmeal::getName, name);

        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);


        //查完后pageInfo已赋值，将pageInfo的属性拷贝到dtoPage
        setmealService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((zim) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(zim, setmealDto);
            //分类id
            Long categoryId = zim.getCategoryId();
            //根据分类id查询分类对象
            Category category = cateGoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            return setmealDto;
        }).collect(Collectors.toList());


        dtoPage.setRecords(list);


        return R.success(dtoPage);
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true /*删除setmealCache分类下的所有缓存数据*/)
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.deleteWithDish(ids);
        return R.success("删除成功");
    }


    //根据条件查询套餐数据
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);


        return R.success(list);
    }

    //启售停售套餐
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, Long ids) {
        log.info(ids.toString());
        log.info(status.toString());

        Setmeal setmeal = setmealService.getById(ids);

        if (setmeal != null) {
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
            return R.success("修改成功");
        }

        return R.error("设置错误");
    }

    //修改套餐

    @GetMapping("/{id}")
    public R<SetmealDto> getData(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getDate(id);

        return R.success(setmealDto);
    }


}
