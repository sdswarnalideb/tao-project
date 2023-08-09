package com.tao.test.domain;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Entity(name = "approval_queue")
@Getter
@Setter
@NoArgsConstructor
public class ApprovalQueue {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(type = "uuid-char")
	UUID approvalId;
	@Type(type = "uuid-char")
	UUID productId;
	Date requestDate;

	public ApprovalQueue(UUID productId) {
		this.requestDate = new Date();
		this.productId = productId;
	}

}
