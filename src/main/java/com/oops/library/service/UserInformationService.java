package com.oops.library.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oops.library.repository.UserRepository;
import com.oops.library.enchanted.exception.EnchantedLibraryException;
import com.oops.library.entity.*;
import java.util.*;


@Service
public class UserInformationService {
	
	@Autowired
	private UserRepository userRepository;
	
	public List<User> getAllRegisteredUsers() throws EnchantedLibraryException
	{
		List<User> registeredUsers=new ArrayList<>();
		registeredUsers=userRepository.findAll();
		if(registeredUsers.isEmpty()||registeredUsers==null)
		{
			throw new EnchantedLibraryException("There are no registered users at this moment");
		}
		return registeredUsers;
	}

}
