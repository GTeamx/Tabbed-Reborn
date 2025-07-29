package cloud.gteam.tabbedReborn.item;

import cloud.gteam.tabbedReborn.util.Skin;
import cloud.gteam.tabbedReborn.util.Skins;

import java.util.Objects;

/**
 * A tab item with custom text, ping and skin.
 */
public class TextTabItem implements TabItem {

    private String text;
    private int ping;
    private Skin skin;

    private String newText;
    private int newPing;
    private Skin newSkin;

    public TextTabItem(final String text) {
        this(text, 1000);
    }

    public TextTabItem(final String text, final int ping) {
        this(text, ping, Skins.DEFAULT_SKIN);
    }

    public TextTabItem(final String text, final int ping, final Skin skin) {
        this.newText = text;
        this.newPing = ping;
        this.newSkin = skin;
        updateText();
        updatePing();
        updateSkin();
    }

    public void setText(final String text) {
        this.newText = text;
    }

    public void setPing(final int ping) {
        this.newPing = ping;
    }

    public void setSkin(final Skin skin) {
        this.newSkin = skin;
    }

    @Override
    public boolean updateText() {
        boolean update = !Objects.equals(this.text, this.newText);
        this.text = this.newText;
        return update;
    }

    @Override
    public boolean updatePing() {
        boolean update = this.ping != this.newPing;
        this.ping = this.newPing;
        return update;
    }

    @Override
    public boolean updateSkin() {
        boolean update = !Objects.equals(this.skin, this.newSkin);
        this.skin = this.newSkin;
        return update;
    }

    @Override
    public boolean equals(final Object object) {

        if (!(object instanceof TextTabItem other)) return false;

        return this.text.equals(other.getText()) && this.skin.equals(other.getSkin()) && this.ping == other.getPing();

    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public int getPing() {
        return ping;
    }

    @Override
    public Skin getSkin() {
        return skin;
    }

}
