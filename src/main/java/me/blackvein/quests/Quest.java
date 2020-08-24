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

package me.blackvein.quests;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


import me.blackvein.quests.actions.Action;
import me.blackvein.quests.events.quester.QuesterPostChangeStageEvent;
import me.blackvein.quests.events.quester.QuesterPostCompleteQuestEvent;
import me.blackvein.quests.events.quester.QuesterPostFailQuestEvent;
import me.blackvein.quests.events.quester.QuesterPreChangeStageEvent;
import me.blackvein.quests.events.quester.QuesterPreCompleteQuestEvent;
import me.blackvein.quests.events.quester.QuesterPreFailQuestEvent;
import me.blackvein.quests.util.ConfigUtil;
import me.blackvein.quests.util.InventoryUtil;
import me.blackvein.quests.util.ItemUtil;
import me.blackvein.quests.util.Lang;
import net.citizensnpcs.api.npc.NPC;

public class Quest {

    protected Quests plugin;
    protected String id;
    private String name;
    protected String description;
    protected String finished;
    protected ItemStack guiDisplay = null;
    private final LinkedList<Stage> orderedStages = new LinkedList<Stage>();
    protected NPC npcStart;
    protected Location blockStart;
    protected String regionStart = null;
    protected Action initialAction;
    private final Requirements reqs = new Requirements();
    private final Planner pln = new Planner();
    private final Rewards rews = new Rewards();
    private final Options opts = new Options();
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String getFinished() {
        return finished;
    }
    
    public void setFinished(final String finished) {
        this.finished = finished;
    }
    
    public String getRegionStart() {
        return regionStart;
    }
    
    public void setRegionStart(final String regionStart) {
        this.regionStart = regionStart;
    }
    
    public ItemStack getGUIDisplay() {
        return guiDisplay;
    }
    
    public void setGUIDisplay(final ItemStack guiDisplay) {
        this.guiDisplay = guiDisplay;
    }
    
    public Stage getStage(final int index) {
        try {
            return orderedStages.get(index);
        } catch (final Exception e) {
            return null;
        }
    }
    
    public LinkedList<Stage> getStages() {
        return orderedStages;
    }
    
    public NPC getNpcStart() {
        return npcStart;
    }
    
    public void setNpcStart(final NPC npcStart) {
        this.npcStart = npcStart;
    }
    
    public Location getBlockStart() {
        return blockStart;
    }
    
    public void setBlockStart(final Location blockStart) {
        this.blockStart = blockStart;
    }
    
    public Action getInitialAction() {
        return initialAction;
    }
    
    public void setInitialAction(final Action initialAction) {
        this.initialAction = initialAction;
    }
    
    public Requirements getRequirements() {
        return reqs;
    }
    
    public Planner getPlanner() {
        return pln;
    }
    
    public Rewards getRewards() {
        return rews;
    }
    
    public Options getOptions() {
        return opts;
    }

    /**
     * Force player to proceed to the next ordered stage
     * 
     * @param quester Player to force
     * @param allowSharedProgress Whether to distribute progress to fellow questers
     */
    public void nextStage(final Quester quester, final boolean allowSharedProgress) {
        final Stage currentStage = quester.getCurrentStage(this);
        if (currentStage == null) {
            plugin.getLogger().severe("Current stage was null for quester " + quester.getPlayer().getUniqueId());
            return;
        }
        final String stageCompleteMessage = currentStage.completeMessage;
        if (stageCompleteMessage != null) {
            if (quester.getOfflinePlayer().isOnline()) {
                quester.getPlayer().sendMessage(ConfigUtil.parseStringWithPossibleLineBreaks(stageCompleteMessage, 
                        this, quester.getPlayer()));
            }
        }
        if (quester.getPlayer().hasPermission("quests.compass")) {
            quester.resetCompass();
            quester.findCompassTarget();
        }
        if (currentStage.delay < 0) {
            if (currentStage.finishAction != null) {
                currentStage.finishAction.fire(quester, this);
            }
            if (quester.currentQuests.get(this) == (orderedStages.size() - 1)) {
                if (currentStage.script != null) {
                    plugin.getDenizenTrigger().runDenizenScript(currentStage.script, quester);
                }
                completeQuest(quester);
            } else {
                setStage(quester, quester.currentQuests.get(this) + 1);
            }
            if (quester.getQuestData(this) != null) {
                quester.getQuestData(this).setDelayStartTime(0);
                quester.getQuestData(this).setDelayTimeLeft(-1);
            }
            
            // Multiplayer
            if (opts.getShareProgressLevel() == 3) {
                final List<Quester> mq = quester.getMultiplayerQuesters(this);
                for (final Quester qq : mq) {
                    if (currentStage.equals(qq.getCurrentStage(this))) {
                        nextStage(qq, allowSharedProgress);
                    }
                }
            }
        } else {
            quester.startStageTimer(this);
        }
        quester.updateJournal();
    }

