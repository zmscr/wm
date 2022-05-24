package com.zim.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zim.reggie.common.R;
import com.zim.reggie.entity.Category;
import com.zim.reggie.service.CateGoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CateGoryService cateGoryService;


    //新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        cateGoryService.save(category);
        return R.success("新增分类成功");

    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<Category>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<Category>();
        //添加排序条件，根据sort进行排序
        lqw.orderByAsc(Category::getSort);

        //进行分页查询
        cateGoryService.page(pageInfo,lqw);


        return R.success(pageInfo);

    }

    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类，id为：{}",ids);


        //
        //cateGoryService.removeById(ids);
        cateGoryService.remove(ids);
        return R.success("成功");
    }


    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改信息{}",category);
        cateGoryService.updateById(category);

        return R.success("修改成功");
    }


    //菜品分类下拉框
    @GetMapping("/list")
    public R<List<Category>> list (Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<Category>();
        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = cateGoryService.list(queryWrapper);

        return R.success(list);
    }
}
