package org.xapps.apps.weatherx.views.utils


data class Message (
    val type: Type,
    val data: String? = null
) {

    enum class Type {
        MESSAGE,
        ERROR,
        READY
    }

    companion object {

        fun message(data: String? = null): Message = Message(Type.MESSAGE, data)
        fun error(data: String? = null): Message = Message(Type.ERROR, data)
        fun ready(data: String? = null): Message = Message(Type.READY, data)

    }

}