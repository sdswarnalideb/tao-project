package com.tao.test.domain;

import com.tao.test.domain.dto.ProductUpdateDTO;
import com.tao.test.domain.enums.Status;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Entity(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(type = "uuid-char")
	@Column(name = "product_id")
	UUID productId;
	String name;
	double price;
	Status status;
	Date createdOn;
	Date updatedOn;

	public Product(String name, double price, Status status) {
		this.name = name;
		this.price = price;
		this.status = status;
		this.createdOn = new Date();
		this.updatedOn = new Date();
	}

	public Product updateFromDTO(ProductUpdateDTO productUpdateDTO) {
		this.name = productUpdateDTO.getName();
		this.price = productUpdateDTO.getPrice();
		this.status = productUpdateDTO.getStatus();
		this.createdOn = new Date();
		this.updatedOn = new Date();

		return this;
	}
}
