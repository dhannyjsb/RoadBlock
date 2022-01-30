/*
 * Copyright (c) 2022 Tim Savage.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.winterhavenmc.roadblock.commands;

import com.winterhavenmc.roadblock.PluginMain;
import com.winterhavenmc.roadblock.sounds.SoundId;
import com.winterhavenmc.roadblock.messages.MessageId;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.*;


/**
 * A class that implements the materials subcommand
 */
final class MaterialsCommand extends AbstractSubcommand {

	// reference to the plugin main class
	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to the plugin main class
	 */
	MaterialsCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("materials");
		this.setUsage("/roadblock materials");
		this.setDescription(MessageId.COMMAND_HELP_MATERIALS);
		this.setMaxArgs(0);
	}

	@Override
	public boolean onCommand(final CommandSender sender, final List<String> argsList) {

		// check that sender has permission for status command
		if (!sender.hasPermission("roadblock.materials")) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_MATERIALS_PERMISSION).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check max arguments
		if (argsList.size() > getMaxArgs()) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		List<Material> materialsSorted = new ArrayList<>(plugin.blockManager.getRoadBlockMaterials());

		materialsSorted.sort(Comparator.comparing(Enum::toString));

		sender.sendMessage(ChatColor.GREEN + "Configured materials: "
				+ ChatColor.RESET + materialsSorted);

		return true;
	}
}
