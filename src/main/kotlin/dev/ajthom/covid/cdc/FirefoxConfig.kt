package dev.ajthom.covid.cdc

import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile

val firefoxProfile = FirefoxProfile().apply {
	setPreference("browser.download.folderList", 2)
	setPreference("browser.download.dir", rootDir)
	setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv")
}

val firefoxOptions = FirefoxOptions().apply {
	profile = firefoxProfile
}
