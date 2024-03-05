package com.chrispbacon.chesschat.remote;

import org.json.JSONObject;

public interface InstructionListener {
    void onInstruction(JSONObject object);
}

