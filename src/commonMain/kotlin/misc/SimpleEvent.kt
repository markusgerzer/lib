package misc


class SimpleEvent {
    private val _callbacks = mutableListOf<() -> Unit>()
    fun addCallback(callback: () -> Unit) = _callbacks.add(callback)
    operator fun invoke() { for (callback in _callbacks) callback() }
}

class SimpleEventSuspend {
    private val _callbacks = mutableListOf<suspend () -> Unit>()
    fun addCallback(callback: suspend () -> Unit) = _callbacks.add(callback)
    suspend operator fun invoke() { for (callback in _callbacks) callback() }
}

// Example
/*
class Example {
    private val _exit = SimpleEvent()
    fun onExit() = _exit.addCallback {  }
    fun exit() { _exit() }
}
*/
