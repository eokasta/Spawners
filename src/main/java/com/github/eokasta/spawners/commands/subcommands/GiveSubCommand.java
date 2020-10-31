package com.github.eokasta.spawners.commands.subcommands;

import com.github.eokasta.commandlib.annotations.SubCommandInformation;
import com.github.eokasta.commandlib.exceptions.CommandLibException;
import com.github.eokasta.commandlib.providers.SubCommand;
import com.github.eokasta.spawners.SpawnerPlugin;
import com.github.eokasta.spawners.utils.Helper;
import com.github.eokasta.spawners.utils.Verifications;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@SubCommandInformation(name = {"give", "dar"},
        permission = "spawners.give",
        usage = "&c/spawner give <player> <entitytype> <amount> &7- &cGive spawners to a player."
)
public class GiveSubCommand extends SubCommand {

    private final SpawnerPlugin plugin;

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandLibException {
        if (args.length < 3)
            throw new CommandLibException(getUsage());

        final Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null)
            throw new CommandLibException("&cPlayer is not online.");

        try {
            final EntityType entityType = EntityType.valueOf(args[1].toUpperCase());
            if (!entityType.isSpawnable())
                throw new CommandLibException("&cEntity type not found.");

            final Double amount = Verifications.getDouble(args[2].split("\\.")[0]);
            if (amount == null || amount <= 0)
                throw new CommandLibException("&cAmount must be greater than 0.");

            target.getInventory().addItem(plugin.getManager().getSpawnerItem(entityType, amount));
            message("&aGiven %s %s spawners for %s.", Helper.formatBalance(amount), entityType.getName(), target.getName());
        } catch (Exception e) {
            throw new CommandLibException("&cEntity type not found.");
        }
    }
}
