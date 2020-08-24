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

package me.blackvein.quests.actions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import me.blackvein.quests.Quest;
import me.blackvein.quests.QuestMob;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;
import me.blackvein.quests.tasks.ActionTimer;
import me.blackvein.quests.util.ConfigUtil;
import me.blackvein.quests.util.InventoryUtil;
import me.blackvein.quests.util.Lang;

public class Action {

    private final Quests plugin;
    private String name = "";
    protected String message = null;
    protected boolean clearInv = false;
    protected boolean failQuest = false;
    protected LinkedList<Location> explosions = new LinkedList<Location>();
    protected Map<Location, Effect> effects = new HashMap<Location, Effect>();
    protected LinkedList<ItemStack> items = new LinkedList<ItemStack>();
    protected World stormWorld = null;
    protected int stormDuration = 0;
    protected World thunderWorld = null;
    protected int thunderDuration = 0;
    protected int timer = 0;
    protected boolean cancelTimer = false;
    protected LinkedList<QuestMob> mobSpawns = new LinkedList<QuestMob>() {

        private static final long serialVersionUID = -761974607799449780L;

        @Override
        public boolean equals(final Object o) {
            if (o instanceof LinkedList) {
                @SuppressWarnings("unchecked")
                final
                LinkedList<QuestMob> other = (LinkedList<QuestMob>) o;
                if (size() != other.size()) {
                    return false;
                }
                for (int i = 0; i < size(); i++) {
                    if (get(i).equals(other.get(i)) == false) {
                        return false;
                    }
                }
            }
            return false;
        }
    };
    protected LinkedList<Location> lightningStrikes = new LinkedList<Location>();
    protected LinkedList<String> commands = new LinkedList<String>();
    protected LinkedList<PotionEffect> potionEffects = new LinkedList<PotionEffect>();
    protected int hunger = -1;
    protected int saturation = -1;
    protected float health = -1;
    protected Location teleport;
    protected String book = "";
    protected String denizenScript;

