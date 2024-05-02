package immersivevoip;

import immersivevoip.fmod.IVNative;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.debug.DebugLog;

import java.util.HashMap;

// global state variables and some util
public class IV {

    public static final String VERSION = "0.1a";

    public static boolean nativeLoaded = false;
    public static boolean nativeActive = false;
    public static boolean modEnabled = false;
    public static boolean ready = false;

    // init global IV state (Lua init is done elsewhere)
    public static void init(long voipChannelGroup){
        log("Immersive VOIP init begin");

        // fmod native
        IVNative.init(voipChannelGroup);

        IVLua.updateConfig();
    }

    // we are only ready if the native is loaded, active, and the mod is enabled
    public static void checkReady(){
        debug("Updating ready: " + nativeLoaded + " & " + nativeActive + " & " + modEnabled);
        ready = nativeLoaded && nativeActive && modEnabled;
    }

    public static void debug(String s){
        DebugLog.Voice.debugln("[IV]: "+s);
    }
    public static void log(String s){
        DebugLog.Mod.warn("[IV]: "+s);
    }
}
