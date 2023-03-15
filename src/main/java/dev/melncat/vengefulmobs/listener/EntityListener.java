package dev.melncat.vengefulmobs.listener;

import com.destroystokyo.paper.entity.ai.MobGoals;
import com.destroystokyo.paper.entity.ai.PaperVanillaGoal;
import com.destroystokyo.paper.entity.ai.VanillaGoal;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import dev.melncat.vengefulmobs.VengefulMobs;
import dev.melncat.vengefulmobs.config.Config;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class EntityListener implements Listener {
	private final VengefulMobs plugin;
	
	public EntityListener(VengefulMobs plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	void on(EntityAddToWorldEvent event) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Entity entity = event.getEntity();
		if (!plugin.config().isEnabled(entity.getType())) return;
		if (!(entity instanceof Creature mob)) return; // This should never happen
		Config.MobConfig config = plugin.config().fromType(entity.getType());
		mob.registerAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
		Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE))
			.setBaseValue(config.damage());
		MobGoals goals = Bukkit.getMobGoals();
		net.minecraft.world.entity.Entity nmsEntity = (net.minecraft.world.entity.Entity)
			mob.getClass().getMethod("getHandle").invoke(mob);
		if (!(nmsEntity instanceof PathfinderMob handle)) return;
		goals.addGoal(mob, 1, new PaperVanillaGoal<>(new MeleeAttackGoal(handle, 1.0, false)));
		goals.removeGoal(mob, VanillaGoal.PANIC);
		switch (config.mode()) {
			case RETALIATE ->
				goals.addGoal(mob, 1, new PaperVanillaGoal<>(
					new HurtByTargetGoal(handle)
				));
			case HOSTILE ->
				goals.addGoal(mob, 3, new PaperVanillaGoal<>(
					new NearestAttackableTargetGoal<>(handle, Player.class, true)
				));
			case MURDER_ALL ->
				goals.addGoal(mob, 3, new PaperVanillaGoal<>(
					new NearestAttackableTargetGoal<>(handle, LivingEntity.class, 10, true, true, LivingEntity::attackable)
				));
			case MURDER_OTHERS ->
				goals.addGoal(mob, 3, new PaperVanillaGoal<>(
					new NearestAttackableTargetGoal<>(handle, LivingEntity.class, 10, true, true,
						e -> e.attackable() && e.getType() != handle.getType())
				));
		}
	}
}
