package com.atguigu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atguigu.beans.Department;
import com.atguigu.beans.Msg;
import com.atguigu.service.DepartmentService;

/**
 * 处理和部门有关的请求
 * @author xixihaha
 *
 */
@Controller
public class DepartmentController {

	@Autowired
	private DepartmentService departmentService;
	
	@RequestMapping("/depts")
	@ResponseBody
	public Msg getDepts() {
		List<Department> list = departmentService.getAll();
		return Msg.success().add("depts", list);
	}
	
}
