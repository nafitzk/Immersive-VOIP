package immersivevoip;

import fmod.fmod.FMODManager;
import fmod.javafmodJNI;
import org.joml.Math;
import org.joml.Vector2f;

import static immersivevoip.fmod.IVFMOD.FMOD_DSP_COMPRESSOR.*;
import static immersivevoip.fmod.IVFMOD.FMOD_DSP_DISTORTION.FMOD_DSP_DISTORTION_LEVEL;
import static immersivevoip.fmod.IVFMOD.FMOD_DSP_FADER.FMOD_DSP_FADER_GAIN;
import static immersivevoip.fmod.IVFMOD.FMOD_DSP_OSCILLATOR.FMOD_DSP_OSCILLATOR_TYPE;
import static immersivevoip.fmod.IVFMOD.FMOD_DSP_THREE_EQ.*;
import static immersivevoip.fmod.IVFMOD.FMOD_DSP_THREE_EQ.FMOD_DSP_THREE_EQ_CROSSOVERSLOPE;
import static immersivevoip.fmod.IVFMOD.FMOD_DSP_TYPE.*;

public class RadioVoiceFilter extends VoiceFilter {

    public static final float FMOD_MIN_DECIBEL = -80f;

    // vectors are just a convenient way of storing two vars
    // x: at max quality, y: at min quality
    // in the future, these can be configurable by a server
    public static final float RADIO_BANDPASS_LOW = 400f;
    public static final float RADIO_BANDPASS_HIGH = 2400f;
    public static final Vector2f RADIO_BANDPASS_LOW_V = new Vector2f(400f, 420f);
    public static final Vector2f RADIO_BANDPASS_HIGH_V = new Vector2f(2400f, 800f);

    public static final float RADIO_DISTORTION_AMOUNT = 1f;
    public static final Vector2f RADIO_DISTORTION_AMOUNT_V = new Vector2f(0.3f, 1.0f);

    public static final float RADIO_COMPRESSOR_RATIO = 25f;
    public static final float RADIO_COMPRESSOR_THRESHOLD = 5f;
    public static final float RADIO_COMPRESSOR_ATTACK = 5f;
    public static final float RADIO_COMPRESSOR_RELEASE = 5f;
    public static final Vector2f RADIO_COMPRESSOR_THRESHOLD_V = new Vector2f(0f, -10f);

    // the ratio between current distance and max distance at which the quality is max and min
    public static final float RADIO_DIST_MAX_QUALITY = 0.6f;
    public static final float RADIO_DIST_MIN_QUALITY = 0.9f;

    // fmod handles
    private final long distortionDsp;           // radio distortion DSP
    private final long voiceCompressorDsp;      // compressor DSP for loud distortion signals
    private final long bandpassFilterDsp;       // radio bandpass DSP

    private final BackgroundNoise noise;

    // voice filter parameters
    private float quality = 1f;

