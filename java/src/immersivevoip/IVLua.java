package immersivevoip;

import se.krka.kahlua.integration.annotations.LuaMethod;

public class IVLua {

    @LuaMethod(
        name="ivTest",
        global = true
    )
    public static void test(int i){

    }
}
