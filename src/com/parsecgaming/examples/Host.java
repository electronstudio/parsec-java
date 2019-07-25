package com.parsecgaming.examples;

import com.parsecgaming.parsec.*;
import com.parsecgaming.parsec.ParsecLibrary.ParsecStatus;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.Memory;

import static com.parsecgaming.parsec.ParsecLibrary.ParsecGuestState.GUEST_CONNECTED;
import static com.parsecgaming.parsec.ParsecLibrary.ParsecGuestState.GUEST_DISCONNECTED;
import static com.parsecgaming.parsec.ParsecLibrary.ParsecHostEventType.HOST_EVENT_GUEST_STATE_CHANGE;
import static com.parsecgaming.parsec.ParsecLibrary.ParsecHostMode.HOST_DESKTOP;
import static com.parsecgaming.parsec.ParsecLibrary.ParsecLogLevel.LOG_DEBUG;

public class Host {

    /* NOTE: ensure your callbacks do not get garbage collected by Java */
    private static LogCallback logCallback = new LogCallback();


    private static void guestStateChange(ParsecGuest guest) {
        switch (guest.state) {
            case GUEST_CONNECTED:
                System.out.println(new String(guest.name) + "#" + guest.id + " connected.");
                break;
            case GUEST_DISCONNECTED:
                System.out.println(new String(guest.name) + "#" + guest.id + "disconnected.");
                break;
        }
    }


    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2) {
            System.out.println("Usage: java Host sessionID serverID");
            return;
        }
        PointerByReference pointer = new PointerByReference();
        Pointer parsec = null;
        try {
            int e = ParsecLibrary.ParsecInit(ParsecLibrary.PARSEC_VER, null, null, pointer);
            if (e != ParsecStatus.PARSEC_OK) throw new RuntimeException("Parsec error " + e);
            parsec = pointer.getValue();

            ParsecLibrary.ParsecSetLogCallback(logCallback, null);

            ParsecHostConfig cfg = ParsecLibrary.ParsecHostDefaults();

            String idString = args[0];
            Memory sessionId = new Memory(idString.length() + 1);
            sessionId.setString(0, idString);

            ParsecLibrary.ParsecHostStart(parsec, HOST_DESKTOP, cfg, null, sessionId, Integer.parseInt(args[1]));

            ParsecHostEvent event = new ParsecHostEvent();
            while (true) {
                if (ParsecLibrary.ParsecHostPollEvents(parsec, 1000, event) == 1) {
                    switch (event.type) {
                        case HOST_EVENT_GUEST_STATE_CHANGE:
                            event.field1.setType(ParsecGuestStateChangeEvent.class);
                            event.field1.read();
                            guestStateChange(event.field1.guestStateChange.guest);
                            break;
                    }
                }
            }
        } catch (Throwable e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            ParsecLibrary.ParsecDestroy(parsec);
        }
    }
}

class LogCallback implements ParsecLibrary.ParsecLogCallback {
    @Override
    public void apply(int level, Pointer msg, Pointer opaque) {
        System.out.println("[" + (level == LOG_DEBUG ? "D" : "I") + "] " + msg.getString(0));
    }
}



