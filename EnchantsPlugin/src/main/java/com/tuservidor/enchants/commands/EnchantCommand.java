package com.tuservidor.enchants.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantCommand implements CommandExecutor {
    private static final Map<String, Enchantment> ENCHANTMENT_ALIASES = new HashMap<>();
    static {
        ENCHANTMENT_ALIASES.put("proteccion", Enchantment.PROTECTION_ENVIRONMENTAL);
        ENCHANTMENT_ALIASES.put("filo", Enchantment.DAMAGE_ALL);
        ENCHANTMENT_ALIASES.put("eficiencia", Enchantment.DIG_SPEED);
        ENCHANTMENT_ALIASES.put("irrompibilidad", Enchantment.DURABILITY);
        ENCHANTMENT_ALIASES.put("fortuna", Enchantment.LOOT_BONUS_BLOCKS);
        ENCHANTMENT_ALIASES.put("poder", Enchantment.ARROW_DAMAGE);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Comando solo para jugadores."));
            return true;
        }
        Player player = (Player) sender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(Component.text("¡Debes tener un item en la mano!", NamedTextColor.RED));
            return true;
        }
        if (args.length != 2) {
            player.sendMessage(Component.text("Uso: /encantar <encantamiento> <nivel>", NamedTextColor.RED));
            return true;
        }
        String enchantName = args[0].toLowerCase();
        Enchantment enchantment = ENCHANTMENT_ALIASES.get(enchantName);
        if (enchantment == null) {
            enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantName));
        }
        if (enchantment == null) {
            player.sendMessage(Component.text("Encantamiento '" + enchantName + "' no existe.", NamedTextColor.RED));
            return true;
        }
        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("El nivel debe ser un número.", NamedTextColor.RED));
            return true;
        }
        itemInHand.addUnsafeEnchantment(enchantment, level);
        updateLore(itemInHand);
        player.sendMessage(Component.text("¡Item encantado con " + formatEnchantmentName(enchantment) + " " + level + "!", NamedTextColor.GREEN));
        return true;
    }
    
    private void updateLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
            Enchantment ench = entry.getKey();
            int level = entry.getValue();
            Component loreLine = Component.text(formatEnchantmentName(ench) + " ").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).append(Component.text(toRoman(level)).color(NamedTextColor.GRAY));
            lore.add(loreLine);
        }
        meta.lore(lore);
        item.setItemMeta(meta);
    }
    
    private String formatEnchantmentName(Enchantment enchantment) {
        String name = enchantment.getKey().getKey().replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            capitalized.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return capitalized.toString().trim();
    }
    
    private String toRoman(int number) {
        if (number < 1 || number > 3999) return String.valueOf(number);
        String[] M = {"", "M", "MM", "MMM"};
        String[] C = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] X = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] I = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        return M[number/1000] + C[(number%1000)/100] + X[(number%100)/10] + I[number%10];
    }
}