package com.juan.spongeskills.command;

import com.juan.spongeskills.Main;
import com.juan.spongeskills.config.Account;
import com.juan.spongeskills.config.AccountManager;
import com.juan.spongeskills.skills.Skill;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Locale;
import java.util.UUID;

public class SkillsCommand implements CommandExecutor {

    private Main plugin;
    private AccountManager accountManager;

    public SkillsCommand(Main plugin) {
        this.plugin = plugin;

        accountManager = plugin.getAccountManager();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player player = ((Player) src).getPlayer().get();
            UUID uuid = player.getUniqueId();
            Account account = new Account(plugin, accountManager, uuid);

            player.sendMessage(Text.of(TextColors.DARK_GREEN, "\n<--------------- Skills ----------------->\n"));
            for (String skill : Skill.SKILLS) {
                int exp = account.getSkillExp(skill);
                int level = account.getSkillLevel(skill);
                int expToLevel = level * 100;

                player.sendMessage(Text.of( TextColors.YELLOW," - ", skill.toUpperCase(), " | Level ", level, " - ", exp, " / ", expToLevel, " exp"));
            }
            player.sendMessage(Text.of(TextColors.DARK_GREEN, "\n<--------------- Skills ----------------->\n"));

            return CommandResult.success();
        }
        return CommandResult.empty();
    }
}
