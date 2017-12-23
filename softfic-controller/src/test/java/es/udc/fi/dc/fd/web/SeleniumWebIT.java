package es.udc.fi.dc.fd.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.config.WebSecurityConfigurationAware;

public class SeleniumWebIT extends WebSecurityConfigurationAware {

	private WebDriver driver;
	private String baseUrl;

	@Before
	public void setUp() throws Exception {
		checkOS();
		driver = new FirefoxDriver();
		baseUrl = "http://localhost:8231/softfic-controller/";

	}

	@After
	public void close() {
		driver.quit();
	}

	private void checkOS() {
		String OS = System.getProperty("os.name").toLowerCase();
		if ((OS.indexOf("win") >= 0)) { // is windows
			System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
		}

	}

	@Transactional
	@Test
	public void pacaTest() throws InterruptedException {
		driver.get(baseUrl);
		driver.findElement(By.xpath("(//a[contains(@href, '/signin')])[2]")).click();
		driver.findElement(By.id("inputEmail")).click();
		driver.findElement(By.id("inputEmail")).clear();
		driver.findElement(By.id("inputEmail")).sendKeys("dani_valcarce@example.com");
		driver.findElement(By.id("inputPassword")).clear();
		driver.findElement(By.id("inputPassword")).sendKeys("admin");
		driver.findElement(By.id("loginButton")).click();
		driver.findElement(By.linkText("Profile")).click();
		driver.findElement(By.linkText("Follow")).click();
		driver.findElement(By.id("inputEmail")).click();
		driver.findElement(By.id("inputEmail")).clear();
		driver.findElement(By.id("inputEmail")).sendKeys("javi");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		driver.findElement(By.id("follow-button25")).click();
		driver.findElement(
				By.xpath("//td[@onclick=\"window.location.href = '/softfic-controller/myprofile?accId=25'\"]")).click();
		assertEquals("Javier will attend Triatlón la semana que viene",
				driver.findElement(By.xpath("//div[@id='moreRaces']/table/tbody/tr/td[2]")).getText());
		driver.findElement(By.xpath("(//button[@type='button'])[2]")).click();
		driver.findElement(By.xpath("(//button[@type='button'])[2]")).click();
		driver.findElement(By.id("inputEmail")).click();
		driver.findElement(By.id("inputEmail")).clear();
		driver.findElement(By.id("inputEmail")).sendKeys("paca");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		driver.findElement(By.id("follow-button24")).click();
		driver.findElement(
				By.xpath("//td[@onclick=\"window.location.href = '/softfic-controller/myprofile?accId=24'\"]")).click();
		assertEquals("Paca will attend Triatlón la semana que viene",
				driver.findElement(By.xpath("//div[@id='moreRaces']/table/tbody/tr/td[2]")).getText());
		assertEquals("Paca will attend Carrera Popular Solidaria",
				driver.findElement(By.xpath("//div[@id='moreRaces']/table/tbody/tr[2]/td[2]")).getText());
		assertEquals("Paca has reached the #5 position in Media maratón en bilbao 2017",
				driver.findElement(By.xpath("//div[@id='moreRaces']/table/tbody/tr[3]/td[2]")).getText());
		driver.findElement(By.linkText("Triatlón la semana que viene")).click(); //

		assertEquals("23",
				driver.findElement(By.xpath("//div[@id='attendanceRefreshZone']/div[2]/span/a/span")).getText());
		assertEquals("4", driver.findElement(By.id("friends")).getText());

	}

	@Transactional
	@Test
	public void carballinoTest() throws InterruptedException {
		driver.get(baseUrl);
		WebElement element = driver.findElement(By.id("inputPlace"));
		element.clear();
		element.sendKeys("O Carballino");
		element = driver.findElement(By.id("inputDistance"));
		element.clear();
		element.sendKeys("30");

		// Slider movement
		element = driver.findElement(By.className("slider-track"));
		int width = element.getSize().width;
		WebElement sliderA = driver.findElements(By.cssSelector("div.slider-handle.max-slider-handle.round")).get(0);
		Actions action = new Actions(driver);
		action.clickAndHold(sliderA).moveByOffset(100 * width / 100, 0).release().perform();
		WebElement sliderB = driver.findElement(By.cssSelector("div.slider-handle.min-slider-handle.round"));
		action.clickAndHold(sliderB).moveByOffset(44 * width / 100, 0).release().perform();

		sliderA = driver.findElements(By.cssSelector("div.slider-handle.max-slider-handle.round")).get(1);
		action.clickAndHold(sliderA).moveByOffset(8 * width / 100, 0).release().perform();

		new Select(driver.findElement(By.id("medicalTestSelect"))).selectByVisibleText("Without medical test");
		driver.findElement(By.id("medicalTestSelect")).click();

		Thread.sleep(100);
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		assertEquals("Longoseiros", driver.findElement(By.xpath("//table[@id='races']/tbody/tr/td[2]")).getText());

	}

