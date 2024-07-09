package com.juan.spongeskills;

import com.juan.spongeskills.command.SkillsCommand;
import com.juan.spongeskills.config.AccountManager;
import com.juan.spongeskills.config.SkillManager;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;


@Plugin (id="spongeskills", name="Sponge Skills", description = "Simple skills plugin",version="1.0.0")
public class Main {


    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    Logger logger;

    @Inject
    private Game game;

    @Inject
    private PluginContainer pluginContainer;

    private ConfigurationNode config;
    private AccountManager accountManager;
    private SkillManager skillManager;


    @Listener
    public void preInit(GamePreInitializationEvent e) {

        setupConfig();
        accountManager = new AccountManager(this);
        skillManager = new SkillManager(this);
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) { logger.info( "Sponge Skills Plugin has been started");}

    @Listener
    public void init(GameInitializationEvent e){
        createAndRegisterCommands();
        game.getEventManager().registerListeners(this, skillManager);
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join e) {
        UUID playerUUID = e.getTargetEntity().getUniqueId();

        accountManager.createAccount(playerUUID);
    }

    private void setupConfig() {
        try {
            if (!defaultConfig.toFile().exists()) {
                pluginContainer.getAsset("spongeskills.conf").get().copyToFile(defaultConfig);
            }
            config = loader.load();
        } catch (IOException e) {
            logger.warn("Error occured while setting up the main configuration file");
        }
    }

    private void createAndRegisterCommands() {
        CommandSpec skillsCommand = CommandSpec.builder()
                .description(Text.of("Display Skills levels and experience amount"))
                .executor(new SkillsCommand(this))
                .build();
        game.getCommandManager().register(this, skillsCommand, "skills");
    }



    public Logger getLogger(){ return logger; }
    public Path getConfigDir(){ return configDir; }
    public AccountManager getAccountManager(){ return accountManager; }
    public SkillManager getSkillManager(){ return skillManager; }
    public PluginContainer getPluginContainer(){ return pluginContainer; }
}
