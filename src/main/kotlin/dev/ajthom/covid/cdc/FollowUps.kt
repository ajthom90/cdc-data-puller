@file:Suppress("unused")

package dev.ajthom.covid.cdc

import java.io.File
import java.io.PrintWriter
import kotlin.reflect.KClass

sealed class FollowUp {
	abstract fun doFollowUp()
	abstract fun getClass(): KClass<out FollowUp>

	open val dependsOn: List<FollowUp> = emptyList()

	fun run(toRunAfter: () -> Unit) {
		println("Starting: ${getClass().simpleName}")
		if (hasRun()) {
			println("Skipping: ${getClass().simpleName}")
			return
		}
		dependsOn.forEach {
			it.doFollowUp()
		}
		println("Running: ${getClass().simpleName}")
		toRunAfter()
		setRun()
	}

	private fun hasRun(): Boolean {
		if (hasRunMap[getClass()] == true) {
			return true
		}
		return false
	}

	private fun setRun() {
		hasRunMap[getClass()] = true
	}

	companion object {
		private var hasRunMap = mutableMapOf<KClass<out FollowUp>, Boolean>()

		private val all = FollowUp::class.sealedSubclasses.mapNotNull { it.objectInstance }

		fun doAll() {
			reset()
			all.forEach {
				it.doFollowUp()
			}
			reset()
		}

		private fun reset() {
			hasRunMap = mutableMapOf()
			all.forEach {
				hasRunMap[it.getClass()] = false
			}
		}
	}
}

object DeleteDownloadedFiles: FollowUp() {
	override val dependsOn = listOf(ReverseCSVs)

	override fun getClass(): KClass<out FollowUp> {
		return DeleteDownloadedFiles::class
	}

	override fun doFollowUp() = super.run {
		PageAndInfo.all.forEach {
			File(it.filename).delete()
		}
	}
}

object ReverseCSVs: FollowUp() {
	override fun doFollowUp() = run {
		PageAndInfo.all.forEach {
			reverseCSV(it.filename, it.reversedFilename)
		}
	}

	override fun getClass(): KClass<out FollowUp> {
		return this::class
	}

	private fun reverseCSV(filename: String, reversedFilename: String) {
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
}


