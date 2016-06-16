package com.rmpi.nukkit.spa.task;

import com.rmpi.nukkit.spa.SimpleAnnouncer;

public class ConfigSaveTask extends cn.nukkit.scheduler.PluginTask<SimpleAnnouncer> {
	public ConfigSaveTask(SimpleAnnouncer plugin) {
		super(plugin);
	}

	@Override
	public void onRun(int currentTick) {
		if (getOwner().settings != null) {
			getOwner().settings.set("messages", getOwner().messages);
			getOwner().settings.save();
		}
	}
}
