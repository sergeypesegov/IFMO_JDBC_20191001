package com.efimchick.ifmo.web.jdbc;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        throw new UnsupportedOperationException();
        SetMapper<Set<Employee>> result = new SetMapper<Set<Employee>>() {
            @Override
            public Set<Employee> mapSetEmployees(ResultSet rs) {
                Set<Employee> list = new HashSet<Employee>();
                try {
                    while (rs.next()) {
                        list.add(mapRow(rs));
                    }
                } catch (SQLException e) {
                    return null;
                }
                return list;
            }
        };
        return result;

        private Employee mapRow(ResultSet rs) {
            try {
                BigInteger id = new BigInteger(rs.getString("ID"));
                FullName fullname = new FullName(rs.getString("FIRSTNAME"),
                        rs.getString("LASTNAME"),
                        rs.getString("MIDDLENAME"));
                Position position = Position.valueOf(rs.getString("POSITION"));
                //BigInteger manager = BigInteger.valueOf(rs.getInt("MANAGER"));
                LocalDate hireDate = LocalDate.parse(rs.getString("HIREDATE"));
                BigDecimal salary = rs.getBigDecimal("SALARY");
                //BigInteger department = BigInteger.valueOf(rs.getInt("DEPARTMENT"));

                Employee manager = null;

                if (rs.getString("MANAGER") != null) {
                    BigInteger managerId = new BigInteger(rs.getString("MANAGER"));
                    int current = rs.getRow();
                    rs.beforeFirst();
                    while (manager == null && rs.next()) {
                        if (rs.getInt("ID") == managerId) {
                            manager = mapRow(rs);
                        }
                    }
                    rs.absolute(current);
                }

                Employee employee = new Employee(id, fullname, position, hireDate, salary, manager);
                return employee;
            }

            catch (SQLException e) {
                return null;
            }
    }

