package com.tuservidor.ranksystem.commands;

import com.tuservidor.ranksystem.RankSystem;
import com.tuservidor.ranksystem.objects.Kit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class KitCommand implements CommandExecutor {

    private final RankSystem plugin;

    public KitCommand(RankSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Este comando debe ser usado por un jugador."));
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("ranksystem.admin.kit")) {
            if (args.length == 0) {
                plugin.getKitGUI().open(player);
            } else {
                handlePlayerClaimKit(player, args[0]);
            }
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("Uso para admins: /kit <create|delete|setitem|...>", NamedTextColor.YELLOW));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "create":
                handleCreateCommand(player, args);
                break;
            case "delete":
                handleDeleteCommand(player, args);
                break;
            case "setitem":
                handleSetItemCommand(player, args);
                break;
            default:
                player.sendMessage(Component.text("Sub-comando desconocido. Uso: /kit <create|delete|setitem|...>", NamedTextColor.RED));
                break;
        }
        return true;
    }
    
    private void handlePlayerClaimKit(Player player, String kitName) {
        String rankName = plugin.getPlayerDataManager().getPlayerRank(player.getUniqueId());
        List<String> allowedKits = plugin.getRankManager().getAllKits(rankName);

        if (!allowedKits.contains(kitName.toLowerCase())) {
            player.sendMessage(Component.text("No tienes permiso para reclamar este kit.").color(NamedTextColor.RED));
            return;
        }
        
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            player.sendMessage(Component.text("El kit '" + kitName + "' no existe.").color(NamedTextColor.RED));
            return;
        }

        long lastClaimed = plugin.getPlayerDataManager().getKitCooldown(player.getUniqueId(), kitName);
        long cooldown = kit.getCooldown();
        long timeRemaining = (lastClaimed + cooldown) - (System.currentTimeMillis() / 1000);

        if (timeRemaining > 0) {
            player.sendMessage(Component.text("¡Aún no puedes reclamar este kit!").color(NamedTextColor.RED));
            return;
        }

        for (ItemStack item : kit.getItems()) {
            player.getInventory().addItem(item);
        }

        plugin.getPlayerDataManager().setKitCooldown(player.getUniqueId(), kitName);
        player.sendMessage(Component.text("¡Has reclamado el kit '" + kitName + "'!").color(NamedTextColor.GREEN));
    }

    private void handleCreateCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Component.text("Uso correcto: /kit create <nombre> <cooldown_en_segundos>", NamedTextColor.RED));
            return;
        }
        String kitName = args[1];
        long cooldown;
        try {
            cooldown = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("El cooldown debe ser un número en segundos.", NamedTextColor.RED));
            return;
        }

        boolean created = plugin.getKitManager().createKit(kitName, cooldown);

        if (created) {
            sender.sendMessage(Component.text("¡Kit '" + kitName + "' creado con un cooldown de " + cooldown + " segundos!", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("El kit '" + kitName + "' ya existe.", NamedTextColor.RED));
        }
    }
    
    private void handleDeleteCommand(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Component.text("Uso: /kit delete <nombre>", NamedTextColor.RED));
            return;
        }
        String kitName = args[1];
        if (plugin.getKitManager().deleteKit(kitName)) {
            sender.sendMessage(Component.text("Kit '" + kitName + "' eliminado.", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("El kit '" + kitName + "' no existe.", NamedTextColor.RED));
        }
    }
    
    private void handleSetItemCommand(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(Component.text("Uso correcto: /kit setitem <nombre_del_kit>", NamedTextColor.RED));
            return;
        }

        String kitName = args[1];
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            player.sendMessage(Component.text("El kit '" + kitName + "' no existe. Créalo primero con /kit create.", NamedTextColor.RED));
            return;
        }

        Block targetBlock = player.getTargetBlock(null, 5);
        if (!(targetBlock.getState() instanceof Chest)) {
            player.sendMessage(Component.text("Debes estar mirando un cofre para usar este comando.", NamedTextColor.RED));
            return;
        }

        Chest chest = (Chest) targetBlock.getState();
        List<ItemStack> kitItems = Arrays.stream(chest.getBlockInventory().getContents())
                                          .filter(Objects::nonNull)
                                          .collect(Collectors.toList());

        if (kitItems.isEmpty()) {
            player.sendMessage(Component.text("El cofre está vacío. No se han añadido items al kit.", NamedTextColor.YELLOW));
            return;
        }
        
        kit.getItems().clear();
        kit.getItems().addAll(kitItems);
        
        plugin.getKitManager().saveKits();
        
        player.sendMessage(Component.text("¡Se han establecido " + kitItems.size() + " tipos de items para el kit '" + kitName + "'!", NamedTextColor.GREEN));
    }
}