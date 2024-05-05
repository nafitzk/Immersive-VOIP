package immersivevoip;

import fmod.javafmodJNI;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.debug.DebugLog;

import java.util.HashMap;

// global state variables and some util
public class IV {

    public static final String VERSION = "0.1c"; // this must match the version in mod.lua

    public static boolean modEnabled = false;

    public static long ivChannelGroup;

    // init global IV state (Lua init is done elsewhere)
    public static void init(){
        log("Immersive VOIP init begin");

        ivChannelGroup = javafmodJNI.FMOD_System_CreateChannelGroup("Immersive-VOIP");
        if(ivChannelGroup == 0L) {
            log("Failed to create Immersive-VOIP channel group");
        }
        else {
            debug("Channel group created successfully: "+ivChannelGroup);
        }
    }

    public static boolean ready(){
        return modEnabled;
    }

    public static void debug(String s){
        DebugLog.Voice.debugln("[IV]: "+s);
    }
    public static void log(String s){
        DebugLog.Mod.warn("[IV]: "+s);
    }
}
