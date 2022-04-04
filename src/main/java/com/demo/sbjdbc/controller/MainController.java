package com.demo.sbjdbc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.demo.sbjdbc.dao.BankAccountDAO;
import com.demo.sbjdbc.exception.BankTransactionException;
import com.demo.sbjdbc.form.SendMoneyForm;
import com.demo.sbjdbc.model.BankAccountInfo;

@Controller
public class MainController {

	@Autowired
	private BankAccountDAO bankAccountDAO;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showBankAccounts(Model model) {
		
		List<BankAccountInfo> list = bankAccountDAO.getBankAccounts();
		model.addAttribute("accountInfo", list);
		
		return "accountsPage";
	}
	
	@RequestMapping(value = "/sendMoney", method = RequestMethod.GET)
	public String viewSendMoneyPage(Model model) {
		
		SendMoneyForm form = new SendMoneyForm(1L, 2L, 700d);
		
		model.addAttribute("sendMoneyForm", form);
		
		return "sendMoneyPage";
	}
	
	@RequestMapping(value = "/sendMoney", method = RequestMethod.POST)
	public String processSendMoney(Model model, SendMoneyForm sendMoneyForm) {
		
		System.out.println("Send Money: " + sendMoneyForm.getAmount());
		
		try {
			bankAccountDAO.sendMoney(sendMoneyForm.getFromAccount(), sendMoneyForm.getToAccount() , sendMoneyForm.getAmount());
		} catch (BankTransactionException e) {
			model.addAttribute("errorMessage", "Error: " + e.getMessage());
			
			return "/sendMoneyPage";
		}
		
		return "redirect:/";
	}
}
