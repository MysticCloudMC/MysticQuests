/*******************************************************************************************************
 * Continued by PikaMug (formerly HappyPikachu) with permission from _Blackvein_. All rights reserved.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package me.blackvein.quests.convo.quests.rewards;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.inventory.ItemStack;

import me.blackvein.quests.CustomReward;
import me.blackvein.quests.Quests;
import me.blackvein.quests.convo.generic.ItemStackPrompt;
import me.blackvein.quests.convo.generic.OverridePrompt;
import me.blackvein.quests.convo.quests.QuestsEditorNumericPrompt;
import me.blackvein.quests.convo.quests.QuestsEditorStringPrompt;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenNumericPromptEvent;
import me.blackvein.quests.events.editor.quests.QuestsEditorPostOpenStringPromptEvent;
import me.blackvein.quests.util.CK;
import me.blackvein.quests.util.ItemUtil;
import me.blackvein.quests.util.Lang;
import me.blackvein.quests.util.MiscUtil;

public class RewardsPrompt extends QuestsEditorNumericPrompt {

	private final Quests plugin;
	private final String classPrefix;
	private boolean hasReward = false;
	private final int size = 12;

	public RewardsPrompt(final ConversationContext context) {
		super(context);
		this.plugin = (Quests) context.getPlugin();
		this.classPrefix = getClass().getSimpleName();
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public String getTitle(final ConversationContext context) {
		return Lang.get("rewardsTitle").replace("<quest>", (String) context.getSessionData(CK.Q_NAME));
	}

	@Override
	public ChatColor getNumberColor(final ConversationContext context, final int number) {
		switch (number) {
		case 1:
			if (plugin.getDependencies().getVaultEconomy() != null) {
				return ChatColor.BLUE;
			} else {
				return ChatColor.GRAY;
			}
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			return ChatColor.BLUE;
		case 7:
			return ChatColor.GRAY;
		case 8:
			return ChatColor.GRAY;
		case 9:
			return ChatColor.GRAY;
		case 10:
			return ChatColor.BLUE;
		case 11:
			if (context.getSessionData(CK.REW_DETAILS_OVERRIDE) == null) {
				if (!hasReward) {
					return ChatColor.GRAY;
				} else {
					return ChatColor.BLUE;
				}
			} else {
				return ChatColor.BLUE;
			}
		case 12:
			return ChatColor.GREEN;
		default:
			return null;
		}
	}

	@Override
	public String getSelectionText(final ConversationContext context, final int number) {
		switch (number) {
		case 1:
			if (plugin.getDependencies().getVaultEconomy() != null) {
				return ChatColor.YELLOW + Lang.get("rewSetMoney");
			} else {
				return ChatColor.GRAY + Lang.get("rewSetMoney");
			}
		case 2:
			return ChatColor.YELLOW + Lang.get("rewSetQuestPoints").replace("<points>", Lang.get("questPoints"));
		case 3:
			return ChatColor.YELLOW + Lang.get("rewSetItems");
		case 4:
			return ChatColor.YELLOW + Lang.get("rewSetExperience");
		case 5:
			return ChatColor.YELLOW + Lang.get("rewSetCommands");
		case 6:
			return ChatColor.YELLOW + Lang.get("rewSetPermission");
		case 7:
			return ChatColor.GRAY + Lang.get("rewSetMcMMO");
		case 8:
			return ChatColor.GRAY + Lang.get("rewSetHeroes");
		case 9:
			return ChatColor.GRAY + Lang.get("rewSetPhat");
		case 10:
			return ChatColor.DARK_PURPLE + Lang.get("rewSetCustom");
		case 11:
			if (!hasReward) {
				return ChatColor.GRAY + Lang.get("overrideCreateSet");
			} else {
				return ChatColor.YELLOW + Lang.get("overrideCreateSet");
			}
		case 12:
			return ChatColor.YELLOW + Lang.get("done");
		default:
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getAdditionalText(final ConversationContext context, final int number) {
		switch (number) {
		case 1:
			if (plugin.getDependencies().getVaultEconomy() != null) {
				if (context.getSessionData(CK.REW_MONEY) == null) {
					return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
				} else {
					final int moneyRew = (Integer) context.getSessionData(CK.REW_MONEY);
					return ChatColor.GRAY + "(" + ChatColor.AQUA + moneyRew + " "
							+ (moneyRew > 1 ? plugin.getDependencies().getCurrency(true)
									: plugin.getDependencies().getCurrency(false))
							+ ChatColor.GRAY + ")";
				}
			} else {
				return ChatColor.GRAY + "(" + Lang.get("notInstalled") + ")";
			}
		case 2:
			if (context.getSessionData(CK.REW_QUEST_POINTS) == null) {
				return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
			} else {
				return ChatColor.GRAY + "(" + ChatColor.AQUA + context.getSessionData(CK.REW_QUEST_POINTS) + " "
						+ Lang.get("questPoints") + ChatColor.GRAY + ")";
			}
		case 3:
			if (context.getSessionData(CK.REW_ITEMS) == null) {
				return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
			} else {
				String text = "\n";
				final LinkedList<ItemStack> items = (LinkedList<ItemStack>) context.getSessionData(CK.REW_ITEMS);
				for (int i = 0; i < items.size(); i++) {
					text += ChatColor.GRAY + "     - " + ChatColor.BLUE + ItemUtil.getName(items.get(i))
							+ ChatColor.GRAY + " x " + ChatColor.AQUA + items.get(i).getAmount() + "\n";
				}
				return text;
			}
		case 4:
			if (context.getSessionData(CK.REW_EXP) == null) {
				return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
			} else {
				return ChatColor.GRAY + "(" + ChatColor.AQUA + context.getSessionData(CK.REW_EXP) + " "
						+ Lang.get("points") + ChatColor.DARK_GRAY + ")";
			}
		case 5:
			if (context.getSessionData(CK.REW_COMMAND) == null) {
				return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
			} else {
				String text = "\n";
				final List<String> commands = (List<String>) context.getSessionData(CK.REW_COMMAND);
				final List<String> overrides = (List<String>) context.getSessionData(CK.REW_COMMAND_OVERRIDE_DISPLAY);
				int index = 0;
				for (final String cmd : commands) {
					text += ChatColor.GRAY + "     - " + ChatColor.AQUA + cmd;
					if (overrides != null) {
						if (index < overrides.size()) {
							text += ChatColor.GRAY + " (\"" + ChatColor.AQUA + overrides.get(index) + ChatColor.GRAY
									+ "\")";
						}
					}
					text += "\n";
					index++;
				}
				return text;
			}
		case 6:
			if (context.getSessionData(CK.REW_PERMISSION) == null) {
				return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
			} else {
				String text = "\n";
				final List<String> permissions = (List<String>) context.getSessionData(CK.REW_PERMISSION);
				final List<String> worlds = (List<String>) context.getSessionData(CK.REW_PERMISSION_WORLDS);
				int index = 0;
				for (final String perm : permissions) {
					text += ChatColor.GRAY + "     - " + ChatColor.AQUA + perm;
					if (worlds != null) {
						if (index < worlds.size()) {
							text += ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + worlds.get(index) + ChatColor.GRAY
									+ "]";
						}
					}
					text += "\n";
					index++;
				}
				return text;
			}
		case 7:
			return ChatColor.GRAY + "(" + Lang.get("notInstalled") + ")";
		case 8:
			return ChatColor.GRAY + "(" + Lang.get("notInstalled") + ")";
		case 9:
			return ChatColor.GRAY + "(" + Lang.get("notInstalled") + ")";
		case 10:
			if (context.getSessionData(CK.REW_CUSTOM) == null) {
				return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
			} else {
				String text = "\n";
				final LinkedList<String> customRews = (LinkedList<String>) context.getSessionData(CK.REW_CUSTOM);
				for (final String s : customRews) {
					text += ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "  - " + ChatColor.LIGHT_PURPLE + s + "\n";
				}
				return text;
			}
		case 11:
			if (context.getSessionData(CK.REW_DETAILS_OVERRIDE) == null) {
				if (!hasReward) {
					return ChatColor.GRAY + "(" + Lang.get("stageEditorOptional") + ")";
				} else {
					return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
				}
			} else {
				String text = "\n";
				final LinkedList<String> overrides = new LinkedList<String>();
				overrides.addAll((List<String>) context.getSessionData(CK.REW_DETAILS_OVERRIDE));
				for (int i = 0; i < overrides.size(); i++) {
					text += ChatColor.GRAY + "     - " + ChatColor.AQUA + overrides.get(i) + "\n";
				}
				return text;
			}
		case 12:
			return "";
		default:
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getPromptText(final ConversationContext context) {
		final String input = (String) context.getSessionData(classPrefix + "-override");
		if (input != null && !input.equalsIgnoreCase(Lang.get("cancel"))) {
			if (input.equalsIgnoreCase(Lang.get("clear"))) {
				context.setSessionData(CK.REW_DETAILS_OVERRIDE, null);
			} else {
				final LinkedList<String> overrides = new LinkedList<String>();
				if (context.getSessionData(CK.REW_DETAILS_OVERRIDE) != null) {
					overrides.addAll((List<String>) context.getSessionData(CK.REW_DETAILS_OVERRIDE));
				}
				overrides.add(input);
				context.setSessionData(CK.REW_DETAILS_OVERRIDE, overrides);
				context.setSessionData(classPrefix + "-override", null);
			}
		}
		checkReward(context);

		final QuestsEditorPostOpenNumericPromptEvent event = new QuestsEditorPostOpenNumericPromptEvent(context, this);
		context.getPlugin().getServer().getPluginManager().callEvent(event);

		String text = ChatColor.LIGHT_PURPLE + getTitle(context).replace((String) context.getSessionData(CK.Q_NAME),
				ChatColor.AQUA + (String) context.getSessionData(CK.Q_NAME) + ChatColor.LIGHT_PURPLE) + "\n";
		for (int i = 1; i <= size; i++) {
			text += getNumberColor(context, i) + "" + ChatColor.BOLD + i + ChatColor.RESET + " - "
					+ getSelectionText(context, i) + " " + getAdditionalText(context, i) + "\n";
		}
		return text;
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext context, final Number input) {
		switch (input.intValue()) {
		case 1:
			if (plugin.getDependencies().getVaultEconomy() != null) {
				return new RewardsMoneyPrompt(context);
			} else {
				return new RewardsPrompt(context);
			}
		case 2:
			return new RewardsQuestPointsPrompt(context);
		case 3:
			return new RewardsItemListPrompt(context);
		case 4:
			return new RewardsExperiencePrompt(context);
		case 5:
			return new RewardsCommandsPrompt(context);
		case 6:
			return new RewardsPermissionsListPrompt(context);
		case 7:
			return new RewardsPrompt(context);
		case 8:
			return new RewardsPrompt(context);
		case 9:
			return new RewardsPrompt(context);
		case 10:
			return new CustomRewardsPrompt(context);
		case 11:
			if (hasReward) {
				return new OverridePrompt.Builder().source(this).promptText(Lang.get("overrideCreateEnter")).build();
			} else {
				context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("invalidOption"));
				return new RewardsPrompt(context);
			}
		case 12:
			return plugin.getQuestFactory().returnToMenu(context);
		default:
			return new RewardsPrompt(context);
		}
	}

	public boolean checkReward(final ConversationContext context) {
		if (context.getSessionData(CK.REW_MONEY) != null || context.getSessionData(CK.REW_EXP) != null
				|| context.getSessionData(CK.REW_QUEST_POINTS) != null || context.getSessionData(CK.REW_ITEMS) != null
				|| context.getSessionData(CK.REW_COMMAND) != null || context.getSessionData(CK.REW_PERMISSION) != null
				|| context.getSessionData(CK.REW_MCMMO_SKILLS) != null
				|| context.getSessionData(CK.REW_HEROES_CLASSES) != null
				|| context.getSessionData(CK.REW_PHAT_LOOTS) != null || context.getSessionData(CK.REW_CUSTOM) != null) {
			hasReward = true;
			return true;
		}
		return false;
	}

	public class RewardsMoneyPrompt extends QuestsEditorStringPrompt {

		public RewardsMoneyPrompt(final ConversationContext context) {
			super(context);
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return null;
		}

		@Override
		public String getQueryText(final ConversationContext context) {
			return Lang.get("rewMoneyPrompt");
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenStringPromptEvent event = new QuestsEditorPostOpenStringPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			String text = getQueryText(context);
			if (plugin.getDependencies().getVaultEconomy() != null) {
				text = text.replace("<money>", ChatColor.AQUA
						+ (plugin.getDependencies().getVaultEconomy().currencyNamePlural().isEmpty() ? Lang.get("money")
								: plugin.getDependencies().getVaultEconomy().currencyNamePlural())
						+ ChatColor.YELLOW);
			} else {
				text = text.replace("<money>", ChatColor.AQUA + Lang.get("money") + ChatColor.YELLOW);
			}
			return ChatColor.YELLOW + text;
		}

		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
					&& input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {
				try {
					final int i = Integer.parseInt(input);
					if (i > 0) {
						context.setSessionData(CK.REW_MONEY, i);
					} else {
						context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("inputPosNum"));
						return new RewardsMoneyPrompt(context);
					}
				} catch (final NumberFormatException e) {
					context.getForWhom()
							.sendRawMessage(ChatColor.RED + Lang.get("reqNotANumber").replace("<input>", input));
					return new RewardsMoneyPrompt(context);
				}
			} else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
				context.setSessionData(CK.REW_MONEY, null);
				return new RewardsPrompt(context);
			}
			return new RewardsPrompt(context);
		}
	}

	public class RewardsExperiencePrompt extends QuestsEditorStringPrompt {

		public RewardsExperiencePrompt(final ConversationContext context) {
			super(context);
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return null;
		}

		@Override
		public String getQueryText(final ConversationContext context) {
			return Lang.get("rewExperiencePrompt");
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenStringPromptEvent event = new QuestsEditorPostOpenStringPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			return ChatColor.YELLOW + getQueryText(context);
		}

		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
					&& input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {
				try {
					final int i = Integer.parseInt(input);
					if (i > 0) {
						context.setSessionData(CK.REW_EXP, i);
					} else {
						context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("inputPosNum"));
						return new RewardsExperiencePrompt(context);
					}
				} catch (final NumberFormatException e) {
					context.getForWhom()
							.sendRawMessage(ChatColor.RED + Lang.get("reqNotANumber").replace("<input>", input));
					return new RewardsExperiencePrompt(context);
				}
			} else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
				context.setSessionData(CK.REW_EXP, null);
				return new RewardsPrompt(context);
			}
			return new RewardsPrompt(context);
		}
	}

	public class RewardsQuestPointsPrompt extends QuestsEditorStringPrompt {

		public RewardsQuestPointsPrompt(final ConversationContext context) {
			super(context);
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return null;
		}

		@Override
		public String getQueryText(final ConversationContext context) {
			return Lang.get("rewQuestPointsPrompt").replace("<points>", Lang.get("questPoints"));
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenStringPromptEvent event = new QuestsEditorPostOpenStringPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			return ChatColor.YELLOW + getQueryText(context);
		}

		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
					&& input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {
				try {
					final int i = Integer.parseInt(input);
					if (i > 0) {
						context.setSessionData(CK.REW_QUEST_POINTS, i);
					} else {
						context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("inputPosNum"));
						return new RewardsQuestPointsPrompt(context);
					}
				} catch (final NumberFormatException e) {
					context.getForWhom()
							.sendRawMessage(ChatColor.RED + Lang.get("reqNotANumber").replace("<input>", input));
					return new RewardsQuestPointsPrompt(context);
				}
			} else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
				context.setSessionData(CK.REW_QUEST_POINTS, null);
				return new RewardsPrompt(context);
			}
			return new RewardsPrompt(context);
		}
	}

	public class RewardsItemListPrompt extends QuestsEditorNumericPrompt {

		public RewardsItemListPrompt(final ConversationContext context) {
			super(context);
		}

		private final int size = 3;

		@Override
		public int getSize() {
			return size;
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return Lang.get("itemRewardsTitle");
		}

		@Override
		public ChatColor getNumberColor(final ConversationContext context, final int number) {
			switch (number) {
			case 1:
				return ChatColor.BLUE;
			case 2:
				return ChatColor.RED;
			case 3:
				return ChatColor.GREEN;
			default:
				return null;
			}
		}

		@Override
		public String getSelectionText(final ConversationContext context, final int number) {
			switch (number) {
			case 1:
				return ChatColor.YELLOW + Lang.get("stageEditorDeliveryAddItem");
			case 2:
				return ChatColor.RED + Lang.get("clear");
			case 3:
				return ChatColor.GREEN + Lang.get("done");
			default:
				return null;
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public String getAdditionalText(final ConversationContext context, final int number) {
			switch (number) {
			case 1:
				if (context.getSessionData(CK.REW_ITEMS) == null) {
					return ChatColor.GRAY + " (" + Lang.get("noneSet") + ")";
				} else {
					String text = "\n";
					for (final ItemStack is : (List<ItemStack>) context.getSessionData(CK.REW_ITEMS)) {
						text += ChatColor.GRAY + "- " + ItemUtil.getDisplayString(is) + "\n";
					}
					return text;
				}
			case 2:
			case 3:
				return "";
			default:
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public String getPromptText(final ConversationContext context) {
			// Check/add newly made item
			if (context.getSessionData("newItem") != null) {
				if (context.getSessionData(CK.REW_ITEMS) != null) {
					final List<ItemStack> itemRews = (List<ItemStack>) context.getSessionData(CK.REW_ITEMS);
					itemRews.add((ItemStack) context.getSessionData("tempStack"));
					context.setSessionData(CK.REW_ITEMS, itemRews);
				} else {
					final List<ItemStack> itemRews = new LinkedList<ItemStack>();
					itemRews.add((ItemStack) context.getSessionData("tempStack"));
					context.setSessionData(CK.REW_ITEMS, itemRews);
				}
				context.setSessionData("newItem", null);
				context.setSessionData("tempStack", null);
			}

			final QuestsEditorPostOpenNumericPromptEvent event = new QuestsEditorPostOpenNumericPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			String text = ChatColor.AQUA + "- " + getTitle(context) + " -\n";
			for (int i = 1; i <= size; i++) {
				text += getNumberColor(context, i) + "" + ChatColor.BOLD + i + ChatColor.RESET + " - "
						+ getSelectionText(context, i) + " " + getAdditionalText(context, i) + "\n";
			}
			return text;
		}

		@Override
		protected Prompt acceptValidatedInput(final ConversationContext context, final Number input) {
			switch (input.intValue()) {
			case 1:
				return new ItemStackPrompt(context, RewardsItemListPrompt.this);
			case 2:
				context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("rewItemsCleared"));
				context.setSessionData(CK.REW_ITEMS, null);
				return new RewardsItemListPrompt(context);
			case 3:
				return new RewardsPrompt(context);
			default:
				return new RewardsItemListPrompt(context);
			}
		}
	}

	public class RewardsCommandsPrompt extends QuestsEditorStringPrompt {

		public RewardsCommandsPrompt(final ConversationContext context) {
			super(context);
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return null;
		}

		@Override
		public String getQueryText(final ConversationContext context) {
			return Lang.get("rewCommandPrompt");
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenStringPromptEvent event = new QuestsEditorPostOpenStringPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			return ChatColor.YELLOW + getQueryText(context);
		}

		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
					&& input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {
				final String[] args = input.split(Lang.get("charSemi"));
				final List<String> commands = new LinkedList<String>();
				for (String s : args) {
					if (s.startsWith("/")) {
						s = s.substring(1);
					}
					commands.add(s);
				}
				context.setSessionData(CK.REW_COMMAND, commands.isEmpty() ? null : commands);
			} else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
				context.setSessionData(CK.REW_COMMAND, null);
			}
			return new RewardsPrompt(context);
		}
	}

	public class RewardsPermissionsListPrompt extends QuestsEditorNumericPrompt {

		public RewardsPermissionsListPrompt(final ConversationContext context) {
			super(context);
		}

		private final int size = 4;

		@Override
		public int getSize() {
			return size;
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return Lang.get("permissionRewardsTitle");
		}

		@Override
		public ChatColor getNumberColor(final ConversationContext context, final int number) {
			switch (number) {
			case 1:
				return ChatColor.BLUE;
			case 2:
				if (context.getSessionData(CK.REW_PERMISSION) == null) {
					return ChatColor.GRAY;
				} else {
					return ChatColor.BLUE;
				}
			case 3:
				return ChatColor.RED;
			case 4:
				return ChatColor.GREEN;
			default:
				return null;
			}
		}

		@Override
		public String getSelectionText(final ConversationContext context, final int number) {
			switch (number) {
			case 1:
				return ChatColor.YELLOW + Lang.get("rewSetPermission");
			case 2:
				if (context.getSessionData(CK.REW_PERMISSION) == null) {
					return ChatColor.GRAY + Lang.get("rewSetPermissionWorlds");
				} else {
					return ChatColor.YELLOW + Lang.get("rewSetPermissionWorlds");
				}
			case 3:
				return ChatColor.RED + Lang.get("clear");
			case 4:
				return ChatColor.GREEN + Lang.get("done");
			default:
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public String getAdditionalText(final ConversationContext context, final int number) {
			switch (number) {
			case 1:
				if (context.getSessionData(CK.REW_PERMISSION) == null) {
					return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
				} else {
					String text = "\n";
					for (final String s : (List<String>) context.getSessionData(CK.REW_PERMISSION)) {
						text += ChatColor.GRAY + "     - " + ChatColor.AQUA + s + "\n";
					}
					return text;
				}
			case 2:
				if (context.getSessionData(CK.REW_PERMISSION) == null) {
					return ChatColor.GRAY + "(" + Lang.get("noneSet") + ")";
				} else {
					if (context.getSessionData(CK.REW_PERMISSION_WORLDS) == null) {
						return ChatColor.YELLOW + "(" + Lang.get("stageEditorOptional") + ")";
					} else {
						String text = "\n";
						for (final String s : (List<String>) context.getSessionData(CK.REW_PERMISSION_WORLDS)) {
							text += ChatColor.GRAY + "     - " + ChatColor.AQUA + s + "\n";
						}
						return text;
					}
				}
			case 3:
			case 4:
				return "";
			default:
				return null;
			}
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenNumericPromptEvent event = new QuestsEditorPostOpenNumericPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			String text = ChatColor.GOLD + getTitle(context) + "\n";
			for (int i = 1; i <= size; i++) {
				text += getNumberColor(context, i) + "" + ChatColor.BOLD + i + ChatColor.RESET + " - "
						+ getSelectionText(context, i) + " " + getAdditionalText(context, i) + "\n";
			}
			return text;
		}

		@Override
		protected Prompt acceptValidatedInput(final ConversationContext context, final Number input) {
			switch (input.intValue()) {
			case 1:
				return new PermissionsPrompt(context);
			case 2:
				return new PermissionsWorldsPrompt(context);
			case 3:
				context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("rewPermissionsCleared"));
				context.setSessionData(CK.REW_PERMISSION, null);
				context.setSessionData(CK.REW_PERMISSION_WORLDS, null);
				return new RewardsPermissionsListPrompt(context);
			case 4:
				return new RewardsPrompt(context);
			default:
				return null;
			}
		}

	}

	public class PermissionsPrompt extends QuestsEditorStringPrompt {

		public PermissionsPrompt(final ConversationContext context) {
			super(context);
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return null;
		}

		@Override
		public String getQueryText(final ConversationContext context) {
			return Lang.get("rewPermissionsPrompt");
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenStringPromptEvent event = new QuestsEditorPostOpenStringPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			return ChatColor.YELLOW + getQueryText(context);
		}

		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
					&& input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {
				final String[] args = input.split(" ");
				final List<String> permissions = new LinkedList<String>();
				permissions.addAll(Arrays.asList(args));
				context.setSessionData(CK.REW_PERMISSION, permissions);
			} else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
				context.setSessionData(CK.REW_PERMISSION, null);
			}
			return new RewardsPermissionsListPrompt(context);
		}
	}

	public class PermissionsWorldsPrompt extends QuestsEditorStringPrompt {

		public PermissionsWorldsPrompt(final ConversationContext context) {
			super(context);
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return null;
		}

		@Override
		public String getQueryText(final ConversationContext context) {
			return Lang.get("rewPermissionsWorldPrompt");
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenStringPromptEvent event = new QuestsEditorPostOpenStringPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			return ChatColor.YELLOW + getQueryText(context);
		}

		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
					&& input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {
				final String[] args = input.split(Lang.get("charSemi"));
				final List<String> worlds = new LinkedList<String>();
				worlds.addAll(Arrays.asList(args));
				for (final String w : worlds) {
					if (!w.equals("null") && w != null && context.getPlugin().getServer().getWorld(w) == null) {
						context.getForWhom()
								.sendRawMessage(ChatColor.RED + w + " " + Lang.get("eventEditorInvalidWorld"));
						return new PermissionsWorldsPrompt(context);
					}
				}
				context.setSessionData(CK.REW_PERMISSION_WORLDS, worlds);
			} else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
				context.setSessionData(CK.REW_PERMISSION_WORLDS, null);
			}
			return new RewardsPermissionsListPrompt(context);
		}
	}

	public class RewardsMcMMOListPrompt extends QuestsEditorNumericPrompt {

		public RewardsMcMMOListPrompt(final ConversationContext context) {
			super(context);
		}

		private final int size = 4;

		@Override
		public int getSize() {
			return size;
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return Lang.get("mcMMORewardsTitle");
		}

		@Override
		public ChatColor getNumberColor(final ConversationContext context, final int number) {
			switch (number) {
			case 1:
			case 2:
				return ChatColor.BLUE;
			case 3:
				return ChatColor.RED;
			case 4:
				return ChatColor.GREEN;
			default:
				return null;
			}
		}

		@Override
		public String getSelectionText(final ConversationContext context, final int number) {
			switch (number) {
			case 1:
				return ChatColor.YELLOW + Lang.get("reqSetSkills");
			case 2:
				return ChatColor.YELLOW + Lang.get("reqSetSkillAmounts");
			case 3:
				return ChatColor.RED + Lang.get("clear");
			case 4:
				return ChatColor.GREEN + Lang.get("done");
			default:
				return null;
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public String getAdditionalText(final ConversationContext context, final int number) {
			switch (number) {
			case 1:
				if (context.getSessionData(CK.REW_MCMMO_SKILLS) == null) {
					return ChatColor.GRAY + " (" + Lang.get("noneSet") + ")";
				} else {
					String text = "\n";
					for (final String s : (List<String>) context.getSessionData(CK.REW_MCMMO_SKILLS)) {
						text += ChatColor.GRAY + "     - " + ChatColor.AQUA + s + "\n";
					}
					return text;
				}
			case 2:
				if (context.getSessionData(CK.REW_MCMMO_AMOUNTS) == null) {
					return ChatColor.GRAY + " (" + Lang.get("noneSet") + ")";
				} else {
					String text = "\n";
					for (final Integer i : (List<Integer>) context.getSessionData(CK.REW_MCMMO_AMOUNTS)) {
						text += ChatColor.GRAY + "     - " + ChatColor.AQUA + i + "\n";
					}
					return text;
				}
			case 3:
				return "";
			default:
				return null;
			}
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenNumericPromptEvent event = new QuestsEditorPostOpenNumericPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			String text = ChatColor.AQUA + "- " + getTitle(context) + " -\n";
			for (int i = 1; i <= size; i++) {
				text += getNumberColor(context, i) + "" + ChatColor.BOLD + i + ChatColor.RESET + " - "
						+ getSelectionText(context, i) + " " + getAdditionalText(context, i) + "\n";
			}
			return text;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Prompt acceptValidatedInput(final ConversationContext context, final Number input) {
			switch (input.intValue()) {
			case 1:
				return new McMMOSkillsPrompt(context);
			case 2:
				if (context.getSessionData(CK.REW_MCMMO_SKILLS) == null) {
					context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("rewSetMcMMOSkillsFirst"));
					return new RewardsMcMMOListPrompt(context);
				} else {
					return new McMMOAmountsPrompt(context);
				}
			case 3:
				context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("rewMcMMOCleared"));
				context.setSessionData(CK.REW_MCMMO_SKILLS, null);
				context.setSessionData(CK.REW_MCMMO_AMOUNTS, null);
				return new RewardsMcMMOListPrompt(context);
			case 4:
				int one;
				int two;
				if (context.getSessionData(CK.REW_MCMMO_SKILLS) != null) {
					one = ((List<Integer>) context.getSessionData(CK.REW_MCMMO_SKILLS)).size();
				} else {
					one = 0;
				}
				if (context.getSessionData(CK.REW_MCMMO_AMOUNTS) != null) {
					two = ((List<Integer>) context.getSessionData(CK.REW_MCMMO_AMOUNTS)).size();
				} else {
					two = 0;
				}
				if (one == two) {
					return new RewardsPrompt(context);
				} else {
					context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("rewMcMMOListsNotSameSize"));
					return new RewardsMcMMOListPrompt(context);
				}
			default:
				return new RewardsMcMMOListPrompt(context);
			}
		}
	}

	public class McMMOSkillsPrompt extends QuestsEditorStringPrompt {

		public McMMOSkillsPrompt(final ConversationContext context) {
			super(context);
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return Lang.get("skillListTitle");
		}

		@Override
		public String getQueryText(final ConversationContext context) {
			return Lang.get("rewMcMMOPrompt");
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenStringPromptEvent event = new QuestsEditorPostOpenStringPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			return ChatColor.YELLOW + getQueryText(context);
		}

		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {
				final String[] args = input.split(" ");
				final List<String> skills = new LinkedList<String>();
				for (final String s : args) {
					String text = Lang.get("reqMcMMOError");
					text = text.replace("<input>", ChatColor.LIGHT_PURPLE + s + ChatColor.RED);
					context.getForWhom().sendRawMessage(ChatColor.RED + text);
					return new McMMOSkillsPrompt(context);
				}
				context.setSessionData(CK.REW_MCMMO_SKILLS, skills);
			}
			return new RewardsMcMMOListPrompt(context);
		}
	}

	public class McMMOAmountsPrompt extends QuestsEditorStringPrompt {

		public McMMOAmountsPrompt(final ConversationContext context) {
			super(context);
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return null;
		}

		@Override
		public String getQueryText(final ConversationContext context) {
			return Lang.get("reqMcMMOAmountsPrompt");
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenStringPromptEvent event = new QuestsEditorPostOpenStringPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			return ChatColor.YELLOW + getQueryText(context);
		}

		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {
				final String[] args = input.split(" ");
				final List<Integer> amounts = new LinkedList<Integer>();
				for (final String s : args) {
					try {
						amounts.add(Integer.parseInt(s));
					} catch (final NumberFormatException e) {
						String text = Lang.get("reqNotANumber");
						text = text.replace("<input>", ChatColor.LIGHT_PURPLE + s + ChatColor.RED);
						context.getForWhom().sendRawMessage(ChatColor.RED + text);
						return new McMMOAmountsPrompt(context);
					}
				}
				context.setSessionData(CK.REW_MCMMO_AMOUNTS, amounts);
			}
			return new RewardsMcMMOListPrompt(context);
		}
	}

	public class RewardsHeroesListPrompt extends QuestsEditorNumericPrompt {

		public RewardsHeroesListPrompt(final ConversationContext context) {
			super(context);
		}

		private final int size = 4;

		@Override
		public int getSize() {
			return size;
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return Lang.get("heroesRewardsTitle");
		}

		@Override
		public ChatColor getNumberColor(final ConversationContext context, final int number) {
			switch (number) {
			case 1:
			case 2:
				return ChatColor.BLUE;
			case 3:
				return ChatColor.RED;
			case 4:
				return ChatColor.GREEN;
			default:
				return null;
			}
		}

		@Override
		public String getSelectionText(final ConversationContext context, final int number) {
			switch (number) {
			case 1:
				return ChatColor.YELLOW + Lang.get("rewSetHeroesClasses");
			case 2:
				return ChatColor.YELLOW + Lang.get("rewSetHeroesAmounts");
			case 3:
				return ChatColor.RED + Lang.get("clear");
			case 4:
				return ChatColor.GREEN + Lang.get("done");
			default:
				return null;
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public String getAdditionalText(final ConversationContext context, final int number) {
			switch (number) {
			case 1:
				if (context.getSessionData(CK.REW_HEROES_CLASSES) == null) {
					return ChatColor.GRAY + " (" + Lang.get("noneSet") + ")";
				} else {
					String text = "\n";
					for (final String s : (List<String>) context.getSessionData(CK.REW_HEROES_CLASSES)) {
						text += ChatColor.GRAY + "     - " + ChatColor.AQUA + s + "\n";
					}
					return text;
				}
			case 2:
				if (context.getSessionData(CK.REW_HEROES_AMOUNTS) == null) {
					return ChatColor.GRAY + " (" + Lang.get("noneSet") + ")";
				} else {
					String text = "\n";
					for (final Double d : (List<Double>) context.getSessionData(CK.REW_HEROES_AMOUNTS)) {
						text += ChatColor.GRAY + "     - " + ChatColor.AQUA + d + "\n";
					}
					return text;
				}
			case 3:
			case 4:
				return "";
			default:
				return null;
			}
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenNumericPromptEvent event = new QuestsEditorPostOpenNumericPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			String text = ChatColor.AQUA + "- " + getTitle(context) + " -\n";
			for (int i = 1; i <= size; i++) {
				text += getNumberColor(context, i) + "" + ChatColor.BOLD + i + ChatColor.RESET + " - "
						+ getSelectionText(context, i) + " " + getAdditionalText(context, i) + "\n";
			}
			return text;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Prompt acceptValidatedInput(final ConversationContext context, final Number input) {
			switch (input.intValue()) {
			case 1:
				return new HeroesClassesPrompt(context);
			case 2:
				if (context.getSessionData(CK.REW_HEROES_CLASSES) == null) {
					context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("rewSetHeroesClassesFirst"));
					return new RewardsHeroesListPrompt(context);
				} else {
					return new HeroesExperiencePrompt(context);
				}
			case 3:
				context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("rewHeroesCleared"));
				context.setSessionData(CK.REW_HEROES_CLASSES, null);
				context.setSessionData(CK.REW_HEROES_AMOUNTS, null);
				return new RewardsHeroesListPrompt(context);
			case 4:
				int one;
				int two;
				if (context.getSessionData(CK.REW_HEROES_CLASSES) != null) {
					one = ((List<Integer>) context.getSessionData(CK.REW_HEROES_CLASSES)).size();
				} else {
					one = 0;
				}
				if (context.getSessionData(CK.REW_HEROES_AMOUNTS) != null) {
					two = ((List<Double>) context.getSessionData(CK.REW_HEROES_AMOUNTS)).size();
				} else {
					two = 0;
				}
				if (one == two) {
					return new RewardsPrompt(context);
				} else {
					context.getForWhom().sendRawMessage(ChatColor.RED + Lang.get("rewHeroesListsNotSameSize"));
					return new RewardsHeroesListPrompt(context);
				}
			default:
				return new RewardsHeroesListPrompt(context);
			}
		}
	}

	public class HeroesClassesPrompt extends QuestsEditorStringPrompt {

		public HeroesClassesPrompt(final ConversationContext context) {
			super(context);
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return Lang.get("heroesClassesTitle");
		}

		@Override
		public String getQueryText(final ConversationContext context) {
			return Lang.get("rewHeroesClassesPrompt");
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenStringPromptEvent event = new QuestsEditorPostOpenStringPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			String text = ChatColor.DARK_PURPLE + getTitle(context) + "\n";
			final List<String> list = new LinkedList<String>();
			if (list.isEmpty()) {
				text += ChatColor.GRAY + "(" + Lang.get("none") + ")\n";
			} else {
				Collections.sort(list);
				for (final String s : list) {
					text += ChatColor.LIGHT_PURPLE + s + ", ";
				}
				text = text.substring(0, text.length() - 2) + "\n";
			}
			text += ChatColor.YELLOW + getQueryText(context);
			return text;
		}

		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {
				final String[] arr = input.split(" ");
				final List<String> classes = new LinkedList<String>();
				for (final String s : arr) {
					String text = Lang.get("rewHeroesInvalidClass");
					text = text.replace("<input>", ChatColor.LIGHT_PURPLE + s + ChatColor.RED);
					context.getForWhom().sendRawMessage(ChatColor.RED + text);
					return new HeroesClassesPrompt(context);
				}
				context.setSessionData(CK.REW_HEROES_CLASSES, classes);
				return new RewardsHeroesListPrompt(context);
			} else {
				return new RewardsHeroesListPrompt(context);
			}
		}
	}

	public class HeroesExperiencePrompt extends QuestsEditorStringPrompt {

		public HeroesExperiencePrompt(final ConversationContext context) {
			super(context);
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return Lang.get("heroesExperienceTitle");
		}

		@Override
		public String getQueryText(final ConversationContext context) {
			return Lang.get("rewHeroesExperiencePrompt");
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenStringPromptEvent event = new QuestsEditorPostOpenStringPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			String text = getTitle(context) + "\n";
			text += ChatColor.YELLOW + getQueryText(context);
			return text;
		}

		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false) {
				final String[] arr = input.split(" ");
				final List<Double> amounts = new LinkedList<Double>();
				for (final String s : arr) {
					try {
						final double d = Double.parseDouble(s);
						amounts.add(d);
					} catch (final NumberFormatException nfe) {
						String text = Lang.get("reqNotANumber");
						text = text.replace("<input>", ChatColor.LIGHT_PURPLE + s + ChatColor.RED);
						context.getForWhom().sendRawMessage(ChatColor.RED + text);
						return new HeroesExperiencePrompt(context);
					}
				}
				context.setSessionData(CK.REW_HEROES_AMOUNTS, amounts);
				return new RewardsHeroesListPrompt(context);
			} else {
				return new RewardsHeroesListPrompt(context);
			}
		}
	}

	public class RewardsPhatLootsPrompt extends QuestsEditorStringPrompt {

		public RewardsPhatLootsPrompt(final ConversationContext context) {
			super(context);
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return Lang.get("phatLootsRewardsTitle");
		}

		@Override
		public String getQueryText(final ConversationContext context) {
			return Lang.get("rewPhatLootsPrompt");
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenStringPromptEvent event = new QuestsEditorPostOpenStringPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			String text = ChatColor.DARK_AQUA + getTitle(context) + "\n";
			text += ChatColor.YELLOW + getQueryText(context);
			return text;
		}

		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
					&& input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {
				final String[] arr = input.split(" ");
				final List<String> loots = new LinkedList<String>();
				for (final String s : arr) {
					String text = Lang.get("rewPhatLootsInvalid");
					text = text.replace("<input>", ChatColor.DARK_RED + s + ChatColor.RED);
					context.getForWhom().sendRawMessage(ChatColor.RED + text);
					return new RewardsPhatLootsPrompt(context);
				}
				loots.addAll(Arrays.asList(arr));
				context.setSessionData(CK.REW_PHAT_LOOTS, loots);
				return new RewardsPrompt(context);
			} else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
				context.setSessionData(CK.REW_PHAT_LOOTS, null);
				context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("rewPhatLootsCleared"));
				return new RewardsPrompt(context);
			} else {
				return new RewardsPrompt(context);
			}
		}
	}

	public class CustomRewardsPrompt extends QuestsEditorStringPrompt {

		public CustomRewardsPrompt(final ConversationContext context) {
			super(context);
		}

		@Override
		public String getTitle(final ConversationContext context) {
			return Lang.get("customRewardsTitle");
		}

		@Override
		public String getQueryText(final ConversationContext context) {
			return Lang.get("rewCustomRewardPrompt");
		}

		@Override
		public String getPromptText(final ConversationContext context) {
			final QuestsEditorPostOpenStringPromptEvent event = new QuestsEditorPostOpenStringPromptEvent(context,
					this);
			context.getPlugin().getServer().getPluginManager().callEvent(event);

			String text = ChatColor.LIGHT_PURPLE + getTitle(context) + "\n";
			if (plugin.getCustomRewards().isEmpty()) {
				text += ChatColor.DARK_PURPLE + "(" + Lang.get("stageEditorNoModules") + ") ";
			} else {
				for (final CustomReward cr : plugin.getCustomRewards()) {
					text += ChatColor.DARK_PURPLE + "  - " + cr.getName() + "\n";
				}
			}
			return text + ChatColor.YELLOW + getQueryText(context);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			if (input.equalsIgnoreCase(Lang.get("cmdCancel")) == false
					&& input.equalsIgnoreCase(Lang.get("cmdClear")) == false) {
				CustomReward found = null;
				// Check if we have a custom reward with the specified name
				for (final CustomReward cr : plugin.getCustomRewards()) {
					if (cr.getName().equalsIgnoreCase(input)) {
						found = cr;
						break;
					}
				}
				if (found == null) {
					// No? Check again, but with locale sensitivity
					for (final CustomReward cr : plugin.getCustomRewards()) {
						if (cr.getName().toLowerCase().contains(input.toLowerCase())) {
							found = cr;
							break;
						}
					}
				}
				if (found != null) {
					if (context.getSessionData(CK.REW_CUSTOM) != null) {
						// The custom reward may already have been added, so let's check that
						final LinkedList<String> list = (LinkedList<String>) context.getSessionData(CK.REW_CUSTOM);
						final LinkedList<Map<String, Object>> datamapList = (LinkedList<Map<String, Object>>) context
								.getSessionData(CK.REW_CUSTOM_DATA);
						if (list.contains(found.getName()) == false) {
							// Hasn't been added yet, so let's do it
							list.add(found.getName());
							datamapList.add(found.getData());
							context.setSessionData(CK.REW_CUSTOM, list);
							context.setSessionData(CK.REW_CUSTOM_DATA, datamapList);
						} else {
							// Already added, so inform user
							context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("rewCustomAlreadyAdded"));
							return new CustomRewardsPrompt(context);
						}
					} else {
						// The custom reward hasn't been added yet, so let's do it
						final LinkedList<Map<String, Object>> datamapList = new LinkedList<Map<String, Object>>();
						datamapList.add(found.getData());
						final LinkedList<String> list = new LinkedList<String>();
						list.add(found.getName());
						context.setSessionData(CK.REW_CUSTOM, list);
						context.setSessionData(CK.REW_CUSTOM_DATA, datamapList);
					}
					// Send user to the custom data prompt if there is any needed
					if (found.getData().isEmpty() == false) {
						context.setSessionData(CK.REW_CUSTOM_DATA_DESCRIPTIONS, found.getDescriptions());
						return new RewardCustomDataListPrompt();
					}
				} else {
					context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("rewCustomNotFound"));
					return new CustomRewardsPrompt(context);
				}
			} else if (input.equalsIgnoreCase(Lang.get("cmdClear"))) {
				context.setSessionData(CK.REW_CUSTOM, null);
				context.setSessionData(CK.REW_CUSTOM_DATA, null);
				context.setSessionData(CK.REW_CUSTOM_DATA_TEMP, null);
				context.getForWhom().sendRawMessage(ChatColor.YELLOW + Lang.get("rewCustomCleared"));
			}
			return new RewardsPrompt(context);
		}
	}

	private class RewardCustomDataListPrompt extends StringPrompt {

		@SuppressWarnings("unchecked")
		@Override
		public String getPromptText(final ConversationContext context) {
			String text = ChatColor.AQUA + "- ";
			final LinkedList<String> list = (LinkedList<String>) context.getSessionData(CK.REW_CUSTOM);
			final LinkedList<Map<String, Object>> datamapList = (LinkedList<Map<String, Object>>) context
					.getSessionData(CK.REW_CUSTOM_DATA);
			final String rewName = list.getLast();
			final Map<String, Object> datamap = datamapList.getLast();
			text += rewName + " -\n";
			int index = 1;
			final LinkedList<String> datamapKeys = new LinkedList<String>();
			for (final String key : datamap.keySet()) {
				datamapKeys.add(key);
			}
			Collections.sort(datamapKeys);
			for (final String dataKey : datamapKeys) {
				text += ChatColor.BLUE + "" + ChatColor.BOLD + index + ChatColor.RESET + ChatColor.YELLOW + " - "
						+ dataKey;
				if (datamap.get(dataKey) != null) {
					text += ChatColor.GREEN + " (" + datamap.get(dataKey).toString() + ")\n";
				} else {
					text += ChatColor.RED + " (" + Lang.get("valRequired") + ")\n";
				}
				index++;
			}
			text += ChatColor.GREEN + "" + ChatColor.BOLD + index + ChatColor.YELLOW + " - " + Lang.get("done");
			return text;
		}

		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			@SuppressWarnings("unchecked")
			final LinkedList<Map<String, Object>> datamapList = (LinkedList<Map<String, Object>>) context
					.getSessionData(CK.REW_CUSTOM_DATA);
			final Map<String, Object> datamap = datamapList.getLast();
			int numInput;
			try {
				numInput = Integer.parseInt(input);
			} catch (final NumberFormatException nfe) {
				return new RewardCustomDataListPrompt();
			}
			if (numInput < 1 || numInput > datamap.size() + 1) {
				return new RewardCustomDataListPrompt();
			}
			if (numInput < datamap.size() + 1) {
				final LinkedList<String> datamapKeys = new LinkedList<String>();
				for (final String key : datamap.keySet()) {
					datamapKeys.add(key);
				}
				Collections.sort(datamapKeys);
				final String selectedKey = datamapKeys.get(numInput - 1);
				context.setSessionData(CK.REW_CUSTOM_DATA_TEMP, selectedKey);
				return new RewardCustomDataPrompt();
			} else {
				if (datamap.containsValue(null)) {
					return new RewardCustomDataListPrompt();
				} else {
					context.setSessionData(CK.REW_CUSTOM_DATA_DESCRIPTIONS, null);
					return new RewardsPrompt(context);
				}
			}
		}
	}

	private class RewardCustomDataPrompt extends StringPrompt {

		@Override
		public String getPromptText(final ConversationContext context) {
			String text = "";
			final String temp = (String) context.getSessionData(CK.REW_CUSTOM_DATA_TEMP);
			@SuppressWarnings("unchecked")
			final Map<String, String> descriptions = (Map<String, String>) context
					.getSessionData(CK.REW_CUSTOM_DATA_DESCRIPTIONS);
			if (descriptions.get(temp) != null) {
				text += ChatColor.GOLD + descriptions.get(temp) + "\n";
			}
			String lang = Lang.get("stageEditorCustomDataPrompt");
			lang = lang.replace("<data>", temp);
			text += ChatColor.YELLOW + lang;
			return text;
		}

		@Override
		public Prompt acceptInput(final ConversationContext context, final String input) {
			@SuppressWarnings("unchecked")
			final LinkedList<Map<String, Object>> datamapList = (LinkedList<Map<String, Object>>) context
					.getSessionData(CK.REW_CUSTOM_DATA);
			final Map<String, Object> datamap = datamapList.getLast();
			datamap.put((String) context.getSessionData(CK.REW_CUSTOM_DATA_TEMP), input);
			context.setSessionData(CK.REW_CUSTOM_DATA_TEMP, null);
			return new RewardCustomDataListPrompt();
		}
	}
}
