package cloud.gteam.tabbedReborn;

import org.bukkit.plugin.java.JavaPlugin;

public class TabbedPlugin extends JavaPlugin {

  @Override
  public void onLoad() {
      this.getLogger().info("Tabbed-Reborn API loaded!");
  }

  @Override
  public void onDisable() {
      this.getLogger().info("Tabbed-Reborn API disabled!");
  }

}
