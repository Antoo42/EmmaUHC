package fr.anto42.emma.coreManager.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.uis.gameSaves.MessagesSentByPlayerGUI;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.chat.InteractiveMessage;
import fr.anto42.emma.utils.chat.InteractiveMessageBuilder;
import fr.anto42.emma.utils.saves.SaveSerializationManager;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpecInfoGUI {
    private final KInventory kInventory;

    public SpecInfoGUI(Player p) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(p);
        this.kInventory = new KInventory(54, UHC.getInstance().getConfig().getString("modPrefix").replace("&", "§") + " §6§lMenu de modération");
        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
            this.kInventory.setElement(i, glass);
        }

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            player.closeInventory();
        });
        this.kInventory.setElement(49, back);

        KItem messages = new KItem(new ItemCreator(Material.PAPER).name("§8┃ §fMessages envoyés").lore("", "§8┃ §fConsultez les messages envoyés par ce joueur", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        messages.addCallback((kInventory1, item, player, clickContext) -> {
            new MessagesSentByPlayerGUI(uhcPlayer.getName(), getkInventory(), UHC.getInstance().getGameSave()).getkInventory().open(player);
        });
        this.kInventory.setElement(12, messages);

        KItem playerHead = new KItem(new ItemCreator(SkullList.GOLDENAPPLE.getItemStack()).name("§8┃ §f" + uhcPlayer.getName()).get());
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(" §8§l» §7Kills: §c" + uhcPlayer.getKills());
        lore.add(" §8§l» §7Morts: §c" + uhcPlayer.getDeath());
        lore.add(" §8§l» §7Diamants minés: §b" + uhcPlayer.getDiamondMined());
        lore.add(" §8§l» §7Or miné: §e" + uhcPlayer.getGoldMined());
        lore.add(" §8§l» §7Fer miné: §f" + uhcPlayer.getIronMined());
        lore.add(" §8§l» §7Equipe: §c" + (uhcPlayer.getUhcTeam() != null ? uhcPlayer.getUhcTeam() : "Aucune"));
        lore.add(" §8§l» §7Rôle: §c" + (uhcPlayer.getRole() != null ? uhcPlayer.getRole() : "Aucun"));
        lore.add(" §8§l» §7Statut: §a" + (uhcPlayer.isAlive() ? "§aEn vie" : "§cMort"));
        lore.add(" §8§l» §7Degats: §c" + ((int) uhcPlayer.getMakeDamages()) +  "❤ infligés §8┃ §c" + ((int) uhcPlayer.getReceivedDamages()) +  "❤ reçues");
        lore.add("");
        lore.add(" §8§l» §7Evenements liés");
        UHC.getInstance().getGameSave().getEvents().stream().filter(s -> s.contains(uhcPlayer.getName())).forEach(s -> {
            lore.add("  §8§l» §e" + SaveSerializationManager.fromEventString(s).getTimer() + "§f: " + SaveSerializationManager.fromEventString(s).getString());
        });
        lore.add("");
        lore.add("§8§l» §6Cliquez §fpour vous téléporter à §e" + uhcPlayer.getName() + "§f.");
        playerHead.setDescription(lore);
        this.kInventory.setElement(22, playerHead);
        playerHead.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            player.teleport(p.getLocation());
            player.closeInventory();
        });
        KItem message = new KItem(new ItemCreator(SkullList.BLUE_BALL.getItemStack()).name("§8┃ §fEnvoyer un message").lore("", "§8§l» §6Cliquez§f pour envoyer un message à §e" + p.getName() + "§f.").get());
        message.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            InteractiveMessage interactiveMessage = new InteractiveMessage();
            interactiveMessage.add(new InteractiveMessageBuilder("§8§l» §6Cliquez§f pour envoyer un message à §e" + p.getName() + "§f.")
                    .setHoverMessage("§8§l» §6Cliquez §fpour envoyer un message privé à " + uhcPlayer.getBukkitPlayer().getDisplayName()).setClickAction(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + uhcPlayer.getName() + " ").build());
            interactiveMessage.sendMessage(player);
        });
        this.kInventory.setElement(20, message);

        KItem inv = new KItem(new ItemCreator(SkullList.CHEST.getItemStack()).name("§8┃ §fInventaire").lore("", "§8§l» §6Cliquez§f pour voir l'inventaire de §e" + p.getName()+ "§f.").get());
        inv.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            new PlayerInvSeeGUI(p).getkInventory().open(player);
        });
        this.kInventory.setElement(24, inv);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
