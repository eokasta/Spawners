package com.github.eokasta.spawners.commands;

import com.github.eokasta.commandlib.CommandManager;
import com.github.eokasta.commandlib.annotations.CommandInformation;
import com.github.eokasta.commandlib.exceptions.CommandLibException;
import com.github.eokasta.commandlib.providers.Command;
import com.github.eokasta.spawners.SpawnerPlugin;
import com.github.eokasta.spawners.commands.subcommands.GiveSubCommand;
import org.bukkit.command.CommandSender;

@CommandInformation(name = {"spawner", "spawners"})
public class SpawnerCommand extends Command {

    private final SpawnerPlugin plugin;

    public SpawnerCommand(SpawnerPlugin plugin) {
        this.plugin = plugin;

        registerSubCommand(new GiveSubCommand(plugin));

        CommandManager.registerCommand(plugin, this);
    }

    @Override
    public void perform(CommandSender sender, String label, String[] args) throws CommandLibException {

    }
}
