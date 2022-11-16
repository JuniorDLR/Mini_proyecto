package ni.edu.uca.mini_proyecto


open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Permitir lectura externa pero no escritura
    /**
     * Devuelve el contenido e impide su uso nuevamente.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Devuelve el contenido, incluso si ya ha sido manipulado.
     */
    fun peekContent(): T = content
}