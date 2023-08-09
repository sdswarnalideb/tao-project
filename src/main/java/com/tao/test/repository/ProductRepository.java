package com.tao.test.repository;

import com.tao.test.domain.Product;
import com.tao.test.domain.enums.Status;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
	Collection<Product> findByStatusOrderByCreatedOnDesc(Status active, PageRequest pageable);
	@Query("select u from product u where u.name like %?1% and u.price between ?2 and ?3 and u.createdOn between ?4 and ?5 and u.status = ?6 order by u.createdOn desc")
	Collection<Product> filterSearch(String productName, double minPrice, double maxPrice,
			Date minPostedDate, Date maxPostedDate, Status active, PageRequest pageable);
	Optional<Product> findByProductId(UUID productId);
}
