package immersiveaudio;

// No post-processing here.
public class EmptyVoiceFilter extends VoiceFilter {

    public EmptyVoiceFilter(long channel, VoiceFilterState state) {
        super("Empty", channel, state);
    }

    @Override
    protected void onSetActive(boolean active) {

    }

    @Override
    public void build() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void updateFilter() {

    }
}
