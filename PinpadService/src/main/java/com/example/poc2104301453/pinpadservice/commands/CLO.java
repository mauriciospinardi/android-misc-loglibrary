package com.example.poc2104301453.pinpadservice.commands;

import android.os.Bundle;

import com.example.poc2104301453.pinpadservice.PinpadAbstractionLayer;

import java.util.concurrent.Semaphore;

import br.com.verifone.bibliotecapinpad.AcessoFuncoesPinpad;
import br.com.verifone.bibliotecapinpad.entradas.EntradaComandoClose;

public class CLO {
    private static final String TAG_LOGCAT = CLO.class.getSimpleName();

    public static Bundle clo(Bundle input)
            throws Exception {
        AcessoFuncoesPinpad pinpad = PinpadAbstractionLayer.getInstance().getPinpad();
        String CMD_ID  = input.getString("CMD_ID");

        final Bundle[] output = { new Bundle() };
        final Semaphore[] semaphore = { new Semaphore(0, true) };

        pinpad.close(() -> {
            output[0].putString("RSP_ID", CMD_ID);
            output[0].putInt   ("RSP_STAT", 0);

            semaphore[0].release();
        });

        semaphore[0].acquireUninterruptibly();

        return output[0];
    }
}
