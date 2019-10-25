package com.efimchick.ifmo.web.jdbc;

/**
 * Implement sql queries like described
 */
public class SqlQueries {
    //Select all employees sorted by last name in ascending order
    //language=HSQLDB
    public String select01 = "SELECT * FROM EMPLOYEE ORDER BY lastname";

    //Select employees having no more than 5 characters in last name sorted by last name in ascending order
    //language=HSQLDB
    public String select02 = "SELECT * FROM EMPLOYEE WHERE LENGTH(lastname) <= 5 ORDER BY lastname";

    //Select employees having salary no less than 2000 and no more than 3000
    //language=HSQLDB
    public String select03 = "SELECT * FROM EMPLOYEE WHERE salary >= 2000 AND salary <= 3000";

    //Select employees having salary no more than 2000 or no less than 3000
    //language=HSQLDB
    public String select04 = "SELECT * FROM EMPLOYEE WHERE salary <= 2000 OR salary >= 3000";

    //Select employees assigned to a department and corresponding department name
    //language=HSQLDB
    public String select05 = "SELECT * FROM EMPLOYEE employees INNER JOIN DEPARTMENT dep ON employees.DEPARTMENT = dep.ID";

    //Select all employees and corresponding department name if there is one.
    //Name column containing name of the department "depname".
    //language=HSQLDB
    public String select06 = "SELECT EMPLOYEE.*, DEPARTMENT.NAME as depname "+
            "FROM EMPLOYEE LEFT OUTER JOIN DEPARTMENT ON EMPLOYEE.DEPARTMENT = DEPARTMENT.ID";

    //Select total salary pf all employees. Name it "total".
    //language=HSQLDB
    public String select07 = "SELECT SUM(salary) AS total FROM EMPLOYEE";

    //Select all departments and amount of employees assigned per department
    //Name column containing name of the department "depname".
    //Name column containing employee amount "staff_size".
    //language=HSQLDB

    public String select08 = "SELECT DEPARTMENT.name AS depname, staff_size FROM DEPARTMENT LEFT JOIN (SELECT COUNT(*) AS staff_size, " +
            "DEPARTMENT FROM EMPLOYEE GROUP BY DEPARTMENT) AS amount ON amount.department = department.id  WHERE staff_size > 0";

    //Select all departments and values of total and average salary per department
    //Name column containing name of the department "depname".
    //language=HSQLDB
    public String select09 = "SELECT DEPARTMENT.name AS depname, SUM(EMPLOYEE.salary) AS total, AVG(EMPLOYEE.salary) AS average " +
            "FROM DEPARTMENT JOIN EMPLOYEE ON EMPLOYEE.department = DEPARTMENT.id " +
            "GROUP BY depname ORDER BY total DESC";

    //Select all employees and their managers if there is one.
    //Name column containing employee lastname "employee".
    //Name column containing manager lastname "manager".
    //language=HSQLDB

    public String select10 = "SELECT empl.lastname AS employee, empl2.lastname AS manager " +
            "FROM employee empl LEFT JOIN employee empl2 ON empl.manager = empl2.id";
}