package com.oops.library.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oops.library.entity.BorrowLog;
import com.oops.library.repository.BorrowLogRepository;

@Service
public class BorrowLogService {
	
	@Autowired
	private BorrowLogRepository borrowRepo;

	public void saveBorrowLog(BorrowLog borrowLog) {
		borrowRepo.save(borrowLog);
		
	}
	
	public List<BorrowLog> findAll() {
		// Implementation to fetch all borrow logs
		return borrowRepo.findAll(); // Replace with actual repository call
	}

}
