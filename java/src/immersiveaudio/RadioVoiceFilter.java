package immersiveaudio;

import immersiveaudio.fmod.IVNative;
import zombie.core.raknet.VoiceManagerData;

import static immersiveaudio.fmod.IVFMOD.FMOD_DSP_COMPRESSOR.*;
import static immersiveaudio.fmod.IVFMOD.FMOD_DSP_DISTORTION.FMOD_DSP_DISTORTION_LEVEL;
import static immersiveaudio.fmod.IVFMOD.FMOD_DSP_FADER.FMOD_DSP_FADER_GAIN;
import static immersiveaudio.fmod.IVFMOD.FMOD_DSP_OSCILLATOR.FMOD_DSP_OSCILLATOR_TYPE;
import static immersiveaudio.fmod.IVFMOD.FMOD_DSP_THREE_EQ.*;
import static immersiveaudio.fmod.IVFMOD.FMOD_DSP_THREE_EQ.FMOD_DSP_THREE_EQ_CROSSOVERSLOPE;
import static immersiveaudio.fmod.IVFMOD.FMOD_DSP_TYPE.*;

public class RadioVoiceFilter extends VoiceFilter {

    public static final float FMOD_MIN_DECIBEL = -80f;

    public static final float RADIO_BANDPASS_LOW = 800f;
    public static final float RADIO_BANDPASS_HIGH = 8000f;

    public static final float RADIO_DISTORTION_AMOUNT = 0.6f;

    public static final float RADIO_COMPRESSOR_RATIO = 2.5f;
    public static final float RADIO_COMPRESSOR_THRESHOLD = 0f;
    public static final float RADIO_COMPRESSOR_ATTACK = 1f;
    public static final float RADIO_COMPRESSOR_RELEASE = 5f;

    public static final float RADIO_NOISE_GAIN = -20f;

    // fmod handles
    private final long distortionDsp;           // radio distortion DSP
    private final long voiceCompressorDsp;      // compressor DSP for loud distortion signals
    private final long bandpassFilterDsp;       // radio bandpass DSP
    private final long noiseDsp;                // radio noise DSP
    private final long noiseFaderDsp;           // radio noise compressor

    // voice filter parameters
    private float quality = 1f;

