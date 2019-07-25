package uk.co.electronstudio.parsec

import com.parsecgaming.parsec.ParsecGamepadAxisMessage
import com.parsecgaming.parsec.ParsecGamepadButtonMessage
import com.parsecgaming.parsec.ParsecGamepadUnplugMessage
import com.parsecgaming.parsec.ParsecGuest
import com.parsecgaming.parsec.ParsecHostCallbacks
import com.parsecgaming.parsec.ParsecHostConfig
import com.parsecgaming.parsec.ParsecKeyboardMessage
import com.parsecgaming.parsec.ParsecLibrary
import com.parsecgaming.parsec.ParsecMessage
import com.parsecgaming.parsec.ParsecMouseButtonMessage
import com.parsecgaming.parsec.ParsecMouseMotionMessage
import com.parsecgaming.parsec.ParsecMouseWheelMessage
import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference
import sun.management.Agent.error


class Parsec @JvmOverloads constructor(val logListener: ParsecLogListener, upnp: Boolean? = null, clientPort: Int? = null, serverPort: Int? = null) : AutoCloseable {
    override fun close() {
        dispose()
    }

    val parsecPointer: Pointer?

    val parsecRef: PointerByReference = PointerByReference()

    @Volatile
    var statusCode = 0

    val parsecConfig = ParsecLibrary.ParsecDefaults()

    val parsecLogCallback = object : ParsecLibrary.ParsecLogCallback {
        override fun apply(level: Int, msg: Pointer?, opaque: Pointer?) {
            logListener.log(level, if (msg != null) msg.getString(0) else "")
        }
    }

    lateinit var parsecHostCallbacks: ParsecHostCallbacks   // reference kept to make sure it doesnt get GCed


    private fun createCallBacks(callbacks: ParsecHostListener): ParsecHostCallbacks {

        val gst = object : ParsecHostCallbacks.guestStateChange_callback {
            override fun apply(guest: ParsecGuest?, opaque: Pointer?) {
                if (guest != null) {
                    val name = String(guest.name)
                    when (guest.state) {
                        ParsecLibrary.ParsecGuestState.GUEST_CONNECTED -> {
                            callbacks.guestConnected(guest.id, name, guest.attemptID)
                        }
                        ParsecLibrary.ParsecGuestState.GUEST_DISCONNECTED -> {
                            callbacks.guestDisconnected(guest.id, name, guest.attemptID)
                        }
                        ParsecLibrary.ParsecGuestState.GUEST_CONNECTING -> {
                            callbacks.guestConnecting(guest.id, name, guest.attemptID)
                        }
                        ParsecLibrary.ParsecGuestState.GUEST_FAILED -> {
                            callbacks.guestFailed(guest.id, name, guest.attemptID)
                        }
                        ParsecLibrary.ParsecGuestState.GUEST_WAITING -> {
                            callbacks.guestWaiting(guest.id, name, guest.attemptID)
                        }
                    }
                }
            }
        }

        val udc = object : ParsecHostCallbacks.userData_callback {
            override fun apply(guest: ParsecGuest?, id: Int, text: Pointer?, opaque: Pointer?) {
                if (guest != null) {
                    callbacks.userData(guest, id, if (text != null) text.getString(0) else "")
                }
            }

        }

        val sic = object : ParsecHostCallbacks.serverID_callback {
            override fun apply(hostID: Int, serverID: Int, opaque: Pointer?) {

                callbacks.serverId(hostID, serverID)

            }
        }

        val isi = object : ParsecHostCallbacks.invalidSessionID_callback {
            override fun apply(opaque: Pointer?) {
                callbacks.invalidSessionId()
            }
        }

        return ParsecHostCallbacks(gst, udc, sic, isi)
    }

    init {

        ParsecLibrary.ParsecSetLogCallback(parsecLogCallback, null)


        upnp?.let { parsecConfig.upnp = if (upnp) 1 else 0 }
        clientPort?.let { parsecConfig.clientPort = it }
        serverPort?.let { parsecConfig.serverPort = it }

        statusCode = ParsecLibrary.ParsecInit(parsecConfig, null, parsecRef)



        parsecPointer = parsecRef.value

    }

    val guest = ParsecGuest()
    val msg = ParsecMessage()

    fun hostPollInput(): List<InputEvent> {
        val events = arrayListOf<InputEvent>()
        while (ParsecLibrary.ParsecHostPollInput(parsecPointer, 0, guest, msg).toInt() == 1) {
            val e = InputEvent.build(guest, msg)
            if (e != null) events.add(e)
        }
        return events
    }


    fun hostStart(mode: Int, parsecHostConfig: ParsecHostConfig?, parsecHostListener: ParsecHostListener, nameString: String, sessionId: String, serverId: Int, opaque: Pointer?): Int {
        val sessionIdM = Memory((sessionId.length + 1).toLong()) // WARNING: assumes ascii-only string
        sessionIdM.setString(0, sessionId)

        val name = Memory((nameString.length + 1).toLong()).also {
            it.setString(0, nameString)
        }

        parsecHostCallbacks = createCallBacks(parsecHostListener)

        statusCode = ParsecLibrary.ParsecHostStart(parsecPointer,
                mode,
                parsecHostConfig,
                parsecHostCallbacks,
                opaque,
                name,
                sessionIdM,
                serverId)
        return statusCode
    }

    @JvmOverloads
    fun hostStartDesktop(parsecHostConfig: ParsecHostConfig?, parsecHostListener: ParsecHostListener, name: String, sessionId: String, serverId: Int = 0, opaque: Pointer? = null): Int {
        return hostStart(ParsecLibrary.ParsecHostMode.HOST_DESKTOP, parsecHostConfig, parsecHostListener, name, sessionId, serverId, opaque)
    }

