package cloud.gteam.tabbedReborn.item;

import cloud.gteam.tabbedReborn.util.Skin;
import cloud.gteam.tabbedReborn.util.Skins;

/**
 * A blank TextTabItem
 */
public class BlankTabItem extends TextTabItem {

    public BlankTabItem(final Skin skin) {
        super("", 1000, skin);
    }

    public BlankTabItem() {
        this(Skins.DEFAULT_SKIN);
    }

}
