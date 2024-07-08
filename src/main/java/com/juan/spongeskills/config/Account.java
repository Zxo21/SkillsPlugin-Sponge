package com.juan.spongeskills.config;

import com.juan.spongeskills.Main;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.UUID;

public class Account {

    private Main plugin;
    private AccountManager accountManager;
    private UUID uuid;

    private ConfigurationNode accountsConfig;

    public Account(Main plugin, AccountManager accountManager, UUID uuid){
        this.plugin = plugin;
        this.accountManager = accountManager;
        this.uuid = uuid;

        accountsConfig = accountManager.getAccountConfig();
    }

    public int getSkillExp(String skillName){
        return accountsConfig.getNode(uuid.toString(), "skills", skillName, "exp").getInt(0);
    }

    public void setSkillExp(String skillName, int expAmount) {
        accountsConfig.getNode(uuid.toString(), "skills", skillName, "exp").setValue(expAmount);

        accountManager.saveConfig();
    }

    public int getSkillLevel(String skillName) {
        return accountsConfig.getNode(uuid.toString(), "skills", skillName, "level").getInt(1);
    }

    public void setSkillLevel(String skillName, int skillLevel) {
        accountsConfig.getNode(uuid.toString(), "skills", skillName, "level").setValue(skillLevel);

        accountManager.saveConfig();
    }

}
