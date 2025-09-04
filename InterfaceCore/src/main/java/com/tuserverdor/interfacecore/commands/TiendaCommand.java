package com.tuserverdor.interfacecore.commands;

import com.tuserverdor.interfacecore.InterfaceCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TiendaCommand implements CommandExecutor {

    private final InterfaceCore plugin;

    public TiendaCommand(InterfaceCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Comando solo para jugadores.");
            return true;
        }

        Player player = (Player) sender;
        String storeUrl = plugin.getConfig().getString("store-url", "https://tienda.tuserver.com");

        Component message = Component.text()
                .append(Component.text("Haz click aquí para ir a nuestra tienda online", NamedTextColor.GOLD))
                .append(Component.text("\n» ", NamedTextColor.DARK_GRAY))
                .append(Component.text(storeUrl, NamedTextColor.AQUA, TextDecoration.UNDERLINED))
                .clickEvent(ClickEvent.openUrl(storeUrl))
                .build();
        
        player.sendMessage(message);
        return true;
    }
}