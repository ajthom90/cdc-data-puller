@file:Suppress("unused")

package dev.ajthom.covid.cdc

import com.github.epadronu.balin.core.Browser
import org.openqa.selenium.firefox.FirefoxDriver

sealed class PageAndInfo {
	abstract fun getDataPage(browser: Browser): CDCDataPage
	abstract val filename: String
	abstract val reversedFilename: String

	fun downloadData() {
		val driver = FirefoxDriver(firefoxOptions)
		Browser.drive(driverFactory = { driver }) {
			val casesPage = to { getDataPage(this) }
			casesPage.clickDownload()
		}
	}

	companion object {
		val all = PageAndInfo::class.sealedSubclasses.mapNotNull { it.objectInstance }

		fun downloadAll() {
			all.forEach {
				it.downloadData()
			}
		}
	}
}

object TotalCases: PageAndInfo() {
	override fun getDataPage(browser: Browser): CDCDataPage {
		return TotalCasesPage(browser)
	}

	override val filename = "${rootDir}data_table_for_total_cases__the_united_states.csv"
	override val reversedFilename = "${rootDir}totalCases.csv"
}

object TotalDeaths: PageAndInfo() {
	override fun getDataPage(browser: Browser): CDCDataPage {
		return TotalDeathsPage(browser)
	}

	override val filename = "${rootDir}data_table_for_total_deaths__the_united_states.csv"
	override val reversedFilename = "${rootDir}totalDeaths.csv"
}

object DailyCases: PageAndInfo() {
	override fun getDataPage(browser: Browser): CDCDataPage {
		return DailyCasesPage(browser)
	}

	override val filename = "${rootDir}data_table_for_daily_case_trends__the_united_states.csv"
	override val reversedFilename = "${rootDir}dailyCases.csv"
}

object DailyDeaths: PageAndInfo() {
	override fun getDataPage(browser: Browser): CDCDataPage {
		return DailyDeathsPage(browser)
	}

	override val filename = "${rootDir}data_table_for_daily_death_trends__the_united_states.csv"
	override val reversedFilename = "${rootDir}dailyDeaths.csv"
}

object DailyTests: PageAndInfo() {
	override fun getDataPage(browser: Browser): CDCDataPage {
		return DailyTestVolumePage(browser)
	}

	override val filename = "${rootDir}data_table_for_daily_test_volume__the_united_states.csv"
	override val reversedFilename = "${rootDir}dailyTests.csv"
}
