package com.demo.sbjdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.demo.sbjdbc.model.BankAccountInfo;

public class BankAccountMapper implements RowMapper<BankAccountInfo> {

	public static final String BASE_SQL = "select ba.ID, ba.FULL_NAME, ba.BALANCE from BANK_ACCOUNT ba";

	@Override
	public BankAccountInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		
		Long id = rs.getLong("ID");
		String fullName = rs.getString("FULL_NAME");
		double balance = rs.getDouble("BALANCE");
		
		return new BankAccountInfo(id, fullName, balance);
	}
	
	
	
}
