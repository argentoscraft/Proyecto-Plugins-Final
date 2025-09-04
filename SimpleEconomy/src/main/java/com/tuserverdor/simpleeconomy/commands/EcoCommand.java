package com.tuserverdor.simpleeconomy.commands;

import com.tuserverdor.simpleeconomy.SimpleEconomy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EcoCommand implements CommandExecutor {
    private final SimpleEconomy plugin;

    public EcoCommand(SimpleEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("simpleeconomy.eco")) {
            sender.sendMessage(Component.text("No tienes permiso para usar este comando.", NamedTextColor.RED));
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(Component.text("Uso: /eco <give|set|take> <jugador> <cantidad> <economia>", NamedTextColor.RED));
            return true;
        }

        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("El jugador " + args[1] + " no está en línea.", NamedTextColor.RED));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("La cantidad debe ser un número válido.", NamedTextColor.RED));
            return true;
        }

        if (amount < 0) {
            sender.sendMessage(Component.text("La cantidad no puede ser negativa.", NamedTextColor.RED));
            return true;
        }

        String economyContext = args[3].toLowerCase();
        // Verificamos que la economía exista
        if (!plugin.getEconomyMapping().containsValue(economyContext)) {
            sender.sendMessage(Component.text("La economía '" + economyContext + "' no existe.", NamedTextColor.RED));
            return true;
        }

        switch (action) {
            case "give":
                plugin.getDatabaseManager().addMoney(target.getUniqueId(), economyContext, amount);
                sender.sendMessage(Component.text("Has añadido $" + amount + " a " + target.getName() + " en " + economyContext, NamedTextColor.GREEN));
                target.sendMessage(Component.text("Has recibido $" + amount + "!", NamedTextColor.GREEN));
                break;
            case "set":
                plugin.getDatabaseManager().setMoney(target.getUniqueId(), economyContext, amount);
                sender.sendMessage(Component.text("Has establecido el saldo de " + target.getName() + " a $" + amount + " en " + economyContext, NamedTextColor.GREEN));
                target.sendMessage(Component.text("Tu saldo ha sido establecido a $" + amount + ".", NamedTextColor.GREEN));
                break;
            case "take":
                plugin.getDatabaseManager().removeMoney(target.getUniqueId(), economyContext, amount);
                sender.sendMessage(Component.text("Has quitado $" + amount + " de " + target.getName() + " en " + economyContext, NamedTextColor.GREEN));
                target.sendMessage(Component.text("Te han quitado $" + amount + ".", NamedTextColor.GREEN));
                break;
            default:
                sender.sendMessage(Component.text("Uso: /eco <give|set|take> <jugador> <cantidad> <economia>", NamedTextColor.RED));
                break;
        }
        return true;
    }
}