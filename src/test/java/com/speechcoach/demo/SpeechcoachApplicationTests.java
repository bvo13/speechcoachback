package com.speechcoach.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speechcoach.demo.DTOs.CreateUserDto;
import com.speechcoach.demo.Entities.UserEntity;
import com.speechcoach.demo.Repositories.UserRepository;
import com.speechcoach.demo.Services.JwtService;
import com.speechcoach.demo.Util.LoginRequest;
import com.speechcoach.demo.Util.TokenExtractor;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
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

	@Autowired
	private TokenExtractor tokenExtractor;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;


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

	@Test
	void testThatValidJwtIsGenerated() throws Exception{

		CreateUserDto createUserDto = new CreateUserDto("brady", "bvo13", "password");
		MvcResult mvcResult= mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createUserDto))).andExpect(status().isOk())
				.andExpect(cookie().exists("access_token")).andExpect(cookie().exists("refresh_token")).andReturn();

		Cookie cookie = mvcResult.getResponse().getCookie("access_token");
		String jwt = cookie.getValue();

		UserDetails userDetails = userDetailsService.loadUserByUsername("bvo13");

		boolean valid = jwtService.isTokenValid(jwt, userDetails);

		assertTrue(valid, "Access token should be valid for user");
	}

	@Test
	void testRequestDeniedWithInvalidJwt() throws Exception{

		String invalidJwt = "blah";
		Cookie cookie = new Cookie("fake_token", invalidJwt);

		mockMvc.perform(get("/users/me").cookie(cookie)).andExpect(status().isUnauthorized());

	}
	@Test
	void testLoginIssuesValidJwt() throws Exception{
		UserEntity user = new UserEntity("jake","jacob",passwordEncoder.encode("pd"));
		user.setRole(Role.USER);
		userRepository.save(user);
		LoginRequest loginRequest = new LoginRequest("jacob", "pd");
		MvcResult mvcResult= mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest))).andExpect(status().isOk())
				.andExpect(cookie().exists("access_token")).andExpect(cookie().exists("refresh_token")).andReturn();

		Cookie cookie = mvcResult.getResponse().getCookie("access_token");
		String jwt = cookie.getValue();

		UserDetails userDetails = userDetailsService.loadUserByUsername("jacob");

		boolean valid = jwtService.isTokenValid(jwt, userDetails);

		assertTrue(valid, "Access token should be valid for user");
	}

	@Test
	void testThatLogoutRevokesAccess() throws Exception{
		UserEntity user = new UserEntity("jake","jacob",passwordEncoder.encode("pd"));
		user.setRole(Role.USER);
		userRepository.save(user);
		LoginRequest loginRequest = new LoginRequest("jacob", "pd");
		MvcResult mvcResult= mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest))).andReturn();
		Cookie accessCookie = mvcResult.getResponse().getCookie("access_token");
		Cookie refreshCookie = mvcResult.getResponse().getCookie("refresh_token");

		MvcResult mvcResult2 = mockMvc.perform(post("/users/me/logout")
				.cookie(accessCookie).cookie(refreshCookie)).andExpect(status().isOk()).andReturn();
		Cookie accessCookie2 = mvcResult2.getResponse().getCookie("access_token");
		Cookie refreshCookie2 = mvcResult2.getResponse().getCookie("refresh_token");
		mockMvc.perform(get("/users/me").cookie(accessCookie2).cookie(refreshCookie2)).andExpect(status().isUnauthorized());
	}

}
