package com.tunjid.rcswitchcontrol.nsd.protocols;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.tunjid.rcswitchcontrol.App;
import com.tunjid.rcswitchcontrol.model.Payload;

import java.io.Closeable;
import java.io.PrintWriter;

/**
 * Class for Server communication with input from client
 * <p>
 * Created by tj.dahunsi on 2/6/17.
 */

public abstract class CommsProtocol implements Closeable {

    public static final String PING = "Ping";
    static final String RESET = "Reset";

    final Context appContext;

    @Nullable
    final PrintWriter printWriter;

    CommsProtocol() {
        this(null);
    }

    CommsProtocol(@Nullable PrintWriter printWriter) {
        this.printWriter = printWriter;
        appContext = App.getInstance();
    }

    public final Payload processInput(@Nullable String input) {
        return (input == null || input.equals(PING))
                ? processInput(Payload.builder().setAction(PING).build())
                : input.equals(RESET)
                ? processInput(Payload.builder().setAction(RESET).build())
                : processInput(Payload.deserialize(input));
    }

    protected String getString(@StringRes int id) {
        return appContext.getString(id);
    }

    protected String getString(@StringRes int id, Object... args) {
        return appContext.getString(id, args);
    }

    protected abstract Payload processInput(Payload payload);

}
