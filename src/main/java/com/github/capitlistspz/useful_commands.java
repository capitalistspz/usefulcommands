package com.github.capitlistspz;

import net.fabricmc.api.ModInitializer;
public class useful_commands implements ModInitializer {
    public static final String MOD_ID = "useful_commands";
    @Override
    public void onInitialize() {
        Commands.init();
    }
}
