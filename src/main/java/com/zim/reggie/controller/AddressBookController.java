package com.zim.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zim.reggie.common.BaseContext;
import com.zim.reggie.common.R;
import com.zim.reggie.entity.AddressBook;
import com.zim.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;


    /*
    * 新增
    * */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}",addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        log.info("addressBook:{}" ,addressBook);
        LambdaUpdateWrapper<AddressBook> queryWrapper = new LambdaUpdateWrapper<>();

        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.set(AddressBook::getIsDefault,0);

        addressBookService.update(queryWrapper);

        addressBook.setIsDefault(1);

        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }

    @GetMapping("/{id}")
    public R get(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook != null){
            return R.success(addressBook);
        } else{
            return R.error("没有找到该对象");
        }

    }
    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookService.list(queryWrapper));
    }


    //删除地址
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info(ids.toString());
        addressBookService.removeById(ids);
        return R.success("删除成功");
    }


    //修改地址
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        log.info("修改地址:{}",addressBook);
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

}
