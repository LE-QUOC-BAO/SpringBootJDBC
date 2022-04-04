package com.demo.sbjdbc.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.demo.sbjdbc.exception.BankTransactionException;
import com.demo.sbjdbc.mapper.BankAccountMapper;
import com.demo.sbjdbc.model.BankAccountInfo;

@Repository
@Transactional
public class BankAccountDAO extends JdbcDaoSupport {

	@Autowired
	public BankAccountDAO(DataSource dataSource) {
		this.setDataSource(dataSource);
	}
	
	public List<BankAccountInfo> getBankAccounts(){
		
		String sql = BankAccountMapper.BASE_SQL;
		
		Object[] params = new Object[] {};
		BankAccountMapper mapper = new BankAccountMapper();
		List<BankAccountInfo> list = this.getJdbcTemplate().query(sql, params, mapper);
		
		return list;
	}
	
	public BankAccountInfo findBankAccount(Long id) {
		
		String sql = BankAccountMapper.BASE_SQL + " where ID = ? ";
		
		Object[] params = new Object[] {id};
		BankAccountMapper mapper = new BankAccountMapper();
		try {
			BankAccountInfo bankAccount = this.getJdbcTemplate().queryForObject(sql, params, mapper);
			return bankAccount;
		} catch (EmptyResultDataAccessException e) {
			// TODO: handle exception
			return null;
		}
	}
	
	//Mandatory: The required transaction must be made available in advance.
	@Transactional(propagation = Propagation.MANDATORY)
	public void addAmount(Long id, double amount) throws BankTransactionException {
		
		BankAccountInfo accountInfo = this.findBankAccount(id);
		
		if (accountInfo == null) {
			throw new BankTransactionException("Account not found " + id);
		}
		
		double newBalance = accountInfo.getBalance() + amount;
		if (accountInfo.getBalance() + amount < 0) {
			throw new BankTransactionException("The money in the account '" + id + "' is not enough (" + accountInfo.getBalance() + ")");
		}
		
		accountInfo.setBalance(newBalance);
		
		//Update to database
		String sqlUpdate = "update BANK_ACCOUNT set BALANCE = ? where ID = ?";
		this.getJdbcTemplate().update(sqlUpdate, accountInfo.getBalance(), accountInfo.getId());
	}
	
	//BankTransactionException must not be caught in this method.
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = BankTransactionException.class)
	public void sendMoney(Long fromAccountId, Long toAccountId, double amount) throws BankTransactionException {
		
		addAmount(toAccountId, amount);
		addAmount(fromAccountId, -amount);
	}
	
}
