package roland_a.mc_mods.common.config

import kotlinx.serialization.Serializable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import roland_a.mc_mods.assertConditionIsTrue
import roland_a.mc_mods.assertContentEquals
import roland_a.mc_mods.assertEquals
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

private typealias Result = (
	output: LoadConfigTest.Config,
	configFileContent: String,
	invalidConfigFileContent: String?,
	normalLogs: List<String>,
	errorLogs: List<String>
)->Unit

private class LoadConfigTest {
	@Suppress("PropertyName")
	@Serializable
	data class Config(
		val field_a: Int=123,
		val field_b: String="abc"
	){
		init {
			require(field_a >= 0){
				"NO NEGATIVES ALLOWED"
			}
		}
	}

	val basePath = Paths.get(".").resolve("temp")
	val configPath = basePath.resolve(SubFiles.CONFIG)
	val invalidConfigPath = basePath.resolve(SubFiles.INVALID_CONFIG)

	fun String?.test(fn: Result){
		val normalLogs = mutableListOf<String>()
		val errorLogs = mutableListOf<String>()

		if (this != null){
			basePath.createDirectories()
			configPath.writeText(this)
		}

		return (
			loadConfig<Config>{
				folderPath = basePath

				parseWithType()

				logNormal = normalLogs::add
				logError = errorLogs::add
			}
			.let { result->
				val normalPathContent =
					configPath
					.readText()

				val invalidPathContent =
					invalidConfigPath
					.takeIf {
						it.exists()
					}
					?.readText()

				fn(
					result,
					normalPathContent,
					invalidPathContent,
					normalLogs,
					errorLogs,
				)
			}
		)
	}

	@AfterEach
	fun cleanup(){
		configPath.deleteIfExists()
		invalidConfigPath.deleteIfExists()
		basePath.deleteIfExists()
	}

	@Test
	fun `empty files loads clean config`(){
		null
		.test { output, configFileContent, invalidConfigFileContent, normalLogs, errorLogs ->
			output
			.assertEquals(Config())

			configFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "abc"
				}
				""".trimIndent()
			)

			invalidConfigFileContent
			.assertEquals(null)

			normalLogs
			.assertContentEquals(
				Messages.readingConfig(configPath),
				Messages.done()
			)

			errorLogs
			.assertContentEquals()
		}
	}

	@Test
	fun `valid configs with all fields are accepted`(){
		val content = """
			{
				"field_a": 456,
				"field_b": "def"
			}
		""".trimIndent()

		content
		.test { output, configFileContent, invalidConfigFileContent, normalLogs, errorLogs->
			output
			.assertEquals(
				Config(456, "def")
			)

			configFileContent
			.assertEquals(content)

			invalidConfigFileContent
			.assertEquals(null)

			normalLogs
			.assertContentEquals(
				Messages.readingConfig(configPath),
				Messages.done()
			)

			errorLogs
			.assertContentEquals()
		}
	}

	@Test
	fun `configs with missing field are accepted and reloads clean config`(){
		val content = """
			{
				"field_b": "def"
			}
		""".trimIndent()

		content
		.test { output, configFileContent, invalidConfigFileContent, normalLogs, errorLogs ->
			output
			.assertEquals(
				Config(field_b = "def")
			)

			configFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "def"
				}
				""".trimIndent()
			)

			invalidConfigFileContent
			.assertEquals(
				null
			)

			normalLogs
			.assertContentEquals(
				Messages.readingConfig(configPath),
				Messages.done()
			)

			errorLogs
			.assertContentEquals()
		}
	}

	@Test
	fun `config with invalid syntax are not accepted and reloads clean config`(){
		val content = """
			{
				"field_a": 123,
				"field_b": "abc
			}
		""".trimIndent()

		content
		.test { output, configFileContent, invalidConfigFileContent, normalLogs, errorLogs->
			output
			.assertEquals(
				Config()
			)

			configFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "abc"
				}
				""".trimIndent()
			)

			invalidConfigFileContent
			.assertEquals(
				content
			)

			normalLogs
			.assertContentEquals(
				Messages.readingConfig(configPath),
				Messages.done()
			)

			errorLogs
			.also {
				it
				.size
				.assertEquals(2)
			}
			.also { (first, second) ->
				first
				.assertConditionIsTrue {
					Messages.Errors.invalidSyntax("") in it
				}

				second
				.assertEquals(
					Messages.Errors.movingInvalidConfig(invalidConfigPath)
				)
			}
		}
	}

	@Test
	fun `configs with invalid arguments are not accepted and reloads clean config`(){
		val content = """
			{
				"field_a": -1,
				"field_b": "def"
			}
		""".trimIndent()

		content
		.test { output, configFileContent, invalidConfigFileContent, normalLogs, errorLogs->
			output
			.assertEquals(
				Config()
			)

			configFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "abc"
				}
				""".trimIndent()
			)

			invalidConfigFileContent
			.assertEquals(
				content
			)

			normalLogs
			.assertContentEquals(
				Messages.readingConfig(configPath),
				Messages.done(),
			)

			errorLogs
			.assertContentEquals(
				Messages.Errors.invalidArgument("NO NEGATIVES ALLOWED"),
				Messages.Errors.movingInvalidConfig(invalidConfigPath),
			)
		}
	}

	@Test
	fun `configs with unknown arguments are not accepted and reloads clean config`(){
		val content = """
			{
				"field_a": 123,
				"field_b": "def",
				"field_c": 1.23
			}
		""".trimIndent()

		content
		.test { output, configFileContent, invalidConfigFileContent, normalLogs, errorLogs->
			output
			.assertEquals(
				Config()
			)

			configFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "abc"
				}
				""".trimIndent()
			)

			invalidConfigFileContent
			.assertEquals(
				content
			)

			normalLogs
			.assertContentEquals(
				Messages.readingConfig(configPath),
				Messages.done(),
			)

			//TODO create custom error messages for unknown keys
			errorLogs
			.also {
				it
				.size
				.assertEquals(2)
			}
			.also { (first, second) ->
				first
				.assertConditionIsTrue {
					Messages.Errors.invalidSyntax("") in it
				}

				second
				.assertEquals(
					Messages.Errors.movingInvalidConfig(invalidConfigPath)
				)
			}
		}
	}

	@Test
	fun `invalid config file gets overwritten`(){
		val content = """
			{
				"field_a": 123,
				"field_b": "abc
			}
		""".trimIndent()


		basePath.createDirectories()
		invalidConfigPath.writeText("sdjskdj")

		content
		.test { output, configFileContent, invalidConfigFileContent, normalLogs, errorLogs->
			output
			.assertEquals(
				Config()
			)

			configFileContent
			.assertEquals(
				"""
				{
					"field_a": 123,
					"field_b": "abc"
				}
				""".trimIndent()
			)

			invalidConfigFileContent
			.assertEquals(
				content
			)

			normalLogs
			.assertContentEquals(
				Messages.readingConfig(configPath),
				Messages.done()
			)

			errorLogs
			.also {
				it
				.size
				.assertEquals(2)
			}
			.also { (first, second) ->
				first
				.assertConditionIsTrue {
					Messages.Errors.invalidSyntax("") in it
				}

				second
				.assertEquals(
					Messages.Errors.movingInvalidConfig(invalidConfigPath)
				)
			}
		}
	}
}
