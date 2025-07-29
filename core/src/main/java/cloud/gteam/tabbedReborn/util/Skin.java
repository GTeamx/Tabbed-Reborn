package cloud.gteam.tabbedReborn.util;

import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.google.common.base.Preconditions;

import java.util.Objects;

/**
 * Represents the skin/avatar of a tab item.
 */

public class Skin {

    private final TextureProperty property;
    public static final String TEXTURE_KEY = "textures";

    public Skin(final String value, final String signature) {
        this(new TextureProperty(TEXTURE_KEY, value, signature));
    }

    public Skin(final TextureProperty property) {
        Preconditions.checkArgument(property.getName().equals(TEXTURE_KEY));
        this.property = property;
    }

    @Override
    public boolean equals(final Object object) {

        if (object == this) return true;
        else if (object instanceof Skin other) {

            final  boolean sign = Objects.equals(this.property.getSignature(), other.getProperty().getSignature());
            final  boolean value = Objects.equals(this.property.getValue(), other.getProperty().getValue());

            return sign && value;

        }

        return false;

    }

    public TextureProperty getProperty() {
        return property;
    }

}
