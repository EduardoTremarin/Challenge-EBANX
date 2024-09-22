package com.ebanx.challenge.dto;

import lombok.Data;

@Data
public class Event {

	private String type;
	private float amount;
	private String destination;
	private String origin;
}
