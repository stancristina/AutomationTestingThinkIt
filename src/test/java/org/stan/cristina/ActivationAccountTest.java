package org.stan.cristina;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ActivationAccountTest {

    private static final Logger logger = Logger.getLogger(ActivationAccountTest.class.getName());
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String ACCOUNT_TO_ACTIVATE_USERNAME = "utilizator-nou-2";
    private static final String ACCOUNT_TO_ACTIVATE_PASSWORD = "abc@ABC@123";
    private WebDriver driver = null;
    private WebElement form = null;

    @BeforeEach
    public void openAppHomePage() {
        ChromeOptions option = new ChromeOptions();
        option.addArguments("incognito");

        driver = new ChromeDriver(option);
        driver.get("http://localhost:8080/");
        delay(5000);
    }

    private void delay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void closeApp() {
        driver.quit();
    }

    @Test
    public void activateAccount() {

        navigateToSignIn();
        loginAsAdmin();
        navigateToUserManagement();

        WebElement userAccountTableRow1 = getUserAccountTableRow();
        activateUserAccount(userAccountTableRow1);

        signOut();

        navigateToSignIn();
        loginAsUser();
        checkLibraryIsAccessible();
        signOut();

        navigateToSignIn();
        loginAsAdmin();
        navigateToUserManagement();

        WebElement userAccountTableRow2 = getUserAccountTableRow();
        deactivateUserAccount(userAccountTableRow2);

        signOut();
    }

    private void navigateToSignIn() {
        if(driver.findElements(By.className("form")).size() != 0) {
            // form already on the screen
            return;
        }

        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement accountMenu = driver.findElement(By.id("account-menu"));

        js.executeScript("arguments[0].click();", accountMenu);
        delay(500);

        WebElement dropdown = driver.findElement(By.className("dropdown-menu"));
        WebElement signInItem = dropdown.findElements(By.tagName("li")).get(0);
        Assertions.assertEquals("Sign in", signInItem.getText());
        signInItem.click();
        delay(500);
    }

    private void loginAsAdmin() {
        WebElement userNameFiled =  driver.findElement(By.id("username"));
        userNameFiled.clear();
        userNameFiled.sendKeys(ADMIN_USERNAME);

        WebElement passwordField =  driver.findElement(By.id("password"));
        passwordField.clear();
        driver.findElement(By.id("password")).click();
        passwordField.sendKeys(ADMIN_PASSWORD);

        WebElement signInButton = driver.findElement(By.tagName("form")).findElement(By.tagName("button"));
        Assertions.assertEquals("Sign in", signInButton.getText());
        signInButton.click();
        delay(2000);
    }

    private void navigateToUserManagement() {
        driver.findElement(By.id("admin-menu")).click();
        delay(2000);
        WebElement dropdown = driver.findElements(By.className("dropdown-menu")).get(1);
        WebElement userManagementItem = dropdown.findElements(By.tagName("li")).get(0);
        userManagementItem.click();
        delay(2000);
    }

    private WebElement getUserAccountTableRow() {
        AtomicReference<WebElement> result = new AtomicReference<>();

        do {
            WebElement tableElement = driver.findElement(By.tagName("table"));
            Assertions.assertNotNull(tableElement);

            tableElement.findElements(By.tagName("tr")).forEach(tr -> {
                Optional<WebElement> userNameTd = tr.findElements(By.tagName("td")).stream()
                        .filter(td -> td.getText().equals(ACCOUNT_TO_ACTIVATE_USERNAME)).findFirst();
                if(userNameTd.isPresent()) {
                    result.set(tr);
                }
            });

            if(result.get() != null) {
                return result.get();
            } else {
                Optional<WebElement> nextArrowOpt = driver.findElements(By.className("page-link")).stream()
                        .filter(el -> el.getAttribute("ariaLabel") != null && el.getAttribute("ariaLabel").equals("Next"))
                        .findFirst();
                Assertions.assertTrue(nextArrowOpt.isPresent());
                Assertions.assertTrue(nextArrowOpt.get().isEnabled());

                nextArrowOpt.get().click();
                delay(2000);
            }
        } while(true);

    }

    private void activateUserAccount(WebElement userAccountTableRow) {
        WebElement activationButton = userAccountTableRow.findElement(By.tagName("button"));
        Assertions.assertEquals("Deactivated", activationButton.getText());
        activationButton.click();
    }

    private void deactivateUserAccount(WebElement userAccountTableRow) {
        WebElement activationButton = userAccountTableRow.findElement(By.tagName("button"));
        Assertions.assertEquals("Activated", activationButton.getText());
        activationButton.click();
    }

    private void signOut() {
        driver.findElement(By.id("account-menu")).click();
        delay(500);

        Optional<WebElement> signOutButtonOpt = driver.findElements(By.tagName("a")).stream()
                .filter(a -> a.getText() != null && a.getText().equals("Sign out")).findFirst();
        Assertions.assertTrue(signOutButtonOpt.isPresent());
        signOutButtonOpt.get().click();
        delay(2500);
    }

    private void loginAsUser() {
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).click();
        driver.findElement(By.id("username")).sendKeys(ACCOUNT_TO_ACTIVATE_USERNAME);

        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys(ACCOUNT_TO_ACTIVATE_PASSWORD);

        WebElement signInButton = driver.findElement(By.tagName("form")).findElement(By.tagName("button"));
        Assertions.assertEquals("Sign in", signInButton.getText());
        signInButton.click();
        delay(2000);
    }

    private void checkLibraryIsAccessible() {
        Optional<WebElement> libraryMenu = driver.findElement(By.id("navbarResponsive")).findElements(By.tagName("a"))
                .stream().filter(a -> a.getText() != null && a.getText().equals("Library")).findFirst();
        Assertions.assertTrue(libraryMenu.isPresent());
    }

}
