package fr.anto42.emma.coreManager.achievements.succes;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.game.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public abstract class ProgressiveDiamondAchievement extends Achievement {
    private final GameState requiredState = GameState.PLAYING;

    public ProgressiveDiamondAchievement(String id, String name, String description, int requiredDiamonds) {
        super(id, name, description, requiredDiamonds);
    }

    @EventHandler
    public void onDiamondMine(BlockBreakEvent event) {
        if (!UHC.getInstance().getUhcGame().getGameState().equals(requiredState))
            return;
        if (!event.getBlock().getType().equals(Material.DIAMOND_ORE))
            return;
        Player player = event.getPlayer();
        if (player == null) return;
        AchievementManager.getPlayerData(player).updateProgress(getId(), 1);
    }

    public static class OneDiamond extends ProgressiveDiamondAchievement {
        public OneDiamond() {
            super("one_diamond", "Premier diamant !", "Miner 1 diamant", 1);
        }
    }
    public static class TenDiamonds extends ProgressiveDiamondAchievement {
        public TenDiamonds() {
            super("ten_diamonds", "Petit filon", "Miner 10 diamants", 10);
        }
    }
    public static class TwentyFiveDiamonds extends ProgressiveDiamondAchievement {
        public TwentyFiveDiamonds() {
            super("25_diamonds", "Chercheur aguerri", "Miner 25 diamants", 25);
        }
    }
    public static class FiftyDiamonds extends ProgressiveDiamondAchievement {
        public FiftyDiamonds() {
            super("50_diamonds", "Prospecteur", "Miner 50 diamants", 50);
        }
    }
    public static class HundredDiamonds extends ProgressiveDiamondAchievement {
        public HundredDiamonds() {
            super("100_diamonds", "Roi des mines", "Miner 100 diamants", 100);
        }
    }
    public static class FiveHundredDiamonds extends ProgressiveDiamondAchievement {
        public FiveHundredDiamonds() {
            super("500_diamonds", "Diamantaire", "Miner 500 diamants", 500);
        }
    }
    public static class ThousandDiamonds extends ProgressiveDiamondAchievement {
        public ThousandDiamonds() {
            super("1000_diamonds", "Magnat du diamant", "Miner 1000 diamants", 1000);
        }
    }
    public static class FiveThousandDiamonds extends ProgressiveDiamondAchievement {
        public FiveThousandDiamonds() {
            super("5000_diamonds", "Légende souterraine", "Miner 5000 diamants", 5000);
        }
    }
    public static class TenThousandDiamonds extends ProgressiveDiamondAchievement {
        public TenThousandDiamonds() {
            super("10000_diamonds", "Mythe du diamant", "Miner 10000 diamants", 10000);
        }
    }

}
