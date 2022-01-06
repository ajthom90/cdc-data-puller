package dev.ajthom.covid.cdc

val rootDir: String = System.getProperty("runtime.dir", ".") + "/"

fun main() {
	System.setProperty("webdriver.gecko.driver", "$rootDir/geckodriver")

	try {
		Setup.doAll()
		PageAndInfo.downloadAll()
		FollowUp.doAll()
	} catch (e: Exception) {
		e.printStackTrace()
	}
}
