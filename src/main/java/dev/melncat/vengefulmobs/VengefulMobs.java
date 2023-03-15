package dev.melncat.vengefulmobs;

import dev.melncat.vengefulmobs.config.Config;
import dev.melncat.vengefulmobs.listener.EntityListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class VengefulMobs extends JavaPlugin {
	private Config config;
	
	public @NotNull Config config() {
		return config;
	}
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		config = new Config(this);
		config.loadConfig(getConfig());
		
		Bukkit.getPluginManager().registerEvents(new EntityListener(this), this);
		
		Objects.requireNonNull(getCommand("vengefulmobs")).setExecutor(
			(CommandSender sender, Command command, String label, String[] args) -> {
				if (args.length == 0) {
					sender.sendMessage(
						Component.text(getName()).color(NamedTextColor.YELLOW)
							.append(Component.text(" v").color(NamedTextColor.GRAY))
							.append(Component.text("1.0.0").color(NamedTextColor.GREEN))
							.append(Component.newline())
							.append(Component.text("Made by ").color(NamedTextColor.GRAY))
							.append(Component.text("MelnCat").color(NamedTextColor.GREEN))
					);
					return true;
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("reload")) {
						reloadConfig();
						config.loadConfig(getConfig());
						sender.sendMessage(Component.text("Configuraton reloaded.").color(NamedTextColor.GREEN));
						return true;
					}
				}
				return false;
			}
		);
	}
	
	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}
