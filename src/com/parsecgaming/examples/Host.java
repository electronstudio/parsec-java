package com.parsecgaming.examples;

import com.parsecgaming.parsec.ParsecGuest;
import com.parsecgaming.parsec.ParsecHostCallbacks;
import com.parsecgaming.parsec.ParsecHostConfig;
import com.parsecgaming.parsec.ParsecLibrary;
import com.parsecgaming.parsec.ParsecLibrary.ParsecStatus;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.Memory;

import static com.parsecgaming.parsec.ParsecLibrary.ParsecGuestState.GUEST_CONNECTED;
import static com.parsecgaming.parsec.ParsecLibrary.ParsecGuestState.GUEST_DISCONNECTED;
import static com.parsecgaming.parsec.ParsecLibrary.ParsecHostMode.HOST_DESKTOP;
import static com.parsecgaming.parsec.ParsecLibrary.ParsecLogLevel.LOG_DEBUG;

public class Host {

    /* NOTE: ensure your callbacks do not get garbage collected by Java */
    private static LogCallback logCallback = new LogCallback();
    private static GuestStateChange guestStateChange = new GuestStateChange();


    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2) {
            System.out.println("Usage: java Host sessionID serverID");
            return;
        }
        PointerByReference pointer = new PointerByReference();
        Pointer parsec = null;
        try {
            int e = ParsecLibrary.ParsecInit(null, null, pointer);
            if (e != ParsecStatus.PARSEC_OK) throw new RuntimeException("Parsec error " + e);
            parsec = pointer.getValue();

            ParsecLibrary.ParsecSetLogCallback(logCallback, null);

            ParsecHostConfig cfg = ParsecLibrary.ParsecHostDefaults();
            ParsecHostCallbacks cbs = new ParsecHostCallbacks();
            cbs.guestStateChange = guestStateChange;

            String idString = args[0];
            Memory sessionId = new Memory(idString.length() + 1);
            sessionId.setString(0, idString);

            ParsecLibrary.ParsecHostStart(parsec, HOST_DESKTOP, cfg, cbs, null, null,sessionId, Integer.parseInt(args[1]));

            while (true) {
                Thread.sleep(1000);
            }

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

class GuestStateChange implements ParsecHostCallbacks.guestStateChange_callback {
    @Override
    public void apply(ParsecGuest guest, Pointer opaque) {
        switch (guest.state) {
            case GUEST_CONNECTED:
                System.out.println(new String(guest.name) + "#" + guest.id + " connected.");
                break;
            case GUEST_DISCONNECTED:
                System.out.println(new String(guest.name) + "#" + guest.id + "disconnected.");
                break;
        }
    }

}