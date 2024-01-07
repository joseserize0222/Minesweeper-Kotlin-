import kotlin.random.Random

fun generatePredictablePassword(seed: Int): String {
    var randomPassword = ""
    // write your code here
    val myGenerator = Random(seed)
    repeat(10){
        randomPassword += myGenerator.nextInt(33,127).toChar()
    }
	return randomPassword
}