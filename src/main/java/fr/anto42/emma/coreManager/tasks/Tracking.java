package fr.anto42.emma.coreManager.tasks;

import org.bukkit.Effect;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Tracking extends BukkitRunnable {
    public Arrow arrow;

    public LivingEntity target;

    public Tracking(Arrow arrow, LivingEntity target, Plugin plugin) {
        this.arrow = arrow;
        this.target = target;
        runTaskTimer(plugin, 1L, 1L);
    }

    public void run() {
        Vector newVel;
        double speed = this.arrow.getVelocity().length();
        if (this.arrow.isOnGround() || this.arrow.isDead() || this.target.isDead()) {
            cancel();
            return;
        }
        Vector to = this.target.getLocation().clone().add(new Vector(0.0D, 0.5D, 0.0D)).subtract(this.arrow.getLocation()).toVector();
        Vector dirVel = this.arrow.getVelocity().clone().normalize();
        Vector dirTarget = to.clone().normalize();
        double ang = dirVel.angle(dirTarget);
        double speed_ = 0.9D * speed + 0.13999999999999999D;
        if (this.target instanceof Player && this.arrow.getLocation().distance(this.target.getLocation()) < 8.0D) {
            Player player = (Player)this.target;
            if (player.isBlocking())
                speed_ = speed * 0.6D;
        }
        if (ang < 0.12D) {
            newVel = dirVel.clone().multiply(speed_);
        } else {
            Vector newDir = dirVel.clone().multiply((ang - 0.12D) / ang).add(dirTarget.clone().multiply(0.12D / ang));
            newDir.normalize();
            newVel = newDir.clone().multiply(speed_);
        }
        this.arrow.setVelocity(newVel.add(new Vector(0.0D, 0.03D, 0.0D)));
        this.arrow.getWorld().playEffect(this.arrow.getLocation(), Effect.SMOKE, 0);
    }
}