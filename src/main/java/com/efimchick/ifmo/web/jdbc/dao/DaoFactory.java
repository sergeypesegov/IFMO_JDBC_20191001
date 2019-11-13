package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DaoFactory {
    private List<Employee> allemployees = getEmployees();
    private List<Department> alldepartments = getDepartment();

    private Connection createConnection() throws SQLException{
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connect = connectionSource.createConnection();
            if (connect != null) {
                System.out.println("connection is fine");
            }
            else {
                System.out.println("connection was failed");
            }
        return connect;
    }

    private List<Employee> getEmployees() {
        try {
            Connection connect = createConnection();
            ResultSet rs = connect.createStatement().executeQuery("SELECT * FROM EMPLOYEE");
            List<Employee> employees = new LinkedList<>();
            while (rs.next()) {
                Employee emp = mapRow(rs);
                employees.add(emp);
            }
            return employees;
        } catch (SQLException e) {
            return  null;
        }
    }

    private Employee mapRow (ResultSet rs){
        try {
            BigInteger id = new BigInteger(rs.getString("ID"));
            FullName fullname = new FullName(rs.getString("FIRSTNAME"),
                    rs.getString("LASTNAME"),
                    rs.getString("MIDDLENAME"));
            Position position = Position.valueOf(rs.getString("POSITION"));
            //BigInteger manager = BigInteger.valueOf(rs.getInt("MANAGER"));
            LocalDate hireDate = LocalDate.parse(rs.getString("HIREDATE"));
            BigDecimal salary = rs.getBigDecimal("SALARY");
            BigInteger department = BigInteger.valueOf(rs.getInt("DEPARTMENT"));

            //Employee manager = null;
            BigInteger manager = new BigInteger(rs.getString("MANAGER"));

            /*if (rs.getString("MANAGER") != null) {
                int managerId = rs.getInt("MANAGER");
                int current = rs.getRow();
                rs.beforeFirst();
                while (rs.next()) {
                    if (Integer.parseInt(rs.getString("ID")) == managerId) {
                        manager = mapRow(rs);
                    }
                }
                rs.absolute(current);
            }*/

            Employee employee = new Employee(id, fullname, position, hireDate, salary, manager, department);
            return employee;
        } catch (SQLException e) {
            return null;
        }
    }

    public Department getDepartment(ResultSet rs) throws SQLException {
        BigInteger id = new BigInteger(rs.getString("ID"));
        String name = rs.getString("NAME");
        String location = rs.getString("LOCATION");
        Department department = new Department(id, name, location);
        return department;
    }

    public EmployeeDao employeeDAO() {
       return new EmployeeDao() {
           @Override
           public List<Employee> getByDepartment(Department department) {
               List<Employee> empByDepartment = new ArrayList<>();
               for (Employee emp : allemployees) {
                   if (emp.getDepartmentId().equals(department.getId())) {
                       empByDepartment.add(emp);
                   }
               }
               return empByDepartment;
           }

           @Override
           public List<Employee> getByManager(Employee employee) {
               List<Employee> empByManager = new ArrayList<>();
               for (Employee emp : allemployees) {
                   if (emp.getManagerId().equals(employee.getId())) {
                       empByManager.add(emp);
                   }
               }
               return empByManager;
           }

           @Override
           public Optional<Employee> getById(BigInteger Id) {
               Optional<Employee> employee = Optional.empty();
               for (Employee emp : allemployees) {
                   if (emp.getId().equals(Id)) {
                       //employee.add(emp)  где-то здесь были потрачены 30 минут моей жизни,
                       //employee.set(emp)  чтобы понять, что с Optional нужно использовать .of()
                       employee = employee.of(emp);
                   }
               }
               return employee;
           }

           @Override
           public List<Employee> getAll() {
               return allemployees;
           }

           @Override
           public Employee save(Employee employee) {
               try {
                   allemployees.add(employee);
                   return employee;
               }
               catch (Exception e){
                   return null;
               }
           }

           @Override
           public void delete(Employee employee) {
               try {
                   allemployees.remove(employee);
               }
               catch (Exception e) {
                   e.printStackTrace();
               }
           }
       };
    }

    public DepartmentDao departmentDAO() {
        //throw new UnsupportedOperationException();
        return new DepartmentDao() {

            @Override
            public Optional<Department> getById(BigInteger Id) {
                Optional<Department> department = Optional.empty();
                for (Department dep : alldepartments) {
                    if (dep.getId().equals(Id)) {
                        department = department.of(dep);
                    }
                }
                return department;
            }

            @Override
            public List<Department> getAll() {
                return alldepartments;
            }

            @Override
            public Department save(Department department) {
                try {
                    for (Department dep : alldepartments) {
                        alldepartments.remove(dep);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                return department;
            }

            @Override
            public void delete(Department department) {
                try {
                    alldepartments.remove(department);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }
}
