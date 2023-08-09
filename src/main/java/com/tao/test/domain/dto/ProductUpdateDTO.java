package com.tao.test.domain.dto;

import com.tao.test.domain.enums.Status;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductUpdateDTO {

	@NotBlank(message = "Please enter a Name for the Product") String name;
	@NotNull(message = "Please enter Price for the Product")
	@Max(10000)
	double price;
	@NotNull(message = "Please enter Status for the Product") Status status;
}
