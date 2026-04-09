package com.jawshthedark.adignore;

import com.jawshthedark.adignore.commands.IgnoreCommand;
import meteordevelopment.meteorclient.commands.Commands;
import com.jawshthedark.adignore.modules.AdIgnoreModule;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;



public class AdIgnoreAddon extends MeteorAddon {
    public static final Category CATEGORY = new Category("AdIgnore", Items.BARRIER.getDefaultStack());

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public void onInitialize() {
        IgnoreManager.init();
        Commands.add(new IgnoreCommand());
        Modules.get().add(new AdIgnoreModule());

        System.out.println("[AdIgnore] Loaded.");
    }

    @Override
    public String getPackage() {
        return "com.jawshthedark.adignore";
    }
}