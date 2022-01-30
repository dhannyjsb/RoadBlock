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

import com.winterhavenmc.roadblock.messages.MessageId;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;


abstract class AbstractSubcommand implements Subcommand {

	private String name;
	private Collection<String> aliases = new LinkedHashSet<>();
	private String usageString;
	private MessageId description;
	private int minArgs;
	private int maxArgs;


	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public Collection<String> getAliases() {
		return aliases;
	}

	@Override
	public void setAliases(final Collection<String> aliases) {
		this.aliases = aliases;
	}

	@Override
	public void addAlias(final String alias) {
		this.aliases.add(alias);
	}

	@Override
	public String getUsage() {
		return usageString;
	}

	@Override
	public void displayUsage(final CommandSender sender) {
		sender.sendMessage(usageString);
	}

	@Override
	public void setUsage(final String usageString) {
		this.usageString = usageString;
	}

	@Override
	public MessageId getDescription() {
		return description;
	}

	@Override
	public void setDescription(final MessageId description) {
		this.description = description;
	}

	@Override
	public int getMinArgs() { return minArgs; }

	@Override
	public void setMinArgs(final int minArgs) {
		this.minArgs = minArgs;
	}

	@Override
	public int getMaxArgs() { return maxArgs; }

	@Override
	public void setMaxArgs(final int maxArgs) {
		this.maxArgs = maxArgs;
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
									  final String alias, final String[] args) {

		return Collections.emptyList();
	}

}
