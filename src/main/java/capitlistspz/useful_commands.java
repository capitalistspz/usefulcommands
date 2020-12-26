package capitlistspz;

import net.fabricmc.api.ModInitializer;
public class useful_commands implements ModInitializer {
    public static final String MOD_ID = "extra";
    @Override
    public void onInitialize() {
        Commands.init();
    }
}