    /**
     * Force player to proceed to the specified stage
     * 
     * @param quester Player to force
     * @param stage Stage number to specify
     * @throws IndexOutOfBoundsException if stage does not exist
     */
    public void setStage(final Quester quester, final int stage) throws IndexOutOfBoundsException {
        final OfflinePlayer player = quester.getOfflinePlayer();
        if (orderedStages.size() - 1 < stage) {
            final String msg = "Tried to set invalid stage number of " + stage + " for quest " + getName() + " on " 
                    + player.getName();
            throw new IndexOutOfBoundsException(msg);
        }
        final Stage currentStage = quester.getCurrentStage(this);
        final Stage nextStage = getStage(stage);
        if (player.isOnline()) {
            final QuesterPreChangeStageEvent preEvent = new QuesterPreChangeStageEvent(quester, this, currentStage, nextStage);
            plugin.getServer().getPluginManager().callEvent(preEvent);
            if (preEvent.isCancelled()) {
                return;
            }
        }
        quester.hardQuit(this);
        quester.hardStagePut(this, stage);
        quester.addEmptiesFor(this, stage);
        if (currentStage.script != null) {
            plugin.getDenizenTrigger().runDenizenScript(currentStage.script, quester);
        }
        if (nextStage.startAction != null) {
            nextStage.startAction.fire(quester, this);
        }
        updateCompass(quester, nextStage);
        if (player.isOnline()) {
            final Player p = quester.getPlayer();
            String msg = Lang.get(p, "questObjectivesTitle");
            msg = msg.replace("<quest>", name);
//            p.sendMessage(ChatColor.GOLD + msg);
            plugin.showObjectives(this, quester, false);
            final String stageStartMessage = quester.getCurrentStage(this).startMessage;
            if (stageStartMessage != null) {
                p.sendMessage(ConfigUtil.parseStringWithPossibleLineBreaks(stageStartMessage, this, p));
            }
        }
        quester.updateJournal();
        if (player.isOnline()) {
            final QuesterPostChangeStageEvent postEvent = new QuesterPostChangeStageEvent(quester, this, currentStage, nextStage);
            plugin.getServer().getPluginManager().callEvent(postEvent);
        }
    }

    /**
     * Set location-objective target for compass.<p>
     * 
     * Method may be called as often as needed.
     * 
     * @param quester The online quester to have their compass updated
     * @param stage The stage to process for targets
     * @return true if successful
     */
    public boolean updateCompass(final Quester quester, final Stage stage) {
        if (quester == null) {
            return false;
        }
        if (stage == null) {
            return false;
        }
        if (!quester.getOfflinePlayer().isOnline()) {
            return false;
        }
        if (!quester.getPlayer().hasPermission("quests.compass")) {
            return false;
        }
        Location targetLocation = null;
        if (stage.citizensToInteract != null && stage.citizensToInteract.size() > 0) {
            targetLocation = plugin.getDependencies().getNPCLocation(stage.citizensToInteract.getFirst());
        } else if (stage.citizensToKill != null && stage.citizensToKill.size() > 0) {
            targetLocation = plugin.getDependencies().getNPCLocation(stage.citizensToKill.getFirst());
        } else if (stage.locationsToReach != null && stage.locationsToReach.size() > 0) {
            targetLocation = stage.locationsToReach.getFirst();
        } else if (stage.itemDeliveryTargets != null && stage.itemDeliveryTargets.size() > 0) {
            final NPC npc = plugin.getDependencies().getCitizens().getNPCRegistry().getById(stage.itemDeliveryTargets
                    .getFirst());
            targetLocation = npc.getStoredLocation();
        }
        if (targetLocation != null && targetLocation.getWorld() != null) {
            if (targetLocation.getWorld().getName().equals(quester.getPlayer().getWorld().getName())) {
                quester.getPlayer().setCompassTarget(targetLocation);
            }
        }
        return targetLocation != null;
    }
    
