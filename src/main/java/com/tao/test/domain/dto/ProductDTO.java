package com.tao.test.domain.dto;

import com.tao.test.domain.Product;
import com.tao.test.domain.enums.Status;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class ProductDTO {

	String name;
	double price;
	Status status;
	Date createdOn;
	Date updatedOn;

	public ProductDTO(Product product) {
		this.name = product.getName();
		this.price = product.getPrice();
		this.status = product.getStatus();
		this.createdOn = product.getCreatedOn();
		this.updatedOn = product.getUpdatedOn();
	}

	public static Collection<ProductDTO> convert(Collection<Product> products) {
		return products.stream().map(t -> new ProductDTO(t))
				.collect(Collectors.toList());
	}
}
