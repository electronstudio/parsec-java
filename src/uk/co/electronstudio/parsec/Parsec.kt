package uk.co.electronstudio.parsec

import com.parsecgaming.parsec.*
import com.parsecgaming.parsec.ParsecLibrary.PARSEC_VER
import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference
import sun.management.Agent.error
import com.parsecgaming.parsec.ParsecLibrary.ParsecHostEventType.*


class Parsec @JvmOverloads constructor(val logListener: ParsecLogListener, upnp: Boolean? = null, clientPort: Int? = null, serverPort: Int? = null) : AutoCloseable {
    override fun close() {
        dispose()
    }

    val parsecPointer: Pointer?

    val parsecRef: PointerByReference = PointerByReference()

    @Volatile
    var statusCode = 0

    val parsecConfig = ParsecConfig()

    val parsecLogCallback = object : ParsecLibrary.ParsecLogCallback {
        override fun apply(level: Int, msg: Pointer?, opaque: Pointer?) {
            logListener.log(level, if (msg != null) msg.getString(0) else "")
        }
    }

    lateinit var parsecHostListener: ParsecHostListener

    init {

        ParsecLibrary.ParsecSetLogCallback(parsecLogCallback, null)

        upnp?.let { parsecConfig.upnp = if (upnp) 1 else 0 }
        clientPort?.let { parsecConfig.clientPort = it }
        serverPort?.let { parsecConfig.hostPort = it }

        statusCode = ParsecLibrary.ParsecInit(PARSEC_VER, parsecConfig, null, parsecRef)



        parsecPointer = parsecRef.value

    }

    val guest = ParsecGuest()
    val msg = ParsecMessage()

    val events = arrayListOf<InputEvent>()

    fun hostPollInput(): List<InputEvent> {
        events.clear()
        while (ParsecLibrary.ParsecHostPollInput(parsecPointer, 0, guest, msg).toInt() == 1) {
            val e = InputEvent.build(guest, msg)
            if (e != null) events.add(e)
        }
        return events
    }

    fun runHostCallbacks() {
        val event = ParsecHostEvent()
        while (ParsecLibrary.ParsecHostPollEvents(parsecPointer, 0, event).toInt() == 1) {
            when (event.type) {
                HOST_EVENT_GUEST_STATE_CHANGE -> {
                    event.field1.setType(ParsecGuestStateChangeEvent::class.java)
                    event.field1.read()
                    val guest = event.field1.guestStateChange.guest
                    val name = String(guest.name.takeWhile { it != 0.toByte() }.toByteArray())
                    when (guest.state) {
                        ParsecLibrary.ParsecGuestState.GUEST_CONNECTED -> {
                            parsecHostListener.guestConnected(guest.id, name, guest.attemptID)
                        }
                        ParsecLibrary.ParsecGuestState.GUEST_DISCONNECTED -> {
                            parsecHostListener.guestDisconnected(guest.id, name, guest.attemptID)
                        }
                        ParsecLibrary.ParsecGuestState.GUEST_CONNECTING -> {
                            parsecHostListener.guestConnecting(guest.id, name, guest.attemptID)
                        }
                        ParsecLibrary.ParsecGuestState.GUEST_FAILED -> {
                            parsecHostListener.guestFailed(guest.id, name, guest.attemptID)
                        }
                        ParsecLibrary.ParsecGuestState.GUEST_WAITING -> {
                            parsecHostListener.guestWaiting(guest.id, name, guest.attemptID)
                        }
                    }
                }
                HOST_EVENT_USER_DATA -> {
                    event.field1.setType(ParsecUserDataEvent::class.java)
                    event.field1.read()
                    val guest = event.field1.userData.guest
                    val id = event.field1.userData.id
                    val key = event.field1.userData.key
                    val buffer = ParsecLibrary.ParsecGetBuffer(parsecPointer, key)
                    parsecHostListener.userData(guest, id, buffer.getString(0))
                    ParsecLibrary.ParsecFree(buffer)
                }
//                HOST_EVENT_SERVER_ID -> {
//                    event.field1.setType(ParsecServerIDEvent::class.java)
//                    event.field1.read()
//                    parsecHostListener.serverId(event.field1.serverID.userID, event.field1.serverID.serverID)
//                }
                HOST_EVENT_INVALID_SESSION_ID -> {
                    parsecHostListener.invalidSessionId()
                }
            }
        }
    }

    fun hostStart(mode: Int, parsecHostConfig: ParsecHostConfig?, parsecHostListener: ParsecHostListener,  sessionId: String): Int {
        val sessionIdM = Memory((sessionId.length + 1).toLong()) // WARNING: assumes ascii-only string
        sessionIdM.setString(0, sessionId)

//        val name = Memory((nameString.length + 1).toLong()).also {
//            it.setString(0, nameString)
//        }

        // parsecHostCallbacks = createCallBacks(parsecHostListener)
        this.parsecHostListener = parsecHostListener

        statusCode = ParsecLibrary.ParsecHostStart(parsecPointer,
                mode,
                parsecHostConfig,
                sessionIdM
                )
        return statusCode
    }