    @JvmOverloads
    fun hostStartGame(parsecHostConfig: ParsecHostConfig?, parsecHostListener: ParsecHostListener, name: String, sessionId: String, serverId: Int = 0, opaque: Pointer? = null): Int {
        return hostStart(ParsecLibrary.ParsecHostMode.HOST_GAME, parsecHostConfig, parsecHostListener, name, sessionId, serverId, opaque)
    }

    fun hostStop() {
        statusCode = 0
        ParsecLibrary.ParsecHostStop(parsecPointer)

    }

    fun submitFrame(textureObjectHandle: Int) {
        ParsecLibrary.ParsecHostGLSubmitFrame(parsecPointer, textureObjectHandle)
    }

    fun hostAllowGuest(attemptID: ByteArray, allow: Boolean) {
        val m = Memory(attemptID.size.toLong())
        m.write(0, attemptID, 0, attemptID.size)
        ParsecLibrary.ParsecHostAllowGuest(parsecPointer, m, if (allow) 1.toByte() else 0.toByte())
    }

    fun dispose() {
        ParsecLibrary.ParsecDestroy(parsecPointer)
    }
}

abstract class InputEvent private constructor() {
    abstract val guestId: Int

    companion object {
        @JvmStatic
        fun build(guest: ParsecGuest, msg: ParsecMessage): InputEvent? {
            when (msg.type) {
                ParsecLibrary.ParsecMessageType.MESSAGE_GAMEPAD_BUTTON -> {
                    msg.field1.setType(ParsecGamepadButtonMessage::class.java)
                    val id = msg.field1.gamepadButton.id
                    val button = msg.field1.gamepadButton.button
                    val pressed = (msg.field1.gamepadButton.pressed.toInt() == 1)
                    return GamepadButtonEvent(guest.id, id, button, pressed)
                }
                ParsecLibrary.ParsecMessageType.MESSAGE_GAMEPAD_AXIS -> {
                    msg.field1.setType(ParsecGamepadAxisMessage::class.java)
                    val axis = msg.field1.gamepadAxis.axis
                    val id = msg.field1.gamepadAxis.id
                    val value = msg.field1.gamepadAxis.value
                    return GamepadAxisEvent(guest.id, id, axis, value)
                }
                ParsecLibrary.ParsecMessageType.MESSAGE_KEYBOARD -> {
                    msg.field1.setType(ParsecKeyboardMessage::class.java)
                    val pressed = (msg.field1.keyboard.pressed.toInt() == 1)
                    val code = msg.field1.keyboard.code
                    val mod = msg.field1.keyboard.mod
                    return KeyboardEvent(guest.id, pressed, code, mod)
                }
                ParsecLibrary.ParsecMessageType.MESSAGE_MOUSE_BUTTON -> {
                    msg.field1.setType(ParsecMouseButtonMessage::class.java)
                    val pressed = (msg.field1.mouseButton.pressed.toInt() == 1)
                    val button = msg.field1.mouseButton.button
                    return MouseButtonEvent(guest.id, pressed, button)
                }
                ParsecLibrary.ParsecMessageType.MESSAGE_MOUSE_WHEEL -> {
                    msg.field1.setType(ParsecMouseWheelMessage::class.java)
                    val x = msg.field1.mouseWheel.x
                    val y = msg.field1.mouseWheel.y
                    return MouseWheelEvent(guest.id, x, y)
                }
                ParsecLibrary.ParsecMessageType.MESSAGE_MOUSE_MOTION -> {
                    msg.field1.setType(ParsecMouseMotionMessage::class.java)
                    val relative = (msg.field1.mouseMotion.relative.toInt() == 1)
                    val x = msg.field1.mouseMotion.x
                    val y = msg.field1.mouseMotion.y
                    return MouseMotionEvent(guest.id, relative, x, y)
                }
                ParsecLibrary.ParsecMessageType.MESSAGE_GAMEPAD_UNPLUG -> {
                    msg.field1.setType(ParsecGamepadUnplugMessage::class.java)
                    val id = msg.field1.gamepadUnplug.id
                    return GamepadUnpluggedEvent(guest.id, id)
                }
                else -> {
                    error("uknown msg type ${msg.type}")
                    return null
                }
            }
        }
    }

    data class GamepadButtonEvent(override val guestId: Int, val id: Int, val button: Int, val pressed: Boolean) : InputEvent()
    data class GamepadAxisEvent(override val guestId: Int, val id: Int, val axis: Int, val value: Short) : InputEvent()
    data class KeyboardEvent(override val guestId: Int, val pressed: Boolean, val code: Int, val mod: Int) : InputEvent()
    data class MouseButtonEvent(override val guestId: Int, val pressed: Boolean, val button: Int) : InputEvent()
    data class MouseWheelEvent(override val guestId: Int, val x: Int, val y: Int) : InputEvent()
    data class MouseMotionEvent(override val guestId: Int, val relative: Boolean, val x: Int, val y: Int) : InputEvent()
    data class GamepadUnpluggedEvent(override val guestId: Int, val id: Int) : InputEvent()
}

interface ParsecHostListener {
    fun userData(guest: ParsecGuest, id: Int, text: String)
    fun serverId(hostID: Int, serverID: Int)
    fun invalidSessionId()
    fun guestConnected(id: Int, name: String, attemptID: ByteArray)
    fun guestDisconnected(id: Int, name: String, attemptID: ByteArray)
    fun guestConnecting(id: Int, name: String, attemptID: ByteArray)
    fun guestFailed(id: Int, name: String, attemptID: ByteArray)
    fun guestWaiting(id: Int, name: String, attemptID: ByteArray)
}

interface ParsecLogListener {
    fun log(level: Int, msg: String)
}



