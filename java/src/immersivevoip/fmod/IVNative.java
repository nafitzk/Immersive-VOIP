package immersivevoip.fmod;

import immersivevoip.IV;

// native functions and library loading
// this is needed because zomboid's fmod integration does not implement everything needed
public class IVNative {

    public static boolean IVNativeLoaded = false;

    // load library on init
    public static boolean init(long voipChannelGroup){
        try{
            System.loadLibrary("ivnative");
        } catch (UnsatisfiedLinkError | SecurityException | NullPointerException e) {
            IV.debug("[FMOD]: ivnative library load failed: "+e);
            return false;
        }
        IVNativeLoaded = true;
        IV.debug("[FMOD]: ivnative library loaded");

        // init native
        int result = Init_IV(voipChannelGroup);
        if(result != 0 ){
            IV.debug("[FMOD]: ivnative init error: "+result);
            return false;
        }
        IV.debug("[FMOD]: ivnative init success");

        return true;
    }

    // set up native
    public static native int Init_IV(long voipChannelGroup);

    // misc fmod functions that aren't implemented in zomboid
    public static native int FMOD_Channel_AddDSP(long channel, int index, long dsp);
    public static native int FMOD_Channel_RemoveDSP(long channel, long dsp);
    public static native int FMOD_Channel_GetNumDSPs(long channel);
    public static native long FMOD_Channel_GetDSP(long channel, int index);
    public static native int FMOD_ChannelGroup_AddDSP(long channel, int index, long dsp);
    public static native int FMOD_ChannelGroup_RemoveDSP(long channel, long dsp);
    public static native int FMOD_ChannelGroup_GetNumDSPs(long channel);
    public static native long FMOD_ChannelGroup_GetDSP(long channel, int index);

    public static native boolean FMOD_DSP_GetActive(long dsp);
    public static native boolean FMOD_DSP_GetBypass(long dsp);
    public static native int FMOD_DSP_SetActive(long dsp, boolean active);
    public static native int FMOD_DSP_SetBypass(long dsp, boolean bypass);
    public static native int FMOD_DSP_SetParameterFloat(long dsp, int index, float value);
    public static native float FMOD_DSP_GetParameterFloat(long dsp, int index);
    public static native int FMOD_DSP_SetParameterInt(long dsp, int index, int value);
    public static native int FMOD_DSP_GetParameterInt(long dsp, int index);
    public static native int FMOD_DSP_SetParameterBool(long dsp, int index, boolean value);
    public static native boolean FMOD_DSP_GetParameterBool(long dsp, int index);
    public static native int FMOD_DSP_GetNumParameters(long dsp);
    public static native int FMOD_DSP_Release(long dsp);
    public static native int FMOD_DSP_AddInput(long dsp, long dspInput);

    public static native long FMOD_System_CreateDSPByType(int type);
    public static native int FMOD_System_SetReverbProperties(int instance, int preset);
}