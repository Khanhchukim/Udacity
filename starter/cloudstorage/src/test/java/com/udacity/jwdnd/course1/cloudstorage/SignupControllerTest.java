package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes={CloudStorageApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SignupControllerTest {
    private WebDriver driver;
    public SignupControllerTest() {

    }

    @BeforeEach
    public void initWebDriver() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/login");
        driver.findElement(By.id("signup")).click();
        //clear input text
        driver.findElement(By.id("inputFirstName")).clear();
        driver.findElement(By.id("inputLastName")).clear();
        driver.findElement(By.id("inputUsername")).clear();
        driver.findElement(By.id("inputPassword")).clear();
    }

    @AfterEach
    public void closeWebDriver() {
        driver.quit();
    }

    @Test
    public void check_if_user_is_created_successfully() {
        createUserAction();
        assertEquals("http://localhost:8080/files", driver.getCurrentUrl());
    }

    @Test
    public void check_if_logout_is_successfully() {
        createUserAction();
        driver.findElement(By.cssSelector("#logout-btn")).click();
        assertEquals("http://localhost:8080/login", driver.getCurrentUrl());
        assertNotNull(driver.findElement(By.cssSelector("#logout-msg")));
    }

    public void createUserAction() {
        //register new user
        driver.findElement(By.id("inputFirstName")).sendKeys("John");
        driver.findElement(By.id("inputLastName")).sendKeys("Connor");
        driver.findElement(By.id("inputUsername")).sendKeys("ResistanceLeader");
        driver.findElement(By.id("inputPassword")).sendKeys("Connor123456@2024");
        driver.findElement(By.id("buttonSignUp")).click();
        driver.findElement(By.id("to-login-page")).click();
        driver.findElement(By.id("inputUsername")).clear();
        driver.findElement(By.id("inputPassword")).clear();
        driver.findElement(By.id("inputUsername")).sendKeys("ResistanceLeader");
        driver.findElement(By.id("inputPassword")).sendKeys("Connor123456@2024");
        driver.findElement(By.id("login-button")).click();
    }
}