    public RadioVoiceFilter(long channel, VoiceFilterState state){
        super("RadioFilter", channel, state);

        // create and configure DSPs
        // radio bandpass
        bandpassFilterDsp = IVNative.FMOD_System_CreateDSPByType(FMOD_DSP_TYPE_THREE_EQ.ordinal());
        IVNative.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_LOWGAIN.ordinal(), FMOD_MIN_DECIBEL);
        IVNative.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_HIGHGAIN.ordinal(), FMOD_MIN_DECIBEL);
        IVNative.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_MIDGAIN.ordinal(), 0.0f);
        IVNative.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_LOWCROSSOVER.ordinal(), RADIO_BANDPASS_LOW);
        IVNative.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_HIGHCROSSOVER.ordinal(), RADIO_BANDPASS_HIGH);
        IVNative.FMOD_DSP_SetParameterInt(bandpassFilterDsp, FMOD_DSP_THREE_EQ_CROSSOVERSLOPE.ordinal(), 2);

        // distortion
        distortionDsp = IVNative.FMOD_System_CreateDSPByType(FMOD_DSP_TYPE_DISTORTION.ordinal());
        IVNative.FMOD_DSP_SetParameterFloat(distortionDsp, FMOD_DSP_DISTORTION_LEVEL.ordinal(), RADIO_DISTORTION_AMOUNT);

        // compressor
        voiceCompressorDsp = IVNative.FMOD_System_CreateDSPByType(FMOD_DSP_TYPE_COMPRESSOR.ordinal());
        IVNative.FMOD_DSP_SetParameterFloat(voiceCompressorDsp, FMOD_DSP_COMPRESSOR_RATIO.ordinal(), 2.5f);
        IVNative.FMOD_DSP_SetParameterFloat(voiceCompressorDsp, FMOD_DSP_COMPRESSOR_THRESHOLD.ordinal(), 0.0f);
        IVNative.FMOD_DSP_SetParameterFloat(voiceCompressorDsp, FMOD_DSP_COMPRESSOR_ATTACK.ordinal(), 1f);

        // noise
        noiseDsp = IVNative.FMOD_System_CreateDSPByType(FMOD_DSP_TYPE_OSCILLATOR.ordinal());
        IVNative.FMOD_DSP_SetParameterInt(noiseDsp, FMOD_DSP_OSCILLATOR_TYPE.ordinal(), 5); // noise

        // noise compressor
        noiseFaderDsp = IVNative.FMOD_System_CreateDSPByType(FMOD_DSP_TYPE_FADER.ordinal());
        IVNative.FMOD_DSP_SetParameterFloat(noiseFaderDsp, FMOD_DSP_FADER_GAIN.ordinal(), -20f);
    }

    @Override
    public void onSetActive(boolean active) {
        IVNative.FMOD_DSP_SetActive(noiseDsp, active);
        IVNative.FMOD_DSP_SetActive(noiseFaderDsp, active);

        IVNative.FMOD_DSP_SetBypass(distortionDsp, !active);
        IVNative.FMOD_DSP_SetBypass(voiceCompressorDsp, !active);
        IVNative.FMOD_DSP_SetBypass(bandpassFilterDsp, !active);
    }

    @Override
    public void build(){
        // build dsp graph
        // [OUT]<--[radio]<--[compressor]<--[distortion]<--[IN]
        //                              ^--[noise compressor]<--[noise]
        IVNative.FMOD_ChannelGroup_AddDSP(channel, 1, distortionDsp);
        IVNative.FMOD_ChannelGroup_AddDSP(channel, 1, voiceCompressorDsp);
        IVNative.FMOD_ChannelGroup_AddDSP(channel, 1, bandpassFilterDsp);

        IVNative.FMOD_DSP_AddInput(bandpassFilterDsp, noiseFaderDsp);
        IVNative.FMOD_DSP_AddInput(noiseFaderDsp, noiseDsp);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void updateFilter() {
        // TODO update radio quality based on distance
    }

    public void setQuality(float quality){
        this.quality = quality;

        // bandpass quality - worse signal quality means signal information is lost (narrower bands of info)
        //IVNative.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_LOWCROSSOVER.ordinal(), map(800.0f, 400.0f,quality)); // default
        //IVNative.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_HIGHCROSSOVER.ordinal(), map(800.0f, 2000.0f,quality)); // default

        // distortion - the lower the quality, the more distorted the signal should be
        IVNative.FMOD_DSP_SetParameterFloat(distortionDsp, FMOD_DSP_DISTORTION_LEVEL.ordinal(), map(1.0f, 0.5f, quality));

        // voice compression - at lower quality, the signal will be weaker, more compressed and harder to hear
        //IVNative.FMOD_DSP_SetParameterFloat(voiceCompressorDsp, FMOD_DSP_COMPRESSOR_RATIO.ordinal(), 2.5f);
        IVNative.FMOD_DSP_SetParameterFloat(voiceCompressorDsp, FMOD_DSP_COMPRESSOR_THRESHOLD.ordinal(), map(-20.0f, -1.0f, quality));

        // noise - the lower the quality, the noisier the signal should sound
        //IVNative.FMOD_DSP_SetParameterFloat(noiseCompressorDsp, FMOD_DSP_COMPRESSOR_RATIO.ordinal(), 5.0f);
        IVNative.FMOD_DSP_SetParameterFloat(noiseFaderDsp, FMOD_DSP_COMPRESSOR_THRESHOLD.ordinal(), map(-30.0f, -30.0f, quality));
    }
}
