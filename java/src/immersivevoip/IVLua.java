package immersivevoip;

import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.LuaCallFrame;
import zombie.Lua.Event;
import zombie.Lua.LuaManager;
import zombie.core.logger.ExceptionLogger;

public class IVLua {

    public static KahluaTable javaConfig;

    // init any necessary Lua components before the mod is loaded
    public static void init() {
        IV.debug("[Lua]: Lua init begin");

        // add this java config table to the Lua environment
        javaConfig = LuaManager.platform.newTable();
        updateConfig();
        LuaManager.env.rawset("immersiveVoipConfigJava", javaConfig);

        // a handshake callback from lua that occurs when the game is loaded
        // it sends back a result from the onGameStart function
        LuaManager.env.rawset("immersiveVoipHandshake", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int i) {
                boolean result = (boolean)luaCallFrame.get(0);
                IV.debug("[Lua]: Handshake: "+result);
                if(result){
                    IV.modEnabled = true;
                    IV.checkReady();
                }
                else {
                    IV.log("[Lua]: Handshake returned false");
                }
                return 0;
            }
        });
    }

    public static void updateConfig(){
        javaConfig.rawset("version", IV.VERSION);
        javaConfig.rawset("status", IV.nativeActive && IV.nativeLoaded);
    }
}
