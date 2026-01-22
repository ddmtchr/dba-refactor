package com.ddmtchr.dbarefactor.integration;

import com.ddmtchr.dbarefactor.security.entity.User;
import com.ddmtchr.dbarefactor.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EstateControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @WithMockUser(authorities = {"HOST"}, username = "host")
    @Test
    void addEstate_Success() throws Exception {
        User owner = new User("host", "pwd", "email@mail.com", 0L);
        userRepository.save(owner);

        mockMvc.perform(post("/estate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Estate",
                                  "price": 100
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Estate"));
    }

    @Test
    void addEstate_NoAuth_403() throws Exception {
        mockMvc.perform(post("/estate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void findAll_ReturnsList() throws Exception {
        mockMvc.perform(get("/estate"))
                .andExpect(status().isOk());
    }
}
