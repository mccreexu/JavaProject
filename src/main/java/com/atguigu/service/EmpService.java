package com.atguigu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atguigu.beans.Employee;
import com.atguigu.beans.EmployeeExample;
import com.atguigu.beans.EmployeeExample.Criteria;
import com.atguigu.dao.EmployeeMapper;

@Service
public class EmpService {

	@Autowired
	EmployeeMapper employeeMapper;

	/**
	 * 查询所有员工
	 * @return
	 */
	public List<Employee> getAll() {
		return employeeMapper.selectByExampleWithDept(null);
	}

	/**
	 * 保存员工
	 * @param employee
	 */
	public void saveEmp(Employee employee) {
		employeeMapper.insertSelective(employee);
	}

	/**
	 * 校验用户名是否可用
	 * @param empName
	 * @return true：代表当前用户名可用；false：不可用
	 */
	public boolean checkUser(String empName) {
		EmployeeExample example = new EmployeeExample();
		Criteria criteria = example.createCriteria();
		criteria.andEmpNameEqualTo(empName);
		long count = employeeMapper.countByExample(example);
		return count==0;
	}

	/**
	 * 根据id查出员工信息
	 * @param id
	 * @return
	 */
	public Employee getEmp(Integer id) {
		Employee employee = employeeMapper.selectByPrimaryKey(id);
		return employee;
	}

	/**
	 * 根据员工id来更新保存员工
	 * @param id
	 */
	public void updateEmp(Employee employee) {
		employeeMapper.updateByPrimaryKeySelective(employee);
	}

	/**
	 * 根据员工id删除员工
	 * @param id
	 */
	public void delEmpById(Integer id) {
		employeeMapper.deleteByPrimaryKey(id);
	}
	
	/**
	 * 根据传进的多个员工id进行批量删除
	 * @param ids
	 */
	public void deleteBatch(List<Integer> ids) {
		EmployeeExample example = new EmployeeExample();
		Criteria criteria = example.createCriteria();
		//拼装删除语句：delete from xxx where emp_id in(1,2,3);
		criteria.andEmpIdIn(ids);
		employeeMapper.deleteByExample(example);
	}

}
