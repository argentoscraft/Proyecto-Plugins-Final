package com.tuservidor.stafftools.managers;

import com.tuservidor.stafftools.StaffTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class StaffModeManager {

    private final StaffTools plugin;
    private final HashMap<UUID, ItemStack[]> playerInventories = new HashMap<>();
    private final HashMap<UUID, ItemStack[]> playerArmor = new HashMap<>();
    private final HashMap<UUID, GameMode> playerGameModes = new HashMap<>();
    private final HashMap<UUID, Boolean> inStaffMode = new HashMap<>();

    public StaffModeManager(StaffTools plugin) {
        this.plugin = plugin;
    }

    public boolean isInStaffMode(Player player) {
        return inStaffMode.getOrDefault(player.getUniqueId(), false);
    }

    public void enableStaffMode(Player player) {
        playerInventories.put(player.getUniqueId(), player.getInventory().getContents());
        playerArmor.put(player.getUniqueId(), player.getInventory().getArmorContents());
        playerGameModes.put(player.getUniqueId(), player.getGameMode());

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.CREATIVE);
        inStaffMode.put(player.getUniqueId(), true);

        giveStaffItems(player);
    }

    public void disableStaffMode(Player player) {
        if (plugin.getVanishManager().isVanished(player)) {
            plugin.getVanishManager().disableVanish(player);
        }

        player.getInventory().clear();
        player.getInventory().setContents(playerInventories.get(player.getUniqueId()));
        player.getInventory().setArmorContents(playerArmor.get(player.getUniqueId()));
        player.setGameMode(playerGameModes.getOrDefault(player.getUniqueId(), GameMode.SURVIVAL));
        inStaffMode.put(player.getUniqueId(), false);

        playerInventories.remove(player.getUniqueId());
        playerArmor.remove(player.getUniqueId());
        playerGameModes.remove(player.getUniqueId());
    }

    private void giveStaffItems(Player player) {
        // Los slots del inventario van de 0 a 35. La hotbar son los slots 0-8.
        player.getInventory().setItem(0, createGamemodeSwitcher(player.getGameMode())); // Slot 1
        player.getInventory().setItem(3, createVanishItem(false));                    // Slot 4
        player.getInventory().setItem(5, createFreezeWand());                         // Slot 6
        player.getInventory().setItem(8, createPlayerNavigator());                    // Slot 9
    }

    public ItemStack createVanishItem(boolean isVanished) {
        ItemStack vanishItem;
        if (isVanished) {
            vanishItem = new ItemStack(Material.LIME_DYE);
            ItemMeta meta = vanishItem.getItemMeta();
            meta.displayName(Component.text("Vanish (Activado)").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(Component.text("Clic derecho para desactivar").color(NamedTextColor.GRAY)));
            vanishItem.setItemMeta(meta);
        } else {
            vanishItem = new ItemStack(Material.GRAY_DYE);
            ItemMeta meta = vanishItem.getItemMeta();
            meta.displayName(Component.text("Vanish (Desactivado)").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(Component.text("Clic derecho para activar").color(NamedTextColor.GRAY)));
            vanishItem.setItemMeta(meta);
        }
        return vanishItem;
    }

    private ItemStack createFreezeWand() {
        ItemStack freezeWand = new ItemStack(Material.ICE);
        ItemMeta meta = freezeWand.getItemMeta();
        meta.displayName(Component.text("Vara Congeladora").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Component.text("Clic izquierdo en un jugador para congelarlo.").color(NamedTextColor.GRAY)));
        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        freezeWand.setItemMeta(meta);
        return freezeWand;
    }
    
    public ItemStack createGamemodeSwitcher(GameMode currentGameMode) {
        ItemStack gmSwitcher;
        if (currentGameMode == GameMode.CREATIVE) {
            gmSwitcher = new ItemStack(Material.GRASS_BLOCK);
            ItemMeta meta = gmSwitcher.getItemMeta();
            meta.displayName(Component.text("Modo: Creativo").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(Component.text("Clic derecho para cambiar a Survival").color(NamedTextColor.GRAY)));
            gmSwitcher.setItemMeta(meta);
        } else {
            gmSwitcher = new ItemStack(Material.BEDROCK);
            ItemMeta meta = gmSwitcher.getItemMeta();
            meta.displayName(Component.text("Modo: Survival").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(Component.text("Clic derecho para cambiar a Creativo").color(NamedTextColor.GRAY)));
            gmSwitcher.setItemMeta(meta);
        }
        return gmSwitcher;
    }

    private ItemStack createPlayerNavigator() {
        ItemStack navigator = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = navigator.getItemMeta();
        meta.displayName(Component.text("Navegador de Jugadores").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(Component.text("Clic derecho para abrir el menú de jugadores.").color(NamedTextColor.GRAY)));
        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        navigator.setItemMeta(meta);
        return navigator;
    }
}