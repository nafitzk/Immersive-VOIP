package immersiveaudio.fmod;

import zombie.debug.DebugLog;

// native functions and library loading
public class IVNative {

    // load library on init
    static{
        System.loadLibrary("CppNatives");
    }

    public static native int Init_IV(long voipChannelGroup);

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
