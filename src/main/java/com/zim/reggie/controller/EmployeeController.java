package com.zim.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zim.reggie.common.R;
import com.zim.reggie.entity.Employee;
import com.zim.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    //登录功能
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 1. 将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());


        //2 .根据页面提交的用户名查数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<Employee>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3. 如果么有查询到数据，返回失败
        if (emp == null) {
            return R.error("登陆失败");
        }

        //4. 对比密码,不一致返回失败
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败,密码错误");
        }

        //5.查看员工状态
        if (emp.getStatus() == 0) {
            return R.error("登陆失败,员工被禁用");
        }

        //6.登录成功，将员工id存入Session并返回成功结果
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }


    //退出功能
    @PostMapping("/logout")
    public R<String> exit(HttpServletRequest request) {
        //1.清理session中保存的员工id
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    //添加员工功能
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工：{}", employee.toString());
        //为新账号设置初始密码并进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //创建时间
        employee.setCreateTime(LocalDateTime.now());
        //更新时间
        employee.setUpdateTime(LocalDateTime.now());
        //创建账号的id
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        //更新人
        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }


    //员工分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper();
        //添加过滤条件
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        lqw.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }

    //修改员工状态
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());
        Long empId = (Long)request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("修改成功");
    }


    //根据id编辑员工
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("查询不到改员工");
    }


}
