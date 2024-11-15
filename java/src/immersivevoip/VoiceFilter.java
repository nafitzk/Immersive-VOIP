package immersivevoip;

public abstract class VoiceFilter {
    protected String name;

    protected boolean active;

    protected final long channel;

    protected final VoiceFilterState state;

    public VoiceFilter(String name, long channel, VoiceFilterState state) {
        this.name = name;
        this.channel = channel;
        this.state = state;
    }

    public void setActive(boolean active){
        this.active = active;
        IV.debug("[FILTER]: "+name+": Set Active: " + active);
        onSetActive(active);
    }

    protected abstract void onSetActive(boolean active);

    public final void enable(){
        setActive(true);
        IVEvent.BeginReceive(state.user.getUsername(), state.radioData, state.source);
    }

    public final void disable(){
        setActive(false);
        IVEvent.EndReceive(state.user.getUsername(), state.radioData, state.source);
    }

    // adds DSPs to the channel
    public abstract void build();

    // remove DSPs from channel. Actually wait is this needed? If the voice channel is deleted anyway?
    public abstract void dispose();

    // update the filter state depending on the given voice filter data
    public abstract void updateFilter();

    // utility, not sure where else to put it
    // map x in [0,1] to [a,b]
    protected static float map(float a, float b, float x){
        return a + (b - a) * (x);
    }

    //Y = (X-A)/(B-A) * (D-C) + C
    // map x in [a0,b0] to [a1,b1]
    protected static float map(float a0, float b0, float a1, float b1, float x){
        return (x - a0) / (b0 - a0) * (b1 - a1) + a1;
    }
}
