package APP_DESIGN_PROJECT.globeyapp.tools

fun checkDateValidity(date:String):Boolean {
    val regex = Regex("[0-9]{2}/[0-9]{2}/[0-9]{4}")
    return date.matches(regex)
}