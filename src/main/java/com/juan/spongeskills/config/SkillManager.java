package com.juan.spongeskills.config;

import com.juan.spongeskills.Main;
import com.juan.spongeskills.skills.Skill;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SkillManager {

    private Logger logger;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode skillConfig;

    private Main plugin;

    private AccountManager accountManager;
    private ConfigurationNode accountConfig;

    public SkillManager(Main plugin) {
        this.plugin = plugin;


        logger = plugin.getLogger();
        accountManager = plugin.getAccountManager();
        accountConfig = accountManager.getAccountConfig();
        setupSkillConfig();
    }

    private void setupSkillConfig() {
        File skillsFile = new File(plugin.getConfigDir().toFile(), "skills.conf");
        loader = HoconConfigurationLoader.builder().setFile(skillsFile).build();

        try {
            if (!skillsFile.exists()) {
                plugin.getPluginContainer().getAsset("skills.conf").get().copyToFile(skillsFile.toPath());
            }
            skillConfig = loader.load();
        } catch (IOException e) {
            logger.warn("Error ocurred while setting up the skills configuration file!");
        }
    }

    private void checkForLevelUp(Player player, Account account, String skillName) {
        int curLvl = account.getSkillLevel(skillName);
        int expAmount = account.getSkillExp(skillName);

        int expToLvl = curLvl * 100;

        if (curLvl == 100) {
            player.sendMessage(Text.of("You are in the max Level of the skill: ", skillName));

        } else {
            if (expAmount >= expToLvl) {
                account.setSkillLevel(skillName, curLvl + 1);
                account.setSkillExp(skillName, 0);
                player.sendMessage(
                        Text.of(TextColors.DARK_GREEN, "Whow you are now in level ",
                                TextColors.YELLOW, account.getSkillLevel(skillName),
                                TextColors.DARK_GREEN, " in ",
                                TextColors.YELLOW, skillName,
                                TextColors.DARK_GREEN, "."));

                ParticleEffect effect = ParticleEffect.builder()
                        .type(ParticleTypes.FIREWORKS_SPARK)
                        .build();

                player.spawnParticles(effect, player.getLocation().getPosition().add(0, 2, 0));

                player.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, player.getLocation().getPosition(), 1);
            }
        }
    }

    private void giveExp(Player player, Account account, String skillName, int expAmount) {
        account.setSkillExp(skillName, account.getSkillExp(skillName) + expAmount);

        checkForLevelUp(player, account, skillName);
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break e, @First Player player) {
        UUID playerUUID = player.getUniqueId();
        String blockName = e.getTransactions().get(0).getOriginal().getState().getType().getName();

        Account account = new Account(plugin, accountManager, playerUUID);

        for (String skillName : Skill.SKILLS) {
            int expAmount = skillConfig.getNode(skillName, blockName).getInt(0);
            if (expAmount > 0) {
                giveExp(player, account, skillName, expAmount);

                break;
            }
        }
    }
}
