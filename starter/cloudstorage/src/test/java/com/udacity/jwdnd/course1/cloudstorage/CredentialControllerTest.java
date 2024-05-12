package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class CredentialControllerTest {
    private WebDriver driver;
    public CredentialControllerTest() {

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
        //register new user
        driver.findElement(By.id("inputFirstName")).sendKeys("John");
        driver.findElement(By.id("inputLastName")).sendKeys("Connor");
        driver.findElement(By.id("inputUsername")).sendKeys("ResistanceLeader");
        driver.findElement(By.id("inputPassword")).sendKeys("Connor123456@2024");
        driver.findElement(By.id("buttonSignUp")).click();
        List<WebElement> errorMsg = driver.findElements(By.className("alert"));
        driver.findElement(By.id("to-login-page")).click();
        if(!errorMsg.isEmpty()) {
            driver.findElement(By.id("inputUsername")).clear();
            driver.findElement(By.id("inputPassword")).clear();
            driver.findElement(By.id("inputUsername")).sendKeys("ResistanceLeader");
            driver.findElement(By.id("inputPassword")).sendKeys("Connor123456@2024");
            driver.findElement(By.id("login-button")).click();
        } else {
            driver.findElement(By.id("inputUsername")).clear();
            driver.findElement(By.id("inputPassword")).clear();
            driver.findElement(By.id("inputUsername")).sendKeys("ResistanceLeader");
            driver.findElement(By.id("inputPassword")).sendKeys("Connor123456@2024");
            driver.findElement(By.id("login-button")).click();
        }
        driver.findElement(By.id("nav-credentials-tab")).click();
        driver.findElements(By.cssSelector(".delete-credential-button")).forEach(webElement -> {
            webElement.click();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            driver.findElement(By.cssSelector("#close-modal")).click();
        });
    }

    @AfterEach
    public void closeWebDriver() {
        driver.quit();
    }

    @Test
    public void check_if_credential_is_created_successfully() throws InterruptedException {
        createCredentialAction(false);
        assertTrue(driver.findElement(By.className("modal")).findElements(By.className("text-danger")).isEmpty());
        WebElement expectedRow = driver.findElement(By.cssSelector("#credentialTable tbody tr"));
        assertEquals("http://localhost:8080/login", expectedRow.findElement(By.cssSelector(".url")).getText());
        assertEquals("ResistanceLeader", expectedRow.findElement(By.cssSelector(".username")).getText());
        //check if created password is encrypted
        assertNotEquals("Connor123456@2024", expectedRow.findElement(By.cssSelector(".password div")).getText());
    }

    @Test
    public void check_if_note_credential_is_cancel() throws InterruptedException {
        createCredentialAction(true);
        assertTrue(driver.findElement(By.className("modal")).findElements(By.className("text-danger")).isEmpty());
        List<WebElement> expectedRowList = driver.findElements(By.cssSelector("#credentialTable tbody tr"));
        assertTrue(expectedRowList.isEmpty());
    }

    @Test
    public void check_if_credential_update_is_successful() throws InterruptedException {
        createCredentialAction(false);
        updateCredentialAction(false);
        assertTrue(driver.findElement(By.className("modal")).findElements(By.className("text-danger")).isEmpty());
        List<WebElement> expectedRowList = driver.findElements(By.cssSelector("#credentialTable tbody tr"));
        assertEquals("http://localhost:8080/signup", expectedRowList.get(0).findElement(By.cssSelector(".url")).getText());
        assertEquals("test", expectedRowList.get(0).findElement(By.cssSelector(".username")).getText());
        //check if updated password is encrypted
        assertNotEquals("test", expectedRowList.get(0).findElement(By.cssSelector(".password div")).getText());
    }

    @Test
    public void check_if_credential_delete_is_successful() throws InterruptedException {
        createCredentialAction(false);
        String rowId = deleteCredentialAction();
        assertTrue(driver.findElement(By.className("modal")).findElements(By.className("text-danger")).isEmpty());
        List<WebElement> expectedRowList = driver.findElements(By.cssSelector("#credentialTable tbody tr"));
        boolean isRowNotExist  = expectedRowList.stream().noneMatch(webElement -> Objects.equals(webElement.getAttribute("id"), rowId));
        assertTrue(isRowNotExist);
    }

    private void createCredentialAction(boolean isCancel) throws InterruptedException {
        WebElement createCredentialButton = driver.findElement(By.id("create-credential-button"));
        createCredentialButton.click();
        Thread.sleep(500);
        driver.findElement(By.id("credential-url")).sendKeys("http://localhost:8080/login");
        driver.findElement(By.id("credential-username")).sendKeys("ResistanceLeader");
        driver.findElement(By.id("credential-password")).sendKeys("Connor123456@2024");
        if (!isCancel)
        {
            driver.findElement(By.id("save-credential-changes")).click();
        } else {
            driver.findElement(By.id("cancel-credential-changes")).click();
        }
    }

    private void updateCredentialAction(boolean isCancel) throws InterruptedException {
        List<WebElement> expectedRowList = driver.findElements(By.cssSelector("#credentialTable tbody tr"));
        driver.findElement(By.cssSelector("#close-modal")).click();
        expectedRowList.get(0).findElement(By.cssSelector(".edit-credential-button")).click();
        Thread.sleep(500);
        //check if editing password is unencrypted
        assertEquals("Connor123456@2024", driver.findElement(By.id("credential-password")).getAttribute("value"));
        driver.findElement(By.id("credential-url")).clear();
        driver.findElement(By.id("credential-username")).clear();
        driver.findElement(By.id("credential-password")).clear();
        driver.findElement(By.id("credential-url")).sendKeys("http://localhost:8080/signup");
        driver.findElement(By.id("credential-username")).sendKeys("test");
        driver.findElement(By.id("credential-password")).sendKeys("test");
        if (!isCancel)
        {
            driver.findElement(By.id("save-credential-changes")).click();
        } else {
            driver.findElement(By.id("cancel-credential-changes")).click();
        }
    }

    private String deleteCredentialAction() throws InterruptedException {
        List<WebElement> expectedRowList = driver.findElements(By.cssSelector("#credentialTable tbody tr"));
        WebElement expectedRow = expectedRowList.get(0);
        driver.findElement(By.cssSelector("#close-modal")).click();
        String rowId = expectedRow.getAttribute("id");
        expectedRow.findElement(By.cssSelector(".delete-credential-button")).click();
        return rowId;
    }
}