    public Action(final Quests plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public boolean isClearInv() {
        return clearInv;
    }

    public void setClearInv(final boolean clearInv) {
        this.clearInv = clearInv;
    }

    public boolean isFailQuest() {
        return failQuest;
    }

    public void setFailQuest(final boolean failQuest) {
        this.failQuest = failQuest;
    }

    public LinkedList<Location> getExplosions() {
        return explosions;
    }

    public void setExplosions(final LinkedList<Location> explosions) {
        this.explosions = explosions;
    }

    public Map<Location, Effect> getEffects() {
        return effects;
    }

    public void setEffects(final Map<Location, Effect> effects) {
        this.effects = effects;
    }

    public LinkedList<ItemStack> getItems() {
        return items;
    }

    public void setItems(final LinkedList<ItemStack> items) {
        this.items = items;
    }

    public World getStormWorld() {
        return stormWorld;
    }

    public void setStormWorld(final World stormWorld) {
        this.stormWorld = stormWorld;
    }

    public int getStormDuration() {
        return stormDuration;
    }

    public void setStormDuration(final int stormDuration) {
        this.stormDuration = stormDuration;
    }

    public World getThunderWorld() {
        return thunderWorld;
    }

    public void setThunderWorld(final World thunderWorld) {
        this.thunderWorld = thunderWorld;
    }

    public int getThunderDuration() {
        return thunderDuration;
    }

    public void setThunderDuration(final int thunderDuration) {
        this.thunderDuration = thunderDuration;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(final int timer) {
        this.timer = timer;
    }

    public boolean isCancelTimer() {
        return cancelTimer;
    }

    public void setCancelTimer(final boolean cancelTimer) {
        this.cancelTimer = cancelTimer;
    }

    public LinkedList<QuestMob> getMobSpawns() {
        return mobSpawns;
    }

    public void setMobSpawns(final LinkedList<QuestMob> mobSpawns) {
        this.mobSpawns = mobSpawns;
    }

    public LinkedList<Location> getLightningStrikes() {
        return lightningStrikes;
    }

    public void setLightningStrikes(final LinkedList<Location> lightningStrikes) {
        this.lightningStrikes = lightningStrikes;
    }

    public LinkedList<String> getCommands() {
        return commands;
    }

    public void setCommands(final LinkedList<String> commands) {
        this.commands = commands;
    }

    public LinkedList<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public void setPotionEffects(final LinkedList<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(final int hunger) {
        this.hunger = hunger;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(final int saturation) {
        this.saturation = saturation;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(final float health) {
        this.health = health;
    }

    public Location getTeleport() {
        return teleport;
    }

    public void setTeleport(final Location teleport) {
        this.teleport = teleport;
    }

    public String getBook() {
        return book;
    }

    public void setBook(final String book) {
        this.book = book;
    }
    
    public String getDenizenScript() {
        return book;
    }

    public void setDenizenScript(final String scriptName) {
        this.denizenScript = scriptName;
    }

    public void fire(final Quester quester, final Quest quest) {
        final Player player = quester.getPlayer();
        if (message != null) {
            player.sendMessage(ConfigUtil.parseStringWithPossibleLineBreaks(message, quest, player));
        }
        if (clearInv == true) {
            player.getInventory().clear();
        }
        if (explosions.isEmpty() == false) {
            for (final Location l : explosions) {
                l.getWorld().createExplosion(l, 4F, false);
            }
        }
        if (effects.isEmpty() == false) {
            for (final Location l : effects.keySet()) {
                l.getWorld().playEffect(l, effects.get(l), 1);
            }
        }
        if (items.isEmpty() == false) {
            for (final ItemStack is : items) {
                try {
                    InventoryUtil.addItem(player, is);
                } catch (final Exception e) {
                    plugin.getLogger().severe("Unable to add null item to inventory of " 
                            + player.getName() + " during quest " + quest.getName() + " event " + name);
                    player.sendMessage(ChatColor.RED + "Quests encountered a problem with an item. "
                            + "Please contact an administrator.");
                }
            }
        }
        if (stormWorld != null) {
            stormWorld.setStorm(true);
            stormWorld.setWeatherDuration(stormDuration);
        }
        if (thunderWorld != null) {
            thunderWorld.setThundering(true);
            thunderWorld.setThunderDuration(thunderDuration);
        }
        if (mobSpawns.isEmpty() == false) {
            for (final QuestMob questMob : mobSpawns) {
                questMob.spawn();
            }
        }
        if (lightningStrikes.isEmpty() == false) {
            for (final Location l : lightningStrikes) {
                l.getWorld().strikeLightning(l);
            }
        }
        if (commands.isEmpty() == false) {
            for (final String s : commands) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), 
                        s.replace("<player>", quester.getPlayer().getName()));
            }
        }
        if (potionEffects.isEmpty() == false) {
            for (final PotionEffect p : potionEffects) {
                player.addPotionEffect(p);
            }
        }
        if (hunger != -1) {
            player.setFoodLevel(hunger);
        }
        if (saturation != -1) {
            player.setSaturation(saturation);
        }
        if (health != -1) {
            player.setHealth(health);
        }
        if (teleport != null) {
            player.teleport(teleport);
        }
        if (failQuest == true) {
            quest.failQuest(quester);
        }
        if (timer > 0) {
            player.sendMessage(ChatColor.GREEN + Lang.get(player, "timerStart")
                    .replace("<time>", ChatColor.RED + String.valueOf(timer) + ChatColor.GREEN));
            if (timer > 60) {
                quester.getTimers().put(new ActionTimer(quester, quest, 60, false)
                        .runTaskLater(plugin, (timer-60)*20).getTaskId(), quest);
            }
            if (timer > 30) {
                quester.getTimers().put(new ActionTimer(quester, quest, 30, false)
                        .runTaskLater(plugin, (timer-30)*20).getTaskId(), quest);
            }
            if (timer > 10) {
                quester.getTimers().put(new ActionTimer(quester, quest, 10, false)
                        .runTaskLater(plugin, (timer-10)*20).getTaskId(), quest);
            }
            if (timer > 5) {
                quester.getTimers().put(new ActionTimer(quester, quest, 5, false)
                        .runTaskLater(plugin, (timer-5)*20).getTaskId(), quest);
            }
            if (timer > 4) {
                quester.getTimers().put(new ActionTimer(quester, quest, 4, false)
                        .runTaskLater(plugin, (timer-4)*20).getTaskId(), quest);
            }
            if (timer > 3) {
                quester.getTimers().put(new ActionTimer(quester, quest, 3, false)
                        .runTaskLater(plugin, (timer-3)*20).getTaskId(), quest);
            }
            if (timer > 2) {
                quester.getTimers().put(new ActionTimer(quester, quest, 2, false)
                        .runTaskLater(plugin, (timer-2)*20).getTaskId(), quest);
            }
            if (timer > 1) {
                quester.getTimers().put(new ActionTimer(quester, quest, 1, false)
                        .runTaskLater(plugin, (timer-1)*20).getTaskId(), quest);
            }
            quester.getTimers().put(new ActionTimer(quester, quest, 0, true)
                    .runTaskLater(plugin, timer*20).getTaskId(), quest);
        }
        if (cancelTimer) {
            for (final Map.Entry<Integer, Quest> entry : quester.getTimers().entrySet()) {
                if (entry.getValue().getName().equals(quest.getName())) {
                    plugin.getServer().getScheduler().cancelTask(entry.getKey());
                    quester.getTimers().remove(entry.getKey());
                }
            }
        }
        if (denizenScript != null) {
            plugin.getDenizenTrigger().runDenizenScript(denizenScript, quester);
        }
    }
}
    
