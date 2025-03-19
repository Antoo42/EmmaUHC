package fr.anto42.emma.game.modes.deathNote.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.uis.config.GameModeGUI;
import fr.anto42.emma.game.modes.deathNote.DeathNoteModule;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;

public class DeathNoteConfigGUI {
    private final KInventory kInventory;

    public DeathNoteConfigGUI(DeathNoteModule module){
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §3§lDeath§6§lNote");
        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 4).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 4).get());
            this.kInventory.setElement(i, glass);
        }
        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            new GameModeGUI().getkInventory().open(player);
        });
        this.kInventory.setElement(49, back);


        KItem investPoints = new KItem(new ItemCreator(SkullList.EYE.getItemStack()).name("§8┃ §fPoints pour l'enquête").lore("", "§8» §fStatut: §c" + module.getDeathNoteConfig().getPointsForInvestigation() + " §fpoints", "", "§8┃ §6Configurez §fle nombre de points", "§8┃ §frecquis pour mener à bien une enquête", "", "§8» §6Clique-Droit §fpour diminuer de 100.","§8» §6Clique-Gauche§f pour augmenter de 100.").get());
        investPoints.addCallback((kInventory1, item, player, clickContext) -> {
            if(clickContext.getClickType().isLeftClick()){
                if(module.getDeathNoteConfig().getPointsForInvestigation() == 5000)
                    return;
                module.getDeathNoteConfig().setPointsForInvestigation(module.getDeathNoteConfig().getPointsForInvestigation() + 100);
            }else if(clickContext.getClickType().isRightClick()){
                if(module.getDeathNoteConfig().getPointsForInvestigation() == 100)
                    return;
                module.getDeathNoteConfig().setPointsForInvestigation(module.getDeathNoteConfig().getPointsForInvestigation() - 100);
            }
            investPoints.setItem(new ItemCreator(SkullList.EYE.getItemStack()).name("§8┃ §fPoints pour l'enquête").lore("", "§8» §fStatut: §c" + module.getDeathNoteConfig().getPointsForInvestigation() + " §fpoints", "", "§8┃ §6Configurez §fle nombre de points", "§8┃ §frecquis pour mener à bien une enquête", "", "§8» §6Clique-Droit §fpour diminuer de 100.","§8» §6Clique-Gauche§f pour augmenter de 100.").get());
        });
        kInventory.setElement(21, investPoints);

        KItem dnDist = new KItem(new ItemCreator(Material.COMPASS).name("§8┃ §fDistance du §3Death§6Note").lore("", "§8» §fStatut: §c" + module.getDeathNoteConfig().getDistanceDeathNote() + " §fmètres", "", "§8┃ §fChoisissez le champ d'action du §3Death§6Note", "", "§8» §6Clique-Droit §fpour diminuer de 1.","§8» §6Clique-Gauche§f pour augmenter de 1.").get());
        dnDist.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if(kInventoryClickContext.getClickType().isLeftClick()){
                if(module.getDeathNoteConfig().getDistanceDeathNote() == 50)
                    return;
                module.getDeathNoteConfig().setDistanceDeathNote(module.getDeathNoteConfig().getDistanceDeathNote() + 1);
            }
            else if(kInventoryClickContext.getClickType().isRightClick()){
                if(module.getDeathNoteConfig().getDistanceDeathNote() == 5)
                    return;
                module.getDeathNoteConfig().setDistanceDeathNote(module.getDeathNoteConfig().getDistanceDeathNote() + 1);
            }
           dnDist.setItem(new ItemCreator(Material.COMPASS).name("§8┃ §fDistance du §3Death§6Note").lore("", "§8» §fStatut: §c" + module.getDeathNoteConfig().getDistanceDeathNote() + " §fmètres", "", "§8┃ §fChoisissez le champ d'action du §3Death§6Note", "", "§8» §6Clique-Droit §fpour diminuer de 1.","§8» §6Clique-Gauche§f pour augmenter de 1.").get());
        });
        kInventory.setElement(23, dnDist);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
