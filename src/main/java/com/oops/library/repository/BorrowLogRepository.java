package com.oops.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.oops.library.entity.*;

@Repository
public interface BorrowLogRepository extends JpaRepository<BorrowLog,Long>{
	
	

}
