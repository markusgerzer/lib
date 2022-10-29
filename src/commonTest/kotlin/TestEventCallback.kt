import misc.*
import kotlin.test.*

class TestEventCallback {
     @Test
     fun test1() {
         class Example () {
             private val c = SimpleEvent()
             fun onC(callback: () -> Unit) = c.addCallback(callback)
             fun trigger() = c()
         }

         val e = Example()
         var a = 0
         e.onC{ a += 1 }
         e.onC{ a += 2 }
         assertEquals(0, a)
         e.trigger()
         assertEquals(3, a)
     }
}

