package com.rmpi.nukkit.spa;

import com.rmpi.nukkit.spa.task.AnnounceTask;

import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.LinkedHashMap;

public class SimpleAnnouncer extends cn.nukkit.plugin.PluginBase {
	public java.util.List<String> messages;
	public int interval = 10 * 20;
	private TaskHandler announceHandler = null;
	public Config settings = null;
	private TaskHandler saveHandler = null;
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public void onEnable() {
		getLogger().info("SimpleAnnouncer Enabled");
		getDataFolder().mkdirs();
		LinkedHashMap<String, Object> defaultYAML = new LinkedHashMap<>();
		defaultYAML.put("interval", 10 * 20);
		defaultYAML.put("messages", new java.util.ArrayList<String>());
		settings = new Config(getDataFolder() + File.separator + "messages.yml", Config.YAML, defaultYAML);
		
		if (! checkConfig()) {
			try {
				getLogger().info("Broken YAML, Regenerating");
				java.lang.reflect.Field file = Config.class.getDeclaredField("file");
				File file_real = (File) file.get(settings);
				file_real.delete();
				settings = new Config(getDataFolder() + File.separator + "messages.yml", Config.YAML, defaultYAML);
				
				if (! checkConfig()) {
					getLogger().info("Fatal exception: not compatible with this Nukkit");
					getServer().getPluginManager().disablePlugin(this);
				}
			} catch (Exception e) {
				getLogger().info("Fatal exception: not compatible with this Nukkit");
				getServer().getPluginManager().disablePlugin(this);
			}
		}
		
		interval = settings.getInt("interval");
		saveHandler = getServer().getScheduler().scheduleRepeatingTask(new com.rmpi.nukkit.spa.task.ConfigSaveTask(this), 10);
		announceHandler = getServer().getScheduler().scheduleRepeatingTask(new AnnounceTask(getServer(), messages = new java.util.LinkedList<>(settings.getList("messages"))), interval);
	}
	
	@Override
	public void onDisable() {
		if (announceHandler != null && ! announceHandler.isCancelled()) announceHandler.cancel();
		if (saveHandler != null && ! saveHandler.isCancelled()) announceHandler.cancel();
		getLogger().info("SimpleAnnouncer Disabled");
	}
	
	@Override
	public boolean onCommand(cn.nukkit.command.CommandSender sender, cn.nukkit.command.Command command, String label, String[] args) {
		if (! sender.hasPermission(command.getPermission())) return false;
		
		if (announceHandler == null) {
			sender.sendMessage("Fatal exception: handler null");
			return true;
		}
		
		switch (command.getName()) {
			case "tadd":
				if (args.length == 0) {
					sender.sendMessage(command.getUsage());
					return true;
				}
				
				messages.add(concat(args));
				sender.sendMessage("Successfully added");
				break;
			case "taddi":
				if (args.length < 2) {
					sender.sendMessage(command.getUsage());
					return true;
				}
				
				int index_0;
				
				try {
					index_0 = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					sender.sendMessage(command.getUsage());
					return true;
				}
				
				if (index_0 >= messages.size() || index_0 < 0) {
					sender.sendMessage(command.getUsage());
					return true;
				}
				
				args[0] = "";
				messages.add(index_0, concat(args));
				sender.sendMessage("Successfully added");
				break;
			case "tdel":
				if (args.length > 1) {
					sender.sendMessage(command.getUsage());
					return true;
				}
				
				if (messages.isEmpty()) {
					sender.sendMessage("Nothing in the broadcast list");
					return true;
				}
				
				int index_1;
				
				if (args.length == 0) {
					index_1 = messages.size() - 1;
				} else /* if (args.length == 1) */ {
					try {
						index_1 = Integer.parseInt(args[0]);
					} catch (NumberFormatException e) {
						sender.sendMessage(command.getUsage());
						return true;
					}
					
					if (index_1 >= messages.size() || index_1 < 0) {
						sender.sendMessage(command.getUsage());
						return true;
					}
				}
				
				messages.remove(index_1);
				sender.sendMessage("Successfully deleted");
				break;
			case "ton":
				if (announceHandler.isCancelled()) announceHandler = getServer().getScheduler().scheduleRepeatingTask(new AnnounceTask(getServer(), messages), interval);
				else {
					sender.sendMessage("Task is already running");
					return true;
				}
				
				sender.sendMessage("Successfully toggled on");
				break;
			case "toff":
				if (! announceHandler.isCancelled()) announceHandler.cancel();
				else {
					sender.sendMessage("Task is not running");
					return true;
				}
				
				sender.sendMessage("Successfully toggled off");
				break;
			case "tdump":
				sender.sendMessage(dumpTask());
				break;
		}
		
		return true;
	}
	
	private String concat(String[] args) {
		StringBuilder builder = new StringBuilder();
		
		for (String string : args) {
			builder.append(string);
			builder.append(' ');
		}
		
		if (builder.charAt(builder.length() - 1) == ' ') builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
	
	private String dumpTask() {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < messages.size(); i++) {
			builder.append('[');
			builder.append(i);
			builder.append("] ");
			builder.append(messages.get(i));
			builder.append('\n');
		}
		
		if (builder.charAt(builder.length() - 1) == '\n') builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
	
	private boolean checkConfig() {
		if (! settings.isCorrect()) return false;	
		if (! settings.isInt("interval") || ! settings.isList("messages")) return false;
		return true;
	}
}
