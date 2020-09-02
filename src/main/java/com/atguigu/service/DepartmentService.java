package com.atguigu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atguigu.beans.Department;
import com.atguigu.dao.DepartmentMapper;

@Service
public class DepartmentService {

	@Autowired
	private DepartmentMapper departmentMapper;

	public List<Department> getAll() {
		List<Department> list = departmentMapper.selectByExample(null);
		return list;
	}
	
	
	
}
