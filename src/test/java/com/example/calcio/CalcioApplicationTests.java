package com.example.calcio;

import com.example.calcio.dao.CalcioDao;
import com.example.calcio.dto.RestResponse;
import com.example.calcio.model.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		classes = CalcioApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CalcioApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private CalcioDao calcioDao;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
    @Order(1)
	public void findAllPlayers_Ok() throws Exception {
		mvc.perform(get("/api/players"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(92)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("Francesco Rossi")))
				.andExpect(jsonPath("$[0].position", is("goalkeeper")))
				.andExpect(jsonPath("$[0].clubName", is("Atalanta")));
	}

	@Test
    @Order(2)
	public void getPlayer_Ok() throws Exception {
		mvc.perform(get("/api/players/2"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(2)))
				.andExpect(jsonPath("$.name", is("Marco Sportiello")))
				.andExpect(jsonPath("$.position", is("goalkeeper")))
				.andExpect(jsonPath("$.clubName", is("Atalanta")));
	}

	@Test
    @Order(3)
	public void getPlayer_NotFound() throws Exception {
		mvc.perform(get("/api/players/102"))
				.andExpect(status().isNotFound());
	}

	@Test
    @Order(4)
	public void createPlayer_Ok() throws Exception {
		String name = "Lionel Messi";
		String position = "attacker";
		Integer clubId = 3;
		String body = getBody(name, position, clubId);
		MvcResult mvcResult = mvc.perform(post("/api/players")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andReturn();

		RestResponse response = parseResponse(mvcResult, RestResponse.class);
		int playerId = Integer.parseInt(response.getResult());
		assertThat(playerId).isGreaterThanOrEqualTo(93);

		Player player = calcioDao.getPlayerById(playerId).orElse(null);
		assertThat(player).isNotNull();
		assertThat(player.getName()).isEqualTo(name);
		assertThat(player.getPosition()).isEqualTo(position);
		assertThat(player.getClub().getId()).isEqualTo(clubId);
	}

	@Test
    @Order(5)
	public void createPlayer_emptyJson_badRequest() throws Exception {
		mvc.perform(post("/api/players")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
    @Order(6)
	public void createPlayer_emptyName_badRequest() throws Exception {
		String name = "";
		String position = "attacker";
		Integer clubId = 3;
		String body = getBody(name, position, clubId);
		mvc.perform(post("/api/players")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isBadRequest());
	}

	@Test
    @Order(7)
	public void updatePlayer_Ok() throws Exception {
		String name = "Lionel Messi";
		String position = "attacker";
		Integer clubId = 3;
		String body = getBody(name, position, clubId);
		MvcResult mvcResult = mvc.perform(put("/api/players/2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andReturn();

		Player player = calcioDao.getPlayerById(2).orElse(null);
		assertThat(player).isNotNull();
		assertThat(player.getName()).isEqualTo(name);
		assertThat(player.getPosition()).isEqualTo(position);
		assertThat(player.getClub().getId()).isEqualTo(clubId);
	}

	@Test
    @Order(8)
	public void updatePlayer_emptyName_badRequest() throws Exception {
		String name = "";
		String position = "attacker";
		Integer clubId = 3;
		String body = getBody(name, position, clubId);
		mvc.perform(put("/api/players/2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isBadRequest());
	}

	@Test
    @Order(9)
	public void updatePlayer_wrongId_notFound() throws Exception {
		String name = "Lionel Messi";
		String position = "attacker";
		Integer clubId = 3;
		String body = getBody(name, position, clubId);
		mvc.perform(put("/api/players/102")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isNotFound());
	}

	@Test
    @Order(10)
	public void deletePlayer_Ok() throws Exception {
		mvc.perform(delete("/api/players/40"))
				.andExpect(status().isOk());

		Player player = calcioDao.getPlayerById(40).orElse(null);
		assertThat(player).isNull();
	}

	@Test
    @Order(11)
	public void deletePlayer_wrongId_Ok() throws Exception {
		mvc.perform(delete("/api/players/400"))
				.andExpect(status().isNotFound());
	}

	@Test
    @Order(12)
	public void searchPlayers_Ok() throws Exception {
		String position = "midfielder";
		Integer clubId = 1;
		Integer page = 0;
		Integer size = 3;
		String body = getSearchBody(position, clubId, page, size);
		mvc.perform(post("/api/players/_search")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(3)))
				.andExpect(jsonPath("$.number", is(0)))
				.andExpect(jsonPath("$.size", is(3)))
				.andExpect(jsonPath("$.totalElements", is(10)))
				.andExpect(jsonPath("$.totalPages", is(4)));
	}

	@Test
    @Order(13)
	public void findAllClubs_Ok() throws Exception {
		mvc.perform(get("/api/clubs"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("Atalanta")));
	}

	private <T>T parseResponse(MvcResult mvcResult, Class<T> c) {
		try {
			return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), c);
		} catch (JsonProcessingException | UnsupportedEncodingException e) {
			throw new RuntimeException("Error parsing json", e);
		}
	}

	private String getBody(String name, String position, Integer clubId) {
		return String.format("{\n" +
				"\"name\": \"%s\",\n" +
				"\"position\": \"%s\",\n" +
				"\"clubId\": \"%d\"\n" +
				"}", name, position, clubId);
	}

	private String getSearchBody(String position, Integer clubId, Integer page, Integer size) {
		return String.format("{\n" +
				"\"position\": \"%s\",\n" +
				"\"clubId\": \"%d\",\n" +
				"\"page\": \"%d\",\n" +
				"\"size\": \"%d\"\n" +
				"}", position, clubId, page, size);
	}
}