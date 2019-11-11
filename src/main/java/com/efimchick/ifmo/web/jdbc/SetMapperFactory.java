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
        //throw new UnsupportedOperationException();
        return rs -> {
            Set<Employee> list = new HashSet<>();
            try {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        };
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
                //BigInteger department = BigInteger.valueOf(rs.getInt("DEPARTMENT"));

                Employee manager = null;

                if (rs.getString("MANAGER") != null) {
                    int managerId = rs.getInt("MANAGER");
                    int current = rs.getRow();
                    rs.beforeFirst();
                    while (rs.next()) {
                        if (Integer.parseInt(rs.getString("ID")) == managerId) {
                            manager = mapRow(rs);
                        }
                    }
                    rs.absolute(current);
                }

                Employee employee = new Employee(id, fullname, position, hireDate, salary, manager);
                return employee;
            } catch (SQLException e) {
                return null;
            }
        }
}
