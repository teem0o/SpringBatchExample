package io.spring.batch.model;

import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;


import java.util.Date;

public class CustomerExcelRowMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(RowSet rowSet) throws Exception {
        Customer customer = new Customer();

        customer.setId(Math.round(Float.parseFloat(rowSet.getColumnValue(0))));
        customer.setFirstName(rowSet.getColumnValue(1));
        customer.setLastName(rowSet.getColumnValue(2));
        customer.setBirthdate(new Date(Long.parseLong(rowSet.getColumnValue(3))));


        return customer;
    }
}