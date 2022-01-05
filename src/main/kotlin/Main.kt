import com.github.epadronu.balin.core.Browser
import com.github.epadronu.balin.core.Page
import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.support.ui.ExpectedConditions
import java.io.File
import java.io.PrintWriter
import java.util.concurrent.Executors

val rootDir: String = System.getProperty("runtime.dir") + "/"

abstract class CDCDataPage(browser: Browser, query: String): Page(browser) {
	override val url = "https://covid.cdc.gov/covid-data-tracker/#$query"

	override val at = at {
		title == "CDC COVID Data Tracker"
	}

	private val downloadButton by lazy {
		waitFor {
			ExpectedConditions.elementToBeClickable(By.id("btnUSTrendsTableExport"))
		}
	}

	private val tableHeader by lazy {
		waitFor {
			ExpectedConditions.elementToBeClickable(By.id("us-trends-table-title"))
		}
	}

	fun clickDownload() {
		tableHeader.click()
		downloadButton.click()
	}
}

class TotalCasesPage(browser: Browser): CDCDataPage(browser, "trends_totalcases") {
	companion object {
		val filename = "${rootDir}data_table_for_total_cases__the_united_states.csv"
		val reversedFilename = "${rootDir}cases_reversed.csv"
	}
}
class TotalDeathsPage(browser: Browser): CDCDataPage(browser, "trends_totaldeaths") {
	companion object {
		val filename = "${rootDir}data_table_for_total_deaths__the_united_states.csv"
		val reversedFilename = "${rootDir}deaths_reversed.csv"
	}
}

fun main() {
	val downloadDir = rootDir
	System.setProperty("webdriver.gecko.driver", "$downloadDir/geckodriver")

	val profile = FirefoxProfile()
	profile.setPreference("browser.download.folderList", 2)
	profile.setPreference("browser.download.dir", downloadDir)
	profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv")

	val options = FirefoxOptions()
	options.profile = profile

	val driver = FirefoxDriver(options)
	val driver2 = FirefoxDriver(options)

	val threadPool = Executors.newFixedThreadPool(4)

	val casesFuture = threadPool.submit {
		Browser.drive(driverFactory = { driver }) {
			val casesPage = to(::TotalCasesPage)
			casesPage.clickDownload()
		}
		reverseCSV(TotalCasesPage.filename, TotalCasesPage.reversedFilename)
		File(TotalCasesPage.filename).delete()
	}

	val deathsFuture = threadPool.submit {
		Browser.drive(driverFactory = { driver2 }) {
			val deathsPage = to(::TotalDeathsPage)
			deathsPage.clickDownload()
		}
		reverseCSV(TotalDeathsPage.filename, TotalDeathsPage.reversedFilename)
		File(TotalDeathsPage.filename).delete()
	}

	casesFuture.get()
	deathsFuture.get()

	threadPool.shutdown()
}

fun reverseCSV(filename: String, reversedFilename: String) {
	File(filename).useLines { sequence ->
		val list = sequence.toList()
		val data = list.subList(3, list.size)
		val reversed = data.asReversed()
		val pw = PrintWriter(reversedFilename)
		reversed.forEach {
			pw.println(it)
		}
		pw.close()
	}
}
