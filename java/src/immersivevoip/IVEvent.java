package immersivevoip;

import zombie.characters.IsoPlayer;
import zombie.core.raknet.VoiceManagerData;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Radio;
import zombie.radio.devices.DeviceData;

// general purpose event bus
public class IVEvent {

    // most of this probably should be in Lua, but Zomboid's lua threading makes
    // that really unstable at the moment, so it will be fine here for now

    // return the first transmit-able device, if any
    private static DeviceData getValidDevice(){
        VoiceManagerData vmd = VoiceManagerData.get(IsoPlayer.getInstance().getOnlineID());

        for(VoiceManagerData.RadioData radioData : vmd.radioData){
            if(radioData.isTransmissionAvailable()){
                return radioData.getDeviceData();
            }
        }

        return null;
    }

    // if we have begun transmitting, play the beep
    public static void BeginTransmit(){
        IV.debug("Begin transmit");

        DeviceData device = getValidDevice();
        if(device != null){
            long id = IsoPlayer.getInstance().getEmitter().playSound("transmission_start");
            IsoPlayer.getInstance().getEmitter().setVolume(id, device.getDeviceVolume());
            IV.debug("Beep! "+id+", vol: "+device.getDeviceVolume());
        }
        else{
            IV.debug("No valid device to beep");
        }
    }

    // if transmitting has finished, play a click
    public static void EndTransmit(){
        IV.debug("End transmit");
        DeviceData device = getValidDevice();
        if(device != null){
            long id = IsoPlayer.getInstance().getEmitter().playSound("transmission_end");
            IsoPlayer.getInstance().getEmitter().setVolume(id, device.getDeviceVolume());
        }
    }

    public static void BeginReceive(String username, int source){
        //LuaEventManager.triggerEvent("ivOnReceiveVoiceBegin", username, source);
    }

    public static void EndReceive(String username){
        //LuaEventManager.triggerEvent("ivOnReceiveVoiceEnd", username);
    }


}