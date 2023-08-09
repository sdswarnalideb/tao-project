package com.tao.test.controller;

import com.tao.test.domain.dto.ProductDTO;
import com.tao.test.domain.dto.ProductUpdateDTO;
import com.tao.test.exception.ResourceNotFoundException;
import com.tao.test.service.ProductService;
import java.util.Collection;
import java.util.Date;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for all the Product Related APIs
 * context root /api added in application.properties
 */
@RestController
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;

	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}


	/**
	 * API to List Active Products
	 *
	 * @param pageNumber Page number for pagination (Default: 1 and Minimum: 1)
	 * @param pageSize   Page size for pagination (Default: 10 and Minimum: 1 and Maximum: 50)
	 * @return List of Active Products
	 */
	@GetMapping()
	public ResponseEntity<Collection<ProductDTO>> fetchAllActiveProducts(
			@RequestParam(defaultValue = "1") @Min(1) int pageNumber,
			@RequestParam(defaultValue = "10") @Min(1) @Max(50) int pageSize) {
		return new ResponseEntity<>(ProductDTO.convert(
				this.productService.fetchAllActiveProducts(pageNumber, pageSize)), HttpStatus.OK);
	}

	/**
	 * API to Search Products
	 *
	 * @param productName   Name of the Product. (Optional)
	 * @param minPrice      minimum Price of the Product. (Default: 0.0)
	 * @param maxPrice      maximum Price of the Product. (Default: 1000000000000000000.0)
	 * @param minPostedDate minimum creation date of the Product. (Default: "2000-01-01T00:00")
	 * @param maxPostedDate maximum creation date of the Product. (Default: "9999-12-31T00:00")
	 * @param pageNumber    Page number for pagination (Default: 1 and Minimum: 1)
	 * @param pageSize      Page size for pagination (Default: 10 and Minimum: 1 and Maximum: 50)
	 * @return List of Active Products with filter criteria
	 */
	@GetMapping("/search")
	public ResponseEntity<Collection<ProductDTO>> searchProducts(
			@RequestParam(required = false, defaultValue = "") String productName,
			@RequestParam(required = false, defaultValue = "0.0") double minPrice,
			@RequestParam(required = false, defaultValue = "1000000000000000000.0") double maxPrice,
			@RequestParam(required = false, defaultValue = "2000-01-01T00:00") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date minPostedDate,
			@RequestParam(required = false, defaultValue = "9999-12-31T00:00") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date maxPostedDate,
			@RequestParam(defaultValue = "1") @Min(1) int pageNumber,
			@RequestParam(defaultValue = "10") @Min(1) @Max(50) int pageSize) {

		return new ResponseEntity<>(ProductDTO.convert(
				this.productService.searchProducts(productName, minPrice, maxPrice, minPostedDate,
						maxPostedDate, pageNumber, pageSize)),
				HttpStatus.OK);
	}


	/**
	 * API to Create a Product
	 *
	 * @param product ProductDTO with name, price and status
	 * @return Newly created Product
	 */
	@PostMapping()
	public ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid ProductUpdateDTO product) {
		return new ResponseEntity<>(new ProductDTO(this.productService.createProduct(product)),
				HttpStatus.OK);
	}

	/**
	 * API to Update a Product
	 *
	 * @param productId Unique Id of a Product
	 * @param product   ProductDTO with name, price and status
	 * @return updated Product
	 */
	@PutMapping("/{productId}")
	public ResponseEntity<ProductDTO> updateProduct(@PathVariable String productId,
			@RequestBody @Valid ProductUpdateDTO product) {
		try {
			return new ResponseEntity<>(
					new ProductDTO(this.productService.updateProduct(productId, product)),
					HttpStatus.OK);
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * API to Delete a Product
	 *
	 * @param productId Unique Id of a Product
	 * @return Delete message
	 */
	@DeleteMapping("/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable String productId) {
		try {
			return new ResponseEntity<>(this.productService.deleteProduct(productId),
					HttpStatus.OK);
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * API to Get Products in Approval Queue
	 *
	 * @param pageNumber Page number for pagination (Default: 1 and Minimum: 1)
	 * @param pageSize   Page size for pagination (Default: 10 and Minimum: 1 and Maximum: 50)
	 * @return List of Products in Approval Queue
	 */
	@GetMapping("/approval-queue")
	public ResponseEntity<Collection<ProductDTO>> fetchApprovalQueue(
			@RequestParam(defaultValue = "1") @Min(1) int pageNumber,
			@RequestParam(defaultValue = "10") @Min(1) @Max(50) int pageSize) {
		return new ResponseEntity<>(
				ProductDTO.convert(this.productService.fetchApprovalQueue(pageNumber, pageSize)),
				HttpStatus.OK);
	}

	/**
	 * API to Approve a Product
	 *
	 * @param approvalId Unique Id for approval request
	 * @return Active Product
	 */
	@PutMapping("/approval-queue/{approvalId}/approve")
	public ResponseEntity<ProductDTO> approveProduct(@PathVariable String approvalId) {
		try {
			return new ResponseEntity<>(
					new ProductDTO(this.productService.approveProduct(approvalId)), HttpStatus.OK);
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * API to Reject a Product
	 *
	 * @param approvalId Unique Id for approval request
	 * @return Active Product
	 */
	@PutMapping("/approval-queue/{approvalId}/reject")
	public ResponseEntity<ProductDTO> rejectProduct(@PathVariable String approvalId) {
		try {
			return new ResponseEntity<>(
					new ProductDTO(this.productService.rejectProduct(approvalId)), HttpStatus.OK);
		} catch (ResourceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
}
