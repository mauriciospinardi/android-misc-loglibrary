package io.cloudwalk.pos.pinpadlibrary.utilities;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import io.cloudwalk.pos.loglibrary.Log;
import io.cloudwalk.pos.pinpadlibrary.ABECS;
import io.cloudwalk.pos.pinpadlibrary.commands.CEX;
import io.cloudwalk.pos.pinpadlibrary.commands.CLX;
import io.cloudwalk.pos.pinpadlibrary.commands.GIX;
import io.cloudwalk.pos.pinpadlibrary.commands.OPN;
import io.cloudwalk.pos.pinpadlibrary.commands.TLE;
import io.cloudwalk.pos.pinpadlibrary.commands.TLI;
import io.cloudwalk.pos.pinpadlibrary.commands.TLR;
import io.cloudwalk.pos.utilitieslibrary.utilities.DataUtility;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.US;

public class PinpadUtility {
    private static final String
            TAG = PinpadUtility.class.getSimpleName();

    private PinpadUtility() {
        Log.d(TAG, "PinpadUtility");

        /* Nothing to do */
    }

    /**
     *
     * @param input
     * @return
     */
    private static byte[] wrapDataPacket(byte[] input, int length)
            throws Exception {
        Log.d(TAG, "wrapDataPacket");

        byte[] pkt = new byte[2044 + 4];

        pkt[0] = 0x16; /* PKTSTART */

        int threshold = Math.min(length, 2044 + 4);

        int j = 1;

        for (int i = 0; i < threshold; i++) {
            switch (input[i]) {
                case 0x13: /* DC3 */
                    pkt[j++] = 0x13;
                    pkt[j++] = 0x33;
                    break;

                case 0x16: /* SYN */
                    pkt[j++] = 0x13;
                    pkt[j++] = 0x36;
                    break;

                case 0x17: /* ETB */
                    pkt[j++] = 0x13;
                    pkt[j++] = 0x37;
                    break;

                default:
                    pkt[j++] = input[i];
                    break;
            }
        }

        pkt[j] = 0x17; /* PKTSTOP */

        byte[] crc = new byte[length + 1];

        System.arraycopy(input, 0, crc, 0, length);

        crc[length] = pkt[j];

        crc = DataUtility.CRC16_XMODEM(crc);

        System.arraycopy(crc, 0, pkt, j + 1, crc.length);

        pkt = Arrays.copyOf(pkt, j + 1 + crc.length);

        return pkt;
    }

    private static byte[] unwrapDataPacket(byte[] input, int length)
            throws Exception {
        Log.d(TAG, "unwrapDataPacket");

        byte[] pkt = new byte[2048 - 4];

        int threshold = Math.min(length, 2044 + 4);

        int j = 0;

        for (int i = 1; i < threshold; i++) {
            switch (input[i]) {
                case 0x16: /* PKTSTART */
                    continue;

                case 0x17: /* PKTSTOP  */
                    i = threshold;
                    continue;

                case 0x13:
                    switch (input[++i]) {
                        case 0x33: /* DC3 */
                            pkt[j++] = 0x13;
                            break;

                        case 0x36: /* SYN */
                            pkt[j++] = 0x16;
                            break;

                        case 0x37: /* ETB */
                            pkt[j++] = 0x17;
                            break;

                        default:
                            pkt[j++] = input[i];
                            break;
                    }
                    break;

                default:
                    pkt[j++] = input[i];
                    break;
            }
        }

        // TODO: validate CRC and throw exception

        byte[] output = new byte[j];

        System.arraycopy(pkt, 0, output, 0, j);

        return output;
    }

    /**
     *
     * @param input
     * @return
     */
    public static Bundle parseResponseDataPacket(byte[] input, int length)
            throws Exception {
        Log.d(TAG, "parseResponseDataPacket");

        byte[] response = unwrapDataPacket(input, length);

        String CMD_ID = String.format(US, "%c%c%c", response[0], response[1], response[2]);

        switch (CMD_ID) {
            case ABECS.OPN: return OPN.parseResponseDataPacket(response, response.length);
            case ABECS.GIX: return GIX.parseResponseDataPacket(response, response.length);
            case ABECS.CLX: return CLX.parseResponseDataPacket(response, response.length);

            case ABECS.TLI: return TLI.parseResponseDataPacket(response, response.length);
            case ABECS.TLR: return TLR.parseResponseDataPacket(response, response.length);
            case ABECS.TLE: return TLE.parseResponseDataPacket(response, response.length);

            case ABECS.CEX: return CEX.parseResponseDataPacket(response, response.length);

            default:
                /* Nothing to do */
                break;
        }

        throw new RuntimeException("Unknown or unhandled CMD_ID [" + CMD_ID + "]");
    }

