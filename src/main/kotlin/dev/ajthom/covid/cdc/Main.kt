package dev.ajthom.covid.cdc

import com.github.epadronu.balin.core.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import java.io.File
import java.io.PrintWriter
import java.util.concurrent.Executors

val rootDir: String = System.getProperty("runtime.dir", ".") + "/"

fun main() {
	val threadPool = Executors.newFixedThreadPool(3)

	try {
		val downloadDir = rootDir
		System.setProperty("webdriver.gecko.driver", "$downloadDir/geckodriver")

		val profile = FirefoxProfile()
		profile.setPreference("browser.download.folderList", 2)
		profile.setPreference("browser.download.dir", downloadDir)
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv")

		val options = FirefoxOptions()
		options.profile = profile

		val futures = PageAndInfo::class.sealedSubclasses.map {
			threadPool.submit {
				val pageAndInfo = it.objectInstance ?: return@submit
				val driver = FirefoxDriver(options)
				Browser.drive(driverFactory = { driver }) {
					val casesPage = to { pageAndInfo.getDataPage(this) }
					casesPage.clickDownload()
				}
				reverseCSV(pageAndInfo.filename, pageAndInfo.reversedFilename)
				File(pageAndInfo.filename).delete()
			}
		}

		futures.forEach {
			it.get()
		}
		threadPool.shutdown()
	} catch (e: Exception) {
		e.printStackTrace()
		threadPool.shutdown()
	}
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
