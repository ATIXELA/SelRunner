package org.testingsoftware.selrunner;

import java.io.File;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testingsoftware.selrunner.exceptions.NoWebElementSelectedException;

import com.google.common.base.Function;

import fitnesse.slim.converters.ConverterRegistry;

/**
 * Simple Webdriver adapter intended to be used with Fitnesse/Slim.
 * 
 */
public class SelRunner {
    private WebDriver webdriver;

    private WebElement element;

    private int timeout = 45;

    public SelRunner() {
        newDriver("Firefox");
    }

    public SelRunner(String browserType) {
        newDriver(browserType);
    }

    public SelRunner(String browserType, String profile_path) {
        if (browserType.equals("Firefox")) {
            DesiredCapabilities dc = DesiredCapabilities.firefox();
            File file = new File(profile_path);
            FirefoxProfile fp = new FirefoxProfile(file);
            dc.setCapability(FirefoxDriver.PROFILE, fp);
            webdriver = new FirefoxDriver(dc);
            ConverterRegistry.addConverter(By.class, new ByConverter());
        } else {
            newDriver(browserType);
        }
    }

    /**
     * Clear the current element e.g. input field.
     */
    public void clear() {
        element.clear();
    }

    /**
     * Click the current element.
     */
    public void click() {
        element.click();
    }

    /**
     * Click the element specified by the selector. This uses the standard webdriver convention to specify selectors:
     * <selector type>:<selector>
     * 
     * @param by specify selector e.g. "css:ok_button"
     * @see org.openqa.selenium.remote.server.handler.BySelector
     */
    public void click(By by) {
        element = webdriver.findElement(by);
        click();
    }

    /**
     * Formulate a wait condition.
     *
     * @param  javascript Javascript snippet to formulate the wait condition.
     * @return 
     */
    private Function<WebDriver, Object> condition(final String javascript) {
        // wait until Javascript snippet evaluates to true
        return new Function<WebDriver, Object>() {
            public Object apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript(javascript);
            }
        };
    }

    /**
     * Does the current element contain the text text.
     *
     * @param  text specify the text you are looking for.
     * @return true if current element contains the specified text.
     */    
    public boolean containsText(String text) {
        if (element == null) {
            throw new NoWebElementSelectedException();
        }
        System.out.println(element.getText());
        return element.getText().contains(text);
    }

    private WebDriverWait doWait() {
        return new WebDriverWait(webdriver, timeout);
    }

    private Function<WebDriver, Boolean> elementInvisible(final By by) {
        return new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver) {
                if (driver.findElements(by).size() == 0) {
                    return true;
                }
                WebElement element = driver.findElement(by);
                return !element.isDisplayed();
            }
        };
    }

    private Function<WebDriver, Boolean> elementLocatedIsNotPresent(final By by) {
        return new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.findElements(by).size() == 0;
            }
        };
    }

    private Function<WebDriver, Boolean> elementLocatedIsPresent(final By by) {
        return new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.findElements(by).size() > 0;
            }
        };
    }

    private Function<WebDriver, Boolean> elementVisible(final By by) {
        return new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver) {
                if (driver.findElements(by).size() == 0) {
                    return false;
                }
                WebElement element = driver.findElement(by);
                return element.isDisplayed();
            }
        };
    }

    /**
     * Find element specified by selector.
     *
     * @param  by selector to specify the element.
     * @see click
     */
    public boolean findElement(By by) {
        if (hasElement(by)) {
            element = webdriver.findElement(by);
            return true;
        }
        return false;
    }

    /**
     * Find multiple elements specified by selector.
     * ?? might be private ??
     *
     * @param  by selector to specify the element.
     * @see click
     * @return List of elements
     */
    public List<WebElement> findElements(By by) {
        return webdriver.findElements(by);
    }

    /**
     * Is the element specified by selector contained?
     *
     * @param  by selector to specify the element.
     * @see click
     * @return true if element is contained
     */
    public boolean hasElement(By by) {
        return numberOfElements(by) > 0;
    }

    /**
     * Is the element specified by selector displayed?
     *
     * @param  by selector to specify the element.
     * @see click
     * @return true if element is displayed
     */
    public boolean isDisplayed() {
        return element.isDisplayed();
    }

    /**
     * Is the entry specified by selector selected?
     *
     * @param  by selector to specify the element.
     * @see click
     * @return true if element is selected
     */
    public boolean isSelected() {
        return element.isSelected();
    }

    private void newDriver(String browserType) {
        if (browserType.equals("Chrome")) {
            webdriver = new ChromeDriver();
        } else if (browserType.equals("IE")) {
            // I do not have IE available so please let me know if it works fine
            webdriver = new InternetExplorerDriver();
        } else {
            webdriver = new FirefoxDriver();
        }
        ConverterRegistry.addConverter(By.class, new ByConverter());
    }

    /**
     * Number of elements that match the specified selector?
     *
     * @param  by selector to specify the element.
     * @see click
     * @return number of elements
     */
    public int numberOfElements(By by) {
        return findElements(by).size();
    }

    /**
     * Open URL.
     *
     * @param  url The URL for the page to open.
     */
    public void open(String url) {
        webdriver.get(url);
    }

    /**
     * Select a SelectBox entry by index.
     *
     * @param  index of the SelectBox entry to select.
     */
    public void selectByIndex(int index) {
        new Select(element).selectByIndex(index);
    }

    /**
     * Select a SelectBox entry by text.
     *
     * @param  text Text of SelectBox entry to select.
     */
    public void selectByText(String text) {
        new Select(element).selectByVisibleText(text);
    }

    /**
     * Select a SelectBox entry by its value.
     *
     * @param  value Value of the SelectBox entry to select.
     */
    public void selectByValue(String value) {
        new Select(element).selectByValue(value);
    }

    /**
     * Send single key to current element.
     * ?? is this still needed, or do we use type ??
     *
     * @param  key Key to send.
     */
