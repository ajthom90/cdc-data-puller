package dev.ajthom.covid.cdc

import java.io.File

sealed class Setup {
	abstract fun doSetup()

	companion object {
		private val all = Setup::class.sealedSubclasses.mapNotNull { it.objectInstance }

		fun doAll() {
			all.forEach {
				it.doSetup()
			}
		}
	}
}

object DeleteAnyOldDataDownloads: Setup() {
	override fun doSetup() {
		PageAndInfo.all.forEach {
			val file = File(it.filename)
			if (file.exists()) {
				file.delete()
			}
		}
	}
}
