package immersivevoip;

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

    // sets up the dsp filters for immersive voip
    public void initUserVoiceFilters(short userId, VoiceManagerData userData){
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

    // update the DSPs depending on world state
    // compare listener and source state and activate / deactivate filters
    public void updateUserVoiceFilters(VoiceManagerData.VoiceDataSource source, VoiceManagerData userData, VoiceManagerData.RadioData radioData){
        VoiceFilterState vfs = dataMap.get(userData);
        if(vfs == null){
            IV.debug("No VoiceFilterData found for user channel "+userData.userplaychannel);
            return;
        }

        vfs.update(source, radioData);
    }

    public void userVoiceTimeout(VoiceManagerData userData){
        VoiceFilterState vfd = dataMap.get(userData);
        if(vfd == null){
            IV.debug("Cannot reset null user "+userData.userplaychannel);
            return;
        }
        IV.debug("Resetting user "+vfd.user.getUsername());

        // reset the user to the unknown voice state
        vfd.update(VoiceManagerData.VoiceDataSource.Unknown, null);
    }

    // tears down the dsp filters for immersive voip
    public void releaseUserVoiceFilters(VoiceManagerData userData){
        VoiceFilterState vfs = dataMap.remove(userData);
        if(vfs == null){
            return;
        }

        vfs.dispose();
    }
}
