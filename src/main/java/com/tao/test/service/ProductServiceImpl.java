package com.tao.test.service;

import com.tao.test.domain.ApprovalQueue;
import com.tao.test.domain.Product;
import com.tao.test.domain.dto.ProductUpdateDTO;
import com.tao.test.domain.enums.Status;
import com.tao.test.exception.ResourceNotFoundException;
import com.tao.test.repository.ApprovalQueueRepository;
import com.tao.test.repository.ProductRepository;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final ApprovalQueueRepository approvalQueueRepository;
	private final double maxAutoApprovedPrice;

	@Autowired
	public ProductServiceImpl(ProductRepository productRepository,
			ApprovalQueueRepository approvalQueueRepository,@Value("${app.max.auto-approve.price}") double maxAutoApprovedPrice) {
		this.productRepository = productRepository;
		this.approvalQueueRepository = approvalQueueRepository;
		this.maxAutoApprovedPrice = maxAutoApprovedPrice;
	}

	@Override
	public Collection<Product> fetchAllActiveProducts(int pageNumber, int pageSize) {
		return this.productRepository.findByStatusOrderByCreatedOnDesc(Status.ACTIVE,
				PageRequest.of(pageNumber - 1, pageSize));
	}

	@Override
	public Collection<Product> searchProducts(String productName, double minPrice, double maxPrice,
			Date minPostedDate, Date maxPostedDate, int pageNumber, int pageSize) {
		return this.productRepository.filterSearch(productName, minPrice, maxPrice, minPostedDate,
				maxPostedDate, Status.ACTIVE,
				PageRequest.of(pageNumber - 1, pageSize));
	}

	@Override
	public Product createProduct(ProductUpdateDTO product) {
		Product p;
		if (isApprovalRequired(product)) {
			product.setStatus(Status.PENDING_APPROVAL);
			p = this.productRepository.save(
					new Product(product.getName(), product.getPrice(), product.getStatus()));
			this.approvalQueueRepository.save(new ApprovalQueue(p.getProductId()));
		} else {
			p = this.productRepository.save(
					new Product(product.getName(), product.getPrice(), product.getStatus()));
		}
		return p;
	}

	private boolean isApprovalRequired(ProductUpdateDTO product){
		return product.getPrice() > maxAutoApprovedPrice;
	}

	@Override
	public Product updateProduct(String productId, ProductUpdateDTO productDto) {
		Optional<Product> product = this.productRepository.findByProductId(
				UUID.fromString(productId));
		if (product.isPresent()) {
			// Checking if the price is more than 50% of its previous price, the product should be pushed to the approval queue.
			if (product.get().getPrice() + (product.get().getPrice() / 2) <  productDto.getPrice()) {
				productDto.setStatus(Status.PENDING_APPROVAL);
				Optional<ApprovalQueue> apProduct = this.approvalQueueRepository.findByProductId(
						UUID.fromString(productId));
				if (!apProduct.isPresent()) {
					this.approvalQueueRepository.save(
							new ApprovalQueue(UUID.fromString(productId)));
				}
			}
			return this.productRepository.save(product.get().updateFromDTO(productDto));
		} else {
			throw new ResourceNotFoundException("Invalid Product Id : " + productId);
		}
	}

	@Override
	public String deleteProduct(String productId) {
		Optional<Product> product = this.productRepository.findByProductId(
				UUID.fromString(productId));
		if (product.isPresent()) {
			Optional<ApprovalQueue> apProduct = this.approvalQueueRepository.findByProductId(
					UUID.fromString(productId));
			if (!apProduct.isPresent()) {
				this.approvalQueueRepository.save(new ApprovalQueue(UUID.fromString(productId)));
			}
			product.get().setStatus(Status.INACTIVE);
			this.productRepository.save(product.get());
			return "Product Deleted Successfully";
		} else {
			throw new ResourceNotFoundException("Invalid Product Id : " + productId);
		}
	}

	@Override
	public Collection<Product> fetchApprovalQueue(int pageNumber, int pageSize) {
		return this.approvalQueueRepository.findAllApprovalQueue(
				PageRequest.of(pageNumber - 1, pageSize));
	}

	@Override
	@Transactional
	public Product approveProduct(String approvalId) {
		Optional<ApprovalQueue> apProduct = this.approvalQueueRepository.findByApprovalId(
				UUID.fromString(approvalId));
		if (apProduct.isPresent()) {
			Optional<Product> product = this.productRepository.findByProductId(
					apProduct.get().getProductId());
			if (product.isPresent()) {
				product.get().setStatus(Status.ACTIVE);
				this.approvalQueueRepository.deleteAllByApprovalId(UUID.fromString(approvalId));
				return this.productRepository.save(product.get());
			}
			throw new ResourceNotFoundException(
					"Product Not Found : " + apProduct.get().getProductId());
		}
		throw new ResourceNotFoundException("Invalid Approval Id : " + approvalId);
	}

	@Override
	@Transactional
	public Product rejectProduct(String approvalId) {
		Optional<ApprovalQueue> apProduct = this.approvalQueueRepository.findByApprovalId(
				UUID.fromString(approvalId));
		if (apProduct.isPresent()) {
			Optional<Product> product = this.productRepository.findByProductId(
					apProduct.get().getProductId());
			if (product.isPresent()) {
				product.get().setStatus(Status.REJECTED);
				this.approvalQueueRepository.deleteAllByApprovalId(UUID.fromString(approvalId));
				return this.productRepository.save(product.get());
			}
			throw new ResourceNotFoundException(
					"Product Not Found : " + apProduct.get().getProductId());
		}
		throw new ResourceNotFoundException("Invalid Approval Id : " + approvalId);
	}
}
