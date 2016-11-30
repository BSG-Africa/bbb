package za.co.bsg.controllers;

import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import za.co.bsg.controller.HomeController;
import za.co.bsg.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


public class HomeControllerTest {

    private MockHttpSession mockHttpSession;
    private MockMvc mockMvc;
    private UserRepository userDAO;

    @Test
    public void indexPageIsLoadedCorrectly() throws Exception {
        setUpFixture();
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    private void setUpFixture() {
        mockMvc = standaloneSetup(new HomeController())
                .setViewResolvers(getInternalResourceViewResolver()).build();
    }

    private InternalResourceViewResolver getInternalResourceViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setSuffix(".html");
        return viewResolver;
    }
}