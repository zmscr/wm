package com.zim.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zim.reggie.common.R;
import com.zim.reggie.dto.DishDto;
import com.zim.reggie.entity.Category;
import com.zim.reggie.entity.Dish;
import com.zim.reggie.entity.DishFlavor;
import com.zim.reggie.service.CateGoryService;
import com.zim.reggie.service.DishFlavorService;
import com.zim.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CateGoryService cateGoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    //新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        //清理所有菜品的缓存数据
        /*Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);*/
        /**/
        //清理某个分类下面的缓存
        String keys = "dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(keys);
        return R.success("新增菜品成功");
    }

    /*//菜品分页
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<Dish>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件过滤器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<Dish>();

        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        records.stream().map((zim)->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(zim,dishDto);

            Long categoryId = zim.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = cateGoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        List<DishDto> list = null;

        dishDtoPage.setRecords(list);

        return R.success(pageInfo);
    }*/

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = cateGoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    /*
     * 根据id查询菜品信息和对应口味信息
     * */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);

        return R.success(byIdWithFlavor);
    }


    //修改菜品
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        //清理所有菜品的缓存数据
        /*Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);*/

        //清理某个分类下面的缓存
        String keys = "dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(keys);

        return R.success("新增菜品成功");
    }


    /*
     * 根据条件查询菜品数据
     * */
   /* @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<Dish>();

        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());

        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);



        return R.success(list);
    }*/

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        //先从redis中获取缓存数据
        List<DishDto> dishDtoList = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        //如果存在，直接返回，无需查询数据库
        if (dishDtoList != null) {
            return R.success(dishDtoList);
        }



        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<Dish>();

        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());

        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = cateGoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
            qw.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(qw);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        //如果不存在，则查询数据库,并将查询到的数据缓存到redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }


    //
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, Long ids) {
        log.info(ids.toString());
        log.info(status.toString());

        Dish dish = dishService.getById(ids);

        if (dish != null) {
            dish.setStatus(status);
            dishService.updateById(dish);
            return R.success("修改成功");
        }

        return R.error("设置错误");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam("ids")List<Long> ids){
        log.info(ids.toString());

        dishService.deleteById(ids);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);
        return R.success("菜品删除成功");
    }
}
