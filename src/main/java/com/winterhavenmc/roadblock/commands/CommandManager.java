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
import com.winterhavenmc.roadblock.messages.MessageId;
import com.winterhavenmc.roadblock.sounds.SoundId;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import javax.annotation.Nonnull;
import java.util.*;


/**
 * A class that handles the execution of commands
 */
public final class CommandManager implements CommandExecutor, TabCompleter {

	// reference to main class instance
	private final PluginMain plugin;

	// instantiate subcommand map
	private final SubcommandRegistry subcommandRegistry = new SubcommandRegistry();


	/**
	 * Class constructor
	 *
	 * @param plugin reference to main class
	 */
	public CommandManager(final PluginMain plugin) {

		// reference to main class
		this.plugin = plugin;

		// register this class as command executor
		Objects.requireNonNull(plugin.getCommand("roadblock")).setExecutor(this);

		// register subcommands
		for (SubcommandType subcommandType : SubcommandType.values()) {
			subcommandRegistry.register(subcommandType.create(plugin));
		}

		// register help command
		subcommandRegistry.register(new HelpCommand(plugin, subcommandRegistry));
	}


	@Override
	public List<String> onTabComplete(final @Nonnull CommandSender sender,
									  final @Nonnull Command command,
									  final @Nonnull String alias,
									  final String[] args) {

		// if more than one argument, use tab completer of subcommand
		if (args.length > 1) {

			// get subcommand from map
			Subcommand subcommand = subcommandRegistry.getCommand(args[0]);

			// if no subcommand returned from map, return empty list
			if (subcommand == null) {
				return Collections.emptyList();
			}

			// return subcommand tab completer output
			return subcommand.onTabComplete(sender, command, alias, args);
		}

		// return list of subcommands for which sender has permission
		return matchingCommands(sender, args[0]);

	}


	@Override
	public boolean onCommand(final @Nonnull CommandSender sender,
	                         final @Nonnull Command command,
	                         final @Nonnull String label,
	                         final String[] args) {

		// convert args array to list
		List<String> argsList = new ArrayList<>(Arrays.asList(args));

		String subcommandName;

		// get subcommand, remove from front of list
		if (argsList.size() > 0) {
			subcommandName = argsList.remove(0);
		}

		// if no arguments, set command to help
		else {
			subcommandName = "help";
		}

		// get subcommand from map by name
		Subcommand subcommand = subcommandRegistry.getCommand(subcommandName);

		// if subcommand is null, get help command from map
		if (subcommand == null) {
			subcommand = subcommandRegistry.getCommand("help");
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_INVALID_COMMAND).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_INVALID);
		}

		// execute subcommand
		return subcommand.onCommand(sender, argsList);
	}


	/**
	 * Get matching list of subcommands for which sender has permission
	 * @param sender the command sender
	 * @param matchString the string prefix to match against command names
	 * @return List of String - command names that match prefix and sender has permission
	 */
	private List<String> matchingCommands(final CommandSender sender, final String matchString) {

		List<String> returnList = new ArrayList<>();

		for (String subcommand : subcommandRegistry.getKeys()) {
			if (sender.hasPermission("roadblock." + subcommand)
					&& subcommand.startsWith(matchString.toLowerCase())) {
				returnList.add(subcommand);
			}
		}
		return returnList;
	}

}
