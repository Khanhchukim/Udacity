package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.controllers.LoginController;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginControllerTest {
    private WebDriver driver;
    public LoginControllerTest() {

    }

    @BeforeEach
    public void initWebDriver() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/login");
        Thread.sleep(1000);
    }

    @AfterEach
    public void closeWebDriver() {
        driver.quit();
    }

    @Test
    public void check_if_unauthorized_user_can_not_access_home_page() throws InterruptedException {
        driver.get("http://localhost:8080/files");
        assertEquals("http://localhost:8080/login", driver.getCurrentUrl());
        Thread.sleep(2000);
    }

    @Test
    public void check_if_unauthorized_user_can_access_login_page() throws InterruptedException {
        assertEquals("http://localhost:8080/login", driver.getCurrentUrl());
        Thread.sleep(2000);
    }

    @Test
    public void check_if_unauthorized_user_can_access_signup_page() throws InterruptedException {
        driver.get("http://localhost:8080/signup");
        assertEquals("http://localhost:8080/signup", driver.getCurrentUrl());
        Thread.sleep(2000);
    }
}
