package io.cloudwalk.pos.pinpadlibrary.commands;

import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import io.cloudwalk.pos.loglibrary.Log;
import io.cloudwalk.pos.pinpadlibrary.ABECS;
import io.cloudwalk.pos.pinpadlibrary.utilities.PinpadUtility;
import io.cloudwalk.pos.utilitieslibrary.utilities.DataUtility;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.US;

public class GIX {
    private static final String
            TAG = GIX.class.getSimpleName();

    private static final byte[]
            SPE_IDLIST = new byte[] { 0x00, 0x01 };

    private GIX() {
        Log.d(TAG, "GIX");

        /* Nothing to do */
    }

    public static Bundle parseResponseDataPacket(byte[] input, int length)
            throws Exception {
        Log.d(TAG, "parseResponseDataPacket");

        byte[] RSP_ID       = new byte[3];
        byte[] RSP_STAT     = new byte[3];
        byte[] RSP_LEN1     = new byte[3];
        byte[] RSP_DATA     = null;

        System.arraycopy(input, 0, RSP_ID,   0, 3);
        System.arraycopy(input, 3, RSP_STAT, 0, 3);

        ABECS.STAT STAT = ABECS.STAT.values()[DataUtility.byteArrayToInt(RSP_STAT, RSP_STAT.length)];

        Bundle output = new Bundle();

        output.putString      (ABECS.RSP_ID,   new String(RSP_ID));
        output.putSerializable(ABECS.RSP_STAT, STAT);

        switch (STAT) {
            case ST_OK:
                System.arraycopy(input, 6, RSP_LEN1, 0, 3);

                RSP_DATA = new byte[DataUtility.byteArrayToInt(RSP_LEN1, RSP_LEN1.length)];

                System.arraycopy(input, 9, RSP_DATA, 0, RSP_DATA.length);

                output.putAll(PinpadUtility.parseResponseTLV(RSP_DATA, RSP_DATA.length));

                /* no break */

            default:
                return output;
        }
    }

    public static byte[] buildRequestDataPacket(Bundle input)
            throws Exception {
        Log.d(TAG, "buildRequestDataPacket");

        ByteArrayOutputStream[] stream = { new ByteArrayOutputStream(), new ByteArrayOutputStream() };

        String CMD_ID       = input.getString(ABECS.CMD_ID);
        String SPE_IDLIST   = input.getString(ABECS.SPE_IDLIST);

        if (SPE_IDLIST != null) {
            stream[1].write(PinpadUtility.buildRequestTLV(ABECS.TYPE.B, "0001", SPE_IDLIST));
        }

        byte[] CMD_DATA = stream[1].toByteArray();

        return DataUtility.concatByteArray(CMD_ID.getBytes(UTF_8), String.format(US, "%03d", CMD_DATA.length).getBytes(UTF_8), CMD_DATA);
    }
}
