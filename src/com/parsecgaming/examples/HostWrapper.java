package com.parsecgaming.examples;

import com.parsecgaming.parsec.ParsecGuest;
import com.parsecgaming.parsec.ParsecLibrary.ParsecStatus;
import org.jetbrains.annotations.NotNull;
import uk.co.electronstudio.parsec.*;

import static com.parsecgaming.parsec.ParsecLibrary.ParsecLogLevel.LOG_DEBUG;

public class HostWrapper {


    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) {
            System.out.println("Usage: java HostWrapper sessionID");
            return;
        }


        try (Parsec parsec = new Parsec(new LogListener())) {

            if (parsec.getStatusCode() != ParsecStatus.PARSEC_OK)
                throw new RuntimeException("Parsec error " + parsec.getStatusCode());

            parsec.hostStartDesktop(null, new HostListener(),  args[0]);

            if (parsec.getStatusCode() != ParsecStatus.PARSEC_OK)
                throw new RuntimeException("Parsec error " + parsec.getStatusCode());

            while (true) {
                parsec.runHostCallbacks();
                Thread.sleep(1000);
            }
        }
    }
}


class LogListener implements ParsecLogListener {
    @Override
    public void log(int level, @NotNull String msg) {
        System.out.println("[" + (level == LOG_DEBUG ? "D" : "I") + "] " + msg);
    }
}

class HostListener implements ParsecHostListener {

    @Override
    public void userData(@NotNull ParsecGuest guest, int id, @NotNull String text) {
        System.out.println("guest "+guest.id+" userData "+text);
    }

    @Override
    public void invalidSessionId() {
        System.out.println("invalid sesion id");
    }

    @Override
    public void guestConnected(int id, @NotNull String name, @NotNull byte[] attemptID) {
        System.out.println(name + "#" + id + " connected.");
    }

    @Override
    public void guestDisconnected(int id, @NotNull String name, @NotNull byte[] attemptID) {
        System.out.println(name + "#" + id + " disconnected.");
    }

    @Override
    public void guestConnecting(int id, @NotNull String name, @NotNull byte[] attemptID) {

    }

    @Override
    public void guestFailed(int id, @NotNull String name, @NotNull byte[] attemptID) {

    }

    @Override
    public void guestWaiting(int id, @NotNull String name, @NotNull byte[] attemptID) {

    }
}

