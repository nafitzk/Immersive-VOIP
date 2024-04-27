package immersiveaudio;

import zombie.characters.IsoPlayer;
import zombie.core.raknet.VoiceManagerData;
import zombie.debug.DebugLog;

// a very simple state machine for managing the current filter of a user
public class VoiceFilterState {

    // static identifier data
    public final IsoPlayer user;
    public final long userChannel;

    // zomboid dynamic data
    public VoiceManagerData.VoiceDataSource source = VoiceManagerData.VoiceDataSource.Unknown;
    public VoiceManagerData.RadioData radioData;

    // voice filter states
    private VoiceFilter current;

    private final EmptyVoiceFilter emptyFilter;
    private final RadioVoiceFilter radioFilter;

    public VoiceFilterState(IsoPlayer player, long channel){
        this.user = player;
        this.userChannel = channel;

        // create cached filters
        emptyFilter = new EmptyVoiceFilter(channel, this);
        radioFilter = new RadioVoiceFilter(channel, this);

        // build dsp tree
        radioFilter.build();

        // turn off
        radioFilter.disable();
    }

    public void dispose(){
        // not sure this is needed since fmod disposes channels in its own way
    }

    public void update(VoiceManagerData.VoiceDataSource source, VoiceManagerData.RadioData radioData){
        DebugLog.Voice.debugln("#@# Updating user : "+user.getUsername());
        DebugLog.Voice.debugln("#@# Source : "+source.toString());

        boolean sourceChanged = this.source == source;

        this.source = source;
        this.radioData = radioData;

        // if our source has been updated, then we update the filter state
        if(sourceChanged){
            DebugLog.Voice.debugln("#@# Voice Filter State: source changed: "+this.source.toString()+" -> "+source.toString());

            switch (source){
                case Unknown, Voice, Cheat -> { // not sure what to do with these for now, so we will apply the empty filter aka no filter
                    setCurrentFilter(emptyFilter);
                }
                case Radio -> {
                    setCurrentFilter(radioFilter);
                }
            }
        }

        current.updateFilter();
    }

    private void setCurrentFilter(VoiceFilter filter){
        if(current != null){
            current.disable(); // disable current filter
        }
        current = filter;
        current.enable();
    }
}
