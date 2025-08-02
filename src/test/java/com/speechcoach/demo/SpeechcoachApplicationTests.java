package com.speechcoach.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speechcoach.demo.DTOs.CreateUserDto;
import com.speechcoach.demo.Entities.UserEntity;
import com.speechcoach.demo.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes= SpeechcoachApplication.class, properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
class SpeechcoachApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ObjectMapper objectMapper;



	@BeforeAll
	void clearData(){
		userRepository.deleteAll();
	}

	@Test
	void testThatUserIsCreated() throws Exception{
		CreateUserDto createUserDto = new CreateUserDto("brady"
		,"bvo13","password");
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createUserDto))).andExpect(status().isCreated());
		Optional<UserEntity> user =userRepository.findByUsername(createUserDto.getUsername());
		assertTrue(user.isPresent(),"user not found in database");

	}

}
