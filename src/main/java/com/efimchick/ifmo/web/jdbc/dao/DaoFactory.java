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
    private List<Employee> employees = getEmployees();
    private List<Department> allDepartments = getDepartment();

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

            BigInteger manager = BigInteger.valueOf(0);

            if (rs.getString("MANAGER") != null) {
                int managerId = rs.getInt("MANAGER");
                manager = BigInteger.valueOf(managerId);
            }

            Employee employee = new Employee(id, fullname, position, hireDate, salary, manager, department);
            return employee;
        } catch (SQLException e) {
            return null;
        }
    }

    private Department depMapRow(ResultSet rs) {
        try {
            BigInteger id = BigInteger.valueOf(rs.getInt("ID"));
            String name = rs.getString("NAME");
            String location = rs.getString("LOCATION");
            Department dep = new Department(id, name, location);
            return dep;

        } catch (SQLException e) {
            return null;
        }

    }

    private List<Department> getDepartment() {
        try {
            ResultSet rs = createConnection().createStatement().executeQuery("SELECT * FROM DEPARTMENT");
            List<Department> allDepartments = new ArrayList<>();
            while (rs.next()) {
                Department dep = depMapRow(rs);
                allDepartments.add(dep);
            }
            return allDepartments ;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public EmployeeDao employeeDAO() {
       return new EmployeeDao() {
           @Override
           public List<Employee> getByDepartment(Department department) {
               List<Employee> empByDepartment = new ArrayList<>();
               for (Employee emp : employees) {
                   if (emp.getDepartmentId().equals(department.getId())) {
                       empByDepartment.add(emp);
                   }
               }
               return empByDepartment;
           }

           @Override
           public List<Employee> getByManager(Employee employee) {
               List<Employee> empByManager = new ArrayList<>();
               for (Employee emp : employees) {
                   try {
                       if (emp.getManagerId().equals(employee.getId())) {
                           empByManager.add(emp);
                       }
                   }
                   catch (NullPointerException e) {
                       e.printStackTrace();
                   }
               }
               return empByManager;
           }

           @Override
           public Optional<Employee> getById(BigInteger Id) {
               Optional<Employee> employee = Optional.empty();
               for (Employee emp : employees) {
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
               return employees;
           }

           @Override
           public Employee save(Employee employee) {
               try {
                   employees.add(employee);
                   return employee;
               }
               catch (Exception e){
                   return null;
               }
           }

           @Override
           public void delete(Employee employee) {
               try {
                   employees.remove(employee);
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
                for (Department dep : allDepartments) {
                    if (dep.getId().equals(Id)) {
                        department = department.of(dep);
                    }
                }
                return department;
            }

            @Override
            public List<Department> getAll() {
                return allDepartments;
            }

            @Override
            public Department save(Department department) {
                try {
                    for (Department dep : allDepartments) {
                        allDepartments.remove(dep);
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
                    allDepartments.remove(department);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }
}