    public RadioVoiceFilter(long channel, VoiceFilterState state){
        super("RadioFilter", channel, state);

        // create and configure DSPs
        // radio bandpass
        bandpassFilterDsp = javafmodJNI.FMOD_System_CreateDSPByType(FMOD_DSP_TYPE_THREE_EQ.ordinal());
        javafmodJNI.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_LOWGAIN.ordinal(), FMOD_MIN_DECIBEL);
        javafmodJNI.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_HIGHGAIN.ordinal(), FMOD_MIN_DECIBEL);
        javafmodJNI.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_MIDGAIN.ordinal(), 0.0f);
        javafmodJNI.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_LOWCROSSOVER.ordinal(), 420);
        javafmodJNI.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_HIGHCROSSOVER.ordinal(), 420);
        javafmodJNI.FMOD_DSP_SetParameterInt(bandpassFilterDsp, FMOD_DSP_THREE_EQ_CROSSOVERSLOPE.ordinal(), 2);

        // distortion
        distortionDsp = javafmodJNI.FMOD_System_CreateDSPByType(FMOD_DSP_TYPE_DISTORTION.ordinal());
        javafmodJNI.FMOD_DSP_SetParameterFloat(distortionDsp, FMOD_DSP_DISTORTION_LEVEL.ordinal(), RADIO_DISTORTION_AMOUNT);

        // compressor
        voiceCompressorDsp = javafmodJNI.FMOD_System_CreateDSPByType(FMOD_DSP_TYPE_COMPRESSOR.ordinal());
        javafmodJNI.FMOD_DSP_SetParameterFloat(voiceCompressorDsp, FMOD_DSP_COMPRESSOR_RATIO.ordinal(), RADIO_COMPRESSOR_RATIO);
        javafmodJNI.FMOD_DSP_SetParameterFloat(voiceCompressorDsp, FMOD_DSP_COMPRESSOR_THRESHOLD.ordinal(), RADIO_COMPRESSOR_THRESHOLD);
        javafmodJNI.FMOD_DSP_SetParameterFloat(voiceCompressorDsp, FMOD_DSP_COMPRESSOR_ATTACK.ordinal(), RADIO_COMPRESSOR_ATTACK);

        // noise
        noise = new BackgroundNoise();
    }

    @Override
    public void onSetActive(boolean active) {
        // this weird ass code is needed because the zomboid fmod JNI is half-baked and only the core functionality is properly implemented
        // because of this, any fmod function requiring a bool does not take a bool, but a bool memory address
        // obviously, I cannot provide a boolean memory address due to java being what it is, but fortunately
        // c++ evaluates any valid address in memory as true and with zero being false, and so here we are
        // its wack, but it works
        long a = active ? distortionDsp : 0L;
        long nota = active ? 0L : distortionDsp;

        noise.setPaused(!active);

        javafmodJNI.FMOD_DSP_SetBypass(distortionDsp, nota);
        javafmodJNI.FMOD_DSP_SetBypass(voiceCompressorDsp, nota);
        javafmodJNI.FMOD_DSP_SetBypass(bandpassFilterDsp, nota);

        // reset radio quality
        quality = 1f;
    }

    @Override
    public void build(){
        // build dsp graph
        // [OUT]<--[radio]<--[compressor]<--[distortion]<--[IN]
        javafmodJNI.FMOD_ChannelGroup_AddDSP(channel, 1, distortionDsp);
        javafmodJNI.FMOD_ChannelGroup_AddDSP(channel, 1, voiceCompressorDsp);
        javafmodJNI.FMOD_ChannelGroup_AddDSP(channel, 1, bandpassFilterDsp);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void updateFilter() {
        float newQuality = quality;

        // as the radio approaches its maximum range, quality begins to drop sharply after passing a distance threshold
        if(state.radioData != null){
            if(state.radioData.lastReceiveDistance > RADIO_DIST_MAX_QUALITY){
                float a = state.radioData.lastReceiveDistance / state.radioData.distance;
                float b = map(0f, 1f, RADIO_DIST_MAX_QUALITY, RADIO_DIST_MIN_QUALITY, a);
                float distMod = 1f-b;
                newQuality *= distMod;
            }
        }

        // only update quality when needed
        if(newQuality != quality){
            setQuality(newQuality);
        }
    }

    public void setQuality(float quality){
        this.quality = quality;

        // bandpass quality - worse signal quality means signal information is lost (narrower bands of info)
        javafmodJNI.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_LOWCROSSOVER.ordinal(),
                map(RADIO_BANDPASS_LOW_V.y, RADIO_BANDPASS_LOW_V.x, quality));
        javafmodJNI.FMOD_DSP_SetParameterFloat(bandpassFilterDsp, FMOD_DSP_THREE_EQ_HIGHCROSSOVER.ordinal(),
                map(RADIO_BANDPASS_HIGH_V.y, RADIO_BANDPASS_HIGH_V.x, quality));

        // distortion - the lower the quality, the more distorted the signal should be
        javafmodJNI.FMOD_DSP_SetParameterFloat(distortionDsp, FMOD_DSP_DISTORTION_LEVEL.ordinal(),
                map(RADIO_DISTORTION_AMOUNT_V.y, RADIO_DISTORTION_AMOUNT_V.x, quality));

        // voice compression - at lower quality, the signal will be weaker, more compressed and harder to hear
        javafmodJNI.FMOD_DSP_SetParameterFloat(voiceCompressorDsp, FMOD_DSP_COMPRESSOR_THRESHOLD.ordinal(),
                map(RADIO_COMPRESSOR_THRESHOLD_V.y, RADIO_COMPRESSOR_THRESHOLD_V.x, quality));
    }

    // manages overall background noise for the radio voice filter
    public static class BackgroundNoise{
        public static final float RADIO_NOISE_GAIN = -40f;

        private static long silenceSound; // handle for silence sound

        private final long noiseChannel;
        private final long noiseDsp;                // radio noise DSP
        private final long noiseFaderDsp;           // radio noise compressor
        private final long noiseBandpassFilterDsp;  // radio noise compressor

        public BackgroundNoise(){
            // sound of silence, only needed to load once
            if(silenceSound == 0L) {
                silenceSound = javafmodJNI.FMOD_System_CreateSound("test/silence.wav", FMODManager.FMOD_LOOP_NORMAL);
                javafmodJNI.FMOD_Sound_SetMode(silenceSound, FMODManager.FMOD_LOOP_NORMAL);
            }

            // noise channel
            noiseChannel = javafmodJNI.FMOD_System_PlaySound(silenceSound, 1); // start the sound, but make it paused
            javafmodJNI.FMOD_Channel_SetChannelGroup(noiseChannel, IV.ivChannelGroup);

            // noise
            noiseDsp = javafmodJNI.FMOD_System_CreateDSPByType(FMOD_DSP_TYPE_OSCILLATOR.ordinal());
            javafmodJNI.FMOD_DSP_SetParameterInt(noiseDsp, FMOD_DSP_OSCILLATOR_TYPE.ordinal(), 5); // noise

            // noise compressor
            noiseFaderDsp = javafmodJNI.FMOD_System_CreateDSPByType(FMOD_DSP_TYPE_FADER.ordinal());
            javafmodJNI.FMOD_DSP_SetParameterFloat(noiseFaderDsp, FMOD_DSP_FADER_GAIN.ordinal(), RADIO_NOISE_GAIN);

            // noise bandpass
            noiseBandpassFilterDsp = javafmodJNI.FMOD_System_CreateDSPByType(FMOD_DSP_TYPE_THREE_EQ.ordinal());
            javafmodJNI.FMOD_DSP_SetParameterFloat(noiseBandpassFilterDsp, FMOD_DSP_THREE_EQ_LOWGAIN.ordinal(), FMOD_MIN_DECIBEL);
            javafmodJNI.FMOD_DSP_SetParameterFloat(noiseBandpassFilterDsp, FMOD_DSP_THREE_EQ_HIGHGAIN.ordinal(), FMOD_MIN_DECIBEL);
            javafmodJNI.FMOD_DSP_SetParameterFloat(noiseBandpassFilterDsp, FMOD_DSP_THREE_EQ_MIDGAIN.ordinal(), 0.0f);
            javafmodJNI.FMOD_DSP_SetParameterFloat(noiseBandpassFilterDsp, FMOD_DSP_THREE_EQ_LOWCROSSOVER.ordinal(), RADIO_BANDPASS_LOW);
            javafmodJNI.FMOD_DSP_SetParameterFloat(noiseBandpassFilterDsp, FMOD_DSP_THREE_EQ_HIGHCROSSOVER.ordinal(), RADIO_BANDPASS_HIGH);
            javafmodJNI.FMOD_DSP_SetParameterInt(noiseBandpassFilterDsp, FMOD_DSP_THREE_EQ_CROSSOVERSLOPE.ordinal(), 2);

            build();
        }

        private void build(){
            // [OUT]<--[radio]<--[fader]<--[noise]<--[silence loop]
            javafmodJNI.FMOD_Channel_AddDSP(noiseChannel, 1, noiseDsp);
            javafmodJNI.FMOD_Channel_AddDSP(noiseChannel, 1, noiseFaderDsp);
            javafmodJNI.FMOD_Channel_AddDSP(noiseChannel, 1, noiseBandpassFilterDsp);
        }

        public void setPaused(boolean paused){
            javafmodJNI.FMOD_Channel_SetPaused(noiseChannel, paused ? 1L : 0L);
        }
    }
}
