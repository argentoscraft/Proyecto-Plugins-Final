package com.tuservidor.tpaplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TPAPlugin extends JavaPlugin implements Listener, CommandExecutor {

    // --- ALMACENAMIENTO DE DATOS ---
    private final HashMap<UUID, TPARequest> tpaRequests = new HashMap<>();
    private final HashMap<UUID, HashMap<UUID, Long>> tpaCooldowns = new HashMap<>();
    private final HashMap<UUID, List<Long>> tpaCounts = new HashMap<>();
    private final HashSet<UUID> tpaDisabled = new HashSet<>();
    private final HashMap<UUID, BukkitRunnable> activeTeleports = new HashMap<>();
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Carga y crea el config.yml
        getServer().getPluginManager().registerEvents(this, this); // Registra los eventos de esta clase

        // Registra todos los comandos para que sean manejados por esta misma clase
        getCommand("tpa").setExecutor(this);
        getCommand("tpahere").setExecutor(this);
        getCommand("tpaccept").setExecutor(this);
        getCommand("tpdeny").setExecutor(this);
        getCommand("tpatoggle").setExecutor(this);

        getLogger().info("TPAPlugin (versión unificada y completa) ha sido habilitado.");
    }

    // --- MANEJO DE TODOS LOS COMANDOS EN UN SOLO LUGAR ---
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por un jugador.");
            return true;
        }
        Player player = (Player) sender;
        String commandName = command.getName().toLowerCase();

        switch (commandName) {
            case "tpa":
            case "tpahere":
                handleTpaRequest(player, commandName, args);
                break;
            case "tpaccept":
                handleTpaAccept(player);
                break;
            case "tpdeny":
                handleTpaDeny(player);
                break;
            case "tpatoggle":
                handleTpaToggle(player);
                break;
        }
        return true;
    }

    // --- LÓGICA DE LOS COMANDOS ---

    private void handleTpaRequest(Player requester, String commandName, String[] args) {
        if (args.length != 1) {
            requester.sendMessage(serializer.deserialize("&cUso incorrecto. Usa: /" + commandName + " <jugador>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            requester.sendMessage(serializer.deserialize(getConfig().getString("messages.player_not_online", "&cEl jugador {target} no está en línea.").replace("{target}", args[0])));
            return;
        }

        if (target == requester) {
            requester.sendMessage(serializer.deserialize(getConfig().getString("messages.cannot_send_to_self", "&cNo puedes enviarte una petición a ti mismo.")));
            return;
        }
        
        if (tpaDisabled.contains(target.getUniqueId())) {
            requester.sendMessage(serializer.deserialize(getConfig().getString("messages.target_has_tpa_disabled", "&cEse jugador no acepta peticiones de TPA en este momento.")));
            return;
        }

        UUID requesterUUID = requester.getUniqueId();
        UUID targetUUID = target.getUniqueId();
        long currentTime = System.currentTimeMillis() / 1000;
        
        // Lógica de Cooldown específico por jugador
        long cooldownTime = getConfig().getLong("cooldowns.tpa_specific_player", 60);
        if (tpaCooldowns.containsKey(requesterUUID) && tpaCooldowns.get(requesterUUID).containsKey(targetUUID)) {
            long lastRequestTime = tpaCooldowns.get(requesterUUID).get(targetUUID);
            long timeRemaining = cooldownTime - (currentTime - lastRequestTime);
            if (timeRemaining > 0) {
                requester.sendMessage(serializer.deserialize(getConfig().getString("messages.on_cooldown", "&cDebes esperar {time} segundos.").replace("{time}", String.valueOf(timeRemaining))));
                return;
            }
        }

        // Lógica de Límite de peticiones
        int maxRequests = getConfig().getInt("limits.max_requests", 3);
        long period = getConfig().getLong("limits.max_requests_period", 60);
        List<Long> requestTimes = tpaCounts.getOrDefault(requesterUUID, new ArrayList<>());
        requestTimes.removeIf(time -> (currentTime - time) > period);
        if (requestTimes.size() >= maxRequests) {
            requester.sendMessage(serializer.deserialize(getConfig().getString("messages.rate_limit_exceeded", "&cHas alcanzado el límite de {limit} peticiones.").replace("{limit}", String.valueOf(maxRequests))));
            return;
        }

        // Procesar y enviar la petición
        boolean isTpaHere = commandName.equals("tpahere");
        tpaRequests.put(targetUUID, new TPARequest(requesterUUID, isTpaHere));
        
        tpaCooldowns.computeIfAbsent(requesterUUID, k -> new HashMap<>()).put(targetUUID, currentTime);
        requestTimes.add(currentTime);
        tpaCounts.put(requesterUUID, requestTimes);
        
        String sentMsgKey = isTpaHere ? "messages.tpahere_sent" : "messages.tpa_sent";
        requester.sendMessage(serializer.deserialize(getConfig().getString(sentMsgKey, "&aPetición enviada a {target}.").replace("{target}", target.getName())));

        sendRequestMessage(target, requester, isTpaHere);
    }
    
    private void sendRequestMessage(Player target, Player requester, boolean isTpaHere) {
        String receivedMsgKey = isTpaHere ? "messages.tpahere_request_received" : "messages.tpa_request_received";
        String messageLine1 = getConfig().getString(receivedMsgKey, "&b{player} quiere teletransportarse hacia ti.").replace("{player}", requester.getName());
        String acceptButtonText = getConfig().getString("messages.accept_button", "&a&l[ACEPTAR]");
        String denyButtonText = getConfig().getString("messages.deny_button", "&c&l[RECHAZAR]");

        Component messageComponent = serializer.deserialize(messageLine1);
        Component acceptComponent = serializer.deserialize(acceptButtonText)
                .clickEvent(ClickEvent.runCommand("/tpaccept"))
                .hoverEvent(HoverEvent.showText(Component.text("Clic para aceptar")));
        Component denyComponent = serializer.deserialize(denyButtonText)
                .clickEvent(ClickEvent.runCommand("/tpdeny"))
                .hoverEvent(HoverEvent.showText(Component.text("Clic para rechazar")));
        
        Component border = serializer.deserialize("&6------------------------------------------");

        target.sendMessage(border);
        target.sendMessage(messageComponent);
        target.sendMessage(acceptComponent.append(Component.text(" ")).append(denyComponent));
        target.sendMessage(border);
        target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
    
    private void handleTpaAccept(Player target) {
        TPARequest request = tpaRequests.get(target.getUniqueId());
        if (request == null) {
            target.sendMessage(serializer.deserialize(getConfig().getString("messages.no_pending_request", "&cNo tienes ninguna petición pendiente.")));
            return;
        }

        long expirationTime = getConfig().getLong("cooldowns.request_expiration", 60) * 1000;
        if (System.currentTimeMillis() - request.getTimestamp() > expirationTime) {
            target.sendMessage(serializer.deserialize(getConfig().getString("messages.request_expired", "&cLa petición de TPA ha expirado.")));
            tpaRequests.remove(target.getUniqueId());
            return;
        }

        Player requester = Bukkit.getPlayer(request.getSender());
        if (requester == null || !requester.isOnline()) {
             target.sendMessage(serializer.deserialize(getConfig().getString("messages.player_not_online", "&cEl jugador {target} no está en línea.").replace("{target}", "El jugador")));
             tpaRequests.remove(target.getUniqueId());
             return;
        }

        Player playerToTeleport = request.isTpaHere() ? target : requester;
        Player destinationPlayer = request.isTpaHere() ? requester : target;

        TeleportTask task = new TeleportTask(playerToTeleport, destinationPlayer);
        task.runTaskTimer(this, 0L, 20L);
        activeTeleports.put(playerToTeleport.getUniqueId(), task);

        requester.sendMessage(serializer.deserialize(getConfig().getString("messages.request_accepted_sender", "&a{target} ha aceptado tu petición.").replace("{target}", target.getName())));
        target.sendMessage(serializer.deserialize(getConfig().getString("messages.request_accepted_target", "&aHas aceptado la petición de {player}.").replace("{player}", requester.getName())));
        
        tpaRequests.remove(target.getUniqueId());
    }

    private void handleTpaDeny(Player target) {
        TPARequest request = tpaRequests.get(target.getUniqueId());
        if (request == null) {
            target.sendMessage(serializer.deserialize(getConfig().getString("messages.no_pending_request", "&cNo tienes ninguna petición pendiente.")));
            return;
        }

        Player requester = Bukkit.getPlayer(request.getSender());
        if (requester != null && requester.isOnline()) {
            requester.sendMessage(serializer.deserialize(getConfig().getString("messages.request_denied_sender", "&c{target} ha rechazado tu petición.").replace("{target}", target.getName())));
        }

        target.sendMessage(serializer.deserialize(getConfig().getString("messages.request_denied_target", "&eHas rechazado la petición.").replace("{player}", requester != null ? requester.getName() : "un jugador")));
        tpaRequests.remove(target.getUniqueId());
    }
    
    private void handleTpaToggle(Player player) {
        if (tpaDisabled.contains(player.getUniqueId())) {
            tpaDisabled.remove(player.getUniqueId());
            player.sendMessage(serializer.deserialize(getConfig().getString("messages.tpa_toggle_on", "&aAhora puedes recibir peticiones de TPA.")));
        } else {
            tpaDisabled.add(player.getUniqueId());
            player.sendMessage(serializer.deserialize(getConfig().getString("messages.tpa_toggle_off", "&cYa no recibirás peticiones de TPA.")));
        }
    }

    // --- LISTENER PARA EL MOVIMIENTO ---
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        if (activeTeleports.containsKey(playerUUID)) {
            if (event.hasChangedBlock()) {
                activeTeleports.get(playerUUID).cancel();
                // El remove se hace en el método cancel() de la TeleportTask
                event.getPlayer().sendMessage(serializer.deserialize(getConfig().getString("messages.teleport_cancelled_movement", "&cTeletransporte cancelado porque te moviste.")));
            }
        }
    }

    // --- CLASES INTERNAS PARA ORGANIZAR ---
    private static class TPARequest {
        private final UUID sender;
        private final boolean isTpaHere;
        private final long timestamp;

        public TPARequest(UUID sender, boolean isTpaHere) {
            this.sender = sender;
            this.isTpaHere = isTpaHere;
            this.timestamp = System.currentTimeMillis();
        }
        public UUID getSender() { return sender; }
        public boolean isTpaHere() { return isTpaHere; }
        public long getTimestamp() { return timestamp; }
    }

    private class TeleportTask extends BukkitRunnable {
        private final Player playerToTeleport;
        private final Player destinationPlayer;
        private int countdown;

        public TeleportTask(Player playerToTeleport, Player destinationPlayer) {
            this.playerToTeleport = playerToTeleport;
            this.destinationPlayer = destinationPlayer;
            this.countdown = getConfig().getInt("cooldowns.teleport_delay", 3);
        }

        @Override
        public void run() {
            if (!playerToTeleport.isOnline() || !destinationPlayer.isOnline()) {
                this.cancel();
                return;
            }

            if (countdown > 0) {
                playerToTeleport.sendMessage(serializer.deserialize(getConfig().getString("messages.teleport_countdown", "&eTeletransporte en {time}...").replace("{time}", String.valueOf(countdown))));
                playerToTeleport.playSound(playerToTeleport.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                countdown--;
            } else {
                Location originalLocation = playerToTeleport.getLocation();
                playerToTeleport.teleport(destinationPlayer.getLocation());
                
                originalLocation.getWorld().spawnParticle(Particle.PORTAL, originalLocation, 50);
                originalLocation.getWorld().playSound(originalLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                destinationPlayer.getLocation().getWorld().spawnParticle(Particle.END_ROD, destinationPlayer.getLocation().add(0, 1, 0), 30);
                destinationPlayer.playSound(destinationPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);

                this.cancel();
            }
        }
        
        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            activeTeleports.remove(playerToTeleport.getUniqueId());
        }
    }
}