    /**
     * Check that a quester has met all Requirements to accept this quest<p>
     * 
     * Item, permission and custom Requirements are only checked for online players
     * 
     * @param quester The quester to check
     * @return true if all Requirements have been met
     */
    public boolean testRequirements(final Quester quester) {
        return testRequirements(quester.getOfflinePlayer());
    }
    
    /**
     * Check that a player has met all Requirements to accept this quest<p>
     * 
     * Item, permission and custom Requirements are only checked for online players
     * 
     * @param player The player to check
     * @return true if all Requirements have been met
     */
    protected boolean testRequirements(final OfflinePlayer player) {
        final Quester quester = plugin.getQuester(player.getUniqueId());
        if (reqs.getMoney() != 0 && plugin.getDependencies().getVaultEconomy() != null) {
            if (plugin.getDependencies().getVaultEconomy().getBalance(player) < reqs.getMoney()) {
                return false;
            }
        }
        if (quester.questPoints < reqs.getQuestPoints()) {
            return false;
        }
        if (quester.completedQuests.containsAll(reqs.getNeededQuests()) == false) {
            return false;
        }
        for (final String q : reqs.getBlockQuests()) {
            final Quest questObject = new Quest();
            questObject.name = q;
            if (quester.completedQuests.contains(q) || quester.currentQuests.containsKey(questObject)) {
                return false;
            }
        }
        if (player.isOnline()) {
            final Player p = (Player)player;
            final PlayerInventory inventory = p.getInventory();
            int num = 0;
            for (final ItemStack is : reqs.getItems()) {
                for (final ItemStack stack : inventory.getContents()) {
                    if (stack != null) {
                        if (ItemUtil.compareItems(is, stack, true) == 0) {
                            num += stack.getAmount();
                        }
                    }
                }
                if (num < is.getAmount()) {
                    return false;
                }
                num = 0;
            }
            for (final String s : reqs.getPermissions()) {
                if (p.hasPermission(s) == false) {
                    return false;
                }
            }
            for (final String s : reqs.getCustomRequirements().keySet()) {
                CustomRequirement found = null;
                for (final CustomRequirement cr : plugin.getCustomRequirements()) {
                    if (cr.getName().equalsIgnoreCase(s)) {
                        found = cr;
                        break;
                    }
                }
                if (found != null) {
                    if (found.testRequirement(p, reqs.getCustomRequirements().get(s)) == false) {
                        return false;
                    }
                } else {
                    plugin.getLogger().warning("Quester \"" + p.getName() + "\" attempted to take Quest \"" + name 
                            + "\", but the Custom Requirement \"" + s + "\" could not be found. Does it still exist?");
                }
            }
        }
        return true;
    }
    
