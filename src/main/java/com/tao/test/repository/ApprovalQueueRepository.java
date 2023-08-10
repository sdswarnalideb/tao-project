package com.tao.test.repository;

import com.tao.test.domain.ApprovalQueue;
import com.tao.test.domain.Product;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalQueueRepository extends JpaRepository<ApprovalQueue, String> {
	Optional<ApprovalQueue> findByProductId(UUID productId);
	@Query("Select u from product u, approval_queue t where u.productId = t.productId order by t.requestDate")
	Collection<Product> findAllApprovalQueue(PageRequest of);
	Optional<ApprovalQueue> findByApprovalId(UUID approvalId);
	void deleteAllByApprovalId(UUID approvalId);
}
