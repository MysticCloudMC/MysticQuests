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

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.blackvein.quests.reflect.denizen.DenizenAPI;
import me.blackvein.quests.reflect.worldguard.WorldGuardAPI;
import me.blackvein.quests.util.Lang;
import net.citizensnpcs.api.CitizensPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Dependencies {

	private final Quests plugin;
	private static Economy economy = null;
	private static Permission permission = null;
	private static WorldGuardAPI worldGuardApi = null;
	private static CitizensPlugin citizens = null;
	private static DenizenAPI denizenApi = null;

	public Dependencies(final Quests plugin) {
		this.plugin = plugin;
	}

	public Economy getVaultEconomy() {
		if (economy == null && isPluginAvailable("Vault")) {
			if (!setupEconomy()) {
				plugin.getLogger().warning("Economy not found.");
			}
		}
		return economy;
	}

	public Permission getVaultPermission() {
		if (permission == null && isPluginAvailable("Vault")) {
			if (!setupPermissions()) {
				plugin.getLogger().warning("Permissions not found.");
			}
		}
		return permission;
	}

	public WorldGuardAPI getWorldGuardApi() {
		if (worldGuardApi == null && isPluginAvailable("WorldGuard")) {
			worldGuardApi = new WorldGuardAPI(plugin.getServer().getPluginManager().getPlugin("WorldGuard"));
		}
		return worldGuardApi;
	}

	public CitizensPlugin getCitizens() {
		if (citizens == null && isPluginAvailable("Citizens")) {
			try {
				citizens = (CitizensPlugin) plugin.getServer().getPluginManager().getPlugin("Citizens");
				plugin.getLogger()
						.info("Successfully linked Quests with Citizens " + citizens.getDescription().getVersion());
			} catch (final Exception e) {
				plugin.getLogger().warning("Legacy version of Citizens found. Citizens in Quests not enabled.");
			}
		}
		return citizens;
	}

	public void disableCitizens() {
		citizens = null;
	}

	public DenizenAPI getDenizenApi() {
		if (denizenApi == null && isPluginAvailable("Denizen")) {
			denizenApi = new DenizenAPI();
		}
		return denizenApi;
	}

	public boolean isPluginAvailable(final String pluginName) {
		if (plugin.getServer().getPluginManager().getPlugin(pluginName) != null) {
			if (!plugin.getServer().getPluginManager().getPlugin(pluginName).isEnabled()) {
				plugin.getLogger().warning(
						pluginName + " was detected, but is not enabled! Fix " + pluginName + " to allow linkage.");
			} else {
				return true;
			}
		}
		return false;
	}

	void init() {
		getCitizens();
		getWorldGuardApi();
		getDenizenApi();
		getVaultEconomy();
		getVaultPermission();
	}

	private boolean setupEconomy() {
		try {
			final RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager()
					.getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
			return (economy != null);
		} catch (final Exception e) {
			return false;
		}
	}

	private boolean setupPermissions() {
		final RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	public String getCurrency(final boolean plural) {
		if (getVaultEconomy() == null) {
			return Lang.get("money");
		}
		if (plural) {
			if (getVaultEconomy().currencyNamePlural().trim().isEmpty()) {
				return Lang.get("money");
			} else {
				return getVaultEconomy().currencyNamePlural();
			}
		} else {
			if (getVaultEconomy().currencyNameSingular().trim().isEmpty()) {
				return Lang.get("money");
			} else {
				return getVaultEconomy().currencyNameSingular();
			}
		}
	}

	public boolean runDenizenScript(final String scriptName, final Quester quester) {
		return plugin.getDenizenTrigger().runDenizenScript(scriptName, quester);
	}

	public Location getNPCLocation(final int id) {
		return citizens.getNPCRegistry().getById(id).getStoredLocation();
	}

	public String getNPCName(final int id) {
		return citizens.getNPCRegistry().getById(id).getName();
	}

}
