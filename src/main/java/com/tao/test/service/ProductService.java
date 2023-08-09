package com.tao.test.service;

import com.tao.test.domain.Product;
import com.tao.test.domain.dto.ProductUpdateDTO;
import java.util.Collection;
import java.util.Date;

public interface ProductService {

	Collection<Product> fetchAllActiveProducts(int pageNumber, int pageSize);

	Collection<Product> searchProducts(String productName, double minPrice, double maxPrice,
			Date minPostedDate, Date maxPostedDate, int pageNumber, int pageSize);

	Product createProduct(ProductUpdateDTO product);

	Product updateProduct(String productId, ProductUpdateDTO product);

	String deleteProduct(String productId);

	Collection<Product> fetchApprovalQueue(int pageNumber, int pageSize);

	Product approveProduct(String approvalId);

	Product rejectProduct(String approvalId);
}