	@Transactional
	@Test
	public void darAltatest() throws InterruptedException {
		Random randomGenerator = new Random();
		String nombreCarrera = "Carrera Aceptacion#" + randomGenerator.nextInt(100);
		driver.get(baseUrl);
		driver.findElement(By.linkText("Add New Race")).click();
		driver.findElement(By.id("inputEmail")).click();
		driver.findElement(By.id("inputEmail")).clear();
		driver.findElement(By.id("inputEmail")).sendKeys("user@example.com");
		driver.findElement(By.id("inputPassword")).clear();
		driver.findElement(By.id("inputPassword")).sendKeys("demo");

		driver.findElement(By.id("loginButton")).click();
		Thread.sleep(1500);
		driver.findElement(By.id("addRaceButton")).click();
		driver.findElement(By.id("isInternalButtonNo")).click();
		Thread.sleep(100);
		driver.findElement(By.id("inputName")).click();
		driver.findElement(By.id("inputName")).clear();
		driver.findElement(By.id("inputName")).sendKeys(nombreCarrera);
		driver.findElement(By.id("inputPlaceAdd")).clear();
		driver.findElement(By.id("inputPlaceAdd")).sendKeys("Ferrol, A Coruña, Spain");
		driver.findElement(By.name("date")).clear();
		driver.findElement(By.name("date")).sendKeys("12-30-2017");
		driver.findElement(By.id("inputPlaceAdd")).click();
		driver.findElement(By.id("inputMedicalTest")).click();
		driver.findElement(By.id("inputChip")).click();
		driver.findElement(By.id("inputChipPrice")).click();
		driver.findElement(By.id("inputChipPrice")).clear();
		driver.findElement(By.id("inputChipPrice")).sendKeys("5");
		driver.findElement(By.id("priceInput")).click();
		driver.findElement(By.id("priceInput")).clear();

		// ---
		driver.findElement(By.id("priceInput")).sendKeys("80");
		driver.findElement(By.id("minAgeInput")).clear();
		driver.findElement(By.id("minAgeInput")).sendKeys("40");
		driver.findElement(By.id("maxAgeInput")).clear();
		driver.findElement(By.id("maxAgeInput")).sendKeys("99");
		driver.findElement(By.id("add-row")).click();
		driver.findElement(By.xpath("(//input[@id='priceInput'])[2]")).click();
		driver.findElement(By.xpath("(//input[@id='priceInput'])[2]")).clear();
		driver.findElement(By.xpath("(//input[@id='priceInput'])[2]")).sendKeys("100");
		driver.findElement(By.xpath("(//input[@id='minAgeInput'])[2]")).click();
		driver.findElement(By.xpath("(//input[@id='minAgeInput'])[2]")).clear();
		driver.findElement(By.xpath("(//input[@id='minAgeInput'])[2]")).sendKeys("0");
		driver.findElement(By.xpath("(//input[@id='maxAgeInput'])[2]")).click();
		driver.findElement(By.xpath("(//input[@id='maxAgeInput'])[2]")).clear();
		driver.findElement(By.xpath("(//input[@id='maxAgeInput'])[2]")).sendKeys("40");
		//
		driver.findElement(By.xpath("//form[@id='addRaceForm']/div/div[10]/div[2]")).click();

		driver.findElement(By.id("addNewRace")).click();
		Thread.sleep(8000);
		List<WebElement> elements = driver.findElements(By.cssSelector("td"));
		assertEquals(nombreCarrera, elements.get(0).getText());
		assertTrue(elements.get(1).getText().contains("Ferrol"));
		assertEquals("Yes", elements.get(5).getText());
		assertEquals("5.00€", elements.get(6).getText());

	}

}
