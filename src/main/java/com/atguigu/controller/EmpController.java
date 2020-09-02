package com.atguigu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atguigu.beans.Employee;
import com.atguigu.beans.Msg;
import com.atguigu.service.EmpService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Controller
public class EmpController {

	@Autowired
	EmpService empService;

	/**
	 * 单个批量二合一的删除方法 单个删除：1 批量删除：传入的ids字符串类似于：1-2-3
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/emp/{ids}", method = RequestMethod.DELETE)
	@ResponseBody
	public Msg delEmpById(@PathVariable("ids") String ids) {
		List<Integer> list = new ArrayList<>();
		if (ids.contains("-")) {
			String[] strings = ids.split("-");
			for (String str : strings) {
				list.add(Integer.parseInt(str));
			}
			empService.deleteBatch(list);
		} else {
			Integer id = Integer.parseInt(ids);
			empService.delEmpById(id);
		}
		return Msg.success();
	}

	/**
	 * 更新员工
	 * 
	 * 问题：请求体中有数据，但是Employee对象封装不上。
	 * 
	 * 原因：Tomcat将请求体重的数据封装成一个map；SpringMVC封装POJO对象时，
	 * 会把POJO中每个属性的值通过request.getParameter("empName")从这个map中取值；
	 * 通过AJAX发送PUT请求时，请求体中的数据不会被Tomcat封装成map，所以request.getParameter()方法拿不到值，
	 * 只有POST形式的请求才会被封装
	 * 
	 * 解决方法：在web.xml中配置上HttpPutFormContentFilter。
	 * 作用：HttpPutFormContentFilter将请求体中的数据解析包装成一个map，request被重新包装，
	 * request.getParameter()方法被重写，就会从自己封装的map中取数据
	 * 
	 * @param employee
	 * @return
	 */
	@RequestMapping(value = "/emp/{empId}", method = RequestMethod.PUT)
	@ResponseBody
	public Msg updateEmp(Employee employee) {
		System.out.println("根据请求封装的employee是：" + employee);
		empService.updateEmp(employee);
		return Msg.success();
	}

	/**
	 * 根据路径传过来的员工id查询该员工的信息并返回
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/emp/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Msg getEmp(@PathVariable("id") Integer id) {
		Employee employee = empService.getEmp(id);
		return Msg.success().add("emp", employee);
	}

	/**
	 * 检查用户名是否可用
	 * 
	 * @param empName
	 * @return
	 */
	@RequestMapping("/checkuser")
	@ResponseBody
	public Msg checkuser(@RequestParam("empName") String empName) {
		// 先判断用户名是否是合法的表达式
		String regx = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\\u2E80-\\u9FFF]{2,5})";
		if (!empName.matches(regx)) {
			return Msg.fail().add("va_msg", "用户名必须是6-16位英文和数字的组合或2-5位中文");
		}
		// 数据库用户名重复校验
		boolean b = empService.checkUser(empName);
		if (b) {
			return Msg.success();
		} else {
			return Msg.fail().add("va_msg", "用户名已存在");
		}
	}

	/**
	 * 处理ajax请求，返回所有员工的分页信息
	 * 
	 * @param pn
	 * @return
	 */
	@RequestMapping("/emps")
	@ResponseBody
	public Msg getEmps(@RequestParam(value = "pn", defaultValue = "1") Integer pn) {
		System.out.println("请求过来了，准备处理...");
		PageHelper.startPage(pn, 5);
		List<Employee> emps = empService.getAll();
		PageInfo info = new PageInfo(emps, 5);
		System.out.println("当前页码：" + info.getPageNum());
		System.out.println("总页码：" + info.getPages());
		System.out.println("总记录数" + info.getTotal());
		return Msg.success().add("pageInfo", info);
	}

	/**
	 * 员工保存 保存前先在后端用JSR303对员工名和邮箱进行校验，结果存在BindingResult中
	 * 1、想要使用JSR303需要引入hibernate-validator依赖，
	 * 2、然后在需要校验的javaBean的属性上添加相应的注解，在传入的封装后的javaBean对象参数上添加@Valid注解 3、最后取出校验结果
	 * 
	 * @param employee
	 * @return
	 */
	@RequestMapping(value = "/emp", method = RequestMethod.POST)
	@ResponseBody
	public Msg saveEmp(@Valid Employee employee, BindingResult result) {
		// 后端利用JSR303对填写的员工名和邮箱进行校验
		Map<String, Object> map = new HashMap<>();
		if (result.hasErrors()) {
			List<FieldError> errors = result.getFieldErrors();
			for (FieldError fieldError : errors) {
				System.out.println("错误的字段：" + fieldError.getField());
				System.out.println("错误的信息" + fieldError.getDefaultMessage());
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return Msg.fail().add("errorFields", map);
		}
		empService.saveEmp(employee);
		return Msg.success();
	}

}
