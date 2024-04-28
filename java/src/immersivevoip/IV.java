package immersivevoip;

import immersivevoip.fmod.IVNative;
import zombie.debug.DebugLog;
import zombie.world.logger.Log;

// global state variables and some util
public class IV {

    public static boolean debug = true;
    public static boolean nativeLoaded = false;

    // init fmod native
    public static void init(long voipChannelGroup){
        debug("Immersive VOIP Init");

        // lua

        // init native
        if(!IVNative.init(voipChannelGroup)){
            debug("Native init failed");
            return;
        }
        debug("Native init success");
    }

    public static void debug(String s){
        if(debug) DebugLog.Voice.debugln("[IV]: "+s);
    }
}
