package com.tuservidor.ranksystem.commands;

import com.tuservidor.ranksystem.RankSystem;
import com.tuservidor.ranksystem.objects.Rank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class RankCommand implements CommandExecutor {

    private final RankSystem plugin;

    public RankCommand(RankSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Uso: /rank <list|create|delete|setprefix|setchatcolor|setparent|addperm|delperm|addkit|delkit>", NamedTextColor.RED));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "list":
                handleListCommand(sender);
                break;
            case "create":
                handleCreateCommand(sender, args);
                break;
            case "delete":
                handleDeleteCommand(sender, args);
                break;
            case "setprefix":
                handleSetPrefixCommand(sender, args);
                break;
            case "setchatcolor":
                handleSetChatColorCommand(sender, args);
                break;
            case "setparent":
                handleSetParentCommand(sender, args);
                break;
            case "addperm":
                handleAddPermCommand(sender, args);
                break;
            case "delperm":
                handleDelPermCommand(sender, args);
                break;
            case "addkit":
                handleAddKitCommand(sender, args);
                break;
            case "delkit":
                handleDelKitCommand(sender, args);
                break;
            default:
                sender.sendMessage(Component.text("Sub-comando desconocido.", NamedTextColor.RED));
                break;
        }
        return true;
    }

    private void handleListCommand(CommandSender sender) {
        String rankList = String.join(", ", plugin.getRankManager().getAllRankNames());
        sender.sendMessage(Component.text("Rangos disponibles: ").color(NamedTextColor.YELLOW).append(Component.text(rankList).color(NamedTextColor.WHITE)));
    }
    
    private void handleCreateCommand(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Component.text("Uso correcto: /rank create <nombreDelRango>", NamedTextColor.RED));
            return;
        }
        String rankName = args[1];
        boolean created = plugin.getRankManager().createRank(rankName);
        if (created) {
            sender.sendMessage(Component.text("¡Rango '" + rankName + "' creado con éxito!", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("El rango '" + rankName + "' ya existe.", NamedTextColor.RED));
        }
    }

    private void handleDeleteCommand(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Component.text("Uso: /rank delete <rango>", NamedTextColor.RED));
            return;
        }
        String rankName = args[1];
        if (plugin.getRankManager().deleteRank(rankName)) {
            sender.sendMessage(Component.text("Rango '" + rankName + "' eliminado con éxito.", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("El rango '" + rankName + "' no existe, es el rango por defecto, o es padre de otro rango.", NamedTextColor.RED));
        }
    }

    private void handleSetPrefixCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Component.text("Uso: /rank setprefix <rango> <prefijo>", NamedTextColor.RED));
            return;
        }
        String rankName = args[1];
        String prefix = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));

        Rank rank = plugin.getRankManager().getRank(rankName);
        if (rank == null) {
            sender.sendMessage(Component.text("El rango '" + rankName + "' no existe.", NamedTextColor.RED));
            return;
        }
        rank.setPrefix(prefix);
        plugin.getRankManager().saveRanks();
        updateTablistForRank(rankName);
        sender.sendMessage(Component.text("Prefijo del rango '" + rankName + "' actualizado.", NamedTextColor.GREEN));
    }

    private void handleSetChatColorCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Component.text("Uso: /rank setchatcolor <rango> <color>", NamedTextColor.RED));
            return;
        }
        String rankName = args[1];
        String color = args[2];

        Rank rank = plugin.getRankManager().getRank(rankName);
        if (rank == null) {
            sender.sendMessage(Component.text("El rango '" + rankName + "' no existe.", NamedTextColor.RED));
            return;
        }
        rank.setChatColor(color);
        plugin.getRankManager().saveRanks();
        sender.sendMessage(Component.text("Color de chat del rango '" + rankName + "' actualizado.", NamedTextColor.GREEN));
    }

    private void handleSetParentCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Component.text("Uso: /rank setparent <rango_hijo> <rango_padre>", NamedTextColor.RED));
            return;
        }
        String childRankName = args[1];
        String parentRankName = args[2];

        Rank childRank = plugin.getRankManager().getRank(childRankName);
        if (childRank == null) {
            sender.sendMessage(Component.text("El rango '" + childRankName + "' no existe.", NamedTextColor.RED));
            return;
        }

        Rank parentRank = plugin.getRankManager().getRank(parentRankName);
        if (parentRank == null) {
            sender.sendMessage(Component.text("El rango padre '" + parentRankName + "' no existe.", NamedTextColor.RED));
            return;
        }

        childRank.setParent(parentRankName);
        plugin.getRankManager().saveRanks();
        updatePermissionsForRank(childRankName);
        sender.sendMessage(Component.text("El rango '" + parentRankName + "' es ahora el padre de '" + childRankName + "'.", NamedTextColor.GREEN));
    }

    private void handleAddPermCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Component.text("Uso correcto: /rank addperm <rango> <permiso>", NamedTextColor.RED));
            return;
        }
        String rankName = args[1];
        String permission = args[2];
        Rank rank = plugin.getRankManager().getRank(rankName);
        if (rank == null) {
            sender.sendMessage(Component.text("El rango '" + rankName + "' no existe.", NamedTextColor.RED));
            return;
        }
        rank.getPermissions().add(permission);
        plugin.getRankManager().saveRanks();
        updatePermissionsForRank(rankName);
        sender.sendMessage(Component.text("Permiso '" + permission + "' añadido al rango '" + rankName + "'.", NamedTextColor.GREEN));
    }

    private void handleDelPermCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Component.text("Uso correcto: /rank delperm <rango> <permiso>", NamedTextColor.RED));
            return;
        }
        String rankName = args[1];
        String permission = args[2];
        Rank rank = plugin.getRankManager().getRank(rankName);
        if (rank == null) {
            sender.sendMessage(Component.text("El rango '" + rankName + "' no existe.", NamedTextColor.RED));
            return;
        }
        if (rank.getPermissions().remove(permission)) {
            plugin.getRankManager().saveRanks();
            updatePermissionsForRank(rankName);
            sender.sendMessage(Component.text("Permiso '" + permission + "' eliminado del rango '" + rankName + "'.", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("El rango '" + rankName + "' no tenía ese permiso.", NamedTextColor.RED));
        }
    }
    
    private void handleAddKitCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Component.text("Uso correcto: /rank addkit <rango> <kit>", NamedTextColor.RED));
            return;
        }
        String rankName = args[1];
        String kitName = args[2].toLowerCase();

        Rank rank = plugin.getRankManager().getRank(rankName);
        if (rank == null) {
            sender.sendMessage(Component.text("El rango '" + rankName + "' no existe.", NamedTextColor.RED));
            return;
        }

        if (plugin.getKitManager().getKit(kitName) == null) {
            sender.sendMessage(Component.text("El kit '" + kitName + "' no existe.", NamedTextColor.RED));
            return;
        }
        
        if (rank.getKits().contains(kitName)) {
            sender.sendMessage(Component.text("Ese rango ya tiene acceso a ese kit.", NamedTextColor.YELLOW));
            return;
        }

        rank.getKits().add(kitName);
        plugin.getRankManager().saveRanks();
        sender.sendMessage(Component.text("Kit '" + kitName + "' añadido al rango '" + rankName + "'.", NamedTextColor.GREEN));
    }

    private void handleDelKitCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Component.text("Uso correcto: /rank delkit <rango> <kit>", NamedTextColor.RED));
            return;
        }
        String rankName = args[1];
        String kitName = args[2].toLowerCase();

        Rank rank = plugin.getRankManager().getRank(rankName);
        if (rank == null) {
            sender.sendMessage(Component.text("El rango '" + rankName + "' no existe.", NamedTextColor.RED));
            return;
        }

        if (rank.getKits().remove(kitName)) {
            plugin.getRankManager().saveRanks();
            sender.sendMessage(Component.text("Kit '" + kitName + "' eliminado del rango '" + rankName + "'.", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("El rango '" + rankName + "' no tenía acceso a ese kit.", NamedTextColor.RED));
        }
    }
    
    private void updatePermissionsForRank(String updatedRankName) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String playerRankName = plugin.getPlayerDataManager().getPlayerRank(player.getUniqueId());
            if (plugin.getRankManager().getAllPermissions(playerRankName).containsAll(plugin.getRankManager().getAllPermissions(updatedRankName))) {
                plugin.getPlayerJoinListener().applyPermissions(player);
            }
        }
    }

    private void updateTablistForRank(String rankName) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String playerRankName = plugin.getPlayerDataManager().getPlayerRank(player.getUniqueId());
            if (playerRankName.equalsIgnoreCase(rankName)) {
                plugin.updatePlayerTablist(player);
            }
        }
    }
}