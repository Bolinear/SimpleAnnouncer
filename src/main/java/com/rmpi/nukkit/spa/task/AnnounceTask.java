package com.rmpi.nukkit.spa.task;

import cn.nukkit.Server;

import java.util.List;

public class AnnounceTask extends cn.nukkit.scheduler.Task {
	public Server server;
	public List<String> messages;
	private int runCounter = 0;
	
	public AnnounceTask(Server server, List<String> messages) {
		this.server = server;
		this.messages = messages;
	}
	
	@Override
	public void onRun(int currentTick) {
		if (messages.isEmpty()) return;
		if (runCounter >= messages.size()) runCounter = 0;
		
		try {
			for (cn.nukkit.Player player : server.getOnlinePlayers().values()) player.sendMessage(messages.get(runCounter));
		} catch (IndexOutOfBoundsException e) {
			runCounter = 0;
			onRun(currentTick);
		}
		
		runCounter++;
	}
}