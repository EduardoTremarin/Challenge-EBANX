package com.ebanx.challenge.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ebanx.challenge.dto.Event;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void resetState() throws Exception {
		this.mockMvc.perform(post("/reset"))
			.andDo(print())
			.andExpect(status().isOk());
	}
	
	@Test
	public void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
		this.mockMvc.perform(get("/balance/123456"))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

    @Test
    public void transferFromNonExistingAccount() throws Exception {
    	Event event = new Event();
		event.setType("transfer");
		event.setAmount(15);
		event.setOrigin("200");
		event.setDestination("300");

		ObjectMapper objectMapper = new ObjectMapper();
		String eventJson = objectMapper.writeValueAsString(event);

        this.mockMvc.perform(post("/event")
            .contentType(MediaType.APPLICATION_JSON)
            .content(eventJson))
            .andDo(print())
            .andExpect(status().isNotFound());
    }
}
