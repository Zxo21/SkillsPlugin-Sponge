package com.juan.spongeskills.config;

import com.juan.spongeskills.Main;
import com.juan.spongeskills.skills.Skill;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AccountManager {

    private Logger logger;
    private File accountsFile;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode accountConfig;

    private Main plugin;

    public AccountManager(Main plugin) {
        this.plugin = plugin;

        logger = plugin.getLogger();

        setupAccountConfig();
    }

    private void setupAccountConfig(){
        accountsFile = new File(plugin.getConfigDir().toFile(), "accounts.conf");
        loader = HoconConfigurationLoader.builder().setFile(accountsFile).build();

        try {
            accountConfig = loader.load();

            if (!accountsFile.exists()){
                accountConfig.getNode("placeholder").setValue(true);
                loader.save(accountConfig);
            }

        }catch (IOException e) {
            logger.warn("Error setting up the account configuration");
        }

    }

    public void createAccount(UUID uuid){
        Account account = new Account(plugin, this, uuid);
        for(String skillName : Skill.SKILLS){
            if (!hasAccount(uuid)) {
                account.setSkillLevel(skillName, 1);
                account.setSkillExp(skillName, 0);

                saveConfig();
            }
        }

    }

    public boolean hasAccount(UUID uuid) {
        return accountConfig.getNode(uuid.toString()).getValue() != null;
    }

    public ConfigurationNode getAccountConfig(){
        return accountConfig;
    }

    public void saveConfig() {
        try {
            loader.save(accountConfig);
        } catch (IOException e) {
            logger.warn("Error saving the accounts configuration");
        }

    }

}
