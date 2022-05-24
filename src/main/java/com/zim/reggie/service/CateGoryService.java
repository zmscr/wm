package com.zim.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zim.reggie.entity.Category;

public interface CateGoryService extends IService<Category> {
    public void remove(Long ids);
}
