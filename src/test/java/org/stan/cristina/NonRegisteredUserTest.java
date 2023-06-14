package org.stan.cristina;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Random;
import java.util.logging.Logger;

public class NonRegisteredUserTest {

    private static final Logger logger = Logger.getLogger(NonRegisteredUserTest.class.getName());
    private static final String PASSWORD = "abc@ABC@123";
    private static final String WRONG_PASSWORD = "abc@ABC@124";
    private static String USERNAME = null;
    private static String EMAIL = null;
    private WebDriver driver = null;
    private WebElement form = null;

    @BeforeAll
    public static void generateRandomData() {
        Random rand = new Random();
        int id = rand.nextInt();
        USERNAME = "utilizator-nou" + id;
        EMAIL = "utilizator-nou-" + id + "@gmail.com";

        logger.info("Test username: " + USERNAME);
        logger.info("Test email: " + EMAIL);
    }

    @BeforeEach
    public void openAppHomePage() {
        ChromeOptions option = new ChromeOptions();
        option.addArguments("incognito");

        driver = new ChromeDriver(option);
        driver.get("http://localhost:8080/");
        delay(5000);
    }

    @AfterEach
    public void closeApp() {
        driver.quit();
    }

    @Test
    public void testRegister() {

        //Given
        navigateToRegister();

        //When
        addAccount();
        addEmail();
        addPassword();
        addWrongPasswordConfirmation();
        tryRegisterWithWrongPassword();
        addPasswordConfirmation();
        clickRegister();
        delay(2000);

        //Then
        validateRegistrationSucceeded();
    }

    private void navigateToRegister() {
        driver.findElement(By.id("account-menu")).click();
        WebElement dropdown = driver.findElement(By.className("dropdown-menu"));
        WebElement registerItem = dropdown.findElements(By.tagName("li")).get(1);
        Assertions.assertEquals("Register", registerItem.getText());
        registerItem.click();

        delay(2000);
        form = driver.findElement(By.name("form"));
        Assertions.assertNotNull(form);
    }

    private void addAccount() {
        form.findElement(By.id("login")).click();
        driver.findElement(By.id("account-menu")).click();
        String validationMessage = form.findElement(By.className("text-danger")).getText();
        Assertions.assertEquals("Your username is required.", validationMessage);
        form.findElement(By.id("login")).sendKeys(USERNAME);
    }

    private void addEmail() {
        form.findElement(By.id("email")).click();
        driver.findElement(By.id("account-menu")).click();
        String validationMessage = form.findElement(By.className("text-danger")).getText();
        Assertions.assertEquals("Your email is required.", validationMessage);
        form.findElement(By.id("email")).sendKeys(EMAIL);
    }

    private void addPassword() {
        form.findElement(By.id("password")).click();
        driver.findElement(By.id("account-menu")).click();
        String validationMessage = form.findElement(By.className("text-danger")).getText();
        Assertions.assertEquals("Your password is required.", validationMessage);
        form.findElement(By.id("password")).sendKeys(PASSWORD);
    }

    private void addWrongPasswordConfirmation() {
        form.findElement(By.id("confirmPassword")).click();
        driver.findElement(By.id("account-menu")).click();
        String validationMessage = form.findElement(By.className("text-danger")).getText();
        Assertions.assertEquals("Your confirmation password is required.", validationMessage);
        form.findElement(By.id("confirmPassword")).sendKeys(WRONG_PASSWORD);
    }

    private void tryRegisterWithWrongPassword() {
        clickRegister();

        String errorMessage = driver.findElement(By.className("alert-danger")).getText();
        Assertions.assertEquals("The password and its confirmation do not match!", errorMessage);
    }

    private void addPasswordConfirmation() {
        form.findElement(By.id("confirmPassword")).clear();
        form.findElement(By.id("confirmPassword")).sendKeys(PASSWORD);
    }

    private void clickRegister() {
        WebElement registerButton = form.findElement(By.tagName("button"));
        Assertions.assertEquals("Register", registerButton.getText());
        registerButton.click();
    }

    private void validateRegistrationSucceeded() {
        String registrationMessage = driver.findElement(By.className("alert-success")).getText();
        Assertions.assertTrue(registrationMessage.contains("Registration saved!"));
    }

    private void delay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
