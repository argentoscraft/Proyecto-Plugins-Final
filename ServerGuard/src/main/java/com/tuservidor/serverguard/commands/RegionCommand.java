package com.tuservidor.serverguard.commands;

import com.tuservidor.serverguard.ServerGuard;
import com.tuservidor.serverguard.objects.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class RegionCommand implements CommandExecutor {

    private final ServerGuard plugin;
    private final List<String> validFlags = Arrays.asList("block-break", "block-place", "pvp", "mob-spawning", "animal-spawning", "use", "fly", "invincible", "entry-message");
    private final List<String> flagValues = Arrays.asList("allow", "deny");

    public RegionCommand(ServerGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Comando solo para jugadores.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Component.text("Uso: /region <list|define|delete|flag> ...", NamedTextColor.RED));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "define":
                handleDefineCommand(player, args);
                break;
            case "delete":
                handleDeleteCommand(player, args);
                break;
            case "list":
                handleListCommand(player);
                break;
            case "flag":
                handleFlagCommand(player, args);
                break;
            default:
                player.sendMessage(Component.text("Subcomando desconocido.", NamedTextColor.RED));
                break;
        }
        return true;
    }

    private void handleDefineCommand(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(Component.text("Uso: /region define <nombre>", NamedTextColor.RED));
            return;
        }
        String regionName = args[1];
        if (plugin.getRegionManager().getPos1(player.getUniqueId()) == null || plugin.getRegionManager().getPos2(player.getUniqueId()) == null) {
            player.sendMessage(Component.text("Debes seleccionar dos posiciones primero con un hacha de madera.", NamedTextColor.RED));
            return;
        }
        if (plugin.getRegionManager().defineRegion(regionName, player.getUniqueId())) {
            player.sendMessage(Component.text("¡Región '" + regionName + "' creada con éxito!", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("La región '" + regionName + "' ya existe o no tienes una selección completa.", NamedTextColor.RED));
        }
    }
    
    private void handleDeleteCommand(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(Component.text("Uso: /region delete <nombre>", NamedTextColor.RED));
            return;
        }
        String regionName = args[1];
        if (plugin.getRegionManager().deleteRegion(regionName)) {
            player.sendMessage(Component.text("¡Región '" + regionName + "' eliminada!", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("La región '" + regionName + "' no existe o es padre de otra región.", NamedTextColor.RED));
        }
    }

    private void handleListCommand(Player player) {
        java.util.Set<String> regionNames = plugin.getRegionManager().getRegionNames();
        if (regionNames.isEmpty()) {
            player.sendMessage(Component.text("No hay regiones definidas.", NamedTextColor.YELLOW));
            return;
        }
        String list = String.join(", ", regionNames);
        player.sendMessage(Component.text("Regiones: ", NamedTextColor.GOLD).append(Component.text(list, NamedTextColor.WHITE)));
    }

    private void handleFlagCommand(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Component.text("Uso: /region flag <region> <flag> <valor>", NamedTextColor.RED));
            return;
        }
        String regionName = args[1];
        String flagName = args[2].toLowerCase();

        Region region = plugin.getRegionManager().getRegionByName(regionName);
        if (region == null) {
            player.sendMessage(Component.text("La región '" + regionName + "' no existe.", NamedTextColor.RED));
            return;
        }

        if (!validFlags.contains(flagName)) {
            player.sendMessage(Component.text("La flag '" + flagName + "' no es válida.", NamedTextColor.RED));
            return;
        }

        String value;
        if (flagName.equals("entry-message")) {
            value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        } else {
            if (args.length != 4) {
                player.sendMessage(Component.text("Uso: /region flag <region> <flag> <allow|deny>", NamedTextColor.RED));
                return;
            }
            value = args[3].toLowerCase();
            if (!flagValues.contains(value)) {
                player.sendMessage(Component.text("El valor de la flag debe ser 'allow' o 'deny'.", NamedTextColor.RED));
                return;
            }
        }

        region.setFlag(flagName, value);
        plugin.getRegionManager().saveRegions();
        player.sendMessage(Component.text("Flag '" + flagName + "' establecida para la región '" + regionName + "'.", NamedTextColor.GREEN));
    }
}