//    public void sendKey(Keys key) {
//        element.sendKeys(key);
//    }

    /**
     * Send multiple keys.
     *
     * @param  keys Keys to send to the current element.
     */
//    public void sendKeys(CharSequence[] keys) {
//        element.sendKeys(keys);
//    }

    /**
     * Select the current element e.g. CheckBox.
     */
    public void setSelected() {
        if (!isSelected()) {
            toggle();
        }
    }

    /**
     * Set the default timeout for doWait.
     * Default timeout is currently set to 45seconds.
     */
    public void setTimeout(int seconds) {
        timeout = seconds;
    }

    /**
     * Un-select the current element e.g. CheckBox.
     */
    public void setUnselected() {
        if (isSelected()) {
            toggle();
        }
    }

    /**
     * Stop the test and close the browser.
     */
    public void stop() {
        webdriver.close();
    }

    /**
     * Text contents of the current element.
     * 
     * @return the text contents of the current element.
     */
    public String text() {
        return element.getText();
    }

    /**
     * Title of the current page.
     * 
     * @return the title of the current page.
     */
    public String title() {
        return webdriver.getTitle();
    }

    /**
     * Toggle the select state of the current element e.g. CheckBox.
     */
    public void toggle() {
        element.click();
    }

    public void type(String keys) {
        element.sendKeys(keys);
    }

    /**
     * Wait for specified number of milliseconds.
     *
     * @param  milliseconds specify number of milliseconds to wait for.
     */
    public void wait(int milliseconds) throws Exception {
        Thread.sleep(milliseconds);
    }

    /**
     * Wait until condition condition.
     *
     * @param  snippet Javascript snippet to formulate the wait condition.
     */
    public void waitForCondition(String snippet) {
        doWait().until(condition("return (" + snippet + ");"));
    }

    /**
     * Wait until element specified by selector becomes invisible.
     *
     * @param  by selector to specify an element.
     * @see click
     */
    public void waitForElementInvisible(By by) {
        doWait().until(elementInvisible(by));
    }

    /**
     * Wait until element specified by selector is in state "not present".
     *
     * @param  by selector to specify the element.
     * @see click
     */
    public void waitForElementNotPresent(By by) {
        doWait().until(elementLocatedIsNotPresent(by));
    }

    /**
     * Wait until element specified by selector is present.
     *
     * @param  by selector to specify the element.
     * @see click
     */
    public void waitForElementPresent(By by) {
        doWait().until(elementLocatedIsPresent(by));
    }

    /**
     * Wait until element specified by selector is visible.
     *
     * @param  by selector to specify the element.
     * @see click
     */
    public void waitForElementVisible(By by) {
        doWait().until(elementVisible(by));
    }
}
