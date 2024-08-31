package info.passdaily.camrelconvertapp.typeofuser.parent.notification

open class ListItem(
    val type: Int
) {
    companion object {
        const val TYPE_DATE = 0
        const val TYPE_GENERAL = 1
    }
}