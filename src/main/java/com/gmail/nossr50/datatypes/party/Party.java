package com.gmail.nossr50.datatypes.party;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;

public class Party {
    private final LinkedHashSet<String> members = new LinkedHashSet<String>();

    private String leader;
    private String name;
    private String password;
    private boolean locked;
    private Party ally;
    private int level;
    private float xp;

    private ShareMode xpShareMode   = ShareMode.NONE;
    private ShareMode itemShareMode = ShareMode.NONE;

    private boolean shareLootDrops        = true;
    private boolean shareMiningDrops      = true;
    private boolean shareHerbalismDrops   = true;
    private boolean shareWoodcuttingDrops = true;
    private boolean shareMiscDrops        = true;

    public Party(String name) {
        this.name = name;
    }

    public Party(String leader, String name) {
        this.leader = leader;
        this.name = name;
        this.locked = true;
        this.level = 0;
    }

    public Party(String leader, String name, String password) {
        this.leader = leader;
        this.name = name;
        this.password = password;
        this.locked = true;
        this.level = 0;
    }

    public Party(String leader, String name, String password, boolean locked) {
        this.leader = leader;
        this.name = name;
        this.password = password;
        this.locked = locked;
        this.level = 0;
    }

    public LinkedHashSet<String> getMembers() {
        return members;
    }

    public List<Player> getOnlineMembers() {
        List<Player> onlineMembers = new ArrayList<Player>();

        for (String memberName : members) {
            Player member = mcMMO.p.getServer().getPlayerExact(memberName);

            if (member != null) {
                onlineMembers.add(member);
            }
        }

        return onlineMembers;
    }

    public String getName() {
        return name;
    }

    public String getLeader() {
        return leader;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLocked() {
        return locked;
    }

    public Party getAlly() {
        return ally;
    }

    public List<String> getItemShareCategories() {
        List<String> shareCategories = new ArrayList<String>();

        for (ItemShareType shareType : ItemShareType.values()) {
            if (sharingDrops(shareType)) {
                shareCategories.add(shareType.getLocaleString());
            }
        }

        return shareCategories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setAlly(Party ally) {
        this.ally = ally;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public void addXp(float xp) {
        setXp(getXp() + xp);
    }

    protected float levelUp() {
        float xpRemoved = getXpToLevel();

        setLevel(getLevel() + 1);
        setXp(getXp() - xpRemoved);

        return xpRemoved;
    }

    public int getXpToLevel() {
        FormulaType formulaType = ExperienceConfig.getInstance().getFormulaType();
        return (mcMMO.getFormulaManager().getCachedXpToLevel(level, formulaType)) * 2;
    }

    /**
     * Applies an experience gain
     *
     * @param xp Experience amount to add
     */
    public void applyXpGain(float xp) {
        if (!EventUtils.handlePartyXpGainEvent(this, xp)) {
            return;
        }

        if (getXp() < getXpToLevel()) {
            return;
        }

        int levelsGained = 0;
        float xpRemoved = 0;

        while (getXp() >= getXpToLevel()) {
            if (hasReachedLevelCap()) {
                setXp(0);
                break;
            }

            xpRemoved += levelUp();
            levelsGained++;
        }

        if (!EventUtils.handlePartyLevelChangeEvent(this, levelsGained, xpRemoved)) {
            return;
        }

        Player leader = mcMMO.p.getServer().getPlayer(this.leader);

        if (Config.getInstance().getLevelUpSoundsEnabled()) {
            leader.playSound(leader.getLocation(), Sound.LEVEL_UP, Misc.LEVELUP_VOLUME, Misc.LEVELUP_PITCH);
        }

        leader.sendMessage(LocaleLoader.getString("Party.LevelUp", levelsGained, getLevel()));
    }

    private boolean hasReachedLevelCap() {
        return Config.getInstance().getPartyLevelCap() < getLevel() + 1;
    }

    public void setXpShareMode(ShareMode xpShareMode) {
        this.xpShareMode = xpShareMode;
    }

    public ShareMode getXpShareMode() {
        return xpShareMode;
    }

    public void setItemShareMode(ShareMode itemShareMode) {
        this.itemShareMode = itemShareMode;
    }

    public ShareMode getItemShareMode() {
        return itemShareMode;
    }

    public boolean sharingDrops(ItemShareType shareType) {
        switch (shareType) {
            case HERBALISM:
                return shareHerbalismDrops;

            case LOOT:
                return shareLootDrops;

            case MINING:
                return shareMiningDrops;

            case MISC:
                return shareMiscDrops;

            case WOODCUTTING:
                return shareWoodcuttingDrops;

            default:
                return false;
        }
    }

    public void setSharingDrops(ItemShareType shareType, boolean enabled) {
        switch (shareType) {
            case HERBALISM:
                shareHerbalismDrops = enabled;
                break;

            case LOOT:
                shareLootDrops = enabled;
                break;

            case MINING:
                shareMiningDrops = enabled;
                break;

            case MISC:
                shareMiscDrops = enabled;
                break;

            case WOODCUTTING:
                shareWoodcuttingDrops = enabled;
                break;

            default:
                return;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Party)) {
            return false;
        }

        Party other = (Party) obj;

        if ((this.getName() == null) || (other.getName() == null)) {
            return false;
        }

        return this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
