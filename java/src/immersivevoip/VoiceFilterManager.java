package immersivevoip;

import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.raknet.VoiceManagerData;
import zombie.network.GameClient;

import java.util.HashMap;

// handles the state switches of the voice dsp filters
public class VoiceFilterManager {
    public static final VoiceFilterManager instance = new VoiceFilterManager();

    private final HashMap<VoiceManagerData, VoiceFilterState> dataMap;

    public VoiceFilterManager(){
        dataMap = new HashMap<>();
    }

    public static void exposeLua(){
        LuaManager.exposer.setExposed(VoiceFilterManager.class);
    }

    // sets up voice filter state for a new user
    public void initUserVoiceFilters(short userId, VoiceManagerData userData){
        if(!IV.modEnabled){
            return;
        }

        IV.debug("New user IV init: "+userId);

        IsoPlayer user = GameClient.instance.getPlayerByOnlineID(userId);
        if(user == null){
            IV.debug("User "+userId+" is null!");
            return;
        }

        IV.debug("Init user voice state: "+user.getUsername());
        VoiceFilterState vfs = new VoiceFilterState(user, userData.userplaychannel);
        IV.debug("Done");

        dataMap.put(userData, vfs);
        IV.debug("User init complete!");
    }

    // update voice filter state for a particular user
    public void updateUserVoiceFilters(VoiceManagerData.VoiceDataSource source, VoiceManagerData userData, VoiceManagerData.RadioData radioData){
        VoiceFilterState vfs = dataMap.get(userData);
        if(vfs == null){
            IV.debug("No VoiceFilterData found for user channel "+userData.userplaychannel);
            return;
        }

        vfs.update(source, radioData);
    }

    // reset the voice filter state of a user
    public void userVoiceTimeout(VoiceManagerData userData){
        VoiceFilterState vfs = dataMap.get(userData);
        if(vfs == null){
            IV.debug("Cannot reset null user "+userData.userplaychannel);
            return;
        }
        IV.debug("Resetting user "+vfs.user.getUsername());

        // reset the user to the unknown voice state
        vfs.update(VoiceManagerData.VoiceDataSource.Unknown, null);
    }

    // tears down the dsp filters for a user
    public void releaseUserVoiceFilters(VoiceManagerData userData){
        VoiceFilterState vfs = dataMap.remove(userData);
        if(vfs == null){
            return;
        }

        vfs.dispose();
    }
}
