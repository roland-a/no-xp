package roland_a.mc_mods

import org.junit.jupiter.api.Assertions

fun <T> T.assertEquals(expected: T){
	Assertions.assertEquals(expected, this)
}

fun <T> List<T>.assertContentEquals(vararg expectedContent: T){
	Assertions.assertEquals(expectedContent.toList(), this)
}

fun <T> T.assertConditionIsTrue(condition: (T)->Boolean){
	Assertions.assertEquals(true, condition(this), "CONDITION NOT TRUE WITH $this")
}
