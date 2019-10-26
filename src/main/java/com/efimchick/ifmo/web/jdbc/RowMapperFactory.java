package com.efimchick.ifmo.web.jdbc;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.Position;
import com.efimchick.ifmo.web.jdbc.domain.FullName;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RowMapperFactory {
    public RowMapper<Employee> employeeRowMapper() throws SQLException {
            RowMapper<Employee> rowMapper = new RowMapper<Employee>() {
                @Override
                public Employee mapRow(ResultSet rs) {
                    try {
                        BigInteger id = BigInteger.valueOf(rs.getInt("ID"));
                        FullName fullname = new FullName(rs.getString("FIRSTNAME"),
                                rs.getString("LASTNAME"),
                                rs.getString("MIDDLENME"));
                        Position position = Position.valueOf(rs.getString("POSITION"));
                        //BigInteger manager = BigInteger.valueOf(rs.getInt("MANAGER"));
                        LocalDate hireDate = LocalDate.parse(rs.getString("HIREDATE"));
                        BigDecimal salary = rs.getBigDecimal("SALARY");
                        //BigInteger department = BigInteger.valueOf(rs.getInt("DEPARTMENT"));
                        Employee employee = new Employee(id, fullname, position, hireDate, salary);
                        return employee;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };

            return rowMapper;
    }
}
