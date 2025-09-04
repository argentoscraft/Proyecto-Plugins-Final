package com.tuservidor.simpleworldedit.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WandCommand implements CommandExecutor {
    public static final String WAND_NAME = "Varita de Selección";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Este comando solo puede ser usado por un jugador."));
            return true;
        }

        Player player = (Player) sender;
        ItemStack wand = new ItemStack(Material.WOODEN_AXE);
        ItemMeta meta = wand.getItemMeta();

        meta.displayName(Component.text(WAND_NAME).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        meta.lore(List.of(
            Component.text("Clic Izquierdo = Posición 1").color(NamedTextColor.AQUA),
            Component.text("Clic Derecho = Posición 2").color(NamedTextColor.AQUA)
        ));
        wand.setItemMeta(meta);

        player.getInventory().addItem(wand);
        player.sendMessage(Component.text("¡Has recibido la varita de selección!", NamedTextColor.GREEN));
        return true;
    }
}