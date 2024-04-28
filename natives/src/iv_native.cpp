#define WIN32_LEAN_AND_MEAN             // Exclude rarely-used stuff from Windows headers
// Windows Header Files
#include <fmod.hpp>
#include "iv_native.h"

// Macros
#define CAST_FMOD_PTR(var_name, var_type, addr) var_type var_name = reinterpret_cast<var_type>(addr)

// fmod system, should be all that we need
FMOD::System* pz_fmod_system;

////////////////////////////////////////////////
// function definitions

// Init IV fmod native
JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_Init_1IV(JNIEnv* env, jclass clazz, jlong voip_channel_group_addr) {
	FMOD::ChannelGroup* voip_channel_group = reinterpret_cast<FMOD::ChannelGroup*>(voip_channel_group_addr);

	// grab the fmod system from the zomboid voip channel group
	FMOD_RESULT result = voip_channel_group->getSystemObject(&pz_fmod_system);

	return result;
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1Channel_1AddDSP(JNIEnv* env, jclass clazz, jlong channel_addr, jint index, jlong dsp_addr) {
	CAST_FMOD_PTR(channel, FMOD::Channel*, channel_addr);
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	return channel->addDSP(index, dsp);;
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1Channel_1RemoveDSP(JNIEnv* env, jclass clazz, jlong channel_addr, jlong dsp_addr) {
	CAST_FMOD_PTR(channel, FMOD::Channel*, channel_addr);
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	return channel->removeDSP(dsp);
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1Channel_1GetNumDSPs(JNIEnv* env, jclass clazz, jlong channel_addr) {
	CAST_FMOD_PTR(channel, FMOD::Channel*, channel_addr);

	int num;
	channel->getNumDSPs(&num);
	return num;
}

JNIEXPORT jlong JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1Channel_1GetDSP(JNIEnv* env, jclass clazz, jlong channel_addr, jint index) {
	CAST_FMOD_PTR(channel, FMOD::Channel*, channel_addr);

	FMOD::DSP* dsp;
	FMOD_RESULT result = channel->getDSP(index, &dsp);

	if (result == FMOD_OK) {
		return reinterpret_cast<jlong>(dsp);
	}

	return 0L;
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1ChannelGroup_1AddDSP(JNIEnv* env, jclass clazz, jlong channelgroup_addr, jint index, jlong dsp_addr) {
	CAST_FMOD_PTR(channelgroup, FMOD::ChannelGroup*, channelgroup_addr);
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	return channelgroup->addDSP(index, dsp);
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1ChannelGroup_1RemoveDSP(JNIEnv* env, jclass clazz, jlong channelgroup_addr, jlong dsp_addr) {
	CAST_FMOD_PTR(channelgroup, FMOD::ChannelGroup*, channelgroup_addr);
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	return channelgroup->removeDSP(dsp);
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1ChannelGroup_1GetNumDSPs(JNIEnv* env, jclass clazz, jlong channelgroup_addr) {
	CAST_FMOD_PTR(channelgroup, FMOD::ChannelGroup*, channelgroup_addr);

	int num;
	channelgroup->getNumDSPs(&num);
	return num;
}

JNIEXPORT jlong JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1ChannelGroup_1GetDSP(JNIEnv* env, jclass clazz, jlong channelgroup_addr, jint index) {
	CAST_FMOD_PTR(channelgroup, FMOD::ChannelGroup*, channelgroup_addr);

	FMOD::DSP* dsp;
	FMOD_RESULT result = channelgroup->getDSP(index, &dsp);

	if (result == FMOD_OK) {
		return reinterpret_cast<jlong>(dsp);
	}

	return 0L;
}

JNIEXPORT jboolean JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1DSP_1GetActive(JNIEnv* env, jclass clazz, jlong dsp_addr) {
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	bool active;
	FMOD_RESULT result = dsp->getActive(&active);
	return active;
}

JNIEXPORT jboolean JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1DSP_1GetBypass(JNIEnv* env, jclass clazz, jlong dsp_addr) {
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	bool bypass;
	FMOD_RESULT result = dsp->getBypass(&bypass);
	return bypass;
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1DSP_1SetActive(JNIEnv* env, jclass clazz, jlong dsp_addr, jboolean active) {
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	return dsp->setActive(active);
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1DSP_1SetBypass(JNIEnv* env, jclass clazz, jlong dsp_addr, jboolean bypass) {
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	return dsp->setBypass(bypass);
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1DSP_1SetParameterFloat(JNIEnv* env, jclass clazz, jlong dsp_addr, jint index, jfloat value) {
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	FMOD_RESULT result = dsp->setParameterFloat(index, value);
	return result;
}

JNIEXPORT jfloat JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1DSP_1GetParameterFloat(JNIEnv* env, jclass clazz, jlong dsp_addr, jint index) {
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	float value;
	FMOD_RESULT result = dsp->getParameterFloat(index, &value, nullptr, 0);
	return value;
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1DSP_1SetParameterInt(JNIEnv* env, jclass clazz, jlong dsp_addr, jint index, jint value) {
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	FMOD_RESULT result = dsp->setParameterInt(index, value);
	return result;
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1DSP_1GetParameterInt(JNIEnv* env, jclass clazz, jlong dsp_addr, jint index) {
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	int value;
	FMOD_RESULT result = dsp->getParameterInt(index, &value, nullptr, 0);
	return value;
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1DSP_1SetParameterBool(JNIEnv* env, jclass clazz, jlong dsp_addr, jint index, jboolean value) {
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	FMOD_RESULT result = dsp->setParameterBool(index, value);
	return result;
}	

JNIEXPORT jboolean JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1DSP_1GetParameterBool(JNIEnv* env, jclass clazz, jlong dsp_addr, jint index) {
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	bool value;
	FMOD_RESULT result = dsp->getParameterBool(index, &value, nullptr, 0);
	return value;
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1DSP_1Release(JNIEnv* env, jclass clazz, jlong dsp_addr) {
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	return dsp->release();
}

JNIEXPORT jlong JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1System_1CreateDSPByType(JNIEnv* env, jclass clazz, jint dsp_type) {
	FMOD::DSP* dsp;
	FMOD_DSP_TYPE type = static_cast<FMOD_DSP_TYPE>(dsp_type);

	FMOD_RESULT result = pz_fmod_system->createDSPByType(type, &dsp);

	// if dsp was created successfully, we return its address
	if (result == FMOD_OK) {
		return reinterpret_cast<jlong>(dsp);
	}

	// otherwise we return 0, indicating failure to create dsp
	return 0L;
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1DSP_1GetNumParameters(JNIEnv* env, jclass clazz, jlong dsp_addr) {
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);

	int num;
	dsp->getNumParameters(&num);

	return num;
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1DSP_1AddInput(JNIEnv* env, jclass clazz, jlong dsp_addr, jlong input_dsp_addr) {
	CAST_FMOD_PTR(dsp, FMOD::DSP*, dsp_addr);
	CAST_FMOD_PTR(input_dsp, FMOD::DSP*, input_dsp_addr);

	FMOD_RESULT result = dsp->addInput(input_dsp);

	return result;
}

JNIEXPORT jint JNICALL Java_immersivevoip_fmod_IVNative_FMOD_1System_1SetReverbProperties(JNIEnv* env, jclass clazz, jint instance, jint preset) {
	FMOD_REVERB_PROPERTIES properties;

	switch (preset) { // icky hard code
		case 0: properties = FMOD_PRESET_OFF; break;
		case 1: properties = FMOD_PRESET_GENERIC; break;
		case 2: properties = FMOD_PRESET_PADDEDCELL; break;
		case 3: properties = FMOD_PRESET_ROOM; break;
		case 4: properties = FMOD_PRESET_BATHROOM; break;
		case 5: properties = FMOD_PRESET_LIVINGROOM; break;
		case 6: properties = FMOD_PRESET_STONEROOM; break;
		case 7: properties = FMOD_PRESET_AUDITORIUM; break;
		case 8: properties = FMOD_PRESET_CONCERTHALL; break;
		case 9: properties = FMOD_PRESET_CAVE; break;
		case 10: properties = FMOD_PRESET_ARENA; break;
		case 11: properties = FMOD_PRESET_HANGAR; break;
		case 12: properties = FMOD_PRESET_CARPETTEDHALLWAY; break;
		case 13: properties = FMOD_PRESET_HALLWAY; break;
		case 14: properties = FMOD_PRESET_STONECORRIDOR; break;
		case 15: properties = FMOD_PRESET_ALLEY; break;
		case 16: properties = FMOD_PRESET_FOREST; break;
		case 17: properties = FMOD_PRESET_CITY; break;
		case 18: properties = FMOD_PRESET_MOUNTAINS; break;
		case 19: properties = FMOD_PRESET_QUARRY; break;
		case 20: properties = FMOD_PRESET_PLAIN; break;
		case 21: properties = FMOD_PRESET_PARKINGLOT; break;
		case 22: properties = FMOD_PRESET_SEWERPIPE; break;
		case 23: properties = FMOD_PRESET_UNDERWATER; break;
	}

	return pz_fmod_system->setReverbProperties(instance, &properties);
}
