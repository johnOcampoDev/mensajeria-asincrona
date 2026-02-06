package com.company.messagegateway.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "messages_db.authorized_origins")
@Data
public class AuthorizedOrigin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "origin_code", nullable = false, unique = true)
	private String originCode;

	private String description;

	private boolean enabled = true;
}