    @JvmOverloads
    fun hostStartDesktop(parsecHostConfig: ParsecHostConfig?, parsecHostListener: ParsecHostListener, sessionId: String): Int {
        return hostStart(ParsecLibrary.ParsecHostMode.HOST_DESKTOP, parsecHostConfig, parsecHostListener, sessionId)
    }

    @JvmOverloads
    fun hostStartGame(parsecHostConfig: ParsecHostConfig?, parsecHostListener: ParsecHostListener, sessionId: String): Int {
        return hostStart(ParsecLibrary.ParsecHostMode.HOST_GAME, parsecHostConfig, parsecHostListener, sessionId)
    }

    fun hostStop() {
        statusCode = 0
        ParsecLibrary.ParsecHostStop(parsecPointer)

    }

    fun submitFrame(textureObjectHandle: Int) {
        ParsecLibrary.ParsecHostGLSubmitFrame(parsecPointer, textureObjectHandle)
    }

    fun submitAudio(rate: Int, pcm: ByteArray, samples: Int) {
        val buffer = Memory(pcm.size.toLong())
        buffer.write(0L, pcm, 0, pcm.size)
        ParsecLibrary.ParsecHostSubmitAudio(parsecPointer, ParsecLibrary.ParsecPCMFormat.PCM_FORMAT_INT16, rate, buffer, samples)
    }

    fun submitAudioFloat(rate: Int, pcm: ByteArray, samples: Int) {
        val buffer = Memory(pcm.size.toLong())
        buffer.write(0L, pcm, 0, pcm.size)
        ParsecLibrary.ParsecHostSubmitAudio(parsecPointer, ParsecLibrary.ParsecPCMFormat.PCM_FORMAT_FLOAT, rate, buffer, samples)
    }

    fun hostAllowGuest(attemptID: ByteArray, allow: Boolean) {
        val m = Memory(attemptID.size.toLong())
        m.write(0, attemptID, 0, attemptID.size)
        ParsecLibrary.ParsecHostAllowGuest(parsecPointer, m, if (allow) 1.toByte() else 0.toByte())
    }

    fun sendMessage(guestId: Int, text: String){
        val t = Memory((text.length + 1).toLong()).also {
            it.setString(0, text)
        }
        ParsecLibrary.ParsecHostSendUserData(parsecPointer, guestId, 0, t)
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
                    msg.field1.read()
                    val id = msg.field1.gamepadButton.id
                    val button = msg.field1.gamepadButton.button
                    val pressed = (msg.field1.gamepadButton.pressed.toInt() == 1)
                    return GamepadButtonEvent(guest.id, id, button, pressed)
                }
                ParsecLibrary.ParsecMessageType.MESSAGE_GAMEPAD_AXIS -> {
                    msg.field1.setType(ParsecGamepadAxisMessage::class.java)
                    msg.field1.read()
                    val axis = msg.field1.gamepadAxis.axis
                    val id = msg.field1.gamepadAxis.id
                    val value = msg.field1.gamepadAxis.value
                    return GamepadAxisEvent(guest.id, id, axis, value)
                }
                ParsecLibrary.ParsecMessageType.MESSAGE_KEYBOARD -> {
                    msg.field1.setType(ParsecKeyboardMessage::class.java)
                    msg.field1.read()
                    val pressed = (msg.field1.keyboard.pressed.toInt() == 1)
                    val code = msg.field1.keyboard.code
                    val mod = msg.field1.keyboard.mod
                    return KeyboardEvent(guest.id, pressed, code, mod)
                }
                ParsecLibrary.ParsecMessageType.MESSAGE_MOUSE_BUTTON -> {
                    msg.field1.setType(ParsecMouseButtonMessage::class.java)
                    msg.field1.read()
                    val pressed = (msg.field1.mouseButton.pressed.toInt() == 1)
                    val button = msg.field1.mouseButton.button
                    return MouseButtonEvent(guest.id, pressed, button)
                }
                ParsecLibrary.ParsecMessageType.MESSAGE_MOUSE_WHEEL -> {
                    msg.field1.setType(ParsecMouseWheelMessage::class.java)
                    msg.field1.read()
                    val x = msg.field1.mouseWheel.x
                    val y = msg.field1.mouseWheel.y
                    return MouseWheelEvent(guest.id, x, y)
                }
                ParsecLibrary.ParsecMessageType.MESSAGE_MOUSE_MOTION -> {
                    msg.field1.setType(ParsecMouseMotionMessage::class.java)
                    msg.field1.read()
                    val relative = (msg.field1.mouseMotion.relative.toInt() == 1)
                    val x = msg.field1.mouseMotion.x
                    val y = msg.field1.mouseMotion.y
                    return MouseMotionEvent(guest.id, relative, x, y)
                }
                ParsecLibrary.ParsecMessageType.MESSAGE_GAMEPAD_UNPLUG -> {
                    msg.field1.setType(ParsecGamepadUnplugMessage::class.java)
                    msg.field1.read()
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
//    fun serverId(userID: Int, serverID: Int)
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