    /**
     * Proceed to finish this quest, issuing applicable rewards
     * 
     * @param q The quester finishing this quest
     */
    @SuppressWarnings("deprecation")
    public void completeQuest(final Quester q) {
        final OfflinePlayer player = q.getOfflinePlayer();
        if (player.isOnline()) {
            final QuesterPreCompleteQuestEvent preEvent = new QuesterPreCompleteQuestEvent(q, this);
            plugin.getServer().getPluginManager().callEvent(preEvent);
            if (preEvent.isCancelled()) {
                return;
            }
        }
        q.hardQuit(this);
        if (!q.completedQuests.contains(name)) {
            q.completedQuests.add(name);
        }
        for (final Map.Entry<Integer, Quest> entry : q.timers.entrySet()) {
            if (entry.getValue().getName().equals(getName())) {
                plugin.getServer().getScheduler().cancelTask(entry.getKey());
                q.timers.remove(entry.getKey());
            }
        }
        if (player.isOnline()) {
            final Player p = (Player)player;
            final String[] ps = ConfigUtil.parseStringWithPossibleLineBreaks(ChatColor.AQUA + finished, this, p);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

                @Override
                public void run() {
                    p.sendMessage(ps);
                }
            }, 40);
        }
        if (pln.getCooldown() > -1) {
            q.completedTimes.put(this.name, System.currentTimeMillis());
            if (q.amountsCompleted.containsKey(this.name)) {
                q.amountsCompleted.put(this.name, q.amountsCompleted.get(this.name) + 1);
            } else {
                q.amountsCompleted.put(this.name, 1);
            }
        }
        
        // Issue rewards
        boolean issuedReward = false;
        if (rews.getMoney() > 0 && plugin.getDependencies().getVaultEconomy() != null) {
            plugin.getDependencies().getVaultEconomy().depositPlayer(player, rews.getMoney());
            issuedReward = true;
        }
        if (player.isOnline()) {
            final Player p = (Player)player;
            for (final ItemStack i : rews.getItems()) {
                try {
                    InventoryUtil.addItem(p, i);
                } catch (final Exception e) {
                    plugin.getLogger().severe("Unable to add null reward item to inventory of " 
                            + player.getName() + " upon completion of quest " + name);
                    p.sendMessage(ChatColor.RED + "Quests encountered a problem with an item. "
                            + "Please contact an administrator.");
                }
                issuedReward = true;
            }
        }
        for (final String s : rews.getCommands()) {
            String temp = s.replace("<player>", player.getName());
            final String command = temp;
            if (Bukkit.isPrimaryThread()) {
                Bukkit.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
            } else {
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    
                    @Override
                    public void run() {
                        Bukkit.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
                    }
                });
            }
            issuedReward = true;
        }
        for (int i = 0; i < rews.getPermissions().size(); i++) {
            if (plugin.getDependencies().getVaultPermission() != null) {
                final String perm = rews.getPermissions().get(i);
                String world = null;
                if (i < rews.getPermissionWorlds().size()) {
                    world = rews.getPermissionWorlds().get(i);
                }
                if (world == null || world.equals("null")) {
                    plugin.getDependencies().getVaultPermission().playerAdd(null, player, perm);
                } else {
                    plugin.getDependencies().getVaultPermission().playerAdd(world, player, perm);
                }
            }
            issuedReward = true;
        }
        if (rews.getExp() > 0 && player.isOnline()) {
            ((Player)player).giveExp(rews.getExp());
            issuedReward = true;
        }
        if (rews.getQuestPoints() > 0) {
            q.questPoints += rews.getQuestPoints();
            issuedReward = true;
        }
        if (rews.getCustomRewards().isEmpty() == false) {
            issuedReward = true;
        }
        
        // Inform player
        if (player.isOnline()) {
//            final Player p = (Player)player;
//            p.sendMessage(ChatColor.GOLD + Lang.get(p, "questCompleteTitle").replace("<quest>", ChatColor.YELLOW + name
//                    + ChatColor.GOLD));
//            if (plugin.getSettings().canShowQuestTitles()) {
//                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "title " + p.getName()
//                        + " title " + "{\"text\":\"" + Lang.get(p, "quest") + " " + Lang.get(p, "complete") 
//                        +  "\",\"color\":\"gold\"}");
//                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "title " + p.getName()
//                        + " subtitle " + "{\"text\":\"" + name + "\",\"color\":\"yellow\"}");
//            }
//            p.sendMessage(ChatColor.GREEN + Lang.get(p, "questRewardsTitle"));
//            if (!issuedReward) {
//                p.sendMessage(ChatColor.GRAY + "- (" + Lang.get("none") + ")");
//            } else if (!rews.getDetailsOverride().isEmpty()) {
//                for (final String s: rews.getDetailsOverride()) {
//                    String message = ChatColor.DARK_GREEN + ConfigUtil.parseString(
//                            ChatColor.translateAlternateColorCodes('&', s));
//                    p.sendMessage("- " + message);
//                }
//            } else {
//                if (rews.getQuestPoints() > 0) {
//                    p.sendMessage("- " + ChatColor.DARK_GREEN + rews.getQuestPoints() + " " 
//                            + Lang.get(p, "questPoints"));
//                }
//                for (final ItemStack i : rews.getItems()) {
//                    String text = "error";
//                    if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
//                        if (i.getEnchantments().isEmpty()) {
//                            text = "- " + ChatColor.DARK_AQUA + ChatColor.ITALIC + i.getItemMeta().getDisplayName() 
//                                    + ChatColor.RESET + ChatColor.GRAY + " x " + i.getAmount();
//                        } else {
//                            text = "- " + ChatColor.DARK_AQUA + ChatColor.ITALIC + i.getItemMeta().getDisplayName() 
//                                    + ChatColor.RESET;            
//                            try {
//                                if (!i.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
//                                    text +=  ChatColor.GRAY + " " + Lang.get(p, "with") + ChatColor.DARK_PURPLE;
//                                    for (final Entry<Enchantment, Integer> e : i.getEnchantments().entrySet()) {
//                                        text += " " + ItemUtil.getPrettyEnchantmentName(e.getKey()) + ":" + e.getValue();
//                                    }
//                                }
//                            } catch (final Throwable tr) {
//                                // Do nothing, hasItemFlag() not introduced until 1.8.6
//                            }
//                            text += ChatColor.GRAY + " x " + i.getAmount();
//                        }
//                    } else if (i.getDurability() != 0) {
//                        if (i.getEnchantments().isEmpty()) {
//                            text = "- " + ChatColor.DARK_GREEN + ItemUtil.getName(i) + ":" + i.getDurability() + ChatColor.GRAY
//                                    + " x " + i.getAmount();
//                        } else {
//                            text = "- " + ChatColor.DARK_GREEN + ItemUtil.getName(i) + ":" + i.getDurability() + ChatColor.GRAY
//                                    + " " + Lang.get(p, "with");
//                            for (final Entry<Enchantment, Integer> e : i.getEnchantments().entrySet()) {
//                                text += " " + ItemUtil.getPrettyEnchantmentName(e.getKey()) + ":" + e.getValue();
//                            }
//                            text += ChatColor.GRAY + " x " + i.getAmount();
//                        }
//                    } else {
//                        if (i.getEnchantments().isEmpty()) {
//                            text = "- " + ChatColor.DARK_GREEN + ItemUtil.getName(i) + ChatColor.GRAY + " x " + i.getAmount();
//                        } else {
//                            text = "- " + ChatColor.DARK_GREEN + ItemUtil.getName(i);
//                            try {
//                                if (!i.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
//                                    text += ChatColor.GRAY + " " + Lang.get(p, "with");
//                                    for (final Entry<Enchantment, Integer> e : i.getEnchantments().entrySet()) {
//                                        text += " " + ItemUtil.getPrettyEnchantmentName(e.getKey()) + ":" + e.getValue();
//                                    }
//                                }
//                            } catch (final Throwable tr) {
//                                // Do nothing, hasItemFlag() not introduced until 1.8.6
//                            }
//                            text += ChatColor.GRAY + " x " + i.getAmount();
//                        }
//                    }
//                    p.sendMessage(text);
//                }
//                if (rews.getMoney() > 1) {
//                    p.sendMessage("- " + ChatColor.DARK_GREEN + rews.getMoney() + " " + ChatColor.DARK_PURPLE 
//                            + plugin.getDependencies().getCurrency(true));
//                } else if (rews.getMoney() == 1) {
//                    p.sendMessage("- " + ChatColor.DARK_GREEN + rews.getMoney() + " " + ChatColor.DARK_PURPLE 
//                            + plugin.getDependencies().getCurrency(false));
//                }
//                if (rews.getCommands().isEmpty() == false) {
//                    int index = 0;
//                    for (final String s : rews.getCommands()) {
//                        if (rews.getCommandsOverrideDisplay().isEmpty() == false && rews.getCommandsOverrideDisplay().size() 
//                                > index) {
//                            if (!rews.getCommandsOverrideDisplay().get(index).trim().equals("")) {
//                                p.sendMessage("- " + ChatColor.DARK_GREEN 
//                                        + rews.getCommandsOverrideDisplay().get(index));
//                            }
//                        } else {
//                            p.sendMessage("- " + ChatColor.DARK_GREEN + s);
//                        }
//                        index++;
//                    }
//                }
//                if (rews.getPermissions().isEmpty() == false) {
//                    int index = 0;
//                    for (final String s : rews.getPermissions()) {
//                        if (rews.getPermissionWorlds() != null && rews.getPermissionWorlds().size() > index) {
//                            p.sendMessage("- " + ChatColor.DARK_GREEN + s + " (" + rews.getPermissionWorlds().get(index)
//                                    + ")");
//                        } else {
//                            p.sendMessage("- " + ChatColor.DARK_GREEN + s);
//                            
//                        }
//                        index++;
//                    }
//                }
//                if (rews.getMcmmoSkills().isEmpty() == false) {
//                    for (final String s : rews.getMcmmoSkills()) {
//                        p.sendMessage("- " + ChatColor.DARK_GREEN 
//                                + rews.getMcmmoAmounts().get(rews.getMcmmoSkills().indexOf(s)) + " " 
//                                + ChatColor.DARK_PURPLE + s + " " + Lang.get(p, "experience"));
//                    }
//                }
//                if (rews.getHeroesClasses().isEmpty() == false) {
//                    for (final String s : rews.getHeroesClasses()) {
//                        p.sendMessage("- " + ChatColor.AQUA 
//                                + rews.getHeroesAmounts().get(rews.getHeroesClasses().indexOf(s)) + " " + ChatColor.BLUE 
//                                + s + " " + Lang.get(p, "experience"));
//                    }
//                }
//                for (final String s : rews.getCustomRewards().keySet()) {
//                    CustomReward found = null;
//                    for (final CustomReward cr : plugin.getCustomRewards()) {
//                        if (cr.getName().equalsIgnoreCase(s)) {
//                            found = cr;
//                            break;
//                        }
//                    }
//                    if (found != null) {
//                        final Map<String, Object> datamap = rews.getCustomRewards().get(found.getName());
//                        String message = found.getDisplay();
//                        if (message != null) {
//                            for (final String key : datamap.keySet()) {
//                                message = message.replace("%" + key + "%", datamap.get(key).toString());
//                            }
//                            p.sendMessage("- " + ChatColor.GOLD + message);
//                        } else {
//                            plugin.getLogger().warning("Failed to notify player: " 
//                                    + "Custom Reward does not have an assigned name");
//                        }
//                        found.giveReward(p, rews.getCustomRewards().get(s));
//                    } else {
//                        plugin.getLogger().warning("Quester \"" + player.getName() + "\" completed the Quest \"" + name 
//                                + "\", but the Custom Reward \"" + s + "\" could not be found. Does it still exist?");
//                    }
//                }
//            }
        }
        q.saveData();
        if (player.isOnline()) {
            ((Player)player).updateInventory();
        }
        q.updateJournal();
        q.findCompassTarget();
        if (player.isOnline()) {
            final QuesterPostCompleteQuestEvent postEvent = new QuesterPostCompleteQuestEvent(q, this);
            plugin.getServer().getPluginManager().callEvent(postEvent);
        }
        
        // Multiplayer
        if (opts.getShareProgressLevel() == 4) {
            final List<Quester> mq = q.getMultiplayerQuesters(this);
            for (final Quester qq : mq) {
                if (qq.getQuestData(this) != null) {
                    completeQuest(qq);
                }
            }
        }
    }
    
    /**
     * Force player to quit quest and inform them of their failure
     * 
     * @param quester The quester to be ejected
     */
    @SuppressWarnings("deprecation")
    public void failQuest(final Quester quester) {
        final QuesterPreFailQuestEvent preEvent = new QuesterPreFailQuestEvent(quester, this);
        plugin.getServer().getPluginManager().callEvent(preEvent);
        if (preEvent.isCancelled()) {
            return;
        }
        final Player player = quester.getPlayer();
        final String[] messages = {
                ChatColor.GOLD + Lang.get(player, "questObjectivesTitle").replace("<quest>", name),
                ChatColor.RED + Lang.get(player, "questFailed")
        };
        quester.quitQuest(this, messages);
        if (player.isOnline()) {
            player.updateInventory();
        }
        final QuesterPostFailQuestEvent postEvent = new QuesterPostFailQuestEvent(quester, this);
        plugin.getServer().getPluginManager().callEvent(postEvent);
    }
    
    /**
     * Checks if quester is in WorldGuard region start
     * 
     * @param quester The quester to check
     * @return true if quester is in region
     */
    public boolean isInRegion(final Quester quester) {
        return isInRegion(quester.getPlayer());
    }

    /**
     * Checks if player is in WorldGuard region start
     * 
     * @param player The player to check
     * @return true if player is in region
     */
    private boolean isInRegion(final Player player) {
        if (regionStart == null) {
            return false;
        }
        if (plugin.getDependencies().getWorldGuardApi()
                .getApplicableRegionsIDs(player.getWorld(), player.getLocation()).contains(regionStart)) {
            return true;
        }
        return false;
    }
}
