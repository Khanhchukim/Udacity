package com.udacity.pricing.api;

import com.udacity.pricing.service.PricingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class PricingControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    PricingService priceService;

    @Test
    public void should_GetPriceSuccessfully_When_VehicleIdFound() throws Exception {
        // PriceService generates PRICES map constant that contains 20 objects
        mvc.perform(get(new URI("/services/price"))
                .param("vehicleId", String.valueOf(1)))
                .andExpect(status().isOk());
    }

    @Test
    public void should_GetPriceException_When_VehicleIdNotFound() throws Exception {
        // PriceService generates PRICES map constant that contains 20 objects
        // So it should return 404 Error Code when get Price for vehicleId >= 21
        mvc.perform(get(new URI("/services/price"))
                        .param("vehicleId", String.valueOf(21)))
                .andExpect(status().is4xxClientError());
    }
}
