package dev.ajthom.covid.cdc

import com.github.epadronu.balin.core.Browser
import com.github.epadronu.balin.core.Page
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions

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

class TotalCasesPage(browser: Browser): CDCDataPage(browser, "trends_totalcases")
class TotalDeathsPage(browser: Browser): CDCDataPage(browser, "trends_totaldeaths")
class DailyCasesPage(browser: Browser): CDCDataPage(browser, "trends_dailycases")
