package com.baemin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baemin.dao.UserDAO;
import com.baemin.dto.Join;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDAO userDAO;

	@Override
	public void join(Join join) {
		userDAO.join(join);
		
	}

	@Override
	public int overlapCheck(String value, String valueType) {
		// TODO Auto-generated method stub
		return userDAO.overlapCheck(value, valueType);
	}

}
