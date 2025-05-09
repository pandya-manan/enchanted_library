package com.oops.library.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.oops.library.entity.*;

@Repository
public interface BorrowLogRepository extends JpaRepository<BorrowLog,Long>{
	
	List<BorrowLog> findByReturnedFalseAndReturnDateBefore(LocalDate today);
	
	List<BorrowLog> findByBorrowerEmailAndReturnedFalse(String email);

}
