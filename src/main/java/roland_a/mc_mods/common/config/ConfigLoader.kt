package roland_a.mc_mods.common.config

import kotlinx.serialization.SerializationException
import kotlin.io.path.*


fun <T: Any> loadConfig(fn: ConfigLoaderBuilder<T>.()->Unit): T {
	val (folderPath, configToString, stringToConfig, default, logNormal, logError) = ConfigLoaderBuilder<T>().apply(fn)

	require(folderPath != null)
	require(configToString != null)
	require(stringToConfig != null)
	require(default != null)
	require(logNormal != null)
	require(logError != null)

	val configPath = folderPath.resolve(SubFiles.CONFIG)
	val invalidPath = folderPath.resolve(SubFiles.INVALID_CONFIG)

	fun logExceptions(e: Throwable){
		when (e) {
			is SerializationException -> {
				logError(Messages.Errors.invalidSyntax(e.message ?: ""))
			}
			is IllegalArgumentException -> {
				logError(Messages.Errors.invalidArgument(e.message ?: ""))
			}
			else -> {
				throw e
			}
		}
	}

	fun moveInvalidConfig(){
		logError(Messages.Errors.movingInvalidConfig(invalidPath))

		configPath.moveTo(invalidPath, overwrite = true)
	}

	logNormal(Messages.readingConfig(configPath))

	folderPath.createDirectories()

	//read from config
	val result =
		configPath
		.takeIf {
			it.exists()
		}
		?.readText()
		?.let(stringToConfig)
		?.getOrElse { e->
			logExceptions(e)
			moveInvalidConfig()

			null
		}
		?: default

	//write to config
	result
	.let(configToString)
	.also {
		configPath.writeText(it)
	}

	logNormal(Messages.done())

	return result
}