    public static Bundle parseResponseTLV(byte[] stream, int length) {
        Bundle output = new Bundle();

        int cursor    = 0;
        int threshold = 0;

        while ((cursor += threshold) < length) {
            byte[] T = new byte[4];
            byte[] L = new byte[4];

            System.arraycopy(stream, cursor, T, 2, 2); cursor += 2;

            int tag = ByteBuffer.wrap(T).getInt();

            System.arraycopy(stream, cursor, L, 2, 2); cursor += 2;

            threshold = ByteBuffer.wrap(L).getInt();

            byte[] V = new byte[threshold];

            System.arraycopy(stream, cursor, V, 0, threshold);

            switch (tag) {
                case 0x8001: output.putString(ABECS.PP_SERNUM,      new String(V)); break;
                case 0x8002: output.putString(ABECS.PP_PARTNBR,     new String(V)); break;
                case 0x8003: output.putString(ABECS.PP_MODEL,       new String(V)); break;
                case 0x8004: output.putString(ABECS.PP_MNNAME,      new String(V)); break;
                case 0x8005: output.putString(ABECS.PP_CAPAB,       new String(V)); break;
                case 0x8006: output.putString(ABECS.PP_SOVER,       new String(V)); break;
                case 0x8007: output.putString(ABECS.PP_SPECVER,     new String(V)); break;
                case 0x8008: output.putString(ABECS.PP_MANVERS,     new String(V)); break;
                case 0x8009: output.putString(ABECS.PP_APPVERS,     new String(V)); break;
                case 0x800A: output.putString(ABECS.PP_GENVERS,     new String(V)); break;
                case 0x8010: output.putString(ABECS.PP_KRNLVER,     new String(V)); break;
                case 0x8011: output.putString(ABECS.PP_CTLSVER,     new String(V)); break;
                case 0x8012: output.putString(ABECS.PP_MCTLSVER,    new String(V)); break;
                case 0x8013: output.putString(ABECS.PP_VCTLSVER,    new String(V)); break;
                case 0x8014: output.putString(ABECS.PP_AECTLSVER,   new String(V)); break;
                case 0x8015: output.putString(ABECS.PP_DPCTLSVER,   new String(V)); break;
                case 0x8016: output.putString(ABECS.PP_PUREVER,     new String(V)); break;
                case 0x8032: output.putString(ABECS.PP_MKTDESP,     new String(V)); break;
                case 0x8033: output.putString(ABECS.PP_MKTDESD,     new String(V)); break;
                case 0x8035: output.putString(ABECS.PP_DKPTTDESP,   new String(V)); break;
                case 0x8036: output.putString(ABECS.PP_DKPTTDESD,   new String(V)); break;
                case 0x8040: output.putString(ABECS.PP_EVENT,       new String(V)); break;
                case 0x8041: output.putString(ABECS.PP_TRK1INC,     new String(V)); break;
                case 0x8042: output.putString(ABECS.PP_TRK2INC,     new String(V)); break;
                case 0x8043: output.putString(ABECS.PP_TRK3INC,     new String(V)); break;

                case 0x8020: // TODO: intercept response @PinpadService!?
                    output.putString(ABECS.PP_DSPTXTSZ, "0000");
                    break;

                case 0x8021: // TODO: intercept response @PinpadService!?
                case 0x8062: // TODO: 0x8020, 0x8021 and 0x8062 are all coming back as { 0x00, 0x00 ... }
                    output.putString((tag != 0x8062) ? ABECS.PP_DSPGRSZ : ABECS.PP_TLRMEM, "00000000");
                    break;

                case 0x805A:
                    output.putString(ABECS.PP_BIGRAND, DataUtility.byteToHexString(V));
                    break;

                default:
                    if (tag >= 0x9100 && tag <= 0x9163) {
                        String key = ABECS.PP_KSNTDESPnn.replace("nn", String.format(US, "%02d", (tag - 0x9100)));

                        output.putString(key, DataUtility.byteToHexString(V));
                        continue;
                    }

                    if (tag >= 0x9200 && tag <= 0x9263) {
                        String key = ABECS.PP_KSNTDESDnn.replace("nn", String.format(US, "%02d", (tag - 0x9200)));

                        output.putString(key, DataUtility.byteToHexString(V));
                        continue;
                    }

                    if (tag >= 0x9300 && tag <= 0x9363) {
                        String key = ABECS.PP_TABVERnn  .replace("nn", String.format(US, "%02d", (tag - 0x9300)));

                        output.putString(key, new String(V));
                        continue;
                    }

                    /* Nothing to do */
                    break;
            }
        }

        return output;
    }

    /**
     *
     * @param input
     * @return
     */
    public static byte[] buildRequestDataPacket(@NotNull Bundle input)
            throws Exception {
        Log.d(TAG, "buildRequestDataPacket");

        byte[] request = null;

        String CMD_ID = input.getString(ABECS.CMD_ID, "UNKNOWN");

        switch (CMD_ID) {
            case ABECS.OPN: request = OPN.buildRequestDataPacket(input); break;
            case ABECS.GIX: request = GIX.buildRequestDataPacket(input); break;
            case ABECS.CLX: request = CLX.buildRequestDataPacket(input); break;

            case ABECS.TLI: request = TLI.buildRequestDataPacket(input); break;
            case ABECS.TLR: request = TLR.buildRequestDataPacket(input); break;
            case ABECS.TLE: request = TLE.buildRequestDataPacket(input); break;

            case ABECS.CEX: request = CEX.buildRequestDataPacket(input); break;

            default:
                /* Nothing to do */
                break;
        }

        if (request != null) {
            return wrapDataPacket(request, request.length);
        }

        throw new RuntimeException("Unknown or unhandled CMD_ID [" + CMD_ID + "]");
    }

    public static byte[] buildRequestTLV(@NotNull ABECS.TYPE type, @NotNull String tag, @NotNull String value)
            throws Exception {
        Log.d(TAG, "buildRequestTLV");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        byte[] T = null;
        byte[] L = null;
        byte[] V = null;

        switch (type) { // TODO: ensure alignment and length!?
            case A:
            case S:
            case N:
                T = DataUtility.hexStringToByteArray(tag);
                L = ByteBuffer.allocate(2).putShort((short) (value.length())).array();
                V = value.getBytes(UTF_8);
                break;

            case H:
            case X:
            case B:
                T = DataUtility.hexStringToByteArray(tag);
                L = ByteBuffer.allocate(2).putShort((short) (value.length() / 2)).array();
                V = DataUtility.hexStringToByteArray(value);
                break;
        }

        stream.write(T);
        stream.write(L);
        stream.write(V);

        return stream.toByteArray();
    }
}
