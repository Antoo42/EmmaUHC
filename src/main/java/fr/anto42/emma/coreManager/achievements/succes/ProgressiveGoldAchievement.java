package fr.anto42.emma.coreManager.achievements.succes;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.game.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public abstract class ProgressiveGoldAchievement extends Achievement {
    private final GameState requiredState = GameState.PLAYING;

    public ProgressiveGoldAchievement(String id, String name, String description, int requiredGold) {
        super(id, name, description, requiredGold);
    }

    @EventHandler
    public void onGoldMine(BlockBreakEvent event) {
        if (!UHC.getInstance().getUhcGame().getGameState().equals(requiredState))
            return;
        if (!event.getBlock().getType().equals(Material.GOLD_ORE))
            return;
        Player player = event.getPlayer();
        if (player == null) return;
        AchievementManager.getPlayerData(player).updateProgress(getId(), 1);
    }

    public static class OneGold extends ProgressiveGoldAchievement {
        public OneGold() {
            super("one_gold", "Premier lingot !", "Miner 1 or", 1);
        }
    }
    public static class TenGolds extends ProgressiveGoldAchievement {
        public TenGolds() {
            super("ten_golds", "Petit filon doré", "Miner 10 ors", 10);
        }
    }
    public static class TwentyFiveGolds extends ProgressiveGoldAchievement {
        public TwentyFiveGolds() {
            super("25_golds", "Chercheur doré", "Miner 25 ors", 25);
        }
    }
    public static class FiftyGolds extends ProgressiveGoldAchievement {
        public FiftyGolds() {
            super("50_golds", "Prospecteur doré", "Miner 50 ors", 50);
        }
    }
    public static class HundredGolds extends ProgressiveGoldAchievement {
        public HundredGolds() {
            super("100_golds", "Roi des filons", "Miner 100 ors", 100);
        }
    }
    public static class FiveHundredGolds extends ProgressiveGoldAchievement {
        public FiveHundredGolds() {
            super("500_golds", "Orpailleur", "Miner 500 ors", 500);
        }
    }
    public static class ThousandGolds extends ProgressiveGoldAchievement {
        public ThousandGolds() {
            super("1000_golds", "Magnat de l'or", "Miner 1000 ors", 1000);
        }
    }
    public static class FiveThousandGolds extends ProgressiveGoldAchievement {
        public FiveThousandGolds() {
            super("5000_golds", "Légende dorée", "Miner 5000 ors", 5000);
        }
    }
    public static class TenThousandGolds extends ProgressiveGoldAchievement {
        public TenThousandGolds() {
            super("10000_golds", "Mythe de l'or", "Miner 10000 ors", 10000);
        }
    }
}
