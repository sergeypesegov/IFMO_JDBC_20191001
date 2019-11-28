package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DaoFactory {

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

    public List<Employee> mapSet(ResultSet res) throws SQLException {
        res.beforeFirst();
        List<Employee> emp = new ArrayList<>();
        try {
            while (res.next()) {
                emp.add(mapRow(res));
            }
        } catch (SQLException ignore) {

            return null;
        }
        return emp;
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
    private ResultSet getResultSet(String SQL) throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return statement.executeQuery(SQL);
    }

    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                try {
                    ResultSet res = getResultSet ("SELECT * FROM employee WHERE department = " + department.getId());
                    return mapSet(res);
                }
                catch(SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                try {
                    ResultSet res = getResultSet("SELECT * FROM employee WHERE manager = " + employee.getId());
                    return mapSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                try {
                    ResultSet resultSet = getResultSet(
                            "SELECT * FROM employee WHERE id = "
                                    + Id.toString());
                    if (resultSet.next()) {
                        List<Employee> emp = mapSet(resultSet);
                        return Optional.of(emp.get(0));
                    }
                    else
                        return Optional.empty();
                }
                catch (SQLException e) {
                    return Optional.empty();
                }
            }


            @Override
            public List<Employee> getAll() {
                try {
                    return mapSet(getResultSet("SELECT * FROM employee"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Employee save(Employee employee) {
                try {
                    getResultSet(
                            "INSERT INTO employee VALUES ('"
                                    + employee.getId()                       + "', '"
                                    + employee.getFullName().getFirstName()  + "', '"
                                    + employee.getFullName().getLastName()   + "', '"
                                    + employee.getFullName().getMiddleName() + "', '"
                                    + employee.getPosition()                 + "', '"
                                    + employee.getManagerId()                + "', '"
                                    + Date.valueOf(employee.getHired())      + "', '"
                                    + employee.getSalary()                   + "', '"
                                    + employee.getDepartmentId()             + "')"
                    );
                    return employee;
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void delete(Employee employee) {
                try {
                    ConnectionSource.instance().createConnection().createStatement().execute(
                            "DELETE FROM employee WHERE ID = " + employee.getId().toString());
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public DepartmentDao departmentDAO() {
        //throw new UnsupportedOperationException();
        return new DepartmentDao() {

            private List<Department> getDepartments(ResultSet resultSet) throws SQLException {
                resultSet.beforeFirst();
                List<Department> departments = new ArrayList<>();
                try {
                    while (resultSet.next()) {
                        departments.add(
                                new Department(
                                        new BigInteger(resultSet.getString("ID")),
                                        resultSet.getString("NAME"),
                                        resultSet.getString("LOCATION")
                                )
                        );
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return departments;
            }

            @Override
            public Optional<Department> getById(BigInteger Id) {
                try {
                    ResultSet res = getResultSet("SELECT * FROM department WHERE id = " + Id);
                    if (res.next() != true) {
                        return null;
                    } else {
                        List<Department> dep = getDepartments(res);
                        return Optional.of(dep.get(0));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Department> getAll() {
                try {
                    return getDepartments(getResultSet("SELECT * FROM department"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Department save(Department department) {
                try {
                    if (getById(department.getId()).equals(Optional.empty())) {
                        getResultSet(
                                "INSERT INTO department VALUES ('" +
                                        department.getId()       + "', '" +
                                        department.getName()     + "', '" +
                                        department.getLocation() + "')"
                        );
                    } else {
                        getResultSet(
                                "UPDATE department SET " +
                                        "NAME = '"     + department.getName()     + "', " +
                                        "LOCATION = '" + department.getLocation() + "' " +
                                        "WHERE ID = '" + department.getId()       + "'"
                        );
                    }
                    return department;
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void delete(Department department) {
                try {
                    ConnectionSource.instance().createConnection().createStatement().execute(
                            "DELETE FROM department WHERE ID = " + department.getId().toString());
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}