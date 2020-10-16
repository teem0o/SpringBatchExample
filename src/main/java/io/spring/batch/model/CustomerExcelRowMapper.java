package io.spring.batch.model;

import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;

import java.text.SimpleDateFormat;

public class CustomerExcelRowMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(RowSet rowSet) throws Exception {
        Customer student = new Customer();

        student.setId(Math.round(Float.parseFloat(rowSet.getColumnValue(0))));
        student.setFirstName(rowSet.getColumnValue(1));
        student.setLastName(rowSet.getColumnValue(2));
        student.setBirthdate(null);


        return student;
    